package com.example.earthquake.service;

import com.example.earthquake.model.Earthquake;

import java.util.List;

public interface EarthquakeService {
    List<Earthquake> fetchAndStore();
    List<Earthquake> getAllEarthquakes();
    List<Earthquake> filterByBiggerThanMagnitude(double minValue);
    List<Earthquake> filterByTimeAfter(Long time);
    void deleteById(Long id);
}
