package com.musicos.service;

import com.musicos.domain.LocalProfile;

import static com.musicos.api.ApiModels.LearningHistoryItemView;

import com.musicos.domain.InstrumentId;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.ExerciseAttemptRepository;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.JournalEntryRepository;
import com.musicos.repository.MissionExperienceRepository;
import com.musicos.repository.MissionRepository;
import com.musicos.repository.PracticeRecordingRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LearningHistoryService {
    private final InstrumentProfileRepository profiles;
    private final MissionExperienceRepository experiences;
    private final MissionRepository missions;
    private final ExerciseAttemptRepository attempts;
    private final ExerciseRepository exercises;
    private final PracticeRecordingRepository recordings;
    private final EvidenceRepository evidence;
    private final JournalEntryRepository journal;

    public LearningHistoryService(InstrumentProfileRepository profiles,
                                  MissionExperienceRepository experiences,
                                  MissionRepository missions,
                                  ExerciseAttemptRepository attempts,
                                  ExerciseRepository exercises,
                                  PracticeRecordingRepository recordings,
                                  EvidenceRepository evidence,
                                  JournalEntryRepository journal) {
        this.profiles = profiles;
        this.experiences = experiences;
        this.missions = missions;
        this.attempts = attempts;
        this.exercises = exercises;
        this.recordings = recordings;
        this.evidence = evidence;
        this.journal = journal;
    }

    public java.util.List<LearningHistoryItemView> history(InstrumentId instrument) {
        var profile = profiles.findByOwnerIdAndInstrument(LocalProfile.DEFAULT_ID, instrument)
                .orElseThrow(() -> new NotFoundException("Perfil instrumental não encontrado"));
        var profileExperiences = experiences.findByInstrumentProfileIdOrderByUpdatedAtDesc(profile.getId());
        var experienceIds = profileExperiences.stream().map(item -> item.getId().toString()).collect(Collectors.toSet());
        var experienceMissionIds = profileExperiences.stream().collect(Collectors.toMap(
                item -> item.getId().toString(), item -> item.getMissionId()));
        var missionIds = profileExperiences.stream().map(item -> item.getMissionId()).collect(Collectors.toSet());
        var missionMap = missions.findAllById(missionIds).stream()
                .collect(Collectors.toMap(item -> item.getId(), Function.identity()));
        var exerciseMap = exercises.findAll().stream()
                .collect(Collectors.toMap(item -> item.getId(), Function.identity()));
        var result = new ArrayList<LearningHistoryItemView>();

        profileExperiences.forEach(item -> {
            var mission = missionMap.get(item.getMissionId());
            result.add(new LearningHistoryItemView("experience-" + item.getId(), "MISSION",
                    mission == null ? item.getMissionId() : mission.getTitle(),
                    item.getStatus() == com.musicos.domain.MissionExperience.Status.COMPLETED
                            ? "Missão concluída" : "Ponto de retomada preservado",
                    item.getCompletedAt() == null ? item.getUpdatedAt() : item.getCompletedAt(),
                    item.getMissionId(), item.getId().toString(), item.getStatus().name()));
        });
        attempts.findByMissionExperienceIdInOrderByPracticedAtDesc(
                        profileExperiences.stream().map(item -> item.getId()).toList()).stream()
                .forEach(item -> {
                    var exercise = exerciseMap.get(item.getExerciseId());
                    result.add(new LearningHistoryItemView("attempt-" + item.getId(), "ATTEMPT",
                            exercise == null ? item.getExerciseId() : exercise.getName(),
                            item.isPassed() ? "Prática registrada com critério atendido" : "Prática registrada",
                            item.getPracticedAt(), experienceMissionIds.get(item.getMissionExperienceId().toString()),
                            item.getId().toString(),
                            item.isPassed() ? "PASSED" : "RECORDED"));
                });
        recordings.findByContextTypeAndContextIdInOrderByCreatedAtDesc(
                        "mission-experience", experienceIds).stream()
                .forEach(item -> result.add(new LearningHistoryItemView("recording-" + item.getId(),
                        "RECORDING", "Gravação de aplicação", item.getOriginalName(), item.getCreatedAt(),
                        experienceMissionIds.get(item.getContextId()), item.getId().toString(), "SAVED")));
        evidence.findByInstrumentProfileIdOrderByOccurredAtDesc(profile.getId()).forEach(item ->
                result.add(new LearningHistoryItemView("evidence-" + item.getId(), "EVIDENCE",
                        "Evidência de aprendizagem", item.getObservation(), item.getOccurredAt(),
                        item.getSourceType() == com.musicos.domain.Evidence.SourceType.MISSION
                                && missionIds.contains(item.getSourceId()) ? item.getSourceId() : null,
                        item.getId(), item.getState().name())));
        journal.findByInstrumentOrderByPracticedAtDesc(instrument).forEach(item ->
                result.add(new LearningHistoryItemView("journal-" + item.getId(), "SESSION",
                        "Sessão de prática", String.join(", ", item.getWorked()), item.getPracticedAt(),
                        null, item.getId().toString(), "RECORDED")));

        return result.stream().sorted(Comparator.comparing(
                LearningHistoryItemView::occurredAt, Comparator.reverseOrder())).toList();
    }
}
