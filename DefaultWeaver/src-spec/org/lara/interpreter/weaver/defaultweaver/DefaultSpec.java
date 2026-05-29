package org.lara.interpreter.weaver.defaultweaver;

import org.lara.langspec2.dsl.WeaverSpec;

/**
 * Weaver specification for the Clava C/C++ weaver, translated from the XML
 * specification files
 * (joinPointModel.xml and artifacts.xml) into the Java DSL.
 */
public class DefaultSpec extends WeaverSpec {

    @Override
    public void define() {
        weaverPrefix("DW");
        packageName("org.lara.interpreter.weaver.defaultweaver");
        rootJoinPoint("workspace");

        // =====================================================================
        // Join point definitions
        // =====================================================================

        joinPoint("folder")
                .defaultAttribute("path")
                .attribute("path", STRING);

        joinPoint("file")
                .defaultAttribute("name")
                .attribute("name", STRING)
                .attribute("absolutePath", STRING);

        joinPoint("function")
                .defaultAttribute("name")
                .attribute("name", STRING)
                .attribute("usesThis")
                    .param("param1", array(THIS))
                    .returns(THIS);

        joinPoint("class");

        joinPoint("method").extending("function");

        joinPoint("workspace")
                .action("report")
                    .returns(VOID);
    }
}
