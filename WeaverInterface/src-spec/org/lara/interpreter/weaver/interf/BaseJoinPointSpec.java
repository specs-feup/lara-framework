package org.lara.interpreter.weaver.interf;

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
public class BaseJoinPointSpec extends WeaverSpec {

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
            .attribute("dump", STRING, "String with a dump of the join point hierarchy")
            .attribute("joinPointType", STRING, "The type name of this join point")
            .attribute("self", THIS, "A reference to this join point")
            .attribute("children", array(jpRef("LaraJoinPoint")), "The children of this join point, ignoring null nodes")
            .attribute("descendants", array(jpRef("LaraJoinPoint")), "All descendants of this join point")
            .attribute("scopeNodes", array(jpRef("LaraJoinPoint")), "The scope nodes of this join point")

            // Tree navigation
            .attribute("parent", jpRef("LaraJoinPoint"), "Returns the parent node in the AST, or undefined if it is the root node")
            .attribute("root", jpRef("LaraJoinPoint"), "The root of the tree")
            .attribute("code", STRING, "String with the code represented by this node")
            .attribute("line", INTEGER, "The starting line of the current node in the original code")
            .attribute("column", INTEGER, "The starting column of the current node in the original code")

            // Actions available on all join points
            .action("insert")
                .param("position", enumRef("InsertPosition"))
                .param("code", STRING)
                .returns(array(jpRef("LaraJoinPoint")))
            .action("insert")
                .param("position", enumRef("InsertPosition"))
                .param("joinpoint", jpRef("LaraJoinPoint"))
                .returns(array(jpRef("LaraJoinPoint")))
            .action("toString")
                .returns(STRING)
            .action("equals")
                .param("jp", THIS)
                .returns(BOOLEAN)
            .action("instanceOf")
                .param("joinpointClassname", STRING)
                .returns(BOOLEAN)
            .action("instanceOf")
                .param("joinpointClassnames", array(STRING))
                .returns(BOOLEAN);
    }
}
