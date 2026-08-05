package com.musicos.repository;

import com.musicos.domain.Curriculum;
import com.musicos.domain.InstrumentId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurriculumRepository extends JpaRepository<Curriculum, String> {
    List<Curriculum> findByInstrumentAndActiveTrue(InstrumentId instrument);
}
