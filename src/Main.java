import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    // поддерживаются значения 1-7
    private static final int LIMIT = 3;

    private static final String ACCESS_KEY = "6146a1f1-cd8a-4b54-9ff6-84e528227f91";

    // extra и hours не нужны для расчета средней температуры, поэтому они выключены
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

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        WeatherResponseHandler weatherResponseHandler = new WeatherResponseHandler(response, LIMIT);

        System.out.printf("%s:\n%s\n\n", "Вывод полного ответа сервиса", weatherResponseHandler.getPrettyResponse());

        System.out.printf("%s:\n%d\n\n", "Вывод текущей температуры", weatherResponseHandler.getCurrentTemp());

        System.out.printf("%s (%d) дней:\n%.2f\n",
                "Среднее арифметическое средних температур за",
                LIMIT,
                weatherResponseHandler.getForecastsAverageTemp());
    }
}
