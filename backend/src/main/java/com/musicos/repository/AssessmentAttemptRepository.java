package com.musicos.repository;

import com.musicos.domain.AssessmentAttempt;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, UUID> {
}
