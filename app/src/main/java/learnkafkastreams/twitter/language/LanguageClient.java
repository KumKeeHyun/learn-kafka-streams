package learnkafkastreams.twitter.language;

import java.util.List;

import learnkafkastreams.twitter.serialization.EntitySentiment;
import learnkafkastreams.twitter.serialization.Tweet;

public interface LanguageClient {
    
    Tweet translate(Tweet tweet, String targetLanguage);

    List<EntitySentiment> getEntitySentiment(Tweet tweet);
}
