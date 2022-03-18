package learnkafkastreams.leaderboard.model;

import lombok.Getter;

@Getter
public class Player {
    
    private Long id;
    
    private String name;

    @Override
    public String toString() {
        return "{ id='" + id + "', name='" + name + "' }";
    }

}
