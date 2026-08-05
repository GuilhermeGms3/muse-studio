package com.musicos.repository;

import com.musicos.domain.Mission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, String> {
    List<Mission> findByCurriculumIdAndStatus(String curriculumId, Mission.Status status);
}
