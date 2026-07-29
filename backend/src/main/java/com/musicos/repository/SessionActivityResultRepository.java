package com.musicos.repository;

import com.musicos.domain.SessionActivityResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionActivityResultRepository extends JpaRepository<SessionActivityResult, UUID> {
    List<SessionActivityResult> findBySessionIdOrderByCompletedAtAsc(UUID sessionId);
    Optional<SessionActivityResult> findBySessionIdAndActivityId(UUID sessionId, String activityId);
}
