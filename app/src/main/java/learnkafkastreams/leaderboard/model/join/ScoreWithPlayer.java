package learnkafkastreams.leaderboard.model.join;

import learnkafkastreams.leaderboard.model.Player;
import learnkafkastreams.leaderboard.model.ScoreEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoreWithPlayer {
    
    private ScoreEvent scoreEvent;
    private Player player;

    @Override
    public String toString() {
        return "{"
        + " scoreEvent='" + scoreEvent
        + "', player='" + player
        + "' }";
    }
}
