package com.example.todoapp.api;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HolidayClient {

    private static final String HOLIDAYS_URL = "https://holidays-jp.github.invalid/api/v1/date.json";

    private final RestClient restClient;

    public HolidayClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        this.restClient = RestClient.create(restTemplate);
    }

    public FetchResult fetch(LocalDate from, LocalDate to) {
        try {
            Map<String, String> holidays = restClient.get()
                    .uri(HOLIDAYS_URL)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, String>>() {
                    });

            if (from == null && to == null) {
                return new FetchResult(holidays, false);
            }

            Map<String, String> filteredHolidays = holidays.entrySet().stream()
                    .filter(entry -> isInRange(entry.getKey(), from, to))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (first, second) -> first,
                            LinkedHashMap::new));
            return new FetchResult(filteredHolidays, false);
        } catch (RestClientException exception) {
            return new FetchResult(new LinkedHashMap<>(), true);
        }
    }

    private boolean isInRange(String dateText, LocalDate from, LocalDate to) {
        LocalDate date = LocalDate.parse(dateText);
        return (from == null || !date.isBefore(from))
                && (to == null || !date.isAfter(to));
    }

    public record FetchResult(Map<String, String> holidays, boolean unavailable) {
    }
}
