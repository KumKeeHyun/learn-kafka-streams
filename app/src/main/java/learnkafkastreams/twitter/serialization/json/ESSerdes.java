package learnkafkastreams.twitter.serialization.json;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import learnkafkastreams.twitter.serialization.EntitySentiment;

public class ESSerdes implements Serde<EntitySentiment> {

    @Override
    public Serializer<EntitySentiment> serializer() {
        return new ESSerializer();
    }

    @Override
    public Deserializer<EntitySentiment> deserializer() {
        return new ESDeserializer();
    }
    
}
