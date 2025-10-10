package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Passenger;
import com.example.primerapruebaweb.entity.PassengerProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PassengerRepositoryTest {

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
