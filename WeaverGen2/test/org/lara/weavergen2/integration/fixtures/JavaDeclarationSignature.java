package org.lara.weavergen2.integration.fixtures;

public record JavaDeclarationSignature(String file, String kind, String signature) {

    public String normalized() {
        return file + "::" + kind + "::" + signature;
    }
}
