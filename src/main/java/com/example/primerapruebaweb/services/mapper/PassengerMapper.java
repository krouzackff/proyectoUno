package com.example.primerapruebaweb.services.mapper;

import com.example.primerapruebaweb.dto.PassengerDTO;
import com.example.primerapruebaweb.entity.Passenger;
import com.example.primerapruebaweb.entity.PassengerProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    Passenger toEntity(PassengerDTO.PassengerRequest req);

    PassengerDTO.PassengerResponse toResponse(Passenger passenger);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Passenger passenger, PassengerDTO.PassengerUpdateRequest req);

    //passenger profile

}
