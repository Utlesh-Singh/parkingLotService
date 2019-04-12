package com.gojek.parkinglot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParkingDetails {
    private GeoPoint geoPoint;
    private Double max_distance_from_geopoint;
}
