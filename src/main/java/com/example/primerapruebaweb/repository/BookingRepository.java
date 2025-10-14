package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Método para buscar por número de reserva
    Optional<Booking> findByBookingNumber(String bookingNumber);

    // Método para verificar reservas duplicadas
//    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.passenger.id = :passengerId AND b.flight.id = :flightId AND b.status != 'CANCELLED'")
//    boolean existsByPassengerAndFlightAndStatusNotCancelled(@Param("passengerId") Long passengerId, @Param("flightId") Long flightId);

    // Método para buscar reservas por pasajero
    List<Booking> findByPassengerId(Long passengerId);

    // Método para buscar reservas por vuelo
    List<Booking> findByFlightId(Long flightId);

    // Método para buscar reservas por estado
    List<Booking> findByStatus(String status);

    // Método para contar reservas activas por vuelo
//    @Query("SELECT COUNT(b) FROM Booking b WHERE b.flight.id = :flightId AND b.status != 'CANCELLED'")
//    Long countActiveBookingsByFlight(@Param("flightId") Long flightId);
}