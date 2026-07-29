package com.musicos.repository;

import com.musicos.domain.Instrument;
import com.musicos.domain.InstrumentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepository extends JpaRepository<Instrument, InstrumentId> {
}
