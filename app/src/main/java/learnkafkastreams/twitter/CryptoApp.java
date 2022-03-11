package learnkafkastreams.twitter;

import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import learnkafkastreams.twitter.language.MockClient;

public class CryptoApp {
    
    public static void main(String[] args) {
        Topology topology = CryptoTopology.buildV2(new MockClient());

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "balns.iptime.org:9092");

        KafkaStreams streams = new KafkaStreams(topology, config);
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        System.out.println("Starting Twitter streams");
        streams.start();
    }
}
