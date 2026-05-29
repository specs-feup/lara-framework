package org.lara.weavergen2.integration.fixtures;

public record ArtifactManifestEntry(String path, String sha256, long bytes, long lines) {

    public String toTsv() {
        return path + "\t" + sha256 + "\t" + bytes + "\t" + lines;
    }

    public static ArtifactManifestEntry fromTsv(String line) {
        String[] parts = line.split("\t");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Expected 4 TSV columns, got " + parts.length + ": " + line);
        }

        return new ArtifactManifestEntry(parts[0], parts[1], Long.parseLong(parts[2]), Long.parseLong(parts[3]));
    }
}
