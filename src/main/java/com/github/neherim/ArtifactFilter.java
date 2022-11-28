package com.github.neherim;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArtifactFilter {
    private final String privateMavenRepoUrl;

    public ArtifactFilter(String privateMavenRepoUrl) {
        this.privateMavenRepoUrl = privateMavenRepoUrl;
    }

    /**
     * Return true if artifact doesn't exist in private maven repository
     */
    public boolean include(Artifact artifact) {
        if (privateMavenRepoUrl == null) {
            return true;
        }
        try {
            var url = new URL(artifact.getArtifactUrl(privateMavenRepoUrl));
            var http = (HttpURLConnection) url.openConnection();
            var include = http.getResponseCode() != 200;
            if (!include) {
                System.out.println("Exclude: " + artifact);
            }
            return include;
        } catch (IOException ex) {
            System.err.println("Artifact filter error: " + ex);
            return true;
        }
    }
}
