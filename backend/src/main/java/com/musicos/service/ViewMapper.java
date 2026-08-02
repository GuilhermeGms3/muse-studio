package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.*;

final class ViewMapper {
    private ViewMapper() {
    }

    static InstrumentView instrument(Instrument value) {
        return new InstrumentView(value.getId(), value.getName(), value.getShortName(), value.getFocus());
    }

    static PlanActivityView activity(PlanActivity value) {
        return new PlanActivityView(value.getId(), value.getScheduledFor(), value.getPosition(), value.getMinutes(),
                value.getTitle(), value.getKind(), value.getInstrument(), value.getTarget(), value.isDone(),
                value.getSkillId());
    }

    static SkillView skill(Skill value) {
        return skill(value, value.getAccuracy(), java.util.List.of());
    }

    static SkillView skill(Skill value, int progress, java.util.List<String> nextRequirements) {
        return new SkillView(value.getId(), value.getFriendlyTitle(), value.getTechnicalName(), value.getDomain(),
                value.getDescription(), value.getState(), value.getHours(), value.getAccuracy(),
                value.getCurrentBpm(), value.getTargetBpm(), value.getInstruments(), value.getPrerequisites(),
                value.getContents(), value.getExercises(), value.getSongs(), value.getNextSkills(),
                value.getPracticeDays(), value.getReviewCount(), value.getExerciseCompletions(),
                value.getSongsCompleted(), value.getSelfRating(), value.getLastPracticedAt(), progress,
                nextRequirements, value.getRetention(), value.getReviewIntervalDays(), value.getNextReviewAt());
    }

    static LibraryContentView library(LibraryContent value) {
        return new LibraryContentView(value.getId(), value.getFriendlyTitle(), value.getTechnicalName(),
                value.getCategory(), value.getSummary(), value.getBody(), value.getExamples(), value.getRelated(),
                value.getSkillId(), value.getLevel(), value.getEstimatedMinutes(), value.getDiagramType(),
                value.getDiagramData(), value.getTablature(), value.getObjectives(), value.getCommonMistakes(),
                value.getSteps().stream().map(step -> new LessonStepView(step.getTitle(), step.getExplanation(),
                        step.getMusicalExample(), step.getNotation(), step.getTablature(),
                        step.getAudioNotes())).toList());
    }

    static SongView song(Song value) {
        return new SongView(value.getId(), value.getTitle(), value.getArtist(), value.getTuning(),
                value.getMusicalKey(), value.getBpm(), value.getInstrument(), value.getDifficulty(),
                value.getStatus(), value.getNotes(), value.getProgress(), value.getTechniques(), value.getScales(),
                value.getSections().stream().map(section -> new SongSectionView(section.getSectionId(),
                        section.getName(), section.getProgress(), section.getBpm(), section.getNote(),
                        section.getSkillIds(), section.getTablature(), section.getStartSeconds(),
                        section.getEndSeconds(), section.getTonePreset())).toList());
    }

    static ExerciseView exercise(Exercise value) {
        return new ExerciseView(value.getId(), value.getName(), value.getTechnique(), value.getInstrument(),
                value.getTargetBpm(), value.getCurrentBpm(), value.getMinutes(), value.getDescription(),
                value.getSkillId(), value.getDifficulty(), value.getMinBpm(), value.getBpmStep(),
                value.getPassAccuracy(), value.getPassRepetitions(), value.getInstructions(),
                value.getVariations().stream().map(variation -> new ExerciseVariationView(
                        variation.getName(), variation.getInstructions(), variation.getBpmOffset(),
                        variation.getDurationMinutes())).toList());
    }

    static ExerciseAttemptView exerciseAttempt(ExerciseAttempt value) {
        return new ExerciseAttemptView(value.getId(), value.getExerciseId(), value.getPracticedAt(),
                value.getBpm(), value.getAccuracy(), value.getDurationSeconds(), value.getRepetitions(),
                value.getPerceivedDifficulty(), value.isPassed());
    }

    static ProjectView project(MusicProject value) {
        return new ProjectView(value.getId(), value.getName(), value.getMusicalKey(), value.getBpm(),
                value.getStatus(), value.getLyrics(), value.getIdeas(), value.getReferences(),
                value.getRiffs().stream().map(riff ->
                        new ProjectRiffView(riff.getRiffId(), riff.getName(), riff.getTab())).toList(),
                value.getVersions().stream().map(version -> new ProjectVersionView(
                        version.getVersionId(), version.getLabel(), version.getCreatedOn())).toList());
    }

    static JournalView journal(JournalEntry value) {
        return new JournalView(value.getId(), value.getPracticedAt(), value.getDurationSeconds(),
                value.getInstrument(), value.getWorked(), value.getDifficulties(), value.getImprovements(),
                value.getNotes());
    }
}
