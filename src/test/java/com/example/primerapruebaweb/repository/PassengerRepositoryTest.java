package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Passenger;
import com.example.primerapruebaweb.entity.PassengerProfile;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")class PassengerRepositoryTest {

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
    private PassengerRepository passengerRepository;

    @Test
    void testSaveAndFind() {
        Passenger passenger = Passenger.builder()
                .fullName("Alice Johnson")
                .email("alice@example.com")
                .profile(PassengerProfile.builder().phone("555-1234").countryCode("US").build())
                .build();

        Passenger saved = passengerRepository.save(passenger);

        assertThat(saved.getId()).isNotNull();
        assertThat(passengerRepository.findById(saved.getId())).isPresent();
    }
}
