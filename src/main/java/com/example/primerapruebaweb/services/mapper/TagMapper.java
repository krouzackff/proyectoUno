package com.example.primerapruebaweb.services.mapper;

import com.example.primerapruebaweb.dto.TagDTO;
import com.example.primerapruebaweb.entity.Tag;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TagMapper {
    Tag toEntity(TagDTO.TagRequest req);

    TagDTO.TagResponse toResponse(Tag tag);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Tag tag, TagDTO.TagUpdateRequest req);
}
