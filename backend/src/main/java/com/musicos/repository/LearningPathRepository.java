package com.musicos.repository;

import com.musicos.domain.LearningPath;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningPathRepository extends JpaRepository<LearningPath, String> {
    Optional<LearningPath> findByInstrumentProfileIdAndStatus(String instrumentProfileId, LearningPath.Status status);
    List<LearningPath> findByCurriculumId(String curriculumId);
}
