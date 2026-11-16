package example;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class InfraiClient {
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final String baseUrl;
    private final String key;

    public InfraiClient(String baseUrl, String key) {
        this.baseUrl = baseUrl;
        this.key = key;
    }

    public String request(String method, String path, String body) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json");
        builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        String envelope = response.body();
        if (!envelope.contains("\"ok\":true")) {
            throw new IOException("Infrai rejected the request: " + envelope);
        }
        return envelope;
    }

    public String ingestLog(String eventId, Map<String, String> fields) throws IOException, InterruptedException {
        String entry = "{\"event_id\":\"" + eventId + "\",\"level\":\"info\",\"message\":\"" + fields.get("message") + "\",\"context\":{\"player_id\":\"" + fields.get("player_id") + "\",\"asset_id\":\"" + fields.get("asset_id") + "\",\"event_type\":\"" + fields.get("event_type") + "\",\"moderation_queue\":\"" + fields.get("moderation_queue") + "\"}}";
        String json = "{\"entries\":[" + entry + "]}";
        return request("POST", "/v1/logs/ingest", json);
    }

    public String searchLogs(String query) throws IOException, InterruptedException {
        return request("GET", "/v1/logs/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8), null);
    }

    public String createTemporaryKey() throws IOException, InterruptedException {
        return request("POST", "/v1/account/keys/create", "{\"name\":\"cutover-demo-temporary\"}");
    }

    public String setBudget() throws IOException, InterruptedException {
        return request("PUT", "/v1/account/budget/set", "{\"hard_cap_usd\":25,\"period\":\"monthly\",\"alert_threshold_usd\":20}");
    }

    public String listKeys() throws IOException, InterruptedException {
        return request("GET", "/v1/account/keys/list", null);
    }
}
