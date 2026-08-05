package com.musicos.repository;

import com.musicos.domain.Lesson;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, String> {
    Optional<Lesson> findByLegacyLibraryContentId(String legacyLibraryContentId);
}
