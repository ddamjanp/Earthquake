package com.example.earthquake.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "Earthquakes")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Earthquake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double magnitude;
    private String place;
    private Long time;
    private Long updated;
    private String magType;
    private String title;
    private Double longitude;
    private Double latitude;

}
