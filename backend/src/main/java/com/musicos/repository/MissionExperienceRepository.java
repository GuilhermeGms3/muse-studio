package com.musicos.repository;

import com.musicos.domain.MissionExperience;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionExperienceRepository extends JpaRepository<MissionExperience, UUID> {
    Optional<MissionExperience> findByMissionIdAndInstrumentProfileId(String missionId, String profileId);
    List<MissionExperience> findByInstrumentProfileIdOrderByUpdatedAtDesc(String profileId);
}
