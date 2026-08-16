package com.musicos.repository;

import com.musicos.domain.StudioProject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudioProjectRepository extends JpaRepository<StudioProject, UUID> {
    List<StudioProject> findByOwnerIdOrderByUpdatedAtDesc(String ownerId);
    Optional<StudioProject> findFirstByOwnerIdAndSourceKindAndSourceIdOrderByUpdatedAtDesc(
            String ownerId, StudioProject.SourceKind sourceKind, String sourceId);
}
