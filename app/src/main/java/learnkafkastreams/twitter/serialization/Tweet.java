package learnkafkastreams.twitter.serialization;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tweet {

    @SerializedName("payload")
    private TweetPayload payload;

    @Getter
    @Setter
    public static class TweetPayload {

        @SerializedName("CreatedAt")
        private Long createdAt;
    
        @SerializedName("Id")
        private Long id;
        
        @SerializedName("Lang")
        private Boolean lang;
        
        @SerializedName("Retweet")
        private Boolean retweet;
        
        @SerializedName("Text")
        private String text;

        @Override
        public String toString() {
            return "TweetPayload [createdAt=" + createdAt + ", id=" + id + ", lang=" + lang + ", retweet=" + retweet
                    + ", text=" + text + "]";
        }
    
    }

    @Override
    public String toString() {
        return "Tweet [payload=" + payload + "]";
    }
}