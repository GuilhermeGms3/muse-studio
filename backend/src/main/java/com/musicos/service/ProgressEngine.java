package com.musicos.service;

import com.musicos.domain.LearningStage;
import com.musicos.domain.Skill;
import com.musicos.domain.SkillState;
import com.musicos.repository.SkillRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProgressEngine {
    private final SkillRepository skills;

    public ProgressEngine(SkillRepository skills) {
        this.skills = skills;
    }

    public Evaluation evaluate(Skill skill) {
        if (!prerequisitesReady(skill)) {
            return new Evaluation(SkillState.LOCKED, 0,
                    List.of("Domine os pré-requisitos para liberar esta habilidade."));
        }
        var bpmRatio = skill.getTargetBpm() == null || skill.getTargetBpm() == 0
                ? 1.0 : Math.min(1.0, (double) value(skill.getCurrentBpm()) / skill.getTargetBpm());
        var score = weightedScore(skill, bpmRatio);
        var state = stateFor(skill, score, bpmRatio);
        return new Evaluation(state, score, requirements(skill, state, bpmRatio));
    }

    public int priority(Skill skill) {
        var evaluation = evaluate(skill);
        if (evaluation.state() == SkillState.LOCKED || evaluation.state() == SkillState.MASTERED
                || evaluation.state() == SkillState.NATURAL || evaluation.state() == SkillState.EXPERT) return -1;
        var overdue = skill.getLastPracticedAt() == null ? 35
                : (int) Math.min(35, Duration.between(skill.getLastPracticedAt(), Instant.now()).toDays() * 5);
        var reviewDue = skill.getNextReviewAt() != null && !skill.getNextReviewAt().isAfter(Instant.now()) ? 45 : 0;
        return 100 - evaluation.progress() + overdue + reviewDue + Math.max(0, 85 - skill.getRetention());
    }

    private boolean prerequisitesReady(Skill skill) {
        return skill.getPrerequisites().stream().allMatch(id -> skills.findById(id)
                .map(required -> {
                    var state = required.getState();
                    return state == SkillState.PRACTICING || state == SkillState.CONSISTENT
                            || state == SkillState.MASTERED || state == SkillState.NATURAL
                            || state == SkillState.EXPERT;
                }).orElse(false));
    }

    private int weightedScore(Skill skill, double bpmRatio) {
        var hoursScore = Math.min(100, skill.getHours() * 8);
        var daysScore = Math.min(100, skill.getPracticeDays() * 10);
        var reviewsScore = Math.min(100, skill.getReviewCount() * 12.5);
        var applicationScore = Math.min(100, skill.getExerciseCompletions() * 8 + skill.getSongsCompleted() * 20);
        return (int) Math.round(skill.getAccuracy() * .30 + hoursScore * .18 + daysScore * .14
                + reviewsScore * .10 + bpmRatio * 100 * .12 + applicationScore * .11
                + skill.getSelfRating() * 20 * .03 + skill.getRetention() * .02);
    }

    private SkillState stateFor(Skill skill, int score, double bpmRatio) {
        if (skill.getHours() == 0 && skill.getPracticeDays() == 0) return SkillState.AVAILABLE;
        if (score < 35) return SkillState.LEARNING;
        var criteria = criteria(skill.getStage());
        if (meets(skill, bpmRatio, criteria)) {
            if (skill.getStage() == LearningStage.ADVANCED && skill.getRetention() >= 92
                    && skill.getReviewCount() >= criteria.reviews() + 4) return SkillState.EXPERT;
            if (skill.getRetention() >= 88 && skill.getPracticeDays() >= criteria.days() + 5
                    && skill.getReviewCount() >= criteria.reviews() + 2) return SkillState.NATURAL;
            return SkillState.MASTERED;
        }
        return score >= 65 ? SkillState.CONSISTENT : SkillState.PRACTICING;
    }

    private List<String> requirements(Skill skill, SkillState state, double bpmRatio) {
        if (state == SkillState.MASTERED || state == SkillState.NATURAL || state == SkillState.EXPERT) {
            return List.of("Continue revisando para conservar o domínio.");
        }
        var criteria = criteria(skill.getStage());
        var result = new ArrayList<String>();
        if (skill.getAccuracy() < criteria.accuracy()) result.add("Atingir " + criteria.accuracy() + "% de precisão.");
        if (skill.getPracticeDays() < criteria.days()) {
            result.add("Praticar em " + (criteria.days() - skill.getPracticeDays()) + " dias diferentes.");
        }
        if (skill.getReviewCount() < criteria.reviews()) {
            result.add("Concluir " + (criteria.reviews() - skill.getReviewCount()) + " revisões.");
        }
        if (skill.getExerciseCompletions() < criteria.exercises()) {
            result.add("Concluir " + (criteria.exercises() - skill.getExerciseCompletions()) + " atividades.");
        }
        if (skill.getSongsCompleted() < criteria.songs()) {
            result.add("Aplicar em " + (criteria.songs() - skill.getSongsCompleted()) + " música(s) completa(s).");
        }
        if (bpmRatio < criteria.bpmRatio() && skill.getTargetBpm() != null) {
            result.add("Chegar a " + Math.round(skill.getTargetBpm() * criteria.bpmRatio()) + " BPM com controle.");
        }
        if (result.isEmpty()) result.add("Aplique a habilidade em uma música completa.");
        return result;
    }

    private boolean meets(Skill skill, double bpmRatio, Criteria criteria) {
        return skill.getAccuracy() >= criteria.accuracy() && skill.getPracticeDays() >= criteria.days()
                && skill.getReviewCount() >= criteria.reviews()
                && skill.getExerciseCompletions() >= criteria.exercises()
                && skill.getSongsCompleted() >= criteria.songs() && bpmRatio >= criteria.bpmRatio();
    }

    private Criteria criteria(LearningStage stage) {
        return switch (stage) {
            case FIRST_STEPS -> new Criteria(75, 3, 1, 3, 0, .65);
            case BEGINNER -> new Criteria(80, 4, 2, 4, 0, .70);
            case BEGINNER_ADVANCED -> new Criteria(82, 5, 2, 5, 1, .75);
            case EARLY_INTERMEDIATE -> new Criteria(85, 6, 3, 6, 1, .80);
            case INTERMEDIATE -> new Criteria(88, 7, 4, 8, 2, .85);
            case UPPER_INTERMEDIATE -> new Criteria(90, 10, 5, 10, 2, .90);
            case ADVANCED -> new Criteria(92, 14, 6, 12, 3, .95);
        };
    }

    private int value(Integer number) { return number == null ? 0 : number; }

    public record Evaluation(SkillState state, int progress, List<String> nextRequirements) {}
    private record Criteria(int accuracy, int days, int reviews, int exercises, int songs, double bpmRatio) {}
}
