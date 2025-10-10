package com.example.primerapruebaweb.services.mapper;

import com.example.primerapruebaweb.dto.SeatInventoryDTO;
import com.example.primerapruebaweb.entity.SeatInventory;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SeatInventoryMapper {

    SeatInventory toEntity(SeatInventoryDTO.SeatInventoryRequest req);
    SeatInventoryDTO.SeatInventoryResponse toResponse(SeatInventory seatInventory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget SeatInventory seatInventory, SeatInventoryDTO.SeatInventoryUpdateRequest req);
}
