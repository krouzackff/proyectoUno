package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.AirlineDTO;
import com.example.primerapruebaweb.entity.Airline;
import com.example.primerapruebaweb.repository.AirlineRepository;
import com.example.primerapruebaweb.services.mapper.AirlineMapper;
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
class AirlineServiceImplTest {

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private AirlineMapper airlineMapper;

    @InjectMocks
    private AirlineServiceImpl airlineService;

    private AirlineDTO.AirlineRequest validRequest;
    private AirlineDTO.AirlineResponse expectedResponse;
    private Airline savedAirline;
    private Airline existingAirline;

    @BeforeEach
    void setUp() {
        validRequest = new AirlineDTO.AirlineRequest("AV", "Avianca");
        expectedResponse = new AirlineDTO.AirlineResponse(1L, "AV", "Avianca");
        savedAirline = Airline.builder().id(1L).code("AV").name("Avianca").build();
        existingAirline = Airline.builder().id(1L).code("AV").name("Avianca").build();
    }

    @Test
    @DisplayName("Aerolinea creada con exito")
    void testCreate() {
        //cuando se haga un requerimiento peticion o solicitud, te obligo a que hagas algo con el thenreturn
        when(airlineMapper.toEntity(validRequest)).thenReturn(savedAirline);
        when(airlineRepository.save(savedAirline)).thenReturn(savedAirline);
        when(airlineMapper.toResponse(savedAirline)).thenReturn(expectedResponse);

        //hacemos una solicitud y retornamos su valor
        AirlineDTO.AirlineResponse result = airlineService.create(validRequest);

        //luego verificamos que sus atributos no hayan sido modificados
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.code()).isEqualTo("AV");
        assertThat(result.name()).isEqualTo("Avianca");

        //ESTO ES PARA VERIFICAR CUANTAS VECES LO HACE LOS METODOS SON DE VERIFY Y USAN LOS METODOS DE LOS MAPPERS
        verify(airlineMapper, times(1)).toEntity(validRequest);
        verify(airlineMapper, times(1)).toResponse(savedAirline);
        verify(airlineRepository, times(1)).save(savedAirline);
    }

    @Test
    @DisplayName("Se encontro la aerolinea satisfactoriamente")
    void testFindById() {
        // Given
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(existingAirline));
        when(airlineMapper.toResponse(existingAirline)).thenReturn(expectedResponse);

        // When
        AirlineDTO.AirlineResponse result = airlineService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Avianca");
        assertThat(result.code()).isEqualTo("AV");

        verify(airlineRepository, times(1)).findById(1L);
        verify(airlineMapper, times(1)).toResponse(existingAirline);
    }

    @Test
    @DisplayName("Update existing airline - Success")
    void testUpdate() {
        // Given
        AirlineDTO.AirlineUpdateRequest updateRequest =
                new AirlineDTO.AirlineUpdateRequest("VV", "Viva Air Updated");
        AirlineDTO.AirlineResponse updatedResponse =
                new AirlineDTO.AirlineResponse(1L, "VV", "Viva Air Updated");

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(existingAirline));
        when(airlineRepository.save(existingAirline)).thenReturn(existingAirline);
        when(airlineMapper.toResponse(existingAirline)).thenReturn(updatedResponse);

        // When
        AirlineDTO.AirlineResponse result = airlineService.update(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Viva Air Updated");
        verify(airlineMapper, times(1)).updateEntity(existingAirline, updateRequest);
        verify(airlineRepository, times(1)).save(existingAirline);
    }

    @Test
    @DisplayName("Find all airlines - Success")
    void testFindAll() {
        // Given
        List<Airline> airlines = List.of(
                Airline.builder().id(1L).code("AV").name("Avianca").build(),
                Airline.builder().id(2L).code("VV").name("Viva Air").build()
        );

        List<AirlineDTO.AirlineResponse> expectedResponses = List.of(
                new AirlineDTO.AirlineResponse(1L, "AV", "Avianca"),
                new AirlineDTO.AirlineResponse(2L, "VV", "Viva Air")
        );

        when(airlineRepository.findAll()).thenReturn(airlines);
        when(airlineMapper.toResponse(airlines.get(0))).thenReturn(expectedResponses.get(0));
        when(airlineMapper.toResponse(airlines.get(1))).thenReturn(expectedResponses.get(1));

        // When
        List<AirlineDTO.AirlineResponse> result = airlineService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).code()).isEqualTo("AV");
        assertThat(result.get(1).code()).isEqualTo("VV");
        verify(airlineRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Delete existing airline - Success")
    void testDelete() {
        // Given
        when(airlineRepository.existsById(1L)).thenReturn(true);

        // When
        airlineService.delete(1L);

        // Then
        verify(airlineRepository, times(1)).existsById(1L);
        verify(airlineRepository, times(1)).deleteById(1L);
    }
}
