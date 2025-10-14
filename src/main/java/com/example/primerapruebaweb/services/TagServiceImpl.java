package com.example.primerapruebaweb.services;

import com.example.primerapruebaweb.dto.TagDTO;
import com.example.primerapruebaweb.entity.Tag;
import com.example.primerapruebaweb.exception.BusinessException;
import com.example.primerapruebaweb.exception.ResourceNotFoundException;
import com.example.primerapruebaweb.repository.TagRepository;
import com.example.primerapruebaweb.services.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagDTO.TagResponse create(TagDTO.TagRequest request) {
        try {
            log.info("Creando nuevo tag: {}", request.name());

            // Validación de negocio - verificar si ya existe un tag con el mismo nombre
            if (tagRepository.existsByName(request.name())) {
                throw new BusinessException("Ya existe un tag con el nombre: " + request.name());
            }

            Tag tag = tagMapper.toEntity(request);
            Tag savedTag = tagRepository.save(tag);
            log.info("Tag creado exitosamente con ID: {}", savedTag.getId());

            return tagMapper.toResponse(savedTag);

        } catch (BusinessException ex) {
            log.warn("Error de negocio al crear tag: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al crear tag", ex);
            throw new BusinessException("Error al crear el tag", ex);
        }
    }

    @Override
    public TagDTO.TagResponse update(Long id, TagDTO.TagUpdateRequest request) {
        try {
            log.info("Actualizando tag con ID: {}", id);

            Tag tag = tagRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Tag", id));

            // Validación de negocio para nombre único en actualización
            if (request.name() != null &&
                    !request.name().equals(tag.getName()) &&
                    tagRepository.existsByName(request.name())) {
                throw new BusinessException("Ya existe otro tag con el nombre: " + request.name());
            }

            tagMapper.updateEntity(tag, request);
            Tag updatedTag = tagRepository.save(tag);
            log.info("Tag actualizado exitosamente: {}", updatedTag.getId());

            return tagMapper.toResponse(updatedTag);

        } catch (ResourceNotFoundException ex) {
            log.warn("Tag no encontrado para actualizar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al actualizar tag: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al actualizar tag ID: {}", id, ex);
            throw new BusinessException("Error al actualizar el tag", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TagDTO.TagResponse findById(Long id) {
        try {
            log.debug("Buscando tag con ID: {}", id);

            return tagRepository.findById(id)
                    .map(tag -> {
                        log.debug("Tag encontrado: {}", tag.getName());
                        return tagMapper.toResponse(tag);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Tag", id));

        } catch (ResourceNotFoundException ex) {
            log.warn("Tag no encontrado: {}", id);
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al buscar tag ID: {}", id, ex);
            throw new BusinessException("Error al buscar el tag", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO.TagResponse> findAll() {
        try {
            log.debug("Obteniendo todos los tags");
            List<TagDTO.TagResponse> tags = tagRepository.findAll()
                    .stream()
                    .map(tagMapper::toResponse)
                    .toList();

            log.debug("Se encontraron {} tags", tags.size());
            return tags;

        } catch (Exception ex) {
            log.error("Error inesperado al obtener todos los tags", ex);
            throw new BusinessException("Error al obtener los tags", ex);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            log.info("Eliminando tag con ID: {}", id);

            Tag tag = tagRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Tag", id));

            // Validaciones de negocio antes de eliminar
            if (isTagInUse(tag)) {
                throw new BusinessException("No se puede eliminar el tag porque está siendo utilizado");
            }

            tagRepository.delete(tag);
            log.info("Tag eliminado exitosamente: {}", id);

        } catch (ResourceNotFoundException ex) {
            log.warn("Tag no encontrado para eliminar: {}", id);
            throw ex;
        } catch (BusinessException ex) {
            log.warn("Error de negocio al eliminar tag: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al eliminar tag ID: {}", id, ex);
            throw new BusinessException("Error al eliminar el tag", ex);
        }
    }

    // Método auxiliar para validaciones de negocio
    private boolean isTagInUse(Tag tag) {
        // Implementa la lógica para verificar si el tag está siendo usado
        // Por ejemplo, si tienes una relación con Posts o Articles:
        // return postRepository.existsByTagsId(tag.getId());
        return false; // Cambiar según tu lógica de negocio
    }
}