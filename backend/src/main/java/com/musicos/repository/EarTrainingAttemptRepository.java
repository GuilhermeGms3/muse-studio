package com.musicos.repository;

import com.musicos.domain.EarTrainingAttempt;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EarTrainingAttemptRepository extends JpaRepository<EarTrainingAttempt, UUID> {
    List<EarTrainingAttempt> findTop100ByOrderByPracticedAtDesc();
}
