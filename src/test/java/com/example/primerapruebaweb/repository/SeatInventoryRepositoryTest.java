package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.*;
import com.example.primerapruebaweb.utilities.Cabin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")class SeatInventoryRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
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
