package learnkafkastreams.leaderboard.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreEvent {
    
    private Long playerId;

    private Long productId;

    private Double score;
}
