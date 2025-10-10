package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.AirlineDTO;
import com.example.primerapruebaweb.dto.AirportDTO;
import com.example.primerapruebaweb.entity.Airline;
import com.example.primerapruebaweb.entity.Airport;
import com.example.primerapruebaweb.repository.AirportRepository;
import com.example.primerapruebaweb.services.mapper.AirportMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportServiceImplTest {

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private AirportMapper airportMapper;

    @InjectMocks
    private AirportServiceImpl airportService;

    private AirportDTO.AirportRequest validRequest;
    private AirportDTO.AirportResponse expectedResponse;
    private Airport savedAirport;
    private Airport existingAirport;


    @BeforeEach
    void setUp() {
        validRequest = new AirportDTO.AirportRequest("SBL", "Simon Bolivar", "Santa Marta");
        expectedResponse = new AirportDTO.AirportResponse(1L, "SBL", "Simon Bolivar", "Santa Marta");
        savedAirport = Airport.builder().id(1L).code("SBL").name("Simon Bolivar").city("Santa Marta").build();
        existingAirport = Airport.builder().id(1L).code("SBL").name("Simon Bolivar").city("Santa Marta").build();
    }

    @Test
    @DisplayName("aeropuerto creado con exito")
    void testCreate() {
        //simulaciones de mockito obligando a funcionar
        when(airportMapper.toEntity(validRequest)).thenReturn(savedAirport);
        when(airportMapper.toResponse(savedAirport)).thenReturn(expectedResponse);
        when(airportRepository.save(savedAirport)).thenReturn(savedAirport);

        AirportDTO.AirportResponse result = airportService.create(validRequest);

        //luego verificamos que sus atributos no hayan sido modificados
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.code()).isEqualTo("SBL");
        assertThat(result.name()).isEqualTo("Simon Bolivar");
        assertThat(result.city()).isEqualTo("Santa Marta");

        //ESTO ES PARA VERIFICAR CUANTAS VECES LO HACE LOS METODOS SON DE VERIFY Y USAN LOS METODOS DE LOS MAPPERS
        verify(airportMapper, times(1)).toEntity(validRequest);
        verify(airportMapper, times(1)).toResponse(savedAirport);
        verify(airportRepository, times(1)).save(savedAirport);

    }

    @Test
    @DisplayName("Se encontro el aeropuerto")
    void testFindById() {
        when(airportRepository.findById(1L)).thenReturn(Optional.of(existingAirport));

        AirportDTO.AirportResponse result = airportService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.code()).isEqualTo("SBL");
        assertThat(result.name()).isEqualTo("Simon Bolivar");
        assertThat(result.city()).isEqualTo("Santa Marta");

        verify(airportRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Se actualizo correctamente")
    void testUpdate(){

        AirportDTO.AirportUpdateRequest updateRequest =
                new AirportDTO.AirportUpdateRequest("SBLUP", "Simon Bolivares", "Santa Marta");
        AirportDTO.AirportResponse updatedResponse =
                new AirportDTO.AirportResponse(1L, "SBLUP", "Simon Bolivares", "Santa Marta");

        when(airportRepository.findById(1L)).thenReturn(Optional.of(existingAirport));
        when(airportRepository.save(existingAirport)).thenReturn(existingAirport);
        when(airportMapper.toResponse(existingAirport)).thenReturn(updatedResponse);

        // When
        AirportDTO.AirportResponse result = airportService.update(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Simon Bolivares Updated");
        verify(airportMapper, times(1)).updateEntity(existingAirport, updateRequest);
        verify(airportRepository, times(1)).save(existingAirport);
    }

    @Test
    @DisplayName("Find all airport - satisfactorio")
    void testFindAll() {
        // Given
        List<Airport> airports = List.of(
                Airport.builder().id(1L).code("SBL").name("Simon Bolivar").city("Santa Marta").build(),
                Airport.builder().id(2L).code("DRD").name("El Dorado").city("Bogota").build()
        );

        List<AirportDTO.AirportResponse> expectedResponses = List.of(
                new AirportDTO.AirportResponse(1L, "SBL", "Simon Bolivar", "Santa Marta"),
                new AirportDTO.AirportResponse(2L, "DRD", "El Dorado", "Bogota")
        );

        when(airportRepository.findAll()).thenReturn(airports);
        when(airportMapper.toResponse(airports.get(0))).thenReturn(expectedResponses.get(0));
        when(airportMapper.toResponse(airports.get(1))).thenReturn(expectedResponses.get(1));

        // When
        List<AirportDTO.AirportResponse> result = airportService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).code()).isEqualTo("SBL");
        assertThat(result.get(1).code()).isEqualTo("DRD");
        verify(airportRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Delete existing airline - Success")
    void testDelete() {
        // Given
        when(airportRepository.existsById(1L)).thenReturn(true);

        // When
        airportService.delete(1L);

        // Then
        verify(airportRepository, times(1)).existsById(1L);
        verify(airportRepository, times(1)).deleteById(1L);
    }
}
