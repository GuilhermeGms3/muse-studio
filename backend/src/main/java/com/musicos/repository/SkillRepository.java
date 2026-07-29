package com.musicos.repository;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.Skill;
import com.musicos.domain.SkillState;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, String> {
    List<Skill> findDistinctByInstrumentsContainingOrderByDomainAscTechnicalNameAsc(InstrumentId instrument);
    List<Skill> findDistinctByInstrumentsContainingAndStateOrderByDomainAscTechnicalNameAsc(
            InstrumentId instrument, SkillState state);
}
