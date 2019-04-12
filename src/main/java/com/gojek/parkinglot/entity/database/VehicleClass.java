package com.gojek.parkinglot.entity.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Embeddable
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleClass {
    @JsonProperty
    private String parking_class_name;
    @JsonProperty
    private Integer vacancy;
    @JsonProperty
    private Double size;
    @JsonProperty
    private Double price;

    public VehicleClass(){}

    @Override
    public int hashCode() {
        return this.parking_class_name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj instanceof VehicleClass && this.parking_class_name.equalsIgnoreCase(((VehicleClass) obj).parking_class_name))
            return true;
        return false;
    }
}
