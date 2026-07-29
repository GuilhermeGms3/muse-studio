package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;

@Entity
@Table(name = "plan_activities")
public class PlanActivity {
    @Id
    private String id;
    private LocalDate scheduledFor;
    private int position;
    private int minutes;
    private String title;
    private String kind;

    @Enumerated(EnumType.STRING)
    private InstrumentId instrument;

    private String target;
    private String skillId;
    private boolean done;

    @Version
    private long version;

    protected PlanActivity() {
    }

    public PlanActivity(String id, LocalDate scheduledFor, int position, int minutes, String title,
                        String kind, InstrumentId instrument, String target, boolean done) {
        this(id, scheduledFor, position, minutes, title, kind, instrument, target, done, null);
    }

    public PlanActivity(String id, LocalDate scheduledFor, int position, int minutes, String title,
                        String kind, InstrumentId instrument, String target, boolean done, String skillId) {
        this.id = id;
        this.scheduledFor = scheduledFor;
        this.position = position;
        this.minutes = minutes;
        this.title = title;
        this.kind = kind;
        this.instrument = instrument;
        this.target = target;
        this.done = done;
        this.skillId = skillId;
    }

    public void setDone(boolean done) { this.done = done; }
    public void attachSkill(String skillId) {
        if (this.skillId == null || this.skillId.isBlank()) this.skillId = skillId;
    }
    public String getId() { return id; }
    public LocalDate getScheduledFor() { return scheduledFor; }
    public int getPosition() { return position; }
    public int getMinutes() { return minutes; }
    public String getTitle() { return title; }
    public String getKind() { return kind; }
    public InstrumentId getInstrument() { return instrument; }
    public String getTarget() { return target; }
    public boolean isDone() { return done; }
    public String getSkillId() { return skillId; }
}
