package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.*;
import com.example.primerapruebaweb.utilities.Cabin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingItemRepositoryTest {

    @Autowired
    private BookingItemRepository bookingItemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Test
    void testSaveAndFind() {
        Passenger passenger = Passenger.builder()
                .fullName("Jane Doe")
                .email("jane@example.com")
                .profile(PassengerProfile.builder().phone("987654321").countryCode("US").build())
                .build();
        passengerRepository.save(passenger);

        Booking booking = Booking.builder()
                .createdAt(OffsetDateTime.now())
                .passenger(passenger)
                .build();
        bookingRepository.save(booking);

        BookingItem item = BookingItem.builder()
                .cabin(Cabin.ECONOMY)
                .price(BigDecimal.valueOf(199.99))
                .segmentOrder(1)
                .booking(booking)
                .build();

        BookingItem saved = bookingItemRepository.save(item);

        assertThat(saved.getId()).isNotNull();
        assertThat(bookingItemRepository.findById(saved.getId())).isPresent();
    }
}
