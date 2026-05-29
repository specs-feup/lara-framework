package org.lara.weavergen2.fixtures.specs.valid;

import org.lara.langspec2.dsl.WeaverSpec;

public class MediumSpec extends WeaverSpec {

    @Override
    public void define() {
        weaverPrefix("Medium");
        packageName("medium.pkg");
        rootJoinPoint("file");

        global().attribute("language", STRING);

        joinPoint("file")
                .defaultAttribute("language")
                .attribute("path", STRING);

        joinPoint("function")
                .action("replaceWith")
                    .param("node", jpRef("joinpoint"))
                    .returns(jpRef("joinpoint"))
                .action("insert")
                    .param("position", STRING, "before")
                    .param("code", STRING)
                    .returns(array(jpRef("joinpoint")))
                .attribute("name", STRING)
                .attribute("params", typeDefRef("Location"));

        joinPoint("var");
        joinPoint("body");
        joinPoint("statement");

        typeDef("Location")
                .field("file", STRING)
                .field("line", INTEGER)
                .end();
    }
}
