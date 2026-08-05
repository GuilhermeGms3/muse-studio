package com.musicos.repository;

import com.musicos.domain.Assessment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment, String> {
    List<Assessment> findByTypeAndActiveTrue(Assessment.Type type);
}
