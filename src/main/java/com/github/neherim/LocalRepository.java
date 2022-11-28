package com.github.neherim;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalRepository {
    private final File root;

    public LocalRepository(String path) {
        root = new File(path);
        root.mkdirs();
    }

    public File getRoot() {
        return root;
    }

    /**
     * Remove all files in root directory
     */
    public void clean() throws IOException {
        FileUtils.cleanDirectory(root);
    }

    /**
     * Return list of all artifacts in repository
     */
    public Set<Artifact> getAllArtifacts() {
        return getAllSubdirs(root).stream()
                .map(this::pathToArtifact)
                .collect(Collectors.toSet());
    }

    private Artifact pathToArtifact(String artifactPath) {
        var file = new File(artifactPath);
        var version = file.getName();
        var artifactId = file.getParentFile().getName();
        var groupId = file.getParentFile().getParentFile().getPath().replace("/", ".");
        return new Artifact(groupId, artifactId, version);
    }

    private Set<String> getAllSubdirs(File dir) {
        var subdirs = dir.listFiles(File::isDirectory);
        if (subdirs == null) {
            throw new RuntimeException(dir + " doesn't exist");
        }
        if (subdirs.length == 0) {
            return Set.of(dir.getName());
        }

        return Arrays.stream(subdirs)
                .map(this::getAllSubdirs)
                .flatMap(Set::stream)
                .map(name -> dir == root ? name : dir.getName() + "/" + name)
                .collect(Collectors.toSet());
    }
}
