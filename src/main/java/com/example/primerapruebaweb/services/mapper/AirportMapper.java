package com.example.primerapruebaweb.services.mapper;


import com.example.primerapruebaweb.entity.Airport;
import com.example.primerapruebaweb.dto.AirportDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AirportMapper {
    Airport toEntity(AirportDTO.AirportRequest req);
    AirportDTO.AirportResponse toResponse(Airport airport);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Airport airport, AirportDTO.AirportUpdateRequest req);
}
