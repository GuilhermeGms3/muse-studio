package com.musicos.repository;

import com.musicos.domain.PracticeRecording;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracticeRecordingRepository extends JpaRepository<PracticeRecording, UUID> {
    List<PracticeRecording> findTop10ByContextTypeAndContextIdOrderByCreatedAtDesc(String contextType,
                                                                                   String contextId);
}
