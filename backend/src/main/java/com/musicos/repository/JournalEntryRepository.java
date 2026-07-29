package com.musicos.repository;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.JournalEntry;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
    List<JournalEntry> findAllByOrderByPracticedAtDesc();
    List<JournalEntry> findByInstrumentOrderByPracticedAtDesc(InstrumentId instrument);
    boolean existsByPracticedAtBetween(Instant start, Instant end);
    boolean existsByPracticedAt(Instant practicedAt);
}
