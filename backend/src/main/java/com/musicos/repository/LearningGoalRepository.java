package com.musicos.repository;

import com.musicos.domain.LearningGoal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningGoalRepository extends JpaRepository<LearningGoal, String> {
    List<LearningGoal> findByInstrumentProfileIdAndStatus(String instrumentProfileId, LearningGoal.Status status);
}
