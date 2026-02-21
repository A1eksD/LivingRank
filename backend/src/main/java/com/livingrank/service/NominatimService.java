package com.livingrank.service;

import com.livingrank.entity.Street;
import com.livingrank.repository.StreetRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NominatimService {

    private final StreetRepository streetRepository;
    private final RestTemplate restTemplate;

    // Rate limit: max 1 request per second to Nominatim
    private long lastRequestTime = 0;

    public NominatimService(StreetRepository streetRepository) {
        this.streetRepository = streetRepository;
        this.restTemplate = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    public List<Street> searchAndCache(String query) {
        // Respect Nominatim rate limit (1 req/sec)
        synchronized (this) {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRequestTime;
            if (elapsed < 1000) {
                try {
                    Thread.sleep(1000 - elapsed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return List.of();
                }
            }
            lastRequestTime = System.currentTimeMillis();
        }

        try {
            String url = "https://nominatim.openstreetmap.org/search?q=" +
                java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8) +
                "&format=json&addressdetails=1&limit=10&countrycodes=de,at,ch,nl,be,fr,pl,cz";

            var headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "LivingRank/1.0");
            var entity = new org.springframework.http.HttpEntity<>(headers);

            var response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET,
                entity, List.class);

            List<Map<String, Object>> results = response.getBody();
            if (results == null) return List.of();

            List<Street> cached = new ArrayList<>();
            for (Map<String, Object> result : results) {
                Map<String, Object> address = (Map<String, Object>) result.get("address");
                if (address == null) continue;

                String road = (String) address.getOrDefault("road",
                    address.getOrDefault("pedestrian",
                        address.getOrDefault("residential", null)));
                if (road == null) continue;

                String city = (String) address.getOrDefault("city",
                    address.getOrDefault("town",
                        address.getOrDefault("village",
                            address.getOrDefault("municipality", "Unbekannt"))));
                String postalCode = (String) address.get("postcode");
                String state = (String) address.get("state");
                String countryCode = (String) address.getOrDefault("country_code", "de");

                Double lat = parseDouble(result.get("lat"));
                Double lon = parseDouble(result.get("lon"));

                // Check if already exists
                var existing = streetRepository.findByStreetNameAndPostalCodeAndCityAndCountry(
                    road, postalCode, city, countryCode.toUpperCase());

                if (existing.isEmpty()) {
                    Street street = new Street(road, postalCode, city, state,
                        countryCode.toUpperCase(), lat, lon);
                    try {
                        cached.add(streetRepository.save(street));
                    } catch (Exception e) {
                        // Ignore duplicates from concurrent requests
                    }
                }
            }
            return cached;
        } catch (Exception e) {
            return List.of();
        }
    }

    private Double parseDouble(Object value) {
        if (value == null) return null;
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
