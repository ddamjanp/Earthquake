package com.example.earthquake.repository;

import com.example.earthquake.model.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EarthquakeRepository extends JpaRepository<Earthquake,Long>{
}
