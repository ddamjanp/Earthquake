package com.example.earthquake.controller;

import com.example.earthquake.model.Earthquake;
import com.example.earthquake.service.EarthquakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/earthquakes")
@RequiredArgsConstructor
public class EarthquakeController {

    private final EarthquakeService earthquakeService;

    @GetMapping("/get")
    public ResponseEntity<List<Earthquake>> fetchAndStore(){
        System.out.println("CALLED MF WHATS THE PROOOB");
        return ResponseEntity.ok(earthquakeService.fetchAndStore());
    }

    @GetMapping
    public ResponseEntity<List<Earthquake>> getAllEarthquakes(){
        return ResponseEntity.ok(earthquakeService.getAllEarthquakes());
    }

    @GetMapping("/filter/magnitude")
    public ResponseEntity<List<Earthquake>> filterByMagnitude(@RequestParam double minValue){
        return ResponseEntity.ok(earthquakeService.filterByBiggerThanMagnitude(minValue));
    }

    @GetMapping("/filter/after")
    public ResponseEntity<List<Earthquake>> filterByTime(@RequestParam Long time){
        return ResponseEntity.ok(earthquakeService.filterByTimeAfter(time));
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id){
        earthquakeService.deleteById(id);
        return "Earthquake запис with id "+ id+ " deleted successfully!";

    }
}
