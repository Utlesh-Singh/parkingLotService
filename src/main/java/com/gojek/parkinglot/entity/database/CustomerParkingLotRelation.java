package com.gojek.parkinglot.entity.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@Getter
@Setter
public class CustomerParkingLotRelation {
    @Id
    private Integer customer_id;
    private Integer licence_id;
    private String class_booked;

    public CustomerParkingLotRelation(){}

    public CustomerParkingLotRelation(Integer customerId, Integer licence_id){
        this.customer_id = customerId;
        this.licence_id = licence_id;
    }


}
