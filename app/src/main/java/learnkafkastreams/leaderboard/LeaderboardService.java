package learnkafkastreams.leaderboard;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import io.javalin.http.Context;
import learnkafkastreams.leaderboard.model.HighScores;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RequiredArgsConstructor
public class LeaderboardService {
    
    private final HostInfo hostInfo;
    private final KafkaStreams streams;

    private static final Logger log = LoggerFactory.getLogger(LeaderboardService.class);

    ReadOnlyKeyValueStore<String, HighScores> getStore() {
        return streams.store(
            StoreQueryParameters.fromNameAndType(
                // state store name
                "leader-boards",
                // state store type
                QueryableStoreTypes.keyValueStore()));
    }

    void start() {
        Javalin app = Javalin.create().start(hostInfo.port());

        /** Local key-value store query: point-lookup / single-key lookup */
        app.get("/leaderboard/:key", this::getKey);
      }

    void getKey(Context ctx) {
        String productId = ctx.pathParam("key");
    
        // find out which host has the key
        KeyQueryMetadata metadata =
            streams.queryMetadataForKey(
                "leader-boards", 
                productId, 
                Serdes.String().serializer());
    
        // the local instance has this key
        if (hostInfo.equals(metadata.activeHost())) {
            log.info("Querying local store for key");
            HighScores highScores = getStore().get(productId);
        
            if (highScores == null) {
                // game was not found
                ctx.status(404);
                return;
            }
        
            // game was found, so return the high scores
            ctx.json(highScores.toList());
            return;
        }
    
        // a remote instance has the key
        String remoteHost = metadata.activeHost().host();
        int remotePort = metadata.activeHost().port();
        String url =
            String.format(
                "http://%s:%d/leaderboard/%s",
                // params
                remoteHost, remotePort, productId);
    
        // issue the request
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
    
        try (Response response = client.newCall(request).execute()) {
            log.info("Querying remote store for key");
            ctx.result(response.body().string());
        } catch (Exception e) {
            ctx.status(500);
        }
      }
}
