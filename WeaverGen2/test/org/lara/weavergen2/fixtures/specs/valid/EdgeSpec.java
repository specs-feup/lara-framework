package org.lara.weavergen2.fixtures.specs.valid;

import org.lara.langspec2.dsl.WeaverSpec;

public class EdgeSpec extends WeaverSpec {

    @Override
    public void define() {
        weaverPrefix("Edge");
        packageName("edge.pkg");
        rootJoinPoint("base");

        global().action("noop").returns(VOID);

        joinPoint("base")
                .attribute("a", INTEGER);

        joinPoint("level1")
                .extending("base")
                .attribute("a", INTEGER)
                .attribute("b", INTEGER);

        joinPoint("level2")
                .extending("level1")
                .attribute("c", INTEGER);

        joinPoint("reservedKeyword")
                .extending("level2")
                .attribute("class", STRING);
    }
}
