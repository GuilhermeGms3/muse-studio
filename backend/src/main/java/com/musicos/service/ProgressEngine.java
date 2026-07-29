package com.musicos.service;

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
                ? 1.0
                : Math.min(1.0, (double) value(skill.getCurrentBpm()) / skill.getTargetBpm());
        var score = weightedScore(skill, bpmRatio);
        var state = stateFor(skill, score, bpmRatio);
        return new Evaluation(state, score, requirements(skill, state, bpmRatio));
    }

    public int priority(Skill skill) {
        var evaluation = evaluate(skill);
        if (evaluation.state() == SkillState.LOCKED || evaluation.state() == SkillState.MASTERED) return -1;
        var overdue = skill.getLastPracticedAt() == null ? 35
                : (int) Math.min(35, Duration.between(skill.getLastPracticedAt(), Instant.now()).toDays() * 5);
        var reviewDue = skill.getNextReviewAt() != null && !skill.getNextReviewAt().isAfter(Instant.now()) ? 45 : 0;
        var retentionRisk = Math.max(0, 85 - skill.getRetention());
        return 100 - evaluation.progress() + overdue + reviewDue + retentionRisk;
    }

    private boolean prerequisitesReady(Skill skill) {
        return skill.getPrerequisites().stream().allMatch(id -> skills.findById(id)
                .map(required -> {
                    var state = required.getState();
                    return state == SkillState.PRACTICING || state == SkillState.CONSISTENT
                            || state == SkillState.MASTERED || state == SkillState.NATURAL
                            || state == SkillState.EXPERT;
                })
                .orElse(false));
    }

    private int weightedScore(Skill skill, double bpmRatio) {
        var hoursScore = Math.min(100, skill.getHours() * 8);
        var daysScore = Math.min(100, skill.getPracticeDays() * 10);
        var reviewsScore = Math.min(100, skill.getReviewCount() * 12.5);
        var applicationScore = Math.min(100,
                skill.getExerciseCompletions() * 8 + skill.getSongsCompleted() * 20);
        var selfScore = skill.getSelfRating() * 20;
        return (int) Math.round(
                skill.getAccuracy() * .30
                        + hoursScore * .18
                        + daysScore * .14
                        + reviewsScore * .10
                        + bpmRatio * 100 * .12
                        + applicationScore * .11
                        + selfScore * .03
                        + skill.getRetention() * .02);
    }

    private SkillState stateFor(Skill skill, int score, double bpmRatio) {
        if (skill.getHours() == 0 && skill.getPracticeDays() == 0) return SkillState.AVAILABLE;
        if (score < 35) return SkillState.LEARNING;
        if (score < 82) return SkillState.PRACTICING;
        if (skill.getAccuracy() >= 88 && skill.getPracticeDays() >= 7 && skill.getReviewCount() >= 4
                && skill.getExerciseCompletions() >= 5 && bpmRatio >= .85) {
            return SkillState.MASTERED;
        }
        return SkillState.PRACTICING;
    }

    private List<String> requirements(Skill skill, SkillState state, double bpmRatio) {
        if (state == SkillState.MASTERED) return List.of("Continue revisando para conservar o domínio.");
        var result = new ArrayList<String>();
        if (skill.getAccuracy() < 88) result.add("Atingir 88% de precisão.");
        if (skill.getPracticeDays() < 7) result.add("Praticar em " + (7 - skill.getPracticeDays()) + " dias diferentes.");
        if (skill.getReviewCount() < 4) result.add("Concluir " + (4 - skill.getReviewCount()) + " revisões.");
        if (skill.getExerciseCompletions() < 5) {
            result.add("Concluir " + (5 - skill.getExerciseCompletions()) + " exercícios.");
        }
        if (bpmRatio < .85 && skill.getTargetBpm() != null) {
            result.add("Chegar a " + Math.round(skill.getTargetBpm() * .85) + " BPM com controle.");
        }
        if (result.isEmpty()) result.add("Aplique a habilidade em uma música completa.");
        return result;
    }

    private int value(Integer number) {
        return number == null ? 0 : number;
    }

    public record Evaluation(SkillState state, int progress, List<String> nextRequirements) {}
}
