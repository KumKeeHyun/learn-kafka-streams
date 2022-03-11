package learnkafkastreams.twitter.serialization;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EntitySentiment {
    
    @SerializedName("CreatedAt")
    private Long createdAt;
    
    @SerializedName("Id")
    private Long id;

    @SerializedName("Entity")
    private String entity;

    @SerializedName("Text")
    private String text;

    @SerializedName("SentimentScore")
    private Double sentimentScore;

    @SerializedName("SentimentMagnitude")
    private Double sentimentMagnitude;

    @SerializedName("Salience")
    private Double salience;
}
