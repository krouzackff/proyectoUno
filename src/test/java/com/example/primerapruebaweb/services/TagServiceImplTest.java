package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.TagDTO;
import com.example.primerapruebaweb.entity.Tag;
import com.example.primerapruebaweb.repository.TagRepository;
import com.example.primerapruebaweb.services.mapper.TagMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    private TagDTO.TagRequest validRequest;
    private TagDTO.TagResponse expectedResponse;
    private Tag savedTag;
    private TagDTO.TagUpdateRequest updateRequest;

    @BeforeEach
    void setUp(){
        validRequest = new TagDTO.TagRequest("nombreTag");
        expectedResponse = new TagDTO.TagResponse(1L, "nombreTag");
        updateRequest = new TagDTO.TagUpdateRequest("nuevoNombre");

        savedTag = Tag.builder()
                .id(1L)
                .name("nombreTag")
                .flights(Set.of())
                .build();
    }

    @Test
    @DisplayName("Crear tag exitosamente")
    void create_WithValidRequest_ShouldReturnTagResponse(){
        // Given
        when(tagMapper.toEntity(validRequest)).thenReturn(savedTag);
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);
        when(tagMapper.toResponse(savedTag)).thenReturn(expectedResponse);

        // When
        TagDTO.TagResponse result = tagService.create(validRequest);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse.id(), result.id());
        assertEquals(expectedResponse.name(), result.name());

        verify(tagMapper, times(1)).toEntity(validRequest);
        verify(tagRepository, times(1)).save(any(Tag.class));
        verify(tagMapper, times(1)).toResponse(savedTag);
    }

    @Test
    @DisplayName("Encontrar tag por ID exitosamente")
    void findById_WithValidId_ShouldReturnTagResponse(){
        // Given
        Long tagId = 1L;
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(savedTag));
        when(tagMapper.toResponse(savedTag)).thenReturn(expectedResponse);

        // When
        TagDTO.TagResponse result = tagService.findById(tagId);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse.id(), result.id());
        assertEquals(expectedResponse.name(), result.name());

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagMapper, times(1)).toResponse(savedTag);
    }

    @Test
    @DisplayName("Falla al encontrar tag con ID inexistente")
    void findById_WithInvalidId_ShouldThrowResponseStatusException(){
        // Given
        Long invalidId = 99L;
        when(tagRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tagService.findById(invalidId);
        });

        assertEquals("404 NOT_FOUND \"Tag not found with id 99\"", exception.getMessage());
        verify(tagRepository, times(1)).findById(invalidId);
        verify(tagMapper, never()).toResponse(any(Tag.class));
    }

    @Test
    @DisplayName("Actualizar tag exitosamente")
    void update_WithValidId_ShouldReturnUpdatedTagResponse(){
        // Given
        Long tagId = 1L;
        Tag updatedTag = Tag.builder().id(1L).name("nuevoNombre").flights(Set.of()).build();
        TagDTO.TagResponse updatedResponse = new TagDTO.TagResponse(1L, "nuevoNombre");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(savedTag));
        doNothing().when(tagMapper).updateEntity(savedTag, updateRequest);
        when(tagRepository.save(savedTag)).thenReturn(updatedTag);
        when(tagMapper.toResponse(updatedTag)).thenReturn(updatedResponse);

        // When
        TagDTO.TagResponse result = tagService.update(tagId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updatedResponse.id(), result.id());
        assertEquals(updatedResponse.name(), result.name());

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagMapper, times(1)).updateEntity(savedTag, updateRequest);
        verify(tagRepository, times(1)).save(savedTag);
        verify(tagMapper, times(1)).toResponse(updatedTag);
    }

    @Test
    @DisplayName("Falla al actualizar tag con ID inexistente")
    void update_WithInvalidId_ShouldThrowResponseStatusException(){
        // Given
        Long invalidId = 99L;
        when(tagRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tagService.update(invalidId, updateRequest);
        });

        assertEquals("404 NOT_FOUND \"Tag not found with id 99\"", exception.getMessage());
        verify(tagRepository, times(1)).findById(invalidId);
        verify(tagMapper, never()).updateEntity(any(Tag.class), any(TagDTO.TagUpdateRequest.class));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("Encontrar todos los tags exitosamente")
    void findAll_ShouldReturnListOfTagResponses(){
        // Given
        Tag tag2 = Tag.builder().id(2L).name("otroTag").flights(Set.of()).build();
        TagDTO.TagResponse response2 = new TagDTO.TagResponse(2L, "otroTag");

        when(tagRepository.findAll()).thenReturn(List.of(savedTag, tag2));
        when(tagMapper.toResponse(savedTag)).thenReturn(expectedResponse);
        when(tagMapper.toResponse(tag2)).thenReturn(response2);

        // When
        List<TagDTO.TagResponse> result = tagService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResponse.id(), result.get(0).id());
        assertEquals(response2.id(), result.get(1).id());

        verify(tagRepository, times(1)).findAll();
        verify(tagMapper, times(1)).toResponse(savedTag);
        verify(tagMapper, times(1)).toResponse(tag2);
    }

    @Test
    @DisplayName("Eliminar tag exitosamente")
    void delete_WithValidId_ShouldDeleteTag(){
        // Given
        Long tagId = 1L;
        when(tagRepository.existsById(tagId)).thenReturn(true);

        // When
        tagService.delete(tagId);

        // Then
        verify(tagRepository, times(1)).existsById(tagId);
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    @DisplayName("Falla al eliminar tag con ID inexistente")
    void delete_WithInvalidId_ShouldThrowResponseStatusException(){
        // Given
        Long invalidId = 99L;
        when(tagRepository.existsById(invalidId)).thenReturn(false);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tagService.delete(invalidId);
        });

        assertEquals("404 NOT_FOUND \"No encontre el tag a borrar\"", exception.getMessage());
        verify(tagRepository, times(1)).existsById(invalidId);
        verify(tagRepository, never()).deleteById(invalidId);
    }

    @Test
    @DisplayName("Encontrar todos los tags cuando no hay ninguno")
    void findAll_WhenNoTags_ShouldReturnEmptyList(){
        // Given
        when(tagRepository.findAll()).thenReturn(List.of());

        // When
        List<TagDTO.TagResponse> result = tagService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tagRepository, times(1)).findAll();
        verify(tagMapper, never()).toResponse(any(Tag.class));
    }
}