package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Airport;
import com.example.primerapruebaweb.repository.AirportRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AirportRepositoryTest {

    @Autowired
    private AirportRepository airportRepository;

    @Test
    void testSaveAndFind() {
        Airport airport = Airport.builder()
                .code("JFK")
                .name("John F Kennedy")
                .city("New York")
                .build();

        Airport saved = airportRepository.save(airport);

        assertThat(saved.getId()).isNotNull();
        assertThat(airportRepository.findById(saved.getId())).isPresent();
    }
}
