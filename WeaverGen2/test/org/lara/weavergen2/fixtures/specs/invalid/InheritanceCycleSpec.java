package org.lara.weavergen2.fixtures.specs.invalid;

import org.lara.langspec2.dsl.WeaverSpec;

public class InheritanceCycleSpec extends WeaverSpec {

    @Override
    public void define() {
        weaverPrefix("Invalid");
        packageName("invalid.pkg");
        rootJoinPoint("A");

        joinPoint("A").extending("A");
    }
}
