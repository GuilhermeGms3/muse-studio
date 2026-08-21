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
    private String agentBaseUrl;
    private String containerMediaRoot;
    private String hostMediaRoot;
    private boolean enabled;
    private String lastStatus;
    private String lastMessage;
    private Instant checkedAt;

    protected ReaperIntegrationSettings() {}

    public ReaperIntegrationSettings(String executablePath, String workspacePath) {
        this.id = LocalProfile.DEFAULT_ID;
        configure(executablePath, workspacePath);
    }

    public ReaperIntegrationSettings(String agentBaseUrl, String containerMediaRoot, String hostMediaRoot,
                                     boolean agentConfiguration) {
        this.id = LocalProfile.DEFAULT_ID;
        configureAgent(agentBaseUrl, containerMediaRoot, hostMediaRoot);
    }

    public void configure(String executablePath, String workspacePath) {
        this.executablePath = executablePath;
        this.workspacePath = workspacePath;
        this.enabled = true;
        updateStatus("AVAILABLE", "Configuração local validada.");
    }

    public void configureAgent(String agentBaseUrl, String containerMediaRoot, String hostMediaRoot) {
        this.executablePath = null;
        this.workspacePath = null;
        this.agentBaseUrl = agentBaseUrl;
        this.containerMediaRoot = containerMediaRoot;
        this.hostMediaRoot = hostMediaRoot;
        this.enabled = true;
        updateStatus("AGENT_OFFLINE", "Configuração salva; aguardando handshake do Muse Reaper Agent.");
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
    public String getAgentBaseUrl() { return agentBaseUrl; }
    public String getContainerMediaRoot() { return containerMediaRoot; }
    public String getHostMediaRoot() { return hostMediaRoot; }
    public boolean isEnabled() { return enabled; }
    public String getLastStatus() { return lastStatus; }
    public String getLastMessage() { return lastMessage; }
    public Instant getCheckedAt() { return checkedAt; }
}
