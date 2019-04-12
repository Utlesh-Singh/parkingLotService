package com.gojek.parkinglot.entity.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerParkingLotRelationDAO extends JpaRepository<CustomerParkingLotRelation,Integer> {
}
