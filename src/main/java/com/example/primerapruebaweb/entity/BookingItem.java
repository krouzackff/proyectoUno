package com.example.primerapruebaweb.entity;

import jakarta.persistence.*;
import lombok.*;
import com.example.primerapruebaweb.utilities.Cabin;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING) // o EnumType.ORDINAL, pero String es más legible
    @Column(nullable = false)
    private Cabin cabin;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "segment_order", nullable = false)
    private Integer segmentOrder;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
}
