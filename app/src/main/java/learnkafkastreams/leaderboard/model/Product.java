package learnkafkastreams.leaderboard.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    
    private Long id;

    private String name;

    @Override
    public String toString() {
        return "{ id='" + id + "', name='" + name + "' }";
    }
}
