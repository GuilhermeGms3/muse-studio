package com.musicos.config;

import com.musicos.domain.Assessment;
import com.musicos.domain.DifficultyDemand;
import com.musicos.domain.Exercise;
import com.musicos.domain.ExerciseVariation;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.LearningStage;
import java.util.List;

/**
 * Definição editorial compacta para manter o catálogo extenso separado por instrumento.
 * Cada registro produz uma Mission, um Exercise observável e um Assessment, preservando
 * os contratos públicos de {@link TeachingContentCatalog}.
 */
record EditorialMissionDefinition(
        String id,
        InstrumentId instrument,
        LearningStage stage,
        String competencyId,
        String title,
        String objective,
        String purpose,
        String conditions,
        List<String> instructions,
        String successCriteria,
        List<ExerciseVariation> variations,
        String activityType,
        int currentBpm,
        int targetBpm,
        int minutes,
        String application,
        Assessment.Type assessmentType,
        List<String> criterionKeys,
        List<String> repertoireIds) {

    EditorialMissionDefinition {
        instructions = List.copyOf(instructions);
        variations = List.copyOf(variations);
        criterionKeys = List.copyOf(criterionKeys);
        repertoireIds = List.copyOf(repertoireIds);
        if (instructions.size() < 3) throw new IllegalArgumentException(id + " precisa de três instruções");
        if (variations.size() < 2) throw new IllegalArgumentException(id + " precisa de duas variações");
    }

    Exercise exercise() {
        var metronomic = currentBpm > 0;
        var current = metronomic ? currentBpm : 60;
        var target = metronomic ? targetBpm : 60;
        var difficulty = Math.min(5, Math.max(1, stage.order() + 1));
        var exercise = new Exercise(exerciseId(), title, title, instrument, target, current, minutes,
                purpose, competencyId, difficulty, metronomic ? Math.max(36, current - 12) : 40, 4,
                Math.min(92, 78 + stage.order() * 2), 3, instructions, variations)
                .withLearningResources(activityType, stage, null, "Material original da Lesson", null,
                        "Use a demonstração, notação ou estímulo auditivo ligado a esta competência.",
                        repertoireIds.isEmpty() ? null : "Adaptação editorial descrita na Mission");
        exercise.configurePedagogicalDefinition(objective, conditions, successCriteria,
                demand(stage), List.of(competencyId));
        return exercise;
    }

    TeachingContentCatalog.Unit unit() {
        var protocol = AssessmentEditorialPolicy.forMission(title, objective, conditions, activityType,
                assessmentType, stage, criterionKeys);
        var structuredApplication = isApplicationActivity(activityType) ? application : null;
        return new TeachingContentCatalog.Unit(id, instrument, stage, title, competencyId,
                lessonId(competencyId), List.of(exerciseId()), minutes + 12, objective, conditions, purpose,
                successCriteria,
                "Tentativa registrada com condições e observações declaradas; sem inferência automática de acerto.",
                structuredApplication, protocol.title(), protocol.purpose(), protocol.instructions(),
                protocol.conditions(), protocol.allowedSupport(), protocol.inconclusiveRule(),
                criterionKeys, assessmentType, repertoireIds, protocol.formal(), protocol.rubricLevels());
    }

    private static boolean isApplicationActivity(String type) {
        return List.of("context", "song", "play_along", "record", "transfer", "create", "review")
                .contains(type);
    }

    private String exerciseId() {
        return id + "-exercise";
    }

    private static String lessonId(String competencyId) {
        return switch (competencyId) {
            case "major-scale" -> "escala-maior";
            case "harmonic-field" -> "campo-harmonico";
            default -> "lesson-" + competencyId;
        };
    }

    private static DifficultyDemand demand(LearningStage stage) {
        var level = Math.min(5, stage.order() + 1);
        return new DifficultyDemand(level, level, level, Math.max(1, level - 1), Math.max(1, level - 1),
                level, Math.max(1, level - 1), Math.max(0, level - 2), level,
                Math.max(1, level - 1), Math.max(1, level - 1), level, Math.max(1, level - 1));
    }
}
