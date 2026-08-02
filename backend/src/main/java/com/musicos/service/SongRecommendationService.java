package com.musicos.service;

import static com.musicos.api.ApiModels.SongRecommendationView;

import tools.jackson.databind.JsonNode;
import com.musicos.repository.UserPreferencesRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SongRecommendationService {
    private final UserPreferencesRepository preferences;
    private final RestClient youtube = RestClient.create("https://www.googleapis.com");
    private final String apiKey;

    public SongRecommendationService(UserPreferencesRepository preferences,
                                     @Value("${music-os.youtube.api-key:}") String apiKey) {
        this.preferences = preferences;
        this.apiKey = apiKey;
    }

    public List<SongRecommendationView> recommend(String skill, String instrument) {
        var profile = preferences.findById("default").orElse(null);
        var tastes = profile == null ? "rock blues" : String.join(" ", profile.getFavoriteGenres()) + " "
                + String.join(" ", profile.getFavoriteArtists());
        var query = (tastes + " " + value(skill) + " " + lessonTerms(instrument)).trim();
        if (apiKey == null || apiKey.isBlank()) return fallback(query, skill, instrument);
        try {
            var response = youtube.get().uri(builder -> builder.path("/youtube/v3/search")
                    .queryParam("part", "snippet")
                    .queryParam("type", "video")
                    .queryParam("videoEmbeddable", "true")
                    .queryParam("safeSearch", "moderate")
                    .queryParam("maxResults", 6)
                    .queryParam("regionCode", "BR")
                    .queryParam("relevanceLanguage", "pt")
                    .queryParam("q", query)
                    .queryParam("key", apiKey)
                    .build()).retrieve().body(JsonNode.class);
            if (response == null) return fallback(query, skill, instrument);
            var result = new ArrayList<SongRecommendationView>();
            for (var item : response.path("items")) {
                var videoId = item.path("id").path("videoId").asText();
                if (videoId.isBlank()) continue;
                var snippet = item.path("snippet");
                result.add(new SongRecommendationView(videoId, snippet.path("title").asText(),
                        snippet.path("channelTitle").asText(),
                        snippet.path("thumbnails").path("medium").path("url").asText(),
                        reason(skill), "https://www.youtube.com/watch?v=" + videoId));
                if (result.size() == 6) break;
            }
            return result.isEmpty() ? fallback(query, skill, instrument) : result;
        } catch (RuntimeException ignored) {
            return fallback(query, skill, instrument);
        }
    }

    private List<SongRecommendationView> fallback(String query, String skill, String instrument) {
        var url = "https://www.youtube.com/results?search_query="
                + URLEncoder.encode(query, StandardCharsets.UTF_8);
        return List.of(
                new SongRecommendationView("", "Buscar músicas alinhadas ao seu gosto", "YouTube", "",
                        reason(skill), url),
                new SongRecommendationView("", "Encontrar backing tracks para praticar", "YouTube", "",
                        "Aplicar a habilidade sem depender da música original",
                        "https://www.youtube.com/results?search_query="
                                + URLEncoder.encode(value(skill) + " " + value(instrument)
                                + " backing track play along", StandardCharsets.UTF_8)));
    }

    private String reason(String skill) {
        return skill == null || skill.isBlank()
                ? "Combina com suas preferências musicais"
                : "Ajuda a aplicar " + skill + " em contexto musical";
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private String lessonTerms(String instrument) {
        return switch (value(instrument).toLowerCase()) {
            case "drums" -> "drum lesson drumless play along";
            case "keys" -> "piano keyboard lesson play along";
            case "acoustic" -> "acoustic guitar lesson play along";
            default -> "guitar lesson play along";
        };
    }
}
