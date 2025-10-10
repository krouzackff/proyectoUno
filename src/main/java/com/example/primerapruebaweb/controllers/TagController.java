package com.example.primerapruebaweb.controllers;

import com.example.primerapruebaweb.dto.TagDTO;
import com.example.primerapruebaweb.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagDTO.TagResponse> create(@RequestBody TagDTO.TagRequest request) {
        TagDTO.TagResponse response = tagService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO.TagResponse> findById(@PathVariable Long id) {
        TagDTO.TagResponse response = tagService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TagDTO.TagResponse>> findAll() {
        List<TagDTO.TagResponse> responses = tagService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO.TagResponse> update(
            @PathVariable Long id,
            @RequestBody TagDTO.TagUpdateRequest request) {
        TagDTO.TagResponse response = tagService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}