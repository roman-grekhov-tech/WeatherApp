import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;

public class WeatherResponseHandler {
    private final ObjectMapper mapper;
    private final JsonNode rootNode;
    private final int limit;

    public WeatherResponseHandler(HttpResponse<String> response, int limit) {
        if (response.statusCode() / 100 != 2) {
            throw new ApiResponseException("HTTP " + response.statusCode() + ": " + response.body());
        }

        this.mapper = new ObjectMapper();

        try {
            this.rootNode = mapper.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new ApiResponseException("Не удалось распарсить JSON: " + e);
        }

        this.limit = limit;
    }

    public String getPrettyResponse() {
        try {
            return this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            throw new ApiResponseException("Не удалось красиво распечатать JSON: " + e);
        }
    }

    public int getCurrentTemp() {
        JsonNode factNode = rootNode.path("fact");
        if (factNode.isMissingNode() || factNode.isNull()) {
            throw new ApiResponseException("В ответе сервиса нет fact");
        }
        JsonNode tempNode = factNode.path("temp");
        if (tempNode.isMissingNode() || tempNode.isNull()) {
            throw new ApiResponseException("В ответе сервиса нет fact.temp");
        }

        return tempNode.asInt();
    }

    public double getForecastsAverageTemp() {
        JsonNode forecasts = rootNode.path("forecasts");
        if (forecasts.isMissingNode() || forecasts.isNull()) {
            throw new ApiResponseException("В ответе сервиса нет forecasts");
        }

        double sum = 0;
        for (int i = 0; i < this.limit; i++) {
            JsonNode day = forecasts.get(i).path("parts").path("day");
            if (day.isMissingNode() || day.isNull()) {
                throw new ApiResponseException("Ошибка в forecasts");
            }

            sum += day.path("temp_avg").asInt();
        }

        return sum / this.limit;
    }
}
