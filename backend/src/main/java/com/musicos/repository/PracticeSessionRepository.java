package com.musicos.repository;

import com.musicos.domain.PracticeSession;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracticeSessionRepository extends JpaRepository<PracticeSession, UUID> {
}
