package learnkafkastreams.leaderboard;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.state.KeyValueStore;

import learnkafkastreams.leaderboard.model.HighScores;
import learnkafkastreams.leaderboard.model.Player;
import learnkafkastreams.leaderboard.model.Product;
import learnkafkastreams.leaderboard.model.ScoreEvent;
import learnkafkastreams.leaderboard.model.join.Enriched;
import learnkafkastreams.leaderboard.model.join.ScoreWithPlayer;
import learnkafkastreams.leaderboard.serialization.json.JsonSerdes;

public class LeaderboardTopologyV2 {
    
    public static Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        // score-events 스트림 생성
        KStream<String, ScoreEvent> scoreEvents =
            builder
                .stream(
                    "score-events", 
                    Consumed.with(Serdes.ByteArray(), JsonSerdes.ScoreEvent()))
                // score-events 토픽의 메시지들은 key가 없음. 
                // key를 지정하기 위해 re-partitioning. 
                .selectKey((k, v) -> v.getPlayerId().toString());

        // players 테이블 생성
        KTable<String, Player> players =
            builder.table(
                "players", 
                Consumed.with(Serdes.String(), JsonSerdes.Player()));

        // product 글로벌 테이블 생성
        GlobalKTable<String, Product> products =
            builder.globalTable(
                "products", 
                Consumed.with(Serdes.String(), JsonSerdes.Product()));

        ValueJoiner<ScoreEvent, Player, ScoreWithPlayer> scorePlayerJoiner =
            (score, player) -> new ScoreWithPlayer(score, player);
        // ...
        Joined<String, ScoreEvent, Player> playerJoinParams =
            Joined.with(
                Serdes.String(), 
                JsonSerdes.ScoreEvent(),
                JsonSerdes.Player());
        // scoreEvents JOIN player -> ScoreWithPlayer
        KStream<String, ScoreWithPlayer> withPlayers =
            scoreEvents.join(players, scorePlayerJoiner, playerJoinParams);

        /**
         * map score-with-player records to products
         *
         * <p>Regarding the KeyValueMapper param types: - String is the key type for the score events
         * stream - ScoreWithPlayer is the value type for the score events stream - String is the lookup
         * key type
         */
        KeyValueMapper<String, ScoreWithPlayer, String> keyMapper =
            (leftKey, scoreWithPlayer) -> {
            return String.valueOf(scoreWithPlayer.getScoreEvent().getProductId());
            };
        ValueJoiner<ScoreWithPlayer, Product, Enriched> productJoiner =
            (scoreWithPlayer, product) -> new Enriched(scoreWithPlayer, product);
        // scoreWithPlayer JOIN product -> Enriched
        KStream<String, Enriched> withProducts = 
            withPlayers.join(products, keyMapper, productJoiner);
        withProducts.print(Printed.<String, Enriched>toSysOut().withLabel("with-products"));

        /** Group the enriched product stream */
        KGroupedStream<String, Enriched> grouped =
            withProducts.groupBy(
                (key, value) -> value.getProductId().toString(),
                Grouped.with(Serdes.String(), JsonSerdes.Enriched()));
        // alternatively, use the following if you want to name the grouped repartition topic:
        // Grouped.with("grouped-enriched", Serdes.String(), JsonSerdes.Enriched()))

        /** The initial value of our aggregation will be a new HighScores instances */
        Initializer<HighScores> highScoresInitializer = HighScores::new;
        /** The logic for aggregating high scores is implemented in the HighScores.add method */
        Aggregator<String, Enriched, HighScores> highScoresAdder =
            (key, value, aggregate) -> aggregate.add(value);

        /** Perform the aggregation, and materialize the underlying state store for querying */
        KTable<String, HighScores> highScores =
        grouped.aggregate(
            highScoresInitializer,
            highScoresAdder,
            Materialized.<String, HighScores, KeyValueStore<Bytes, byte[]>>
                // give the state store an explicit name to make it available for interactive
                // queries
                as("leader-boards")
                .withKeySerde(Serdes.String())
                .withValueSerde(JsonSerdes.HighScores()));

    highScores.toStream().to("high-scores");

        return builder.build();
    }
}
