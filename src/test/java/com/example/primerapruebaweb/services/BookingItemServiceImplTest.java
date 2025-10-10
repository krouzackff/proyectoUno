package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.*;
import com.example.primerapruebaweb.entity.*;
import com.example.primerapruebaweb.repository.BookingItemRepository;
import com.example.primerapruebaweb.repository.BookingRepository;
import com.example.primerapruebaweb.repository.FlightRepository;
import com.example.primerapruebaweb.services.mapper.BookingItemMapper;
import com.example.primerapruebaweb.services.mapper.BookingMapper;
import com.example.primerapruebaweb.utilities.Cabin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class BookingItemServiceImplTest {

    @Mock
    private BookingItemRepository bookingItemRepository;

    @Mock
    private BookingItemMapper bookingItemMapper;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingItemServiceImpl bookingItemService;

    private BookingItemDTO.BookingItemRequest validRequest;
    private BookingItemDTO.BookingItemResponse expectedResponse;
    private BookingItem savedBookingItem;
    private BookingItem existingBookingItem;

    private Booking mockBookingEntity;
    private Flight mockFlightEntity;
    private Passenger mockPassengerEntity;

    private BookingDTO.BookingResponse mockBookingResponse;
    private FlightDTO.FlightResponse mockFlightResponse;

    @BeforeEach
    void setUp() {
        mockPassengerEntity = Passenger.builder()
                .id(1L)
                .fullName("Andres Ramirez")
                .email("andres@gmail.com")
                .profile(PassengerProfile.builder().id(1L).phone("3184862612").countryCode("+57").build())
                .build();

        mockBookingEntity = Booking.builder()
                .id(1L)
                .createdAt(OffsetDateTime.now())
                .passenger(mockPassengerEntity)
                .items(List.of())
                .build();

        mockFlightEntity = Flight.builder()
                .id(1L)
                .number("FL123")
                .departureTime(OffsetDateTime.now())
                .arrivalTime(OffsetDateTime.now())
                .airline(Airline.builder().id(1L).code("AV").name("Avianca").flights(List.of()).build())
                .build();

        //  Crear objetos mock para el response
        mockBookingResponse = new BookingDTO.BookingResponse(1L, OffsetDateTime.now(), /* passenger */ new PassengerDTO.PassengerResponse(1L,
                "Andres Ramirez","andres@gmail.com", new PassengerDTO.PassengerProfileDTO("3184862612", "+57")));

        mockFlightResponse = new FlightDTO.FlightResponse(1L, "FL123", /* dates */ OffsetDateTime.now(), OffsetDateTime.now(), /* airline */ new AirlineDTO.AirlineResponse(1L, "AV", "Avianca"),
                        /* origin */ new AirportDTO.AirportResponse(1L, "SBL", "Simon Bolivar", "Santa Marta"), /* destination */ new AirportDTO.AirportResponse(2L, "DRD", "El Dorado", "Bogota"),
                        /* tags */ Set.of(), /* seatInventories */ List.of());

        validRequest = new BookingItemDTO.BookingItemRequest(Cabin.ECONOMY, BigDecimal.valueOf(1000), 1,1L,1L);
        expectedResponse = new BookingItemDTO.BookingItemResponse(1L, Cabin.ECONOMY, BigDecimal.valueOf(1000), 1, mockBookingResponse, mockFlightResponse);
        savedBookingItem = BookingItem.builder().id(1L).cabin(Cabin.ECONOMY).price(BigDecimal.valueOf(1000)).segmentOrder(1).booking(mockBookingEntity).flight(mockFlightEntity).build();
        existingBookingItem = BookingItem.builder().id(1L).cabin(Cabin.ECONOMY).price(BigDecimal.valueOf(1000)).segmentOrder(1).booking(mockBookingEntity).flight(mockFlightEntity).build();

    }

    @Test
    @DisplayName("se creo el item agendado")
    void testCreate() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBookingEntity));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(mockFlightEntity));




        when(bookingItemRepository.save(any(BookingItem.class))).thenReturn(savedBookingItem);


        when(bookingItemMapper.toResponse(savedBookingItem)).thenReturn(expectedResponse);


        BookingItemDTO.BookingItemResponse result = bookingItemService.create(validRequest);

        // Verificaciones
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.cabin()).isEqualTo(Cabin.ECONOMY);
        assertThat(result.price()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(result.segmentOrder()).isEqualTo(1);
        assertThat(result.booking()).isEqualTo(expectedResponse.booking());
        assertThat(result.flight()).isEqualTo(expectedResponse.flight());


        verify(bookingRepository, times(1)).findById(1L);
        verify(flightRepository, times(1)).findById(1L);
        verify(bookingItemRepository, times(1)).save(any(BookingItem.class));
        verify(bookingItemMapper, times(1)).toResponse(savedBookingItem);

    }


    @Test
    @DisplayName("Se encontro el item agendado satisfactoriamente")
    void testFindById() {
        // CORRECCIÓN: Mockear tanto el repository como el mapper
        when(bookingItemRepository.findById(1L)).thenReturn(Optional.of(existingBookingItem));
        when(bookingItemMapper.toResponse(existingBookingItem)).thenReturn(expectedResponse);

        // When
        BookingItemDTO.BookingItemResponse result = bookingItemService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.cabin()).isEqualTo(Cabin.ECONOMY);
        assertThat(result.price()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(result.segmentOrder()).isEqualTo(1);
        assertThat(result.booking()).isEqualTo(expectedResponse.booking());
        assertThat(result.flight()).isEqualTo(expectedResponse.flight());

        verify(bookingItemRepository, times(1)).findById(1L);
        verify(bookingItemMapper, times(1)).toResponse(existingBookingItem);
    }

    @Test
    @DisplayName("Update existing airline - Success")
    void testUpdate() {
        // Given
        BookingItemDTO.BookingItemUpdateRequest updatedRequest = new BookingItemDTO.BookingItemUpdateRequest(Cabin.ECONOMY, BigDecimal.valueOf(1500), 1,1L,1L);

        BookingItemDTO.BookingItemResponse updatedResponse = new BookingItemDTO.BookingItemResponse(1L,Cabin.ECONOMY, BigDecimal.valueOf(1500), 1,expectedResponse.booking(),expectedResponse.flight());
        new AirlineDTO.AirlineResponse(1L, "VV", "Viva Air Updated");

        when(bookingItemRepository.findById(1L)).thenReturn(Optional.of(existingBookingItem));
        when(bookingItemRepository.save(existingBookingItem)).thenReturn(existingBookingItem);
        when(bookingItemMapper.toResponse(existingBookingItem)).thenReturn(updatedResponse);

        // When
        BookingItemDTO.BookingItemResponse result = bookingItemService.update(1L, updatedRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.price()).isEqualTo(BigDecimal.valueOf(1500)); //verificamos el atributo del cambio
        verify(bookingItemMapper, times(1)).updateEntity(existingBookingItem, updatedRequest);
        verify(bookingItemRepository, times(1)).save(existingBookingItem);
    }

    @Test
    @DisplayName("Find all airlines - Success")
    void testFindAll() {
        // listas (recursos)
        List<BookingItem> bItems = List.of(
                BookingItem.builder().id(1L).cabin(Cabin.ECONOMY).price(BigDecimal.valueOf(1000)).segmentOrder(1).booking(mockBookingEntity).flight(mockFlightEntity).build(),
                BookingItem.builder().id(1L).cabin(Cabin.BUSINESS).price(BigDecimal.valueOf(2000)).segmentOrder(1).booking(mockBookingEntity).flight(mockFlightEntity).build()
        );

        List<BookingItemDTO.BookingItemResponse> expectedResponses = List.of(
                new BookingItemDTO.BookingItemResponse(1L, Cabin.ECONOMY, BigDecimal.valueOf(1000), 1, mockBookingResponse, mockFlightResponse),
                new BookingItemDTO.BookingItemResponse(1L, Cabin.BUSINESS, BigDecimal.valueOf(2000), 1, mockBookingResponse, mockFlightResponse)
        );
        //obligaciones
        when(bookingItemRepository.findAll()).thenReturn(bItems);
        when(bookingItemMapper.toResponse(bItems.get(0))).thenReturn(expectedResponses.get(0));
        when(bookingItemMapper.toResponse(bItems.get(1))).thenReturn(expectedResponses.get(1));

        // When
        List<BookingItemDTO.BookingItemResponse> result = bookingItemService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).cabin()).isEqualTo(Cabin.ECONOMY);
        assertThat(result.get(1).cabin()).isEqualTo(Cabin.BUSINESS);
        verify(bookingItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Delete existing airline - Success")
    void testDelete() {
        // Given
        when(bookingItemRepository.existsById(1L)).thenReturn(true);

        // When
        bookingItemService.delete(1L);

        // Then
        verify(bookingItemRepository, times(1)).existsById(1L);
        verify(bookingItemRepository, times(1)).deleteById(1L);
    }
}
