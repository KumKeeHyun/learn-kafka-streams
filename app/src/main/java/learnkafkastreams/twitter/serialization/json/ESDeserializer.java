package learnkafkastreams.twitter.serialization.json;

import java.nio.charset.StandardCharsets;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.kafka.common.serialization.Deserializer;

import learnkafkastreams.twitter.serialization.EntitySentiment;

public class ESDeserializer implements Deserializer<EntitySentiment> {

    private Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                        .create();

    @Override
    public EntitySentiment deserialize(String topic, byte[] data) {
        if (data == null) return null;
        return gson.fromJson(new String(data, StandardCharsets.UTF_8), EntitySentiment.class);
    }
    
}
