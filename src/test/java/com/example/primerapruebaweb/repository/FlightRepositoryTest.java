package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Airline;
import com.example.primerapruebaweb.entity.Airport;
import com.example.primerapruebaweb.entity.Flight;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FlightRepositoryTest {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Test
    void testSaveAndFind() {
        Airline airline = Airline.builder().code("AA").name("American Airlines").build();
        airlineRepository.save(airline);

        Airport origin = Airport.builder().code("JFK").name("John F Kennedy").city("New York").build();
        Airport destination = Airport.builder().code("LAX").name("Los Angeles").city("Los Angeles").build();
        airportRepository.save(origin);
        airportRepository.save(destination);

        Flight flight = Flight.builder()
                .number("AA100")
                .departureTime(OffsetDateTime.now())
                .arrivalTime(OffsetDateTime.now().plusHours(6))
                .airline(airline)
                .origin(origin)
                .destination(destination)
                .build();

        Flight saved = flightRepository.save(flight);

        assertThat(saved.getId()).isNotNull();
        assertThat(flightRepository.findById(saved.getId())).isPresent();
    }
}
