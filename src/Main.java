import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    private static final int LIMIT = 3;

    private static final String ACCESS_KEY = "6146a1f1-cd8a-4b54-9ff6-84e528227f91";
    private static final String REQUEST_URI = "https://api.weather.yandex.ru/v2/forecast?" +
            "lat=57.1522&" +
            "lon=65.5272&" +
            "limit=" + LIMIT + "&" +
            "extra=false&" +
            "hours=false";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(REQUEST_URI))
                .GET()
                .header("Accept", "application/json")
                .header("X-Yandex-Weather-Key", ACCESS_KEY)
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("HTTP " + response.statusCode() + ": " + response.body());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.body());

        JsonNode factNode = node.get("fact");
        if (factNode == null || factNode.isNull()) {
            throw new IllegalArgumentException("В ответе API нет fact: " + node);
        }
        JsonNode tempNode = factNode.get("temp");
        if (tempNode == null || tempNode.isNull()) {
            throw new IllegalArgumentException("В ответе API нет fact.temp: " + node);
        }

        int temp = factNode.get("temp").asInt();

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
        System.out.println(temp);
    }
}
