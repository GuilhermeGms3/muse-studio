package com.musicos.repository;

import com.musicos.domain.Evidence;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvidenceRepository extends JpaRepository<Evidence, String> {
    List<Evidence> findByInstrumentProfileIdAndCompetencyIdOrderByOccurredAtDesc(
            String instrumentProfileId, String competencyId);
    List<Evidence> findByInstrumentProfileIdOrderByOccurredAtDesc(String instrumentProfileId);
}
