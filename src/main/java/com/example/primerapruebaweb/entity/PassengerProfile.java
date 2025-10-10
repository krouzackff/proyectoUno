package com.example.primerapruebaweb.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passenger_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "phone")
    private String phone;

    @Column(nullable = false, name = "country_code")
    private String countryCode;
}
