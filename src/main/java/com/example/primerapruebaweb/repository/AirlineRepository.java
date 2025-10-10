package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {
    Airline id(Long id);
}
