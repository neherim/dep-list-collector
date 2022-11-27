package com.github.neherim;

import com.jcabi.aether.Aether;
import org.apache.commons.lang3.StringUtils;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DepListCollector {
    public static final String MAVEN_BASE_URL = "https://repo1.maven.org/maven2/";

    private final LocalRepository local;
    private final Aether aether;

    public DepListCollector(LocalRepository local) {
        this.local = local;
        var remotes = List.of(new RemoteRepository("maven-central", "default", MAVEN_BASE_URL));
        this.aether = new Aether(remotes, local.getRoot());
    }

    /**
     * For all passed artifacts, the URLs of all transitive dependencies from maven central are returned
     */
    public Set<String> collect(Collection<String> artifacts) throws IOException {
        try {
            local.clean();
            artifacts.stream()
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .map(DefaultArtifact::new)
                    .forEach(this::resolve);

            return local.getAllArtifacts().stream()
                    .map(d -> MAVEN_BASE_URL + d)
                    .collect(Collectors.toSet());
        } finally {
            local.clean();
        }
    }

    private void resolve(DefaultArtifact artifact) {
        try {
            System.out.println("Resolve: " + artifact);
            aether.resolve(artifact, JavaScopes.COMPILE);
        } catch (DependencyResolutionException ex) {
            throw new RuntimeException(ex);
        }
    }
}
