package example;

public final class GameJobServiceTest {
    public static void main(String[] args) throws Exception {
        InfraiClient recordingClient = new InfraiClient("http://localhost", "test") {
            @Override public String ingestLog(String eventId, java.util.Map<String, String> fields) {
                if (!"manual-review".equals(fields.get("moderation_queue"))) throw new AssertionError("queue was not recorded");
                return "{\"ok\":true}";
            }
        };
        String result = new GameJobService(recordingClient).processAsset("player-17", "asset-884", true);
        if (!"manual-review".equals(result)) throw new AssertionError("moderation decision changed");
        System.out.println("business decision test passed");
    }
}
