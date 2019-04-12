package com.gojek.parkinglot.entity.database;

import com.gojek.parkinglot.entity.database.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ParkingLotDAO extends JpaRepository<ParkingLot,Integer> {
}
