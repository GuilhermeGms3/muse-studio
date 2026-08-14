package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.MissionExperience;
import com.musicos.repository.ExerciseAttemptRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.MissionExperienceRepository;
import com.musicos.repository.MissionRepository;
import com.musicos.repository.PracticeRecordingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MissionExperienceService {
    private final MissionExperienceRepository experiences;
    private final MissionRepository missions;
    private final InstrumentProfileRepository profiles;
    private final ExerciseAttemptRepository exerciseAttempts;
    private final PracticeRecordingRepository recordings;
    private final AssessmentService assessments;

    public MissionExperienceService(MissionExperienceRepository experiences, MissionRepository missions,
                                    InstrumentProfileRepository profiles,
                                    ExerciseAttemptRepository exerciseAttempts,
                                    PracticeRecordingRepository recordings, AssessmentService assessments) {
        this.experiences = experiences;
        this.missions = missions;
        this.profiles = profiles;
        this.exerciseAttempts = exerciseAttempts;
        this.recordings = recordings;
        this.assessments = assessments;
    }

    @Transactional
    public MissionExperienceView start(String missionId, StartMissionExperienceRequest request) {
        var mission = mission(missionId, request.instrument());
        var profile = profile(request.instrument());
        var experience = experiences.findByMissionIdAndInstrumentProfileId(mission.getId(), profile.getId())
                .orElseGet(() -> new MissionExperience(mission.getId(), profile.getId()));
        experience.resume();
        return view(experiences.save(experience));
    }

    @Transactional
    public MissionExperienceView update(String missionId, UpdateMissionExperienceRequest request) {
        mission(missionId, request.instrument());
        var experience = require(missionId, profile(request.instrument()).getId());
        if (request.recordingId() != null) validateRecording(experience, request.recordingId());
        experience.moveTo(request.activityKind(), request.activityId(), request.recordingId());
        if (request.pause()) experience.pause();
        return view(experiences.save(experience));
    }

    @Transactional
    public MissionExperienceView complete(String missionId, CompleteMissionExperienceRequest request) {
        var mission = mission(missionId, request.instrument());
        var experience = require(missionId, profile(request.instrument()).getId());
        if (experience.getStatus() == MissionExperience.Status.COMPLETED) return view(experience);
        for (var exerciseId : mission.getExerciseIds()) {
            if (!exerciseAttempts.existsByExerciseIdAndMissionExperienceId(exerciseId, experience.getId())) {
                throw new IllegalStateException("Conclua todas as práticas desta missão antes da reflexão final");
            }
        }
        if (request.recordingId() != null) validateRecording(experience, request.recordingId());
        java.util.UUID assessmentAttemptId = null;
        if (!mission.getAssessmentIds().isEmpty()) {
            var attempt = assessments.record(mission.getAssessmentIds().getFirst(), new AssessmentAttemptRequest(
                    request.instrument(), request.observerType(), request.challengeLevel(),
                    request.recordingId() == null ? null : request.recordingId().toString(),
                    experience.getId(), request.note(), request.observations()));
            assessmentAttemptId = attempt.id();
        }
        experience.complete(assessmentAttemptId, request.recordingId());
        return view(experiences.save(experience));
    }

    private com.musicos.domain.Mission mission(String missionId, com.musicos.domain.InstrumentId instrument) {
        var mission = missions.findById(missionId)
                .orElseThrow(() -> new NotFoundException("Missão não encontrada"));
        if (mission.getInstrument() != null && mission.getInstrument() != instrument) {
            throw new IllegalArgumentException("Missão não pertence ao instrumento selecionado");
        }
        return mission;
    }

    private com.musicos.domain.InstrumentProfile profile(com.musicos.domain.InstrumentId instrument) {
        return profiles.findByOwnerIdAndInstrument("default", instrument)
                .orElseThrow(() -> new NotFoundException("Perfil instrumental não encontrado"));
    }

    private MissionExperience require(String missionId, String profileId) {
        return experiences.findByMissionIdAndInstrumentProfileId(missionId, profileId)
                .orElseThrow(() -> new IllegalStateException("Inicie a missão antes de continuar"));
    }

    private void validateRecording(MissionExperience experience, java.util.UUID recordingId) {
        var recording = recordings.findById(recordingId)
                .orElseThrow(() -> new NotFoundException("Gravação não encontrada"));
        if (!"mission-experience".equals(recording.getContextType())
                || !experience.getId().toString().equals(recording.getContextId())) {
            throw new IllegalArgumentException("A gravação não pertence a esta experiência");
        }
    }

    public static MissionExperienceView view(MissionExperience value) {
        return new MissionExperienceView(value.getId(), value.getMissionId(), value.getInstrumentProfileId(),
                value.getStatus().name(), value.getCurrentActivityKind().name(), value.getCurrentActivityId(),
                value.getLastRecordingId(), value.getAssessmentAttemptId(), value.getStartedAt(),
                value.getUpdatedAt(), value.getPausedAt(), value.getCompletedAt());
    }
}
