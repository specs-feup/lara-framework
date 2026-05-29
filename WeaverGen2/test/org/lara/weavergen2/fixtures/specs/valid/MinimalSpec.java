package org.lara.weavergen2.fixtures.specs.valid;

import org.lara.langspec2.dsl.WeaverSpec;

public class MinimalSpec extends WeaverSpec {

    @Override
    public void define() {
        weaverPrefix("Minimal");
        packageName("minimal.pkg");
        rootJoinPoint("root");

        joinPoint("root");
    }
}
