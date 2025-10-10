package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {
}
