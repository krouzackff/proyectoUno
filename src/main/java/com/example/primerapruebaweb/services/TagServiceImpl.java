package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.TagDTO;
import com.example.primerapruebaweb.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.primerapruebaweb.repository.TagRepository;
import com.example.primerapruebaweb.services.mapper.TagMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;


    @Override
    @Transactional(readOnly = true)
    public TagDTO.TagResponse create(TagDTO.TagRequest request) {
        Tag tag = tagMapper.toEntity(request);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toResponse(savedTag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDTO.TagResponse update(Long id, TagDTO.TagUpdateRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Tag not found with id " + id));

        tagMapper.updateEntity(tag, request);
        Tag updatedTag = tagRepository.save(tag);
        return tagMapper.toResponse(updatedTag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDTO.TagResponse findById(Long id) {
        return tagRepository.findById(id)
                .map(tagMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Tag not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO.TagResponse> findAll() {
        return tagRepository.findAll()
                .stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!tagRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontre el tag a borrar");
        }
        tagRepository.deleteById(id);
    }
}
