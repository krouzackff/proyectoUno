package com.example.primerapruebaweb.services.mapper;

import com.example.primerapruebaweb.dto.FlightDTO;
import com.example.primerapruebaweb.entity.Flight;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface FlightMapper {
    Flight toEntity(FlightDTO.FlightRequest req);
    FlightDTO.FlightResponse toResponse(Flight flight);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Flight flight, FlightDTO.FlightUpdateRequest req);
}
