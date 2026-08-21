package com.musicos.integration.reaper;

import com.musicos.domain.LocalProfile;

import static com.musicos.api.StudioApiModels.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicos.domain.ReaperIntegrationSettings;
import com.musicos.domain.StudioProject;
import com.musicos.repository.ReaperIntegrationSettingsRepository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalReaperBridge implements ReaperBridge {
    private static final Set<String> TRANSPORT = Set.of("PLAY", "PAUSE", "STOP", "RECORD");
    private static final Set<String> OPERATIONS = Set.of("SET_TEMPO", "SET_METER", "ADD_TRACK", "ADD_MEDIA",
            "ADD_MARKER", "ADD_REGION", "SET_LOOP", "ARM_TRACK", "SET_POSITION", "SAVE_PROJECT");

    private final ReaperIntegrationSettingsRepository settings;
    private final ReaperProjectRenderer renderer;
    private final ObjectMapper json;
    private final HttpClient http;
    private final String agentToken;

    public LocalReaperBridge(ReaperIntegrationSettingsRepository settings, ReaperProjectRenderer renderer,
                             @Value("${music-os.reaper.agent-token:}") String agentToken) {
        this.settings = settings;
        this.renderer = renderer;
        this.json = new ObjectMapper();
        this.agentToken = agentToken;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    }

    @Override
    @Transactional
    public ReaperStatusView status() {
        var value = settings.findById(LocalProfile.DEFAULT_ID).orElse(null);
        if (value == null) return statusView("NOT_CONFIGURED", false, null, null, null,
                null, null, null, null, null,
                "Configure o endereço do Agent e o mapeamento de mídia.");
        if (!value.isEnabled()) return view(value, "AGENT_OFFLINE", null, null, null,
                "Integração desativada no Muse Studio.");
        if (agentToken == null || agentToken.isBlank()) return view(value, "NOT_CONFIGURED", null, null, null,
                "MUSE_REAPER_TOKEN não foi configurado no backend.");
        try {
            var answer = send(value, "GET", "/v1/status", null);
            var current = String.valueOf(answer.getOrDefault("status", "ERROR"));
            var mediaRoot = string(answer, "mediaRoot");
            if (mediaRoot != null && !sameHostPath(mediaRoot, value.getHostMediaRoot())) {
                current = "ERROR";
                value.updateStatus(current, "A raiz de mídia do Agent diverge do mapeamento salvo no Muse.");
            } else {
                value.updateStatus(current, messageFor(current));
            }
            settings.save(value);
            return statusView(current, true, value.getAgentBaseUrl(), value.getContainerMediaRoot(),
                    value.getHostMediaRoot(), string(answer, "agentVersion"), string(answer, "reaperVersion"),
                    string(answer, "projectId"), decimal(answer, "positionSeconds"), integer(answer, "playState"),
                    value.getLastMessage());
        } catch (Exception exception) {
            value.updateStatus("AGENT_OFFLINE", "Muse Reaper Agent não respondeu em localhost.");
            settings.save(value);
            return view(value, "AGENT_OFFLINE", null, null, null, value.getLastMessage());
        }
    }

    @Override
    @Transactional
    public ReaperStatusView configure(ReaperConfigurationRequest request) {
        var agentUrl = validatedAgentUrl(request.agentBaseUrl()).toString();
        var containerRoot = normalizedRoot(request.containerMediaRoot(), "Raiz de mídia do container");
        var hostRoot = normalizedRoot(request.hostMediaRoot(), "Raiz de mídia do Windows");
        var value = settings.findById(LocalProfile.DEFAULT_ID).orElseGet(() ->
                new ReaperIntegrationSettings(agentUrl, containerRoot, hostRoot, true));
        value.configureAgent(agentUrl, containerRoot, hostRoot);
        settings.save(value);
        return status();
    }

    @Override
    public ReaperStatusView testConnection() { return status(); }

    @Override
    @Transactional
    public ReaperStatusView disconnect() {
        var value = requiredSettings();
        value.disable();
        settings.save(value);
        return view(value, "AGENT_OFFLINE", null, null, null, "Integração desativada pelo usuário.");
    }

    @Override
    @Transactional
    public OpenInReaperView open(StudioProject project) {
        var value = requiredEnabledSettings();
        project.ensureExternalIdentities();
        var externalId = project.getExternalProjectId() == null
                ? UUID.randomUUID().toString() : project.getExternalProjectId();
        var rpp = renderer.render(project, mediaPath -> mapMediaPath(value, mediaPath));
        var answer = send(value, "POST", "/v1/projects/open", Map.of(
                "projectId", project.getId().toString(), "externalProjectId", externalId, "rpp", rpp));
        var projectPath = string(answer, "projectPath");
        var current = String.valueOf(answer.getOrDefault("status", "REAPER_OFFLINE"));
        var message = String.valueOf(answer.getOrDefault("message", messageFor(current)));
        value.updateStatus(current, message);
        settings.save(value);
        project.attachExternalProject(externalId, projectPath);
        return new OpenInReaperView(project.getId(), externalId, projectPath, current, message);
    }

    @Override
    public ReaperOperationView transport(ReaperTransportRequest request) {
        var action = request.action().toUpperCase(java.util.Locale.ROOT);
        if (!TRANSPORT.contains(action)) throw new IllegalArgumentException("Ação de transporte inválida");
        var answer = send(requiredEnabledSettings(), "POST", "/v1/transport", Map.of("action", action));
        return new ReaperOperationView(String.valueOf(answer.get("status")), null, action,
                "Comando de transporte aceito pelo Agent.");
    }

    @Override
    public ReaperOperationView command(StudioProject project, ReaperOperationRequest request) {
        var operation = request.operation().toUpperCase(java.util.Locale.ROOT);
        if (!OPERATIONS.contains(operation)) throw new IllegalArgumentException("Operação REAPER inválida");
        var answer = send(requiredEnabledSettings(), "POST", "/v1/projects/" + project.getId() + "/commands",
                Map.of("operation", operation, "payload", request.payload()));
        return new ReaperOperationView(String.valueOf(answer.get("status")), string(answer, "commandId"),
                operation, "Operação tipada enfileirada para o ReaScript.");
    }

    private Map<String, Object> send(ReaperIntegrationSettings value, String method, String endpoint, Object body) {
        try {
            var request = HttpRequest.newBuilder(validatedAgentUrl(value.getAgentBaseUrl()).resolve(endpoint))
                    .timeout(Duration.ofSeconds(5)).header("Authorization", "Bearer " + agentToken)
                    .header("Content-Type", "application/json")
                    .method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                            : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body))).build();
            var response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Agent recusou a operação (HTTP " + response.statusCode() + ")");
            }
            return json.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Comunicação com o Agent interrompida", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Muse Reaper Agent indisponível", exception);
        }
    }

    private String mapMediaPath(ReaperIntegrationSettings value, Path mediaPath) {
        var media = mediaPath.toAbsolutePath().normalize().toString().replace('\\', '/');
        var container = value.getContainerMediaRoot().replace('\\', '/').replaceAll("/+$", "");
        if (!media.equals(container) && !media.startsWith(container + "/")) {
            throw new IllegalArgumentException("Mídia fora da raiz configurada do container");
        }
        var relative = media.substring(container.length()).replaceFirst("^/+", "");
        var host = value.getHostMediaRoot().replace('\\', '/').replaceAll("/+$", "");
        return relative.isBlank() ? host : host + "/" + relative;
    }

    private URI validatedAgentUrl(String raw) {
        try {
            var uri = URI.create(raw.endsWith("/") ? raw : raw + "/");
            var host = uri.getHost();
            if (!"http".equalsIgnoreCase(uri.getScheme()) || host == null
                    || !(host.equalsIgnoreCase("host.docker.internal") || host.equals("127.0.0.1")
                    || host.equalsIgnoreCase("localhost")) || uri.getUserInfo() != null) {
                throw new IllegalArgumentException("Agent deve usar HTTP em localhost/host.docker.internal");
            }
            return uri;
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Endereço do Agent inválido", exception);
        }
    }

    private String normalizedRoot(String raw, String label) {
        var value = raw.trim().replace('\\', '/').replaceAll("/+$", "");
        if (!(value.startsWith("/") || value.matches("^[A-Za-z]:/.*")) || value.contains("/../")) {
            throw new IllegalArgumentException(label + " precisa ser absoluta e normalizada");
        }
        return value;
    }

    private ReaperIntegrationSettings requiredSettings() {
        return settings.findById(LocalProfile.DEFAULT_ID).orElseThrow(() -> new IllegalStateException("REAPER não configurado"));
    }

    private ReaperIntegrationSettings requiredEnabledSettings() {
        var value = requiredSettings();
        if (!value.isEnabled()) throw new IllegalStateException("Integração REAPER desativada");
        if (agentToken == null || agentToken.isBlank()) throw new IllegalStateException("Token do Agent não configurado");
        return value;
    }

    private ReaperStatusView view(ReaperIntegrationSettings value, String status, String agentVersion,
                                  String reaperVersion, String projectId, String message) {
        return statusView(status, true, value.getAgentBaseUrl(), value.getContainerMediaRoot(),
                value.getHostMediaRoot(), agentVersion, reaperVersion, projectId, null, null, message);
    }

    private ReaperStatusView statusView(String status, boolean configured, String agentBaseUrl,
                                        String containerMediaRoot, String hostMediaRoot, String agentVersion,
                                        String reaperVersion, String projectId, Double positionSeconds,
                                        Integer playState, String message) {
        return new ReaperStatusView(status, configured, agentBaseUrl, containerMediaRoot, hostMediaRoot,
                agentVersion, reaperVersion, projectId, positionSeconds, playState, message, Instant.now());
    }

    private String messageFor(String status) {
        return switch (status) {
            case "PROJECT_CONNECTED" -> "Projeto Muse conectado ao ReaScript no REAPER.";
            case "REAPER_AVAILABLE" -> "REAPER respondeu ao handshake; nenhum projeto Muse está ativo.";
            case "REAPER_OFFLINE" -> "Agent online, mas o ReaScript do REAPER não respondeu.";
            default -> "Estado da integração REAPER atualizado.";
        };
    }

    private String string(Map<String, Object> value, String key) {
        var item = value.get(key);
        return item == null ? null : item.toString();
    }

    private Double decimal(Map<String, Object> value, String key) {
        var item = value.get(key);
        return item instanceof Number number ? number.doubleValue() : null;
    }

    private Integer integer(Map<String, Object> value, String key) {
        var item = value.get(key);
        return item instanceof Number number ? number.intValue() : null;
    }

    private boolean sameHostPath(String first, String second) {
        return first.replace('\\', '/').replaceAll("/+$", "").equalsIgnoreCase(
                second.replace('\\', '/').replaceAll("/+$", ""));
    }
}
