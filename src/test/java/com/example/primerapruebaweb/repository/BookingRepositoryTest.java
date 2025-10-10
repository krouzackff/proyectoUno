package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Booking;
import com.example.primerapruebaweb.entity.Passenger;
import com.example.primerapruebaweb.entity.PassengerProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

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
