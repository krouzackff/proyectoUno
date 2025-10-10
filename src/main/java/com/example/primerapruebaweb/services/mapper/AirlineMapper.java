package com.example.primerapruebaweb.services.mapper;

import com.example.primerapruebaweb.dto.AirlineDTO;
import com.example.primerapruebaweb.entity.Airline;
import org.mapstruct.*;

@Mapper(componentModel = "spring") // permite inyectarlo con @Autowired
public interface AirlineMapper {

    Airline toEntity(AirlineDTO.AirlineRequest req);

    AirlineDTO.AirlineResponse toResponse(Airline airline);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Airline airline, AirlineDTO.AirlineUpdateRequest req);
}
