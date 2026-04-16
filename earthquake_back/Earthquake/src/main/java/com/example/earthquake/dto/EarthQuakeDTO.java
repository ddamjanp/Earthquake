package com.example.earthquake.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EarthQuakeDTO {

    private Double magnitude;
    private String place;
    private Long time;
    private Long updated;
    private String magType;
    private String title;
    private Double longitude;
    private Double latitude;
}
