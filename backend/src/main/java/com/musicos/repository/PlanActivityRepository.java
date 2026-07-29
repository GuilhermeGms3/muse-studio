package com.musicos.repository;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.PlanActivity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanActivityRepository extends JpaRepository<PlanActivity, String> {
    List<PlanActivity> findByScheduledForAndInstrumentOrderByPosition(LocalDate date, InstrumentId instrument);
    void deleteByScheduledForAndInstrument(LocalDate date, InstrumentId instrument);
}
