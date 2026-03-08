package org.lara.interpreter.weaver.generator.integration.fixtures;

public record JavaDeclarationSignature(String file, String kind, String signature) {

    public String normalized() {
        return file + "::" + kind + "::" + signature;
    }
}
