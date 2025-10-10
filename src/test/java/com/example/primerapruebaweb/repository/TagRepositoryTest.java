package com.example.primerapruebaweb.repository;

import com.example.primerapruebaweb.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    void testSaveAndFind() {
        Tag tag = Tag.builder().name("International").build();
        Tag saved = tagRepository.save(tag);

        assertThat(saved.getId()).isNotNull();
        assertThat(tagRepository.findById(saved.getId())).isPresent();
    }
}
