package com.gojek.parkinglot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gojek.parkinglot.entity.database.VehicleClass;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class ElasticVacantParkingLotsResponse {
    @JsonProperty
    private Integer licence_id;
    @JsonProperty
    private String area_name;
    @JsonProperty
    private List<VehicleClass> vehicleClasses;
    @JsonProperty
    private GeoPoint location;

    public ElasticVacantParkingLotsResponse(){}

}
