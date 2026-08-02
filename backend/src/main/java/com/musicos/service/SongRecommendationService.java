package com.musicos.service;

import static com.musicos.api.ApiModels.SongRecommendationView;
import static com.musicos.api.ApiModels.PracticeInstrumentView;
import static com.musicos.api.ApiModels.PracticeSongView;
import static com.musicos.api.ApiModels.PracticeTabSectionView;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.Song;
import com.musicos.repository.SongRepository;
import com.musicos.repository.UserPreferencesRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class SongRecommendationService {
    private final UserPreferencesRepository preferences;
    private final SongRepository songs;
    private final RestClient youtube = RestClient.create("https://www.googleapis.com");
    private final String apiKey;
    private final Map<String, CachedPracticeSong> practiceCache = new ConcurrentHashMap<>();

    public SongRecommendationService(UserPreferencesRepository preferences,
                                     SongRepository songs,
                                     @Value("${music-os.youtube.api-key:}") String apiKey) {
        this.preferences = preferences;
        this.songs = songs;
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

    public PracticeSongView searchPracticeSong(String rawQuery) {
        var query = value(rawQuery).trim();
        if (query.isBlank()) throw new IllegalArgumentException("Informe uma m\u00fasica para pesquisar");
        var cacheKey = normalize(query);
        var cached = practiceCache.get(cacheKey);
        if (cached != null && cached.valid()) return cached.song();

        var result = apiKey == null || apiKey.isBlank() ? practiceFallback(query) : practiceFromYoutube(query);
        practiceCache.put(cacheKey, new CachedPracticeSong(result, System.currentTimeMillis()));
        return result;
    }

    private PracticeSongView practiceFromYoutube(String query) {
        try {
            var canonical = searchYoutube(query + " official audio", 5, "Refer\u00eancia da m\u00fasica");
            if (canonical.isEmpty()) return practiceFallback(query);
            var identity = canonical.getFirst();
            var title = cleanTitle(identity.title(), query);
            var artist = cleanArtist(identity.title(), query, identity.channel());
            var instruments = List.of(
                    practiceInstrument(query, title, artist, InstrumentId.DRUMS),
                    practiceInstrument(query, title, artist, InstrumentId.GUITAR),
                    practiceInstrument(query, title, artist, InstrumentId.ACOUSTIC),
                    practiceInstrument(query, title, artist, InstrumentId.KEYS));
            return new PracticeSongView(slug(title), title, artist, identity.thumbnailUrl(), instruments);
        } catch (RuntimeException ignored) {
            return practiceFallback(query);
        }
    }

    private PracticeInstrumentView practiceInstrument(String query, String canonicalTitle, String artist,
                                                       InstrumentId instrument) {
        var local = findLocalSong(query, canonicalTitle, instrument);
        var videos = searchYoutube(query + lessonTermsFor(instrument), 8, "Refer\u00eancia e aula").stream()
                .filter(video -> relevant(query, video.title()))
                .limit(5)
                .toList();
        var backingTracks = searchYoutube(query + backingTermsFor(instrument), 8, "Playback para praticar").stream()
                .filter(video -> relevant(query, video.title()))
                .limit(5)
                .toList();
        return practiceInstrument(query, artist, instrument, local, videos, backingTracks,
                local != null || !videos.isEmpty() || !backingTracks.isEmpty());
    }

    private PracticeInstrumentView practiceInstrument(String query, String artist, InstrumentId instrument, Song local,
                                                       List<SongRecommendationView> videos,
                                                       List<SongRecommendationView> backingTracks,
                                                       boolean available) {
        var tabs = local == null ? List.<PracticeTabSectionView>of() : local.getSections().stream()
                .filter(section -> section.getTablature() != null && !section.getTablature().isBlank())
                .map(section -> new PracticeTabSectionView(section.getSectionId(), section.getName(),
                        section.getBpm(), section.getTablature()))
                .toList();
        return new PracticeInstrumentView(instrument, instrumentLabel(instrument), available,
                local == null ? null : local.getId(), local == null ? null : local.getBpm(),
                tablatureUrl(query, artist, instrument), tabs, videos, backingTracks);
    }

    private PracticeSongView practiceFallback(String query) {
        var instruments = List.of(InstrumentId.DRUMS, InstrumentId.GUITAR, InstrumentId.ACOUSTIC, InstrumentId.KEYS)
                .stream()
                .map(instrument -> {
                    var local = findLocalSong(query, query, instrument);
                    var lessonUrl = youtubeSearch(query + lessonTermsFor(instrument));
                    var backingUrl = youtubeSearch(query + backingTermsFor(instrument));
                    var videos = List.of(new SongRecommendationView("", "Buscar aula para " + instrumentLabel(instrument),
                            "YouTube", "", "Refer\u00eancia e aula", lessonUrl));
                    var backingTracks = List.of(new SongRecommendationView("", "Buscar playback para " + instrumentLabel(instrument),
                            "YouTube", "", "Playback para praticar", backingUrl));
                    return practiceInstrument(query, "", instrument, local, videos, backingTracks, local != null);
                })
                .toList();
        return new PracticeSongView(slug(query), query, "Artista a confirmar", "", instruments);
    }

    private List<SongRecommendationView> searchYoutube(String query, int maxResults, String reason) {
        var response = youtube.get().uri(builder -> builder.path("/youtube/v3/search")
                .queryParam("part", "snippet")
                .queryParam("type", "video")
                .queryParam("videoEmbeddable", "true")
                .queryParam("videoSyndicated", "true")
                .queryParam("safeSearch", "moderate")
                .queryParam("maxResults", maxResults)
                .queryParam("regionCode", "BR")
                .queryParam("relevanceLanguage", "pt")
                .queryParam("q", query)
                .queryParam("key", apiKey)
                .build()).retrieve().body(JsonNode.class);
        if (response == null) return List.of();
        var result = new ArrayList<SongRecommendationView>();
        for (var item : response.path("items")) {
            var videoId = item.path("id").path("videoId").asText();
            if (videoId.isBlank()) continue;
            var snippet = item.path("snippet");
            result.add(new SongRecommendationView(videoId, decode(snippet.path("title").asText()),
                    decode(snippet.path("channelTitle").asText()),
                    snippet.path("thumbnails").path("medium").path("url").asText(), reason,
                    "https://www.youtube.com/watch?v=" + videoId));
        }
        return result;
    }

    private Song findLocalSong(String query, String canonicalTitle, InstrumentId instrument) {
        var queryKey = normalize(query);
        var titleKey = normalize(canonicalTitle);
        return songs.findAll().stream()
                .filter(song -> song.getInstrument() == instrument)
                .filter(song -> {
                    var candidate = normalize(song.getTitle());
                    return candidate.equals(queryKey) || candidate.equals(titleKey)
                            || queryKey.contains(candidate) || candidate.contains(queryKey)
                            || titleKey.contains(candidate) || candidate.contains(titleKey);
                })
                .max(Comparator.comparingInt(song -> normalize(song.getTitle()).length()))
                .orElse(null);
    }

    private boolean relevant(String query, String title) {
        var titleKey = normalize(title);
        var tokens = List.of(normalize(query).split(" ")).stream()
                .filter(token -> token.length() > 2)
                .toList();
        if (tokens.isEmpty()) return true;
        var matches = tokens.stream().filter(titleKey::contains).count();
        return matches >= Math.max(1, (tokens.size() + 1) / 2);
    }

    private String lessonTermsFor(InstrumentId instrument) {
        return switch (instrument) {
            case DRUMS -> " drums drum tab tutorial play along";
            case GUITAR -> " guitar tab tutorial lesson cover";
            case ACOUSTIC -> " acoustic guitar chords tab tutorial cover";
            case KEYS -> " piano keyboard tutorial sheet music cover";
        };
    }

    private String backingTermsFor(InstrumentId instrument) {
        return switch (instrument) {
            case DRUMS -> " drumless backing track play along";
            case GUITAR -> " guitar backing track no guitar play along";
            case ACOUSTIC -> " acoustic backing track karaoke instrumental";
            case KEYS -> " piano backing track no piano karaoke";
        };
    }

    private String instrumentLabel(InstrumentId instrument) {
        return switch (instrument) {
            case DRUMS -> "Bateria";
            case GUITAR -> "Guitarra";
            case ACOUSTIC -> "Viol\u00e3o";
            case KEYS -> "Teclado";
        };
    }

    private String tablatureUrl(String query, String artist, InstrumentId instrument) {
        if (instrument != InstrumentId.DRUMS && !artist.isBlank()) {
            return "https://www.cifraclub.com.br/" + slug(artist) + "/" + slug(query) + "/";
        }
        return "https://www.songsterr.com/?pattern="
                + URLEncoder.encode(query + " " + instrumentLabel(instrument), StandardCharsets.UTF_8);
    }

    private String youtubeSearch(String query) {
        return "https://www.youtube.com/results?search_query="
                + URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    private String cleanTitle(String source, String fallback) {
        var cleaned = source.replaceAll("(?i)\\s*[\\[(].*?(official|audio|video|lyrics?).*?[\\])]", "")
                .replaceAll("(?i)\\s*[-|]\\s*(official.*|lyrics?.*|audio.*)$", "")
                .trim();
        if (cleaned.isBlank()) return fallback;
        return List.of(cleaned.split("\\s+[-|]\\s+")).stream()
                .max(Comparator.comparingLong(part -> matchingTokens(fallback, part)))
                .filter(part -> matchingTokens(fallback, part) > 0)
                .map(String::trim)
                .orElse(cleaned);
    }

    private String cleanArtist(String sourceTitle, String query, String channel) {
        var titleParts = List.of(sourceTitle.split("\\s+[-|]\\s+"));
        if (titleParts.size() > 1) {
            return titleParts.stream()
                    .min(Comparator.comparingLong(part -> matchingTokens(query, part)))
                    .map(String::trim)
                    .filter(part -> !part.isBlank())
                    .orElseGet(() -> cleanChannel(channel));
        }
        return cleanChannel(channel);
    }

    private long matchingTokens(String query, String candidate) {
        var candidateKey = normalize(candidate);
        return List.of(normalize(query).split(" ")).stream()
                .filter(token -> token.length() > 2 && candidateKey.contains(token))
                .count();
    }

    private String cleanChannel(String channel) {
        return channel.replaceAll("(?i)\\s*-\\s*topic$", "").trim();
    }

    private String slug(String value) {
        var slug = normalize(value).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return slug.isBlank() ? "musica" : slug;
    }

    private String normalize(String value) {
        return Normalizer.normalize(value(value), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String decode(String value) {
        return value.replace("&amp;", "&").replace("&#39;", "'")
                .replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">");
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

    private record CachedPracticeSong(PracticeSongView song, long createdAt) {
        boolean valid() {
            return System.currentTimeMillis() - createdAt < 30 * 60 * 1000;
        }
    }
}
