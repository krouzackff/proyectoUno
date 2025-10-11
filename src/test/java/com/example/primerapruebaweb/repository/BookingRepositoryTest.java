package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Booking;
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

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookingRepositoryTest {
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
    private BookingRepository bookingRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Test
    void testSaveAndFind() {
        Passenger passenger = Passenger.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .profile(PassengerProfile.builder().phone("123456789").countryCode("US").build())
                .build();

        passengerRepository.save(passenger);

        Booking booking = Booking.builder()
                .createdAt(OffsetDateTime.now())
                .passenger(passenger)
                .build();

        Booking saved = bookingRepository.save(booking);

        assertThat(saved.getId()).isNotNull();
        assertThat(bookingRepository.findById(saved.getId())).isPresent();
    }
}
