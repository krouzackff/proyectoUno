package com.example.primerapruebaweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "airports")
public class Airport {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "airport_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String city;

    @OneToMany(mappedBy = "origin")
    @Builder.Default
    private Set<Flight> flightsOrigin = new HashSet<>();

    @OneToMany(mappedBy = "destination")
    @Builder.Default
    private Set<Flight> flightsDestination = new HashSet<>();
}
