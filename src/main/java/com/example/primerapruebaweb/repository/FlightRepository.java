package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Método para validar número de vuelo único
    boolean existsByNumber(String number);

    Optional<Flight> findByNumber(String number);

    // Métodos para búsquedas específicas
    List<Flight> findByDepartureTimeAfter(OffsetDateTime departureTime);
    List<Flight> findByOriginIdAndDestinationId(Long originId, Long destinationId);
    List<Flight> findByAirlineId(Long airlineId);
    List<Flight> findByOriginId(Long originId);
    List<Flight> findByDestinationId(Long destinationId);

//    // Método CORREGIDO para verificar vuelos con reservas activas
//    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.flight.id = :flightId AND b.status != 'CANCELLED'")
//    boolean hasActiveBookings(@Param("flightId") Long flightId);
//
//    // Método alternativo si no tienes la entidad Booking
//    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Flight f WHERE f.id = :flightId AND f.departureTime > CURRENT_TIMESTAMP")
//    boolean hasFutureDeparture(@Param("flightId") Long flightId);
}