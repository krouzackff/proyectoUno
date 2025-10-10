package com.example.primerapruebaweb.services.mapper;



import com.example.primerapruebaweb.dto.BookingItemDTO;
import com.example.primerapruebaweb.entity.BookingItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingItemMapper {
    BookingItem toEntity(BookingItemDTO.BookingItemRequest req);

    BookingItemDTO.BookingItemResponse toResponse(BookingItem bookingItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget BookingItem bookingItem, BookingItemDTO.BookingItemUpdateRequest req);
}
