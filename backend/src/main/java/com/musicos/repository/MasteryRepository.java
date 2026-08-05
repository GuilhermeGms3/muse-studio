package com.musicos.repository;

import com.musicos.domain.Mastery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasteryRepository extends JpaRepository<Mastery, String> {
    Optional<Mastery> findByInstrumentProfileIdAndCompetencyId(String instrumentProfileId, String competencyId);
    List<Mastery> findByInstrumentProfileId(String instrumentProfileId);
}
