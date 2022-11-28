package com.github.neherim;

import com.jcabi.aether.Aether;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DepListCollector {

    private final LocalRepository local;
    private final Aether aether;

    public DepListCollector(LocalRepository local, String remoteRepositoryUrl) {
        this.local = local;
        var remotes = List.of(new RemoteRepository("maven-central", "default", remoteRepositoryUrl));
        this.aether = new Aether(remotes, local.getRoot());
    }

    /**
     * For all passed artifacts return list of transitive dependencies from maven central
     */
    public Set<Artifact> collectTransitiveDependencies(Collection<Artifact> artifacts) throws IOException {
        try {
            local.clean();
            artifacts.stream()
                    .distinct()
                    .map(this::toMavenArtifact)
                    .forEach(this::resolve);
            return new HashSet<>(local.getAllArtifacts());
        } finally {
            local.clean();
        }
    }

    private DefaultArtifact toMavenArtifact(Artifact artifact) {
        return new DefaultArtifact(artifact.toString());
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
