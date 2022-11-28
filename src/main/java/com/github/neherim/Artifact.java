package com.github.neherim;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Artifact {
    private final String groupId;
    private final String artifactId;
    private final String version;

    public Artifact(String groupId, String artifactId, String version) {
        Validate.notBlank(groupId);
        Validate.notBlank(artifactId);
        Validate.notBlank(version);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public static Artifact fromString(String str) {
        var arr = StringUtils.split(str, ':');
        if (arr.length != 3) {
            throw new IllegalArgumentException("Can't parse input string " + str
                    + ". Format must be <groupId>:<artifactId>:<version>");
        }
        return new Artifact(arr[0], arr[1], arr[2]);
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }

    public String getArtifactUrl(String baseUrl) {
        var url = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        return url + groupId.replace('.', '/') + "/" + artifactId + "/" + version + "/";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return new EqualsBuilder()
                .append(groupId, artifact.groupId)
                .append(artifactId, artifact.artifactId)
                .append(version, artifact.version)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(groupId).append(artifactId).append(version).toHashCode();
    }
}
