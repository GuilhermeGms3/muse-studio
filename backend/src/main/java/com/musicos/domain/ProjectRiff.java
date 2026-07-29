package com.musicos.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProjectRiff {
    private String riffId;
    private String name;
    private String tab;

    protected ProjectRiff() {
    }

    public ProjectRiff(String riffId, String name, String tab) {
        this.riffId = riffId;
        this.name = name;
        this.tab = tab;
    }

    public String getRiffId() { return riffId; }
    public String getName() { return name; }
    public String getTab() { return tab; }
}
