package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport,Long> {
}
