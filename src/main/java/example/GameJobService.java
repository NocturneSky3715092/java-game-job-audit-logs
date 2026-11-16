package example;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class GameJobService {
    private final InfraiClient client;

    public GameJobService(InfraiClient client) { this.client = client; }

    public String processAsset(String playerId, String assetId, boolean moderationRequired) throws Exception {
        String queue = moderationRequired ? "manual-review" : "clear";
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("message", "asset moderation decision recorded");
        fields.put("player_id", playerId);
        fields.put("asset_id", assetId);
        fields.put("event_type", "asset_published");
        fields.put("moderation_queue", queue);
        client.ingestLog(UUID.randomUUID().toString(), fields);
        return queue;
    }
}
