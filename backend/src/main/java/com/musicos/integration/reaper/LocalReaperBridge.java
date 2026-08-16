package com.musicos.integration.reaper;

import static com.musicos.api.StudioApiModels.*;

import com.musicos.domain.ReaperIntegrationSettings;
import com.musicos.domain.StudioProject;
import com.musicos.repository.ReaperIntegrationSettingsRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalReaperBridge implements ReaperBridge {
    private final ReaperIntegrationSettingsRepository settings;
    private final ReaperProjectRenderer renderer;

    public LocalReaperBridge(ReaperIntegrationSettingsRepository settings, ReaperProjectRenderer renderer) {
        this.settings = settings;
        this.renderer = renderer;
    }

    @Override
    @Transactional(readOnly = true)
    public ReaperStatusView status() {
        return settings.findById("default").map(this::view).orElseGet(() ->
                new ReaperStatusView("NOT_CONFIGURED", false, null, null,
                        "Configure o executável e a pasta de projetos do REAPER.", Instant.now()));
    }

    @Override
    @Transactional
    public ReaperStatusView configure(ReaperConfigurationRequest request) {
        var executable = validatedExecutable(request.executablePath());
        var workspace = validatedWorkspace(request.workspacePath(), true);
        var value = settings.findById("default").orElseGet(() ->
                new ReaperIntegrationSettings(executable.toString(), workspace.toString()));
        value.configure(executable.toString(), workspace.toString());
        return view(settings.save(value));
    }

    @Override
    @Transactional
    public ReaperStatusView testConnection() {
        var value = requiredSettings();
        validatedExecutable(value.getExecutablePath());
        validatedWorkspace(value.getWorkspacePath(), false);
        value.enable();
        value.updateStatus("AVAILABLE", "Executável e pasta local estão acessíveis.");
        return view(settings.save(value));
    }

    @Override
    @Transactional
    public ReaperStatusView disconnect() {
        var value = requiredSettings();
        value.disable();
        return view(settings.save(value));
    }

    @Override
    @Transactional
    public OpenInReaperView open(StudioProject project) {
        var value = requiredSettings();
        if (!value.isEnabled()) throw new IllegalStateException("Integração com REAPER está desconectada");
        var executable = validatedExecutable(value.getExecutablePath());
        var workspace = validatedWorkspace(value.getWorkspacePath(), true);
        var externalId = project.getExternalProjectId() == null
                ? UUID.randomUUID().toString() : project.getExternalProjectId();
        var file = workspace.resolve(fileName(project.getTitle(), externalId)).normalize();
        if (!file.startsWith(workspace)) throw new IllegalArgumentException("Caminho de projeto inválido");
        try {
            Files.writeString(file, renderer.render(project), StandardCharsets.UTF_8);
            new ProcessBuilder(executable.toString(), file.toString()).directory(workspace.toFile()).start();
            value.updateStatus("CONNECTED", "Projeto entregue ao REAPER.");
            settings.save(value);
            project.attachExternalProject(externalId, file.toString());
            return new OpenInReaperView(project.getId(), externalId, file.toString(), "CONNECTED",
                    "Projeto criado e aberto no REAPER.");
        } catch (IOException exception) {
            value.updateStatus("ERROR", "Não foi possível abrir o projeto no REAPER.");
            settings.save(value);
            throw new IllegalStateException("Não foi possível abrir o REAPER", exception);
        }
    }

    private ReaperIntegrationSettings requiredSettings() {
        return settings.findById("default")
                .orElseThrow(() -> new IllegalStateException("REAPER não configurado"));
    }

    private Path validatedExecutable(String raw) {
        var path = Path.of(raw).toAbsolutePath().normalize();
        var name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!name.matches("reaper(?:_x64)?\\.exe") || !Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Selecione um executável REAPER válido");
        }
        return path;
    }

    private Path validatedWorkspace(String raw, boolean create) {
        var path = Path.of(raw).toAbsolutePath().normalize();
        try {
            if (create) Files.createDirectories(path);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Não foi possível preparar a pasta do REAPER");
        }
        if (!Files.isDirectory(path) || !Files.isWritable(path)) {
            throw new IllegalArgumentException("Pasta do REAPER precisa existir e permitir escrita");
        }
        return path;
    }

    private String fileName(String title, String id) {
        var safe = title.replaceAll("[^\\p{L}\\p{N}._-]+", "-").replaceAll("-+", "-");
        if (safe.isBlank()) safe = "muse-studio";
        return safe + "-" + id.substring(0, 8) + ".rpp";
    }

    private ReaperStatusView view(ReaperIntegrationSettings value) {
        return new ReaperStatusView(value.isEnabled() ? value.getLastStatus() : "DISCONNECTED",
                true, value.getExecutablePath(), value.getWorkspacePath(),
                value.getLastMessage(), value.getCheckedAt());
    }
}
