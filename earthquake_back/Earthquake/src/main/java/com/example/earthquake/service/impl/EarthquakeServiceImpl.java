package com.example.earthquake.service.impl;

import com.example.earthquake.dto.EarthQuakeDTO;
import com.example.earthquake.exception.EarthquakeException;
import com.example.earthquake.model.Earthquake;
import com.example.earthquake.repository.EarthquakeRepository;
import com.example.earthquake.service.EarthquakeService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EarthquakeServiceImpl implements EarthquakeService {

    private final EarthquakeRepository earthquakeRepository;
    private final RestClient restClient = RestClient.create();

    @org.springframework.beans.factory.annotation.Value("${usgs.api.url}")
    private String usgsUrl;

    @Override
    public List<Earthquake> fetchAndStore() {
        String response = restClient.get()
                .uri(usgsUrl)
                .retrieve()
                .body(String.class);

        if (response == null) {
            throw new EarthquakeException("No response from API");
        }
        List<EarthQuakeDTO> eqdtos = parseResponse(response);

        List<Earthquake> earthquakes = eqdtos.stream()
                .map(dto -> new Earthquake(
                        null,
                        dto.getMagnitude(),
                        dto.getPlace(),
                        dto.getTime(),
                        dto.getUpdated(),
                        dto.getMagType(),
                        dto.getTitle(),
                        dto.getLongitude(),
                        dto.getLatitude()
                ))
                .collect(Collectors.toList());

        earthquakeRepository.deleteAll();
        earthquakeRepository.saveAll(earthquakes);

        return earthquakes;
    }

    @Override
    public List<Earthquake> getAllEarthquakes() {
        return earthquakeRepository.findAll();
    }

    @Override
    public List<Earthquake> filterByBiggerThanMagnitude(double minValue) {
        return earthquakeRepository.findAll()
                .stream().filter(e -> e.getMagnitude() != null && e.getMagnitude() >= minValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Earthquake> filterByTimeAfter(Long time) {
        return earthquakeRepository.findAll()
                .stream()
                .filter(e -> e.getTime() != null && e.getTime() > time)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (!earthquakeRepository.existsById(id)) {
            throw new EarthquakeException("Earthquake with id " + id + " not found!");
        }
        earthquakeRepository.deleteById(id);
    }

    private List<EarthQuakeDTO> parseResponse(String response) {
        List<EarthQuakeDTO> result = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode features = root.path("features");

            for (JsonNode feature : features) {
                System.out.println("Hello there!!!!");
                JsonNode props = feature.path("properties");

                Double mag = props.path("mag").isNull() ? null : props.path("mag").asDouble();
                String place = props.path("place").isNull() ? null : props.path("place").asText();
                Long time = props.path("time").isNull() ? null : props.path("time").asLong();
                Long updated = props.path("updated").isNull() ? null : props.path("updated").asLong();
                String magType = props.path("magType").isNull() ? null : props.path("magType").asText();
                String title = props.path("title").isNull() ? null : props.path("title").asText();


                JsonNode coordinates = feature.path("geometry").path("coordinates");
                Double longitude = null;
                Double latitude = null;
                if (!coordinates.isMissingNode() && !coordinates.isNull() && coordinates.isArray() && coordinates.size() >= 2) {
                    longitude = coordinates.get(0).asDouble();
                    latitude = coordinates.get(1).asDouble();
                }
                EarthQuakeDTO dto = new EarthQuakeDTO(mag, place, time, updated, magType, title, longitude, latitude);
                result.add(dto);
            }
        } catch (Exception e) {
            System.out.println("Parse error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse USGS response", e);
        }
        return result;
    }
}
