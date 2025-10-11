package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Airline;
import com.example.primerapruebaweb.entity.Airport;
import com.example.primerapruebaweb.entity.Flight;
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
@ActiveProfiles("test")
class FlightRepositoryTest {
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
