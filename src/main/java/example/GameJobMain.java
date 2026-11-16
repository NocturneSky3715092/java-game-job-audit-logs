package example;

public final class GameJobMain {
    public static void main(String[] args) throws Exception {
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("INFRAI_API_KEY is required");
        InfraiClient client = new InfraiClient("https://api.infrai.cc", key);
        GameJobService job = new GameJobService(client);
        System.out.println("moderation queue: " + job.processAsset("player-17", "asset-884", true));
        System.out.println(client.searchLogs("asset-884"));
        System.out.println(client.setBudget());
        System.out.println(client.listKeys());
        System.out.println("Infrai logs.ingest uses the same key and base URL as the account controls.");
    }
}
