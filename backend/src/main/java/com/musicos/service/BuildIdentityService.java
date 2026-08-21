package com.musicos.service;

import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
public class BuildIdentityService {
    private final Optional<BuildProperties> build;
    private final String configuredBuildId;
    private final String configuredGitSha;

    public BuildIdentityService(Optional<BuildProperties> build,
                                @Value("${MUSE_BUILD_ID:}") String configuredBuildId,
                                @Value("${MUSE_GIT_SHA:}") String configuredGitSha) {
        this.build = build;
        this.configuredBuildId = configuredBuildId;
        this.configuredGitSha = configuredGitSha;
    }

    public BuildIdentity identity() {
        var version = build.map(BuildProperties::getVersion).orElse("dev");
        var builtAt = build.map(BuildProperties::getTime).orElse(Instant.EPOCH);
        var gitSha = blank(configuredGitSha) ? "unknown" : configuredGitSha.trim();
        var buildId = blank(configuredBuildId)
                ? version + "-" + ("unknown".equals(gitSha) ? builtAt.toString() : gitSha.substring(0, Math.min(12, gitSha.length())))
                : configuredBuildId.trim();
        return new BuildIdentity("muse-studio-api", version, buildId, builtAt, gitSha,
                build.isPresent() ? "build-info" : "runtime-fallback");
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }

    public record BuildIdentity(String application, String version, String buildId, Instant builtAt,
                                String gitSha, String source) {}
}
