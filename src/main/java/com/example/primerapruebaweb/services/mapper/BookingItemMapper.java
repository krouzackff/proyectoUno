//package com.example.primerapruebaweb.services.mapper;
//
//
//
//import com.example.primerapruebaweb.dto.BookingItemDTO;
//import com.example.primerapruebaweb.entity.BookingItem;
//import org.mapstruct.*;
//
//@Mapper(componentModel = "spring")
//public interface BookingItemMapper {
//    BookingItem toEntity(BookingItemDTO.BookingItemRequest req);
//
//    BookingItemDTO.BookingItemResponse toResponse(BookingItem bookingItem);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateEntity(@MappingTarget BookingItem bookingItem, BookingItemDTO.BookingItemUpdateRequest req);
//}
package com.example.primerapruebaweb.services.mapper;

import com.example.primerapruebaweb.dto.BookingItemDTO;
import com.example.primerapruebaweb.entity.BookingItem;
import com.example.primerapruebaweb.entity.Booking;
import com.example.primerapruebaweb.entity.Flight;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", source = "bookingId")
    @Mapping(target = "flight", source = "flightId")
    BookingItem toEntity(BookingItemDTO.BookingItemRequest req);

    @Mapping(target = "booking", source = "booking")
    @Mapping(target = "flight", source = "flight")
    BookingItemDTO.BookingItemResponse toResponse(BookingItem bookingItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", source = "bookingId")
    @Mapping(target = "flight", source = "flightId")
    void updateEntity(@MappingTarget BookingItem bookingItem, BookingItemDTO.BookingItemUpdateRequest req);

    // Métodos default para convertir IDs a entidades
    default Booking mapBookingId(Long bookingId) {
        if (bookingId == null) return null;
        return Booking.builder().id(bookingId).build();
    }

    default Flight mapFlightId(Long flightId) {
        if (flightId == null) return null;
        return Flight.builder().id(flightId).build();
    }
}