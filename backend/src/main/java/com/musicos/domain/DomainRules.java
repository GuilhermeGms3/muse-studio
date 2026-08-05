package com.musicos.domain;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

final class DomainRules {
    private DomainRules() {
    }

    static String requiredText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " é obrigatório");
        }
        return value.trim();
    }

    static <T> T required(T value, String field) {
        return Objects.requireNonNull(value, field + " é obrigatório");
    }

    static int between(int value, int minimum, int maximum, String field) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(field + " deve estar entre " + minimum + " e " + maximum);
        }
        return value;
    }

    static List<String> distinctIds(Collection<String> values) {
        if (values == null) return List.of();
        var result = new LinkedHashSet<String>();
        values.stream().filter(Objects::nonNull).map(String::trim).filter(value -> !value.isEmpty())
                .forEach(result::add);
        return List.copyOf(result);
    }
}
