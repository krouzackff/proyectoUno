package com.example.primerapruebaweb.repository;
import com.example.primerapruebaweb.entity.Airline;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AirlineRepositoryTest {

    @Autowired
    private AirlineRepository airlineRepository;

    @Test
    void testSaveAndFind() {
        Airline airline = Airline.builder().code("LH").name("Lufthansa").build();
        Airline saved = airlineRepository.save(airline);

        assertThat(saved.getId()).isNotNull();
        assertThat(airlineRepository.findById(saved.getId())).isPresent();
    }
}
