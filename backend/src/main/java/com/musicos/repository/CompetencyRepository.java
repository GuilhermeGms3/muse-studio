package com.musicos.repository;

import com.musicos.domain.Competency;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetencyRepository extends JpaRepository<Competency, String> {
    Optional<Competency> findByLegacySkillId(String legacySkillId);
}
