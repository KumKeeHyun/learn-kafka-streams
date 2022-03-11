package learnkafkastreams.twitter.language;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.base.Splitter;

import learnkafkastreams.twitter.serialization.EntitySentiment;
import learnkafkastreams.twitter.serialization.Tweet;
import learnkafkastreams.twitter.serialization.Tweet.TweetPayload;

public class MockClient implements LanguageClient {

    @Override
    public Tweet translate(Tweet tweet, String targetLanguage) {
        TweetPayload payload = tweet.getPayload();
        payload.setText("Translated: " + payload.getText());
        tweet.setPayload(payload);
        return tweet;
    }

    @Override
    public List<EntitySentiment> getEntitySentiment(Tweet tweet) {
        TweetPayload payload = tweet.getPayload();
        ArrayList<EntitySentiment> results = new ArrayList<>();

        Iterable<String> words = Splitter.on(' ')
            .split(tweet.getPayload()
                .getText()
                .toLowerCase()
                .replace("#", " "));

        for (String entity : words) {
            EntitySentiment entitySentiment = EntitySentiment.builder()
                .createdAt(payload.getCreatedAt())
                .id(payload.getId())
                .entity(entity)
                .text(payload.getText())
                .salience(randomDouble())
                .sentimentScore(randomDouble())
                .sentimentMagnitude(randomDouble())
                .build();
                
            results.add(entitySentiment);
        }

        return results;
    }

    Double randomDouble() {
        return ThreadLocalRandom.current().nextDouble(0, 1);
    }
    
}
