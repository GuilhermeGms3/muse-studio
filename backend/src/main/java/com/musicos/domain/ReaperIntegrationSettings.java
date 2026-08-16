package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "reaper_integration_settings")
public class ReaperIntegrationSettings {
    @Id
    private String id;
    private String executablePath;
    private String workspacePath;
    private boolean enabled;
    private String lastStatus;
    private String lastMessage;
    private Instant checkedAt;

    protected ReaperIntegrationSettings() {}

    public ReaperIntegrationSettings(String executablePath, String workspacePath) {
        this.id = "default";
        configure(executablePath, workspacePath);
    }

    public void configure(String executablePath, String workspacePath) {
        this.executablePath = executablePath;
        this.workspacePath = workspacePath;
        this.enabled = true;
        updateStatus("AVAILABLE", "Configuração local validada.");
    }

    public void updateStatus(String status, String message) {
        this.lastStatus = status;
        this.lastMessage = message;
        this.checkedAt = Instant.now();
    }

    public void disable() {
        this.enabled = false;
        updateStatus("DISCONNECTED", "Integração desconectada pelo usuário.");
    }

    public void enable() {
        this.enabled = true;
    }

    public String getId() { return id; }
    public String getExecutablePath() { return executablePath; }
    public String getWorkspacePath() { return workspacePath; }
    public boolean isEnabled() { return enabled; }
    public String getLastStatus() { return lastStatus; }
    public String getLastMessage() { return lastMessage; }
    public Instant getCheckedAt() { return checkedAt; }
}
