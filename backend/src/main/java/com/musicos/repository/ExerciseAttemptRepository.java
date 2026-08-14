package com.musicos.repository;

import com.musicos.domain.ExerciseAttempt;
import java.util.List;
import java.util.UUID;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseAttemptRepository extends JpaRepository<ExerciseAttempt, UUID> {
    List<ExerciseAttempt> findTop20ByExerciseIdOrderByPracticedAtDesc(String exerciseId);
    boolean existsByExerciseIdAndMissionExperienceId(String exerciseId, UUID missionExperienceId);
    List<ExerciseAttempt> findByMissionExperienceIdInOrderByPracticedAtDesc(
            Collection<UUID> missionExperienceIds);
}
