package com.musicos.domain;

/**
 * Stable identity for the current single-user local profile.
 *
 * <p>This is deliberately explicit until authentication introduces a real
 * request-scoped owner. Keeping it centralized prevents independent implicit
 * identities from diverging across services and persisted aggregates.</p>
 */
public final class LocalProfile {
    public static final String DEFAULT_ID = "default";

    private LocalProfile() {
    }
}
