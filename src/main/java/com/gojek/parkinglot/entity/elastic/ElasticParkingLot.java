package com.gojek.parkinglot.entity.elastic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gojek.parkinglot.model.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticParkingLot {
    @JsonProperty
    private Integer licence_id;
    @JsonProperty
    private String area_name;
    @JsonProperty
    private GeoPoint location;

    public ElasticParkingLot(){
    }

    @Override
    public int hashCode() {
        return licence_id;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        else if(obj instanceof ElasticParkingLot && this.licence_id == ((ElasticParkingLot)obj).licence_id)
            return true;
        return false;
    }
}
