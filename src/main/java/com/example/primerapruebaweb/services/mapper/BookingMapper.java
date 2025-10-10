package com.example.primerapruebaweb.services.mapper;

import com.example.primerapruebaweb.dto.BookingDTO;
import com.example.primerapruebaweb.entity.Booking;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toEntity(BookingDTO.BookingRequest req);

    BookingDTO.BookingResponse toResponse(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Booking booking, BookingDTO.BookingUpdateRequest req);

}
