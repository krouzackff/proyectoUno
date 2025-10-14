package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.TagDTO;

import java.util.List;

public interface TagService {
    TagDTO.TagResponse create(TagDTO.TagRequest request);
    TagDTO.TagResponse update(Long id, TagDTO.TagUpdateRequest request);
    TagDTO.TagResponse findById(Long id);
    List<TagDTO.TagResponse> findAll();
    void delete(Long id);
    // asdasd
}
