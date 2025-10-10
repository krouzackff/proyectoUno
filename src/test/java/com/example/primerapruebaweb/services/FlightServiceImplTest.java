package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.AirlineDTO;
import com.example.primerapruebaweb.dto.AirportDTO;
import com.example.primerapruebaweb.dto.FlightDTO;
import com.example.primerapruebaweb.entity.Airline;
import com.example.primerapruebaweb.entity.Airport;
import com.example.primerapruebaweb.entity.Flight;
import com.example.primerapruebaweb.repository.FlightRepository;
import com.example.primerapruebaweb.services.mapper.FlightMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import javax.print.attribute.standard.Destination;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;
    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightServiceImpl flightService;

    private FlightDTO.FlightRequest validRequest;
    private FlightDTO.FlightResponse expectedResponse;
    private Flight savedFlight;
    private AirlineDTO.AirlineResponse airlineDTO;
    private AirportDTO.AirportResponse airportDTOOrigin;
    private AirportDTO.AirportResponse airportDTOArrival;

    @BeforeEach
    void setUp(){
        airlineDTO = new AirlineDTO.AirlineResponse(1L,"Av","Avianca");
        airportDTOOrigin = new AirportDTO.AirportResponse(1L, "SBL", "Simon Bolivar", "Santa Marta");
        airportDTOArrival = new AirportDTO.AirportResponse(1L, "DRD", "El Dorado", "Bogota");

        validRequest = new FlightDTO.FlightRequest("Av1758", OffsetDateTime.now(), OffsetDateTime.now(), 1L,1L,2L, Set.of(), List.of());
        expectedResponse = new FlightDTO.FlightResponse(1L,"1758", OffsetDateTime.now(), OffsetDateTime.now(), airlineDTO,airportDTOOrigin,airportDTOArrival, Set.of(), List.of());

        savedFlight = Flight.builder().id(1L).number("1758")
                .departureTime(OffsetDateTime.now())
                .arrivalTime(OffsetDateTime.now())
                .airline(Airline.builder().id(1L).code("AV").name("Avianca").flights(List.of()).build())
                .origin(Airport.builder().id(1L).code("SBL").name("Simon Bolivar").city("Santa Marta").build())
                .destination(Airport.builder().id(1L).code("DRD").name("El Dorado").city("Bogota").build())
                .tags(Set.of())
                .seatInventories(List.of()).build();
    }

    @Test
    @DisplayName("vuelo creado con exito")
    void testCreate() {
        when(flightMapper.toEntity(validRequest)).thenReturn(savedFlight);
        when(flightMapper.toResponse(savedFlight)).thenReturn(expectedResponse);
        when(flightRepository.save(savedFlight)).thenReturn(savedFlight);

        FlightDTO.FlightResponse result = flightService.create(validRequest);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.number()).isEqualTo("1758");

        verify(flightMapper, times(1)).toResponse(savedFlight);
        verify(flightMapper, times(1)).toEntity(validRequest);
        verify(flightRepository, times(1)).save(savedFlight);
    }

    @Test
    @DisplayName("buscar vuelo por ID exitoso")
    void testFindById() {
        // ✅ CORRECTO - Mockea las dependencias, no el servicio
        when(flightRepository.findById(1L)).thenReturn(Optional.of(savedFlight));
        when(flightMapper.toResponse(savedFlight)).thenReturn(expectedResponse);

        FlightDTO.FlightResponse result = flightService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.number()).isEqualTo("1758");

        verify(flightRepository, times(1)).findById(1L);
        verify(flightMapper, times(1)).toResponse(savedFlight);
    }

    @Test
    @DisplayName("buscar todos los vuelos exitoso")
    void testFindAll() {
        List<FlightDTO.FlightResponse> flightsDTO = List.of(
                new FlightDTO.FlightResponse(1L,"1758", OffsetDateTime.now(), OffsetDateTime.now(), airlineDTO,airportDTOOrigin,airportDTOArrival, Set.of(), List.of()),
                new FlightDTO.FlightResponse(2L,"1268", OffsetDateTime.now(), OffsetDateTime.now(), airlineDTO,airportDTOOrigin,airportDTOArrival, Set.of(), List.of())
        );

        List<Flight> flights_Entity = List.of(
                Flight.builder().id(1L).number("1758")
                        .departureTime(OffsetDateTime.now())
                        .arrivalTime(OffsetDateTime.now())
                        .airline(Airline.builder().id(1L).code("AV").name("Avianca").flights(List.of()).build())
                        .origin(Airport.builder().id(1L).code("SBL").name("Simon Bolivar").city("Santa Marta").build())
                        .destination(Airport.builder().id(1L).code("DRD").name("El Dorado").city("Bogota").build())
                        .tags(Set.of())
                        .seatInventories(List.of()).build(),
                Flight.builder().id(2L).number("1268")  // ✅ Cambié el ID a 2L
                        .departureTime(OffsetDateTime.now())
                        .arrivalTime(OffsetDateTime.now())
                        .airline(Airline.builder().id(2L).code("VV").name("Avianca").flights(List.of()).build())
                        .origin(Airport.builder().id(1L).code("SBL").name("Simon Bolivar").city("Santa Marta").build())
                        .destination(Airport.builder().id(1L).code("DRD").name("El Dorado").city("Bogota").build())
                        .tags(Set.of())
                        .seatInventories(List.of()).build()
        );

        // ✅ CORRECTO - Mockea las dependencias
        when(flightRepository.findAll()).thenReturn(flights_Entity);
        when(flightMapper.toResponse(flights_Entity.get(0))).thenReturn(flightsDTO.get(0));
        when(flightMapper.toResponse(flights_Entity.get(1))).thenReturn(flightsDTO.get(1));

        List<FlightDTO.FlightResponse> result = flightService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).number()).isEqualTo("1758");
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).number()).isEqualTo("1268");

        verify(flightRepository, times(1)).findAll();
        verify(flightMapper, times(2)).toResponse(any(Flight.class));
    }

    @Test
    @DisplayName("eliminar vuelo exitoso")
    void testDelete() {
        when(flightRepository.existsById(1L)).thenReturn(true);

        flightService.delete(1L);

        verify(flightRepository, times(1)).existsById(1L);
        verify(flightRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar vuelo - no encontrado")
    void testDeleteNotFound() {
        when(flightRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> flightService.delete(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No encontramos el vuelo 1");

        verify(flightRepository, times(1)).existsById(1L);
        verify(flightRepository, never()).deleteById(any());
    }
}