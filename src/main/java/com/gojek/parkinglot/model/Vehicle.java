package com.gojek.parkinglot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
public class Vehicle {
    public String vehicle_num;
    public String owner_name;
    public String color;
    public String vehicle_model_name;

    public Vehicle(){}
}
