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
        global()
            .attribute("dump", STRING, "String with a dump of the join point hierarchy")
            .attribute("joinPointType", STRING, "The type name of this join point")
            .attribute("self", THIS, "A reference to this join point")
            .attribute("children", array(THIS), "The children of this join point")
            .attribute("descendants", array(THIS), "All descendants of this join point")
            .attribute("scopeNodes", array(THIS), "The scope nodes of this join point")

            // Tree navigation
            .attribute("parent", THIS, "The parent of this join point")
            .attribute("root", THIS, "The root of the tree")
            .attribute("code", STRING, "String with the code represented by this node")
            .attribute("line", INTEGER, "The starting line of the current node in the original code")
            .attribute("column", INTEGER, "The starting column of the current node in the original code")

            // Actions available on all join points
            .action("insert").param("position", STRING).param("code", STRING).returns(array(THIS))
            .action("toString").returns(STRING)
            .action("equals").param("jp", THIS).returns(BOOLEAN)
            .action("instanceOf").param("name", STRING).returns(BOOLEAN);
    }
}
