package com.musicos.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class DifficultyDemand {
    private int technicalDemand;
    private int rhythmicDemand;
    private int coordinationDemand;
    private int speedDemand;
    private int enduranceDemand;
    private int cognitiveDemand;
    private int memoryDemand;
    private int readingDemand;
    private int perceptualDemand;
    private int harmonicMelodicDemand;
    private int expressiveDemand;
    private int contextualDemand;
    private int performanceDemand;

    protected DifficultyDemand() {
    }

    public DifficultyDemand(int technicalDemand, int rhythmicDemand, int coordinationDemand, int speedDemand,
                            int enduranceDemand, int cognitiveDemand, int memoryDemand, int readingDemand,
                            int perceptualDemand, int harmonicMelodicDemand, int expressiveDemand,
                            int contextualDemand, int performanceDemand) {
        this.technicalDemand = demand(technicalDemand);
        this.rhythmicDemand = demand(rhythmicDemand);
        this.coordinationDemand = demand(coordinationDemand);
        this.speedDemand = demand(speedDemand);
        this.enduranceDemand = demand(enduranceDemand);
        this.cognitiveDemand = demand(cognitiveDemand);
        this.memoryDemand = demand(memoryDemand);
        this.readingDemand = demand(readingDemand);
        this.perceptualDemand = demand(perceptualDemand);
        this.harmonicMelodicDemand = demand(harmonicMelodicDemand);
        this.expressiveDemand = demand(expressiveDemand);
        this.contextualDemand = demand(contextualDemand);
        this.performanceDemand = demand(performanceDemand);
    }

    public static DifficultyDemand unspecified() {
        return new DifficultyDemand(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    private int demand(int value) {
        return DomainRules.between(value, 0, 5, "demanda");
    }

    public int getTechnicalDemand() { return technicalDemand; }
    public int getRhythmicDemand() { return rhythmicDemand; }
    public int getCoordinationDemand() { return coordinationDemand; }
    public int getSpeedDemand() { return speedDemand; }
    public int getEnduranceDemand() { return enduranceDemand; }
    public int getCognitiveDemand() { return cognitiveDemand; }
    public int getMemoryDemand() { return memoryDemand; }
    public int getReadingDemand() { return readingDemand; }
    public int getPerceptualDemand() { return perceptualDemand; }
    public int getHarmonicMelodicDemand() { return harmonicMelodicDemand; }
    public int getExpressiveDemand() { return expressiveDemand; }
    public int getContextualDemand() { return contextualDemand; }
    public int getPerformanceDemand() { return performanceDemand; }
}
