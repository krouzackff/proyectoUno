package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.*;
import com.example.primerapruebaweb.utilities.Cabin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SeatInventoryRepositoryTest {

    @Autowired
    private SeatInventoryRepository seatInventoryRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Test
    void testSaveAndFind() {
        Airline airline = Airline.builder().code("UA").name("United Airlines").build();
        airlineRepository.save(airline);

        Airport origin = Airport.builder().code("ORD").name("Chicago O'Hare").city("Chicago").build();
        Airport destination = Airport.builder().code("SFO").name("San Francisco").city("San Francisco").build();
        airportRepository.save(origin);
        airportRepository.save(destination);

        Flight flight = Flight.builder()
                .number("UA200")
                .departureTime(OffsetDateTime.now())
                .arrivalTime(OffsetDateTime.now().plusHours(4))
                .airline(airline)
                .origin(origin)
                .destination(destination)
                .build();
        flightRepository.save(flight);

        SeatInventory seatInventory = SeatInventory.builder()
                .flight(flight)
                .cabin(Cabin.BUSINESS)
                .totalSeats(50)
                .availableSeats(45)
                .build();

        SeatInventory saved = seatInventoryRepository.save(seatInventory);

        assertThat(saved.getId()).isNotNull();
        assertThat(seatInventoryRepository.findById(saved.getId())).isPresent();
    }
}
