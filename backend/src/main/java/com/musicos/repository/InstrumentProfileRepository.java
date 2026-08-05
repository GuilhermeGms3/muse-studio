package com.musicos.repository;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.InstrumentProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentProfileRepository extends JpaRepository<InstrumentProfile, String> {
    Optional<InstrumentProfile> findByOwnerIdAndInstrument(String ownerId, InstrumentId instrument);
    Optional<InstrumentProfile> findByOwnerIdAndPrimaryProfileTrue(String ownerId);
    List<InstrumentProfile> findByOwnerIdAndActiveTrue(String ownerId);
}
