package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.PassengerDTO;
import com.example.primerapruebaweb.entity.Passenger;
import com.example.primerapruebaweb.entity.PassengerProfile;
import com.example.primerapruebaweb.repository.PassengerRepository;
import com.example.primerapruebaweb.services.mapper.PassengerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private PassengerDTO.PassengerRequest validRequest;
    private PassengerDTO.PassengerResponse expectedResponse;

    private Passenger savedPassenger;
    private Passenger existingPassenger;

    private PassengerDTO.PassengerProfileDTO profileDTO;
    private PassengerProfile profileEntity;
    @BeforeEach
    void setUp() {
        profileDTO = new PassengerDTO.PassengerProfileDTO("3184862612", "+57"); //este es mi recurso
        profileEntity = PassengerProfile.builder().id(1L).phone("3184862612").countryCode("+57").build();
        validRequest = new PassengerDTO.PassengerRequest("Andres Ramirez", "andres@gmail.com", profileDTO);
        expectedResponse = new PassengerDTO.PassengerResponse(1L, "Andres Ramirez", "andres@gmail.com", profileDTO);

        savedPassenger = Passenger.builder().id(1L).fullName("Andres Ramirez").email("andres@gmail.com").profile(profileEntity).build();
        existingPassenger = Passenger.builder().id(1L).fullName("Andres Ramirez").email("andres@gmail.com").profile(profileEntity).build();
    }

    @Test
    @DisplayName("se creo el pasajero")
    void testCreate() {
        when(passengerMapper.toEntity(validRequest)).thenReturn(savedPassenger);
        when(passengerMapper.toResponse(savedPassenger)).thenReturn(expectedResponse);
        when(passengerRepository.save(savedPassenger)).thenReturn(savedPassenger);

        PassengerDTO.PassengerResponse result = passengerService.create(validRequest);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.fullname()).isEqualTo("Andres Ramirez");

        verify(passengerMapper, times(1)).toEntity(validRequest);
        verify(passengerMapper, times(1)).toResponse(savedPassenger);
        verify(passengerRepository, times(1)).save(savedPassenger);
    }

    @Test
    @DisplayName("se encontro el pasajero satisfactoriamente")
    void testFindById() {
        //mockear las DEPENDENCIAS, no el servicio bajo test
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(existingPassenger));
        when(passengerMapper.toResponse(existingPassenger)).thenReturn(expectedResponse);


        PassengerDTO.PassengerResponse result = passengerService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.fullname()).isEqualTo("Andres Ramirez");
        assertThat(result.email()).isEqualTo("andres@gmail.com");

        // Verify las DEPENDENCIAS
        verify(passengerRepository, times(1)).findById(1L);
        verify(passengerMapper, times(1)).toResponse(existingPassenger);
    }

    @Test
    @DisplayName("se encontraron todos")
    void TestFindAll(){
        // Recursos
        List<Passenger> passengers = List.of(
                Passenger.builder().id(1L).fullName("Andres Ramirez").email("andres@gmail.com").profile(profileEntity).build(),
                Passenger.builder().id(2L).fullName("Sebastian Ramirez").email("sebastian@gmail.com").profile(profileEntity).build()
        );

        List<PassengerDTO.PassengerResponse> expectedResponses = List.of(
                new PassengerDTO.PassengerResponse(1L, "Andres Ramirez", "andres@gmail.com", profileDTO),
                new PassengerDTO.PassengerResponse(2L, "Sebastian Ramirez", "sebastian@gmail.com", profileDTO)
        );

        //Mockear dependencias
        when(passengerRepository.findAll()).thenReturn(passengers);
        when(passengerMapper.toResponse(passengers.get(0))).thenReturn(expectedResponses.get(0));
        when(passengerMapper.toResponse(passengers.get(1))).thenReturn(expectedResponses.get(1));


        List<PassengerDTO.PassengerResponse> result = passengerService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).fullname()).isEqualTo("Andres Ramirez");
        assertThat(result.get(1).fullname()).isEqualTo("Sebastian Ramirez");

        // Verify dependencias
        verify(passengerRepository, times(1)).findAll();
        verify(passengerMapper, times(2)).toResponse(any(Passenger.class));
    }

    @Test
    @DisplayName("Actualizacion satisfactoria en pasajeros")
    void testUpdate(){
        // Given
        PassengerDTO.PassengerUpdateRequest updateRequest =
                new PassengerDTO.PassengerUpdateRequest("Andres Updated", "andres.updated@gmail.com", profileDTO);

        PassengerDTO.PassengerResponse updatedResponse =
                new PassengerDTO.PassengerResponse(1L, "Andres Updated", "andres.updated@gmail.com", profileDTO);

        when(passengerRepository.findById(1L)).thenReturn(Optional.of(existingPassenger));
        when(passengerRepository.save(existingPassenger)).thenReturn(existingPassenger);
        when(passengerMapper.toResponse(existingPassenger)).thenReturn(updatedResponse);

        // When
        PassengerDTO.PassengerResponse result = passengerService.update(1L, updateRequest);

        // Then
        assertThat(result.fullname()).isEqualTo("Andres Updated");
        assertThat(result.email()).isEqualTo("andres.updated@gmail.com");

        verify(passengerRepository, times(1)).findById(1L);
        verify(passengerMapper, times(1)).updateEntity(existingPassenger, updateRequest);
        verify(passengerRepository, times(1)).save(existingPassenger);
    }

    @Test
    @DisplayName("Delete existing airline - Success")
    void testDelete() {
        // Given
        when(passengerRepository.existsById(1L)).thenReturn(true);

        // When
        passengerService.delete(1L);

        // Then
        verify(passengerRepository, times(1)).existsById(1L);
        verify(passengerRepository, times(1)).deleteById(1L);
    }
}
