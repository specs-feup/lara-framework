package org.lara.weavergen2.fixtures.specs.base;

import org.lara.langspec2.dsl.WeaverSpec;

/**
 * The base join point specification shared across all weavers.
 * <p>
 * Defines the attributes and actions that every join point in every weaver must have
 * (e.g., code, line, parent, children, insertBefore, replaceWith, etc.).
 * <p>
 * When WeaverGen2 runs for a weaver, it merges this base spec with the weaver-specific spec
 * to produce the complete model. {@code JoinPoint2} hand-implements these as the base class.
 */
public class BaseSpec extends WeaverSpec {

    @Override
    public void define() {
        packageName("org.lara.interpreter.weaver.interf");

        enumDef("InsertPosition")
                .value("before")
                .value("after")
                .value("replace")
                .end();

        global("LaraJoinPoint")
            .attribute("node", OBJECT, "The underlying AST node represented by this join point")
            .attribute("dump", INT, "String with a dump of the join point hierarchy")
            .attribute("joinPointType", STRING, "The type name of this join point")
            .attribute("self", THIS, "A reference to this join point")
            .attribute("children", array(jpRef("LaraJoinPoint")), "The children of this join point, ignoring null nodes")
            .attribute("descendants", array(STRING), "All descendants of this join point")
            .attribute("scopeNodes", array(jpRef("LaraJoinPoint")), "The scope nodes of this join point")


            // Actions available on all join points
            .action("insert")
                .param("position", enumRef("InsertPosition"))
                .param("code", THIS)
                .returns(array(jpRef("LaraJoinPoint")))
            .action("insert")
                .param("position", enumRef("InsertPosition"))
                .param("joinpoint", jpRef("LaraJoinPoint"))
                .returns(array(jpRef("LaraJoinPoint")))
            .action("insert")
                .param("position", array(enumRef("InsertPosition")))
                .param("code", array(THIS))
                .returns(array(THIS))
            .action("insert")
                .param("position", array(enumRef("InsertPosition")))
                .param("joinpoint", array(jpRef("LaraJoinPoint")))
                .returns(array(THIS));
    }
}
