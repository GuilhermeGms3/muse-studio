package com.musicos.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProjectVersion {
    private String versionId;
    private String label;
    private String createdOn;

    protected ProjectVersion() {
    }

    public ProjectVersion(String versionId, String label, String createdOn) {
        this.versionId = versionId;
        this.label = label;
        this.createdOn = createdOn;
    }

    public String getVersionId() { return versionId; }
    public String getLabel() { return label; }
    public String getCreatedOn() { return createdOn; }
}
