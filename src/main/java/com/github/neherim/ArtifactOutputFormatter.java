package com.github.neherim;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ArtifactOutputFormatter {

    private final String baseRepositoryUrl;

    public ArtifactOutputFormatter(String baseRepositoryUrl) {
        this.baseRepositoryUrl = baseRepositoryUrl;
    }

    /**
     * Format artifact by pattern:
     * [depUrl], #[dependency release date]
     * <p>
     * Example:
     * https://repo1.maven.org/maven2/org/yaml/snakeyaml/1.30/, #2021-12-14
     */
    public String format(Artifact artifact) {
        var url = artifact.getArtifactUrl(baseRepositoryUrl);
        return url + ", #" + getDependencyReleaseDate(url).orElse(null);
    }


    private static Optional<LocalDate> getDependencyReleaseDate(String url) {
        try (var is = new URL(url).openStream()) {
            var text = new String(is.readAllBytes());

            return Pattern.compile("(\\d{4}-\\d{2}-\\d{2})")
                    .matcher(text)
                    .results()
                    .map(MatchResult::group)
                    .map(LocalDate::parse)
                    .max(LocalDate::compareTo);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
}
