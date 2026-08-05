package com.musicos.repository;

import com.musicos.domain.LearningContentRelation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningContentRelationRepository extends JpaRepository<LearningContentRelation, String> {
    List<LearningContentRelation> findBySourceTypeAndSourceId(
            LearningContentRelation.ContentType sourceType, String sourceId);
    List<LearningContentRelation> findByTargetTypeAndTargetId(
            LearningContentRelation.ContentType targetType, String targetId);
    List<LearningContentRelation> findBySourceTypeAndTargetType(
            LearningContentRelation.ContentType sourceType, LearningContentRelation.ContentType targetType);
}
