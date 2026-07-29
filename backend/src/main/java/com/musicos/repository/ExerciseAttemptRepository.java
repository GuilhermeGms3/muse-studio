package com.musicos.repository;

import com.musicos.domain.ExerciseAttempt;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseAttemptRepository extends JpaRepository<ExerciseAttempt, UUID> {
    List<ExerciseAttempt> findTop20ByExerciseIdOrderByPracticedAtDesc(String exerciseId);
}
