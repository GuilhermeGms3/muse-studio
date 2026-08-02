package com.musicos.repository;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.Song;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, String> {
    List<Song> findByInstrumentOrderByTitleAsc(InstrumentId instrument);
    List<Song> findByInstrumentOrderByDifficultyAscTitleAsc(InstrumentId instrument);
    Optional<Song> findFirstByInstrumentAndStatusOrderByTitleAsc(InstrumentId instrument, String status);
}
