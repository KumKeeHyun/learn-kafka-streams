package learnkafkastreams.twitter.serialization.json;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import org.apache.kafka.common.serialization.Serializer;

import learnkafkastreams.twitter.serialization.Tweet;

public class TweetSerializer implements Serializer<Tweet> {

    private Gson gson = new Gson();

    @Override
    public byte[] serialize(String topic, Tweet data) {
        if (data == null) return null;
        return gson.toJson(data).getBytes(StandardCharsets.UTF_8);
    }

}
