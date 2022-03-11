package learnkafkastreams.twitter;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;

import learnkafkastreams.twitter.serialization.Tweet;
import learnkafkastreams.twitter.serialization.json.TweetSerdes;

public class CryptoTopology {
    
    public static Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<byte[], Tweet> stream = builder.stream(
            "tweets", 
            Consumed.with(Serdes.ByteArray(), new TweetSerdes()));
        stream.foreach((key, value) -> {
            System.out.println(value.toString());
        });
        // stream.print(Printed.<byte[], Tweet>toSysOut().withLabel("tweet-stream"));

        return builder.build();
    }
}
