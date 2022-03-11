package learnkafkastreams.twitter;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

import learnkafkastreams.twitter.language.LanguageClient;
import learnkafkastreams.twitter.serialization.EntitySentiment;
import learnkafkastreams.twitter.serialization.Tweet;
import learnkafkastreams.twitter.serialization.json.ESSerdes;
import learnkafkastreams.twitter.serialization.json.TweetSerdes;

public class CryptoTopology {
    
    public static Topology buildV1() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<byte[], Tweet> stream = builder.stream(
            "tweets", 
            Consumed.with(Serdes.ByteArray(), new TweetSerdes()));
        stream.foreach((key, value) -> {
            System.out.println(value.toString());
        });

        return builder.build();
    }

    public static Topology buildV2(LanguageClient client) {
        StreamsBuilder builder = new StreamsBuilder();

        // tweets-stream 토픽에서 메시지 컨슘.
        // TweetSerde를 통해 역직렬화.
        KStream<byte[], Tweet> stream = builder.stream(
            "tweets", 
            Consumed.with(Serdes.ByteArray(), new TweetSerdes()));
        stream.print(Printed.<byte[], Tweet>toSysOut().withLabel("tweets-stream"));

        // 리트윗이 아닌 메시지들만 필터링.
        KStream<byte[], Tweet> filtered = stream.filterNot(
            (key, tweet) -> {
                return tweet.getPayload().getRetweet();
            }
        );    

        // 영어인 트윗와 아닌 트윗으로 스트림을 분리.
        KStream<byte[], Tweet>[] branches = filtered.branch(
            (key, tweet) -> tweet.getPayload().getLang().equals("en"),
            (key, tweet) -> !tweet.getPayload().getLang().equals("en")
        );

        KStream<byte[], Tweet> englishStream = branches[0];
        KStream<byte[], Tweet> nonEnglishStream = branches[1];

        // 영어가 아닌 트윗은 번역.
        KStream<byte[], Tweet> translatedStream = nonEnglishStream.mapValues(
            (tweet) -> {
                return client.translate(tweet, "en");
            }
        );

        // 영어인 트윗와 변역된 트윗을 병합.
        KStream<byte[], Tweet> mergedStream = englishStream.merge(translatedStream);

        // 각 단어를 감정 분석해서 EntitySentiment로 변환.
        KStream<byte[], EntitySentiment> enriched = mergedStream.flatMapValues(
            (tweet) -> client.getEntitySentiment(tweet)
        );

        // 분석한 레코드들을 crypto-sentiment 토픽에 저장.
        // ESSerdes를 통해 직렬화.
        enriched.to(
            "crypto-sentiment",
            Produced.with(Serdes.ByteArray(), new ESSerdes()));

        return builder.build();
    }

}
