package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.SeatInventory;
import com.example.primerapruebaweb.utilities.Cabin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {

    // Método para verificar si ya existe un SeatInventory para un vuelo y cabina específicos
    boolean existsByFlightIdAndCabin(Long flightId, Cabin cabin);

    // Método opcional para buscar por flightId y cabin
    Optional<SeatInventory> findByFlightIdAndCabin(Long flightId, Cabin cabin);

    // Método alternativo usando @Query
    @Query("SELECT COUNT(s) > 0 FROM SeatInventory s WHERE s.flight.id = :flightId AND s.cabin = :cabin")
    boolean existsByFlightAndCabin(@Param("flightId") Long flightId, @Param("cabin") Cabin cabin);

}
