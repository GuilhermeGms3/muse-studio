package com.musicos.repository;

import com.musicos.domain.Exercise;
import com.musicos.domain.InstrumentId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    List<Exercise> findByInstrumentOrderByTechniqueAscNameAsc(InstrumentId instrument);
    List<Exercise> findByInstrumentAndTechniqueIgnoreCaseOrderByNameAsc(InstrumentId instrument, String technique);
}
