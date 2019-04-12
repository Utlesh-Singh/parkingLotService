package com.gojek.parkinglot.entity.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gojek.parkinglot.model.ParkingDetails;
import com.gojek.parkinglot.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class Customer {
    @Id
    @JsonIgnore
    private Integer customer_id;
    @JsonProperty
    private String customer_name;
    @Embedded
    @JsonProperty
    private Vehicle vehicle;

    public Customer(){
    }

    @Override
    public int hashCode() {
        return customer_id;
    }

    @Override
    public boolean equals(Object object){
        if(this == object)
            return true;
        return object instanceof Customer && this.customer_id == ((Customer)object).customer_id;
    }
}
