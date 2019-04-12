package com.gojek.parkinglot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoPoint implements Serializable {
    @JsonProperty
    private double lat;
    @JsonProperty
    private double lon;

    public GeoPoint(){}

    public GeoPoint(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public void reset(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public double lon(){
        return this.lon;
    }

    public double lat(){
        return this.lat;
    }
}
