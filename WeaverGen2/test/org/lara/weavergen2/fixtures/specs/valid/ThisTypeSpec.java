package org.lara.weavergen2.fixtures.specs.valid;

import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.langspec2.types.JpDataType.DirectType;
import org.lara.langspec2.types.JpDataType.ParameterizedType;

public class ThisTypeSpec extends WeaverSpec {

    @Override
    public void define() {
        weaverPrefix("ThisType");
        packageName("thistype.pkg");
        rootJoinPoint("node");

        global()
                .attribute("root", THIS, "Returns the root of the AST with late-bound type")
                .attribute("ancestors", array(THIS), "Returns all ancestors as array of late-bound type")
                .attribute("siblings", list(THIS), "Returns siblings as a list of late-bound type")
                .action("selfTransform")
                    .param("transformName", STRING)
                    .returns(THIS)
                .action("replaceWith")
                    .param("replacement", THIS)
                    .returns(VOID)
                .action("findAll")
                    .param("pattern", STRING)
                    .returns(list(THIS))
                .action("categorize")
                    .returns(map(STRING, THIS));

        joinPoint("node")
                .defaultAttribute("id")
                .attribute("clone", THIS, "Creates a deep clone of this node")
                .attribute("parent", THIS, "Returns the parent node with late-bound type")
                .attribute("ancestorOfType")
                    .param("typeName", STRING)
                    .returns(THIS)
                .attribute("findSimilar")
                    .param("target", THIS)
                    .returns(list(THIS))
                .attribute("findBetween")
                    .param("start", THIS)
                    .param("end", THIS)
                    .returns(list(THIS))
                .attribute("children", array(THIS), "Returns direct children as array")
                .attribute("descendants", array(THIS), "Returns all descendants as array")
                .attribute("childrenMatrix", array(array(THIS)), "Returns children organized in 2D matrix")
                .attribute("attributes", map(STRING, STRING), "Returns attribute name-value pairs")
                .attribute("tags", list(STRING), "Returns list of tags")
                .attribute("properties", map(STRING, OBJECT), "Returns property map")
                .attribute("childList", list(THIS), "Returns children as List")
                .attribute("descendantSet", new ParameterizedType(new DirectType("Set"), java.util.List.of(THIS)), "Returns descendants as Set")
                .attribute("namedChildren", map(STRING, THIS), "Returns children indexed by name")
                .attribute("indexedNodes", map(INTEGER, THIS), "Returns nodes indexed by position")
                .attribute("childGroups", list(list(THIS)), "Returns children grouped in nested lists")
                .attribute("categorizedNodes", map(STRING, list(THIS)), "Returns nodes categorized by type")
                .attribute("hierarchy", map(STRING, map(STRING, THIS)), "Returns nested categorization")
                .attribute("id", STRING)
                .attribute("line", INTEGER)
                .attribute("column", INTEGER)
                .action("copy")
                    .returns(THIS)
                .action("detach")
                    .returns(THIS)
                .action("insertBefore")
                    .param("node", THIS)
                    .returns(VOID)
                .action("insertAfter")
                    .param("node", THIS)
                    .returns(VOID)
                .action("merge")
                    .param("other", THIS)
                    .returns(THIS)
                .action("swap")
                    .param("other", THIS)
                    .param("preserveComments", BOOLEAN)
                    .returns(VOID)
                .action("insertAt")
                    .param("position", INTEGER)
                    .param("node", THIS)
                    .returns(VOID)
                .action("wrapWith")
                    .param("wrapper", THIS)
                    .param("position", STRING)
                    .returns(THIS)
                .action("replaceBetween")
                    .param("start", THIS)
                    .param("end", THIS)
                    .param("replacement", THIS)
                    .returns(VOID)
                .action("getMetadata")
                    .returns(map(STRING, STRING))
                .action("getTags")
                    .returns(list(STRING))
                .action("findChildren")
                    .param("filter", STRING)
                    .returns(list(THIS))
                .action("groupByType")
                    .returns(map(STRING, list(THIS)))
                .action("insertAll")
                    .param("nodes", list(THIS))
                    .returns(VOID)
                .action("replaceAll")
                    .param("replacements", map(STRING, THIS))
                    .returns(VOID)
                .action("partitionChildren")
                    .param("criteria", STRING)
                    .returns(map(STRING, list(THIS)))
                .action("toArray")
                    .returns(array(THIS));

        joinPoint("expr")
                .extending("node")
                .action("evaluate")
                    .returns(THIS)
                .action("substitute")
                    .param("target", THIS)
                    .param("replacement", THIS)
                    .returns(THIS)
                .action("collectTerms")
                    .returns(list(THIS));

        joinPoint("binaryExpr")
                .extending("expr")
                .action("swapOperands")
                    .returns(THIS)
                .action("setOperands")
                    .param("left", THIS)
                    .param("right", THIS)
                    .returns(VOID);

        joinPoint("stmt")
                .extending("node")
                .action("moveAfter")
                    .param("target", THIS)
                    .returns(THIS)
                .action("extractTo")
                    .param("context", STRING)
                    .returns(THIS);

        joinPoint("loop")
                .extending("stmt")
                .action("unroll")
                    .param("factor", INTEGER)
                    .returns(list(THIS))
                .action("tile")
                    .param("tileSize", INTEGER)
                    .returns(THIS)
                .action("interchange")
                    .param("other", THIS)
                    .returns(VOID)
                .action("fuse")
                    .param("other", THIS)
                    .param("checkDependencies", BOOLEAN)
                    .returns(THIS);

        joinPoint("container")
                .action("processNested")
                    .returns(list(map(STRING, THIS)))
                .action("addToAll")
                    .param("items", map(STRING, list(THIS)))
                    .returns(VOID);

        typeDef("NodeInfo")
                .field("node", jpRef("joinpoint"))
                .field("relatedNodes", list(jpRef("joinpoint")))
                .field("metadata", map(STRING, STRING))
                .field("name", STRING)
                .end();

        typeDef("TreeStructure")
                .field("root", jpRef("joinpoint"))
                .field("levels", list(list(jpRef("joinpoint"))))
                .field("nodeIndex", map(STRING, jpRef("joinpoint")))
                .field("depth", INTEGER)
                .end();

        typeDef("SimpleMetadata")
                .field("name", STRING)
                .field("values", list(STRING))
                .field("config", map(STRING, OBJECT))
                .end();

        typeDef("AstContext")
                .tooltip("Contextual information for AST operations")
                .end();

        enumDef("NodeKind")
                .tooltip("Classification of node types")
                .value("EXPRESSION")
                .value("STATEMENT")
                .value("DECLARATION")
                .value("OTHER")
                .end();
    }
}
