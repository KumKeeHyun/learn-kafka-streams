package learnkafkastreams.twitter.serialization.json;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import org.apache.kafka.common.serialization.Serializer;

import learnkafkastreams.twitter.serialization.EntitySentiment;

public class ESSerializer implements Serializer<EntitySentiment> {

    private Gson gson = new Gson();

    @Override
    public byte[] serialize(String topic, EntitySentiment data) {
        if (data == null) return null;
        return gson.toJson(data).getBytes(StandardCharsets.UTF_8);
    }
    
}
