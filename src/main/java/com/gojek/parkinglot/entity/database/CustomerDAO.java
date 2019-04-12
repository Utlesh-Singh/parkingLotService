package com.gojek.parkinglot.entity.database;

import com.gojek.parkinglot.entity.database.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDAO extends JpaRepository<Customer,Integer> {
}
