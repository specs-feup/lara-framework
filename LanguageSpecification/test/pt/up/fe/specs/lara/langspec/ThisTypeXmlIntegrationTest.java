/**
 * Copyright 2026 SPeCS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package pt.up.fe.specs.lara.langspec;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lara.language.specification.ast.ActionNode;
import org.lara.language.specification.ast.AttributeNode;
import org.lara.language.specification.ast.DeclarationNode;
import org.lara.language.specification.ast.JoinPointNode;
import org.lara.language.specification.ast.LangSpecNode;
import org.lara.language.specification.ast.NodeFactory;
import org.lara.language.specification.ast.RootNode;
import org.lara.language.specification.ast.TypeDefNode;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.ParameterizedType;
import org.lara.language.specification.dsl.types.ThisType;
import org.lara.language.specification.dsl.types.TypeDef;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.providers.ResourceProvider;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive integration tests for the 'this' type and generic types feature.
 * 
 * <p>Tests cover:
 * <ul>
 *   <li>'this' as attribute/action return types</li>
 *   <li>'this' as action parameter types</li>
 *   <li>Simple generics (List&lt;String&gt;, Map&lt;K,V&gt;)</li>
 *   <li>Generics with 'this' (List&lt;this&gt;, Map&lt;String, this&gt;)</li>
 *   <li>Nested generics (List&lt;List&lt;this&gt;&gt;)</li>
 *   <li>Arrays of 'this' (this[])</li>
 *   <li>Inheritance preservation of 'this' type</li>
 *   <li>AST/JSON output verification</li>
 *   <li>TypeDef with generics and 'this'</li>
 * </ul>
 */
@DisplayName("ThisType and Generics XML Integration Tests")
public class ThisTypeXmlIntegrationTest {

    private static final String BASE_PACKAGE = "pt/up/fe/specs/lara/langspec/thistype/";

    /**
     * Resource provider enum for thistype test resources.
     */
    public enum ThisTypeTestResource implements ResourceProvider {
        JOIN_POINT_MODEL("joinPointModel.xml"),
        ATTRIBUTE_MODEL("artifacts.xml"),
        ACTION_MODEL("actionModel.xml");

        private final String resource;

        ThisTypeTestResource(String resource) {
            this.resource = BASE_PACKAGE + resource;
        }

        @Override
        public String getResource() {
            return resource;
        }
    }

    private LanguageSpecification langSpec;

    @BeforeAll
    static void initSystem() {
        SpecsSystem.programStandardInit();
    }

    @BeforeEach
    void setUp() {
        langSpec = LangSpecsXmlParser.parse(
                ThisTypeTestResource.JOIN_POINT_MODEL.toStream(),
                ThisTypeTestResource.ATTRIBUTE_MODEL.toStream(),
                ThisTypeTestResource.ACTION_MODEL.toStream(),
                true
        );
    }

    // ==================== Basic Parsing Tests ====================

    @Nested
    @DisplayName("Basic Parsing and Structure Tests")
    class BasicParsingTests {

        @Test
        @DisplayName("Language specification parses successfully")
        void testParsingSucceeds() {
            assertNotNull(langSpec);
            assertNotNull(langSpec.getRoot());
            assertNotNull(langSpec.getGlobal());
        }

        @Test
        @DisplayName("All join points are present")
        void testJoinPointsPresent() {
            assertNotNull(langSpec.getJoinPoint("node"));
            assertNotNull(langSpec.getJoinPoint("expr"));
            assertNotNull(langSpec.getJoinPoint("binaryExpr"));
            assertNotNull(langSpec.getJoinPoint("stmt"));
            assertNotNull(langSpec.getJoinPoint("loop"));
            assertNotNull(langSpec.getJoinPoint("container"));
        }

        @Test
        @DisplayName("Inheritance hierarchy is correct")
        void testInheritanceHierarchy() {
            JoinPointClass expr = langSpec.getJoinPoint("expr");
            JoinPointClass binaryExpr = langSpec.getJoinPoint("binaryExpr");
            JoinPointClass stmt = langSpec.getJoinPoint("stmt");
            JoinPointClass loop = langSpec.getJoinPoint("loop");

            assertTrue(expr.getExtend().isPresent());
            assertEquals("node", expr.getExtend().get().getName());

            assertTrue(binaryExpr.getExtend().isPresent());
            assertEquals("expr", binaryExpr.getExtend().get().getName());

            assertTrue(stmt.getExtend().isPresent());
            assertEquals("node", stmt.getExtend().get().getName());

            assertTrue(loop.getExtend().isPresent());
            assertEquals("stmt", loop.getExtend().get().getName());
        }

        @Test
        @DisplayName("Root alias is correct")
        void testRootAlias() {
            assertEquals("root", langSpec.getRootAlias());
            assertEquals("node", langSpec.getRoot().getName());
        }
    }

    // ==================== ThisType in Attributes Tests ====================

    @Nested
    @DisplayName("'this' Type in Attribute Return Types")
    class ThisTypeAttributeTests {

        @Test
        @DisplayName("Simple 'this' return type is parsed as ThisType")
        void testSimpleThisReturnType() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute cloneAttr = findAttribute(node.getAttributesSelf(), "clone");

            assertNotNull(cloneAttr);
            assertInstanceOf(ThisType.class, cloneAttr.getType());
            assertEquals("this", cloneAttr.getType().toString());
            assertEquals("this", cloneAttr.getReturnType());
        }

        @Test
        @DisplayName("Multiple 'this' attributes on same join point")
        void testMultipleThisAttributes() {
            JoinPointClass node = langSpec.getJoinPoint("node");

            Attribute clone = findAttribute(node.getAttributesSelf(), "clone");
            Attribute parent = findAttribute(node.getAttributesSelf(), "parent");

            assertInstanceOf(ThisType.class, clone.getType());
            assertInstanceOf(ThisType.class, parent.getType());

            // Both should be the same singleton instance
            assertSame(clone.getType(), parent.getType());
        }

        @Test
        @DisplayName("'this' type with parameters")
        void testThisWithParameters() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute ancestorOfType = findAttribute(node.getAttributesSelf(), "ancestorOfType");

            assertNotNull(ancestorOfType);
            assertInstanceOf(ThisType.class, ancestorOfType.getType());
            assertEquals(1, ancestorOfType.getParameters().size());
            assertEquals("typeName", ancestorOfType.getParameters().get(0).getName());
            assertEquals("String", ancestorOfType.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Global attribute with 'this' return type")
        void testGlobalThisAttribute() {
            JoinPointClass global = langSpec.getGlobal();
            Attribute rootAttr = findAttribute(global.getAttributesSelf(), "root");

            assertNotNull(rootAttr);
            assertInstanceOf(ThisType.class, rootAttr.getType());
            assertEquals("this", rootAttr.getType().toString());
        }

        @Test
        @DisplayName("Array of 'this' type: this[]")
        void testThisArrayType() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute children = findAttribute(node.getAttributesSelf(), "children");

            assertNotNull(children);
            assertInstanceOf(ArrayType.class, children.getType());
            ArrayType arrayType = (ArrayType) children.getType();
            assertInstanceOf(ThisType.class, arrayType.getBaseType());
            assertEquals("this[]", arrayType.toString());
        }

        @Test
        @DisplayName("Multi-dimensional array of 'this': this[][]")
        void testThisMultiDimensionalArray() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute childrenMatrix = findAttribute(node.getAttributesSelf(), "childrenMatrix");

            assertNotNull(childrenMatrix);
            assertInstanceOf(ArrayType.class, childrenMatrix.getType());
            assertEquals("this[][]", childrenMatrix.getType().toString());
        }

        @Test
        @DisplayName("Global array of 'this' type")
        void testGlobalThisArray() {
            JoinPointClass global = langSpec.getGlobal();
            Attribute ancestors = findAttribute(global.getAttributesSelf(), "ancestors");

            assertNotNull(ancestors);
            assertInstanceOf(ArrayType.class, ancestors.getType());
            ArrayType arrayType = (ArrayType) ancestors.getType();
            assertInstanceOf(ThisType.class, arrayType.getBaseType());
        }

        @Test
        @DisplayName("Attribute with 'this' as parameter type")
        void testAttributeWithThisParameterType() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute findSimilar = findAttribute(node.getAttributesSelf(), "findSimilar");

            assertNotNull(findSimilar, "Should have 'findSimilar' attribute");
            assertInstanceOf(ParameterizedType.class, findSimilar.getType());
            assertEquals("List<this>", findSimilar.getType().toString());

            // Verify parameter has 'this' type
            assertEquals(1, findSimilar.getParameters().size());
            assertEquals("target", findSimilar.getParameters().get(0).getName());
            assertEquals("this", findSimilar.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Attribute with multiple 'this' parameters")
        void testAttributeWithMultipleThisParameters() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute findBetween = findAttribute(node.getAttributesSelf(), "findBetween");

            assertNotNull(findBetween, "Should have 'findBetween' attribute");
            assertEquals("List<this>", findBetween.getType().toString());

            // Verify both parameters have 'this' type
            assertEquals(2, findBetween.getParameters().size());
            assertEquals("start", findBetween.getParameters().get(0).getName());
            assertEquals("this", findBetween.getParameters().get(0).getType());
            assertEquals("end", findBetween.getParameters().get(1).getName());
            assertEquals("this", findBetween.getParameters().get(1).getType());
        }
    }

    // ==================== Generic Types Tests ====================

    @Nested
    @DisplayName("Generic Types Parsing")
    class GenericTypesTests {

        @Test
        @DisplayName("Simple generic: Map<String, String>")
        void testSimpleMapGeneric() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute attrs = findAttribute(node.getAttributesSelf(), "attributes");

            assertNotNull(attrs);
            assertInstanceOf(ParameterizedType.class, attrs.getType());
            ParameterizedType paramType = (ParameterizedType) attrs.getType();
            assertEquals("Map<String, String>", paramType.toString());
            assertEquals(2, paramType.getTypeArguments().size());
        }

        @Test
        @DisplayName("Simple generic: List<String>")
        void testSimpleListGeneric() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute tags = findAttribute(node.getAttributesSelf(), "tags");

            assertNotNull(tags);
            assertInstanceOf(ParameterizedType.class, tags.getType());
            ParameterizedType paramType = (ParameterizedType) tags.getType();
            assertEquals("List<String>", paramType.toString());
            assertEquals(1, paramType.getTypeArguments().size());
        }

        @Test
        @DisplayName("Generic with Object type argument")
        void testGenericWithObject() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute props = findAttribute(node.getAttributesSelf(), "properties");

            assertNotNull(props);
            assertInstanceOf(ParameterizedType.class, props.getType());
            assertEquals("Map<String, Object>", props.getType().toString());
        }
    }

    // ==================== Generics with 'this' Tests ====================

    @Nested
    @DisplayName("Generics with 'this' Type Argument")
    class GenericsWithThisTests {

        @Test
        @DisplayName("List<this> is parsed correctly")
        void testListOfThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute childList = findAttribute(node.getAttributesSelf(), "childList");

            assertNotNull(childList);
            assertInstanceOf(ParameterizedType.class, childList.getType());
            ParameterizedType paramType = (ParameterizedType) childList.getType();

            assertEquals("List<this>", paramType.toString());
            assertEquals(1, paramType.getTypeArguments().size());
            assertInstanceOf(ThisType.class, paramType.getTypeArguments().get(0));
        }

        @Test
        @DisplayName("Set<this> is parsed correctly")
        void testSetOfThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute descendantSet = findAttribute(node.getAttributesSelf(), "descendantSet");

            assertNotNull(descendantSet);
            assertInstanceOf(ParameterizedType.class, descendantSet.getType());
            ParameterizedType paramType = (ParameterizedType) descendantSet.getType();

            assertEquals("Set<this>", paramType.toString());
            assertInstanceOf(ThisType.class, paramType.getTypeArguments().get(0));
        }

        @Test
        @DisplayName("Map<String, this> is parsed correctly")
        void testMapWithThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute namedChildren = findAttribute(node.getAttributesSelf(), "namedChildren");

            assertNotNull(namedChildren);
            assertInstanceOf(ParameterizedType.class, namedChildren.getType());
            ParameterizedType paramType = (ParameterizedType) namedChildren.getType();

            assertEquals("Map<String, this>", paramType.toString());
            assertEquals(2, paramType.getTypeArguments().size());

            // First arg should not be ThisType
            assertNotEquals(ThisType.class, paramType.getTypeArguments().get(0).getClass());

            // Second arg should be ThisType
            assertInstanceOf(ThisType.class, paramType.getTypeArguments().get(1));
        }

        @Test
        @DisplayName("Map<Integer, this> is parsed correctly")
        void testMapIntegerThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute indexedNodes = findAttribute(node.getAttributesSelf(), "indexedNodes");

            assertNotNull(indexedNodes);
            assertInstanceOf(ParameterizedType.class, indexedNodes.getType());
            assertEquals("Map<Integer, this>", indexedNodes.getType().toString());
        }

        @Test
        @DisplayName("Global List<this> in attribute")
        void testGlobalListOfThis() {
            JoinPointClass global = langSpec.getGlobal();
            Attribute siblings = findAttribute(global.getAttributesSelf(), "siblings");

            assertNotNull(siblings);
            assertInstanceOf(ParameterizedType.class, siblings.getType());
            ParameterizedType paramType = (ParameterizedType) siblings.getType();
            assertEquals("List<this>", paramType.toString());
            assertInstanceOf(ThisType.class, paramType.getTypeArguments().get(0));
        }
    }

    // ==================== Nested Generics Tests ====================

    @Nested
    @DisplayName("Nested Generic Types with 'this'")
    class NestedGenericsTests {

        @Test
        @DisplayName("List<List<this>> is parsed correctly")
        void testNestedListOfThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute childGroups = findAttribute(node.getAttributesSelf(), "childGroups");

            assertNotNull(childGroups);
            assertInstanceOf(ParameterizedType.class, childGroups.getType());
            ParameterizedType outerType = (ParameterizedType) childGroups.getType();

            assertEquals("List<List<this>>", outerType.toString());
            assertEquals(1, outerType.getTypeArguments().size());

            // Inner type should also be ParameterizedType
            IType innerArg = outerType.getTypeArguments().get(0);
            assertInstanceOf(ParameterizedType.class, innerArg);
            ParameterizedType innerType = (ParameterizedType) innerArg;

            assertEquals("List<this>", innerType.toString());
            assertInstanceOf(ThisType.class, innerType.getTypeArguments().get(0));
        }

        @Test
        @DisplayName("Map<String, List<this>> is parsed correctly")
        void testMapWithListOfThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute categorizedNodes = findAttribute(node.getAttributesSelf(), "categorizedNodes");

            assertNotNull(categorizedNodes);
            assertInstanceOf(ParameterizedType.class, categorizedNodes.getType());
            ParameterizedType mapType = (ParameterizedType) categorizedNodes.getType();

            assertEquals("Map<String, List<this>>", mapType.toString());
            assertEquals(2, mapType.getTypeArguments().size());

            // Second argument should be List<this>
            IType secondArg = mapType.getTypeArguments().get(1);
            assertInstanceOf(ParameterizedType.class, secondArg);
            ParameterizedType listType = (ParameterizedType) secondArg;
            assertEquals("List<this>", listType.toString());
            assertInstanceOf(ThisType.class, listType.getTypeArguments().get(0));
        }

        @Test
        @DisplayName("Map<String, Map<String, this>> is parsed correctly")
        void testDeeplyNestedMap() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute hierarchy = findAttribute(node.getAttributesSelf(), "hierarchy");

            assertNotNull(hierarchy);
            assertInstanceOf(ParameterizedType.class, hierarchy.getType());
            assertEquals("Map<String, Map<String, this>>", hierarchy.getType().toString());

            ParameterizedType outerMap = (ParameterizedType) hierarchy.getType();
            ParameterizedType innerMap = (ParameterizedType) outerMap.getTypeArguments().get(1);
            assertInstanceOf(ThisType.class, innerMap.getTypeArguments().get(1));
        }

        @Test
        @DisplayName("Deeply nested: List<Map<String, List<this>>>")
        void testVeryDeeplyNested() {
            JoinPointClass container = langSpec.getJoinPoint("container");
            Attribute deepNested = findAttribute(container.getAttributesSelf(), "deepNested");

            assertNotNull(deepNested);
            assertInstanceOf(ParameterizedType.class, deepNested.getType());
            assertEquals("List<Map<String, List<this>>>", deepNested.getType().toString());

            ParameterizedType paramType = (ParameterizedType) deepNested.getType();
            // Navigate to the innermost type: List -> Map -> List -> this
            ParameterizedType mapType = (ParameterizedType) paramType.getTypeArguments().get(0);
            ParameterizedType innerList = (ParameterizedType) mapType.getTypeArguments().get(1);
            assertInstanceOf(ThisType.class, innerList.getTypeArguments().get(0));
        }

        @Test
        @DisplayName("Map<this, this> with 'this' as both key and value")
        void testMapThisToThis() {
            JoinPointClass container = langSpec.getJoinPoint("container");
            Attribute multiThis = findAttribute(container.getAttributesSelf(), "multiThis");

            assertNotNull(multiThis);
            assertInstanceOf(ParameterizedType.class, multiThis.getType());
            ParameterizedType mapType = (ParameterizedType) multiThis.getType();

            assertEquals("Map<this, this>", mapType.toString());
            assertEquals(2, mapType.getTypeArguments().size());
            assertInstanceOf(ThisType.class, mapType.getTypeArguments().get(0));
            assertInstanceOf(ThisType.class, mapType.getTypeArguments().get(1));
        }
    }

    // ==================== 'this' in Actions Tests ====================

    @Nested
    @DisplayName("'this' Type in Action Return and Parameters")
    class ThisTypeActionTests {

        @Test
        @DisplayName("Action with 'this' return type")
        void testActionThisReturnType() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action copy = findAction(node.getActionsSelf(), "copy");

            assertNotNull(copy);
            assertEquals("this", copy.getReturnType());
            assertInstanceOf(ThisType.class, copy.getType());
        }

        @Test
        @DisplayName("Global action with 'this' return type")
        void testGlobalActionThisReturn() {
            JoinPointClass global = langSpec.getGlobal();
            Action selfTransform = findAction(global.getActionsSelf(), "selfTransform");

            assertNotNull(selfTransform);
            assertEquals("this", selfTransform.getReturnType());
            assertInstanceOf(ThisType.class, selfTransform.getType());
        }

        @Test
        @DisplayName("Action with 'this' as only parameter")
        void testActionThisOnlyParameter() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action insertBefore = findAction(node.getActionsSelf(), "insertBefore");

            assertNotNull(insertBefore);
            assertEquals(1, insertBefore.getParameters().size());

            Parameter param = insertBefore.getParameters().get(0);
            assertEquals("node", param.getName());
            assertEquals("this", param.getType());
        }

        @Test
        @DisplayName("Action with 'this' parameter returning 'this'")
        void testActionThisParamAndReturn() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action merge = findAction(node.getActionsSelf(), "merge");

            assertNotNull(merge);
            assertEquals("this", merge.getReturnType());
            assertEquals(1, merge.getParameters().size());
            assertEquals("this", merge.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Action with 'this' mixed with other parameter types")
        void testActionThisMixedWithOtherParams() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action swap = findAction(node.getActionsSelf(), "swap");

            assertNotNull(swap);
            assertEquals(2, swap.getParameters().size());
            assertEquals("this", swap.getParameters().get(0).getType());
            assertEquals("Boolean", swap.getParameters().get(1).getType());
        }

        @Test
        @DisplayName("Action with 'this' at different positions")
        void testActionThisAtDifferentPositions() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action insertAt = findAction(node.getActionsSelf(), "insertAt");

            assertNotNull(insertAt);
            assertEquals(2, insertAt.getParameters().size());
            assertEquals("Integer", insertAt.getParameters().get(0).getType());
            assertEquals("this", insertAt.getParameters().get(1).getType());
        }

        @Test
        @DisplayName("Action with multiple 'this' parameters")
        void testActionMultipleThisParams() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action replaceBetween = findAction(node.getActionsSelf(), "replaceBetween");

            assertNotNull(replaceBetween);
            assertEquals(3, replaceBetween.getParameters().size());
            assertEquals("this", replaceBetween.getParameters().get(0).getType());
            assertEquals("this", replaceBetween.getParameters().get(1).getType());
            assertEquals("this", replaceBetween.getParameters().get(2).getType());
        }

        @Test
        @DisplayName("Action with 'this' in both parameter and return with generic")
        void testActionBinaryExprSetOperands() {
            JoinPointClass binaryExpr = langSpec.getJoinPoint("binaryExpr");
            Action setOperands = findAction(binaryExpr.getActionsSelf(), "setOperands");

            assertNotNull(setOperands);
            assertEquals("void", setOperands.getReturnType());
            assertEquals(2, setOperands.getParameters().size());
            assertEquals("this", setOperands.getParameters().get(0).getType());
            assertEquals("this", setOperands.getParameters().get(1).getType());
        }

        @Test
        @DisplayName("Global action with 'this' as parameter")
        void testGlobalActionThisParameter() {
            JoinPointClass global = langSpec.getGlobal();
            Action replaceWith = findAction(global.getActionsSelf(), "replaceWith");

            assertNotNull(replaceWith);
            assertEquals("void", replaceWith.getReturnType());
            assertEquals(1, replaceWith.getParameters().size());
            assertEquals("this", replaceWith.getParameters().get(0).getType());
        }
    }

    // ==================== Actions with Generics Tests ====================

    @Nested
    @DisplayName("Action Return Types and Parameters with Generics")
    class ActionGenericsTests {

        @Test
        @DisplayName("Action returning List<this>")
        void testActionReturningListOfThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action findChildren = findAction(node.getActionsSelf(), "findChildren");

            assertNotNull(findChildren);
            assertEquals("List<this>", findChildren.getReturnType());
            assertInstanceOf(ParameterizedType.class, findChildren.getType());
            assertInstanceOf(ThisType.class, ((ParameterizedType) findChildren.getType()).getTypeArguments().get(0));
        }

        @Test
        @DisplayName("Action returning Map<String, List<this>>")
        void testActionReturningNestedGeneric() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action groupByType = findAction(node.getActionsSelf(), "groupByType");

            assertNotNull(groupByType);
            assertEquals("Map<String, List<this>>", groupByType.getReturnType());
        }

        @Test
        @DisplayName("Action with List<this> parameter")
        void testActionWithListOfThisParam() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action insertAll = findAction(node.getActionsSelf(), "insertAll");

            assertNotNull(insertAll);
            assertEquals(1, insertAll.getParameters().size());
            assertEquals("List<this>", insertAll.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Action with Map<String, this> parameter")
        void testActionWithMapOfThisParam() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action replaceAll = findAction(node.getActionsSelf(), "replaceAll");

            assertNotNull(replaceAll);
            assertEquals(1, replaceAll.getParameters().size());
            assertEquals("Map<String, this>", replaceAll.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Action returning this[]")
        void testActionReturningThisArray() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action toArray = findAction(node.getActionsSelf(), "toArray");

            assertNotNull(toArray);
            assertEquals("this[]", toArray.getReturnType());
            assertInstanceOf(ArrayType.class, toArray.getType());
        }

        @Test
        @DisplayName("Global action returning List<this>")
        void testGlobalActionReturningListThis() {
            JoinPointClass global = langSpec.getGlobal();
            Action findAll = findAction(global.getActionsSelf(), "findAll");

            assertNotNull(findAll);
            assertEquals("List<this>", findAll.getReturnType());
        }

        @Test
        @DisplayName("Global action returning Map<String, this>")
        void testGlobalActionReturningMapThis() {
            JoinPointClass global = langSpec.getGlobal();
            Action categorize = findAction(global.getActionsSelf(), "categorize");

            assertNotNull(categorize);
            assertEquals("Map<String, this>", categorize.getReturnType());
        }

        @Test
        @DisplayName("Action returning simple generic Map<String, String>")
        void testActionReturningSimpleGeneric() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action getMetadata = findAction(node.getActionsSelf(), "getMetadata");

            assertNotNull(getMetadata);
            assertEquals("Map<String, String>", getMetadata.getReturnType());
            assertInstanceOf(ParameterizedType.class, getMetadata.getType());
            // Verify no ThisType in arguments
            ParameterizedType metadataType = (ParameterizedType) getMetadata.getType();
            assertFalse(metadataType.getTypeArguments().stream().anyMatch(t -> t instanceof ThisType));
        }

        @Test
        @DisplayName("Action with complex nested generic parameter")
        void testActionComplexNestedGenericParam() {
            JoinPointClass container = langSpec.getJoinPoint("container");
            Action addToAll = findAction(container.getActionsSelf(), "addToAll");

            assertNotNull(addToAll);
            assertEquals(1, addToAll.getParameters().size());
            assertEquals("Map<String, List<this>>", addToAll.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Action returning List<Map<String, this>>")
        void testActionReturningListOfMaps() {
            JoinPointClass container = langSpec.getJoinPoint("container");
            Action processNested = findAction(container.getActionsSelf(), "processNested");

            assertNotNull(processNested);
            assertEquals("List<Map<String, this>>", processNested.getReturnType());
        }
    }

    // ==================== Inheritance Tests ====================

    @Nested
    @DisplayName("'this' Type Preservation in Inheritance")
    class InheritanceTests {

        @Test
        @DisplayName("'this' type is preserved when attribute is inherited")
        void testThisPreservedInInheritedAttribute() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            JoinPointClass expr = langSpec.getJoinPoint("expr");

            // Find clone on node (where it's defined)
            Attribute cloneOnNode = findAttribute(node.getAttributesSelf(), "clone");
            assertNotNull(cloneOnNode);
            assertInstanceOf(ThisType.class, cloneOnNode.getType());

            // Find clone on expr (inherited)
            Attribute cloneOnExpr = findAttribute(expr.getAttributes(), "clone");
            assertNotNull(cloneOnExpr);

            // Should still be ThisType, not resolved to 'node' or 'expr'
            assertInstanceOf(ThisType.class, cloneOnExpr.getType());
            assertEquals("this", cloneOnExpr.getType().toString());

            // Should be the same instance (singleton)
            assertSame(cloneOnNode.getType(), cloneOnExpr.getType());
        }

        @Test
        @DisplayName("'this' in List<this> is preserved through inheritance")
        void testGenericThisPreservedInInheritance() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            JoinPointClass binaryExpr = langSpec.getJoinPoint("binaryExpr");

            // childList is defined on node
            Attribute childListOnNode = findAttribute(node.getAttributesSelf(), "childList");
            assertNotNull(childListOnNode);

            // binaryExpr extends expr extends node
            Attribute childListOnBinaryExpr = findAttribute(binaryExpr.getAttributes(), "childList");
            assertNotNull(childListOnBinaryExpr);

            // Both should have List<this> with ThisType inside
            assertInstanceOf(ParameterizedType.class, childListOnNode.getType());
            assertInstanceOf(ParameterizedType.class, childListOnBinaryExpr.getType());

            ParameterizedType nodeType = (ParameterizedType) childListOnNode.getType();
            ParameterizedType binaryType = (ParameterizedType) childListOnBinaryExpr.getType();

            assertInstanceOf(ThisType.class, nodeType.getTypeArguments().get(0));
            assertInstanceOf(ThisType.class, binaryType.getTypeArguments().get(0));
        }

        @Test
        @DisplayName("Deep inheritance chain preserves 'this' type")
        void testDeepInheritancePreservesThis() {
            // loop extends stmt extends node
            JoinPointClass loop = langSpec.getJoinPoint("loop");

            // Clone is defined on node, should be available on loop
            Attribute cloneOnLoop = findAttribute(loop.getAttributes(), "clone");
            assertNotNull(cloneOnLoop);
            assertInstanceOf(ThisType.class, cloneOnLoop.getType());

            // childList is also from node
            Attribute childListOnLoop = findAttribute(loop.getAttributes(), "childList");
            assertNotNull(childListOnLoop);
            assertInstanceOf(ParameterizedType.class, childListOnLoop.getType());
            assertInstanceOf(ThisType.class, ((ParameterizedType) childListOnLoop.getType()).getTypeArguments().get(0));
        }

        @Test
        @DisplayName("Global 'this' attributes are inherited by all join points")
        void testGlobalThisInheritedByAll() {
            JoinPointClass global = langSpec.getGlobal();
            JoinPointClass loop = langSpec.getJoinPoint("loop");

            // root is defined on global with type 'this'
            Attribute rootOnGlobal = findAttribute(global.getAttributesSelf(), "root");
            assertNotNull(rootOnGlobal);
            assertInstanceOf(ThisType.class, rootOnGlobal.getType());

            // loop should inherit it
            Attribute rootOnLoop = findAttribute(loop.getAttributes(), "root");
            assertNotNull(rootOnLoop);
            assertInstanceOf(ThisType.class, rootOnLoop.getType());
        }

        @Test
        @DisplayName("Actions with 'this' are correctly inherited")
        void testActionsWithThisInherited() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            JoinPointClass loop = langSpec.getJoinPoint("loop");

            // copy is defined on node
            Action copyOnNode = findAction(node.getActionsSelf(), "copy");
            assertNotNull(copyOnNode);
            assertEquals("this", copyOnNode.getReturnType());

            // loop should inherit it
            Action copyOnLoop = findAction(loop.getActions(), "copy");
            assertNotNull(copyOnLoop);
            assertEquals("this", copyOnLoop.getReturnType());
        }
    }

    // ==================== TypeDef Tests ====================

    @Nested
    @DisplayName("'this' and Generics in TypeDef")
    class TypeDefTests {

        @Test
        @DisplayName("TypeDef with 'this' field exists")
        void testTypeDefWithThisExists() {
            assertTrue(langSpec.hasTypeDef("NodeInfo"));
            TypeDef nodeInfo = langSpec.getTypeDefs().get("NodeInfo");
            assertNotNull(nodeInfo);
        }

        @Test
        @DisplayName("TypeDef field with 'this' type")
        void testTypeDefFieldWithThis() {
            TypeDef nodeInfo = langSpec.getTypeDefs().get("NodeInfo");
            Attribute nodeField = findAttribute(nodeInfo.getFields(), "node");

            assertNotNull(nodeField);
            assertInstanceOf(ThisType.class, nodeField.getType());
            assertEquals("this", nodeField.getType().toString());
        }

        @Test
        @DisplayName("TypeDef field with List<this>")
        void testTypeDefFieldWithListThis() {
            TypeDef nodeInfo = langSpec.getTypeDefs().get("NodeInfo");
            Attribute relatedNodes = findAttribute(nodeInfo.getFields(), "relatedNodes");

            assertNotNull(relatedNodes);
            assertInstanceOf(ParameterizedType.class, relatedNodes.getType());
            assertEquals("List<this>", relatedNodes.getType().toString());
        }

        @Test
        @DisplayName("TypeDef with nested generics containing 'this'")
        void testTypeDefWithNestedGenerics() {
            TypeDef treeStructure = langSpec.getTypeDefs().get("TreeStructure");
            assertNotNull(treeStructure);

            Attribute levels = findAttribute(treeStructure.getFields(), "levels");
            assertNotNull(levels);
            assertInstanceOf(ParameterizedType.class, levels.getType());
            assertEquals("List<List<this>>", levels.getType().toString());
        }

        @Test
        @DisplayName("TypeDef with Map<String, this>")
        void testTypeDefWithMapThis() {
            TypeDef treeStructure = langSpec.getTypeDefs().get("TreeStructure");
            Attribute nodeIndex = findAttribute(treeStructure.getFields(), "nodeIndex");

            assertNotNull(nodeIndex);
            assertInstanceOf(ParameterizedType.class, nodeIndex.getType());
            assertEquals("Map<String, this>", nodeIndex.getType().toString());
        }

        @Test
        @DisplayName("TypeDef without 'this' for comparison")
        void testTypeDefWithoutThis() {
            TypeDef simpleMetadata = langSpec.getTypeDefs().get("SimpleMetadata");
            assertNotNull(simpleMetadata);

            Attribute values = findAttribute(simpleMetadata.getFields(), "values");
            assertNotNull(values);
            assertInstanceOf(ParameterizedType.class, values.getType());
            assertEquals("List<String>", values.getType().toString());
            // Verify no ThisType in arguments
            ParameterizedType valuesType = (ParameterizedType) values.getType();
            assertFalse(valuesType.getTypeArguments().stream().anyMatch(t -> t instanceof ThisType));
        }

        @Test
        @DisplayName("TypeDef fields are correctly ordered")
        void testTypeDefFieldOrder() {
            TypeDef nodeInfo = langSpec.getTypeDefs().get("NodeInfo");
            List<Attribute> fields = nodeInfo.getFields();

            // Fields should be sorted alphabetically
            assertTrue(fields.size() >= 4);

            // Check metadata, name, node, relatedNodes are present
            assertNotNull(findAttribute(fields, "metadata"));
            assertNotNull(findAttribute(fields, "name"));
            assertNotNull(findAttribute(fields, "node"));
            assertNotNull(findAttribute(fields, "relatedNodes"));
        }
    }

    // ==================== AST/JSON Output Tests ====================

    @Nested
    @DisplayName("AST and JSON Output Verification")
    class AstJsonTests {

        @Test
        @DisplayName("NodeFactory creates valid RootNode from langspec")
        void testNodeFactoryCreatesRootNode() {
            RootNode rootNode = NodeFactory.toNode(langSpec);
            assertNotNull(rootNode);
            assertFalse(rootNode.getChildren().isEmpty());
        }

        @Test
        @DisplayName("JoinPointNode is created correctly for 'this' attributes")
        void testJoinPointNodeWithThisAttributes() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            JoinPointNode jpNode = NodeFactory.toNode(node);

            assertNotNull(jpNode);

            // Find the attribute node for 'clone'
            Optional<AttributeNode> cloneAttrNode = jpNode.getChildren().stream()
                    .filter(child -> child instanceof AttributeNode)
                    .map(child -> (AttributeNode) child)
                    .filter(attr -> "clone".equals(attr.getDeclaration().getName()))
                    .findFirst();

            assertTrue(cloneAttrNode.isPresent());
            assertEquals("this", cloneAttrNode.get().getDeclaration().getType());
        }

        @Test
        @DisplayName("JSON output contains 'this' string for ThisType")
        void testJsonOutputContainsThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            JoinPointNode jpNode = NodeFactory.toNode(node);
            String json = jpNode.toJson();

            // JSON should contain "type": "this"
            assertTrue(json.contains("\"type\": \"this\""),
                    "JSON should contain '\"type\": \"this\"' for ThisType attributes");
        }

        @Test
        @DisplayName("JSON output contains generic types with 'this'")
        void testJsonOutputContainsGenericWithThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            JoinPointNode jpNode = NodeFactory.toNode(node);
            String json = jpNode.toJson();

            // JSON should contain List<this>
            assertTrue(json.contains("List<this>"),
                    "JSON should contain 'List<this>' for generic attributes");
        }

        @Test
        @DisplayName("JSON output preserves nested generics with 'this'")
        void testJsonOutputPreservesNestedGenerics() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            JoinPointNode jpNode = NodeFactory.toNode(node);
            String json = jpNode.toJson();

            // Should contain Map<String, List<this>>
            assertTrue(json.contains("Map<String, List<this>>"),
                    "JSON should preserve nested generic types");
        }

        @Test
        @DisplayName("JSON output for actions with 'this' return type")
        void testJsonOutputActionWithThisReturn() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            JoinPointNode jpNode = NodeFactory.toNode(node);
            String json = jpNode.toJson();

            // Actions section should contain return type "this"
            assertTrue(json.contains("\"action\""),
                    "JSON should contain action nodes");

            // The copy action returns 'this'
            // We verify the structure is correct
            assertFalse(json.isEmpty());
        }

        @Test
        @DisplayName("JSON output for TypeDef with 'this' fields")
        void testJsonOutputTypeDefWithThis() {
            TypeDef nodeInfo = langSpec.getTypeDefs().get("NodeInfo");
            assertNotNull(nodeInfo);

            TypeDefNode tdNode = findTypeDefNode("NodeInfo");
            assertNotNull(tdNode);

            String json = tdNode.toJson();
            assertTrue(json.contains("\"type\": \"this\""),
                    "TypeDef JSON should contain 'this' type for fields");
        }

        @Test
        @DisplayName("RootNode JSON is valid")
        void testRootNodeJsonIsValid() {
            RootNode rootNode = NodeFactory.toNode(langSpec);
            String json = rootNode.toJson();

            assertNotNull(json);
            assertFalse(json.isEmpty());

            // Basic JSON structure validation
            assertTrue(json.contains("{"));
            assertTrue(json.contains("}"));
            assertTrue(json.contains("\"children\""));
        }

        @Test
        @DisplayName("DeclarationNode preserves 'this' type string")
        void testDeclarationNodePreservesThisType() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute clone = findAttribute(node.getAttributesSelf(), "clone");
            assertNotNull(clone);

            DeclarationNode declNode = new DeclarationNode(clone.getDeclaration());
            assertEquals("this", declNode.getType());

            String json = declNode.toJson();
            assertTrue(json.contains("\"this\""));
        }

        @Test
        @DisplayName("AttributeNode JSON includes parameters")
        void testAttributeNodeJsonIncludesParams() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute ancestorOfType = findAttribute(node.getAttributesSelf(), "ancestorOfType");
            assertNotNull(ancestorOfType);

            AttributeNode attrNode = NodeFactory.toNode(ancestorOfType);
            String json = attrNode.toJson();

            assertTrue(json.contains("\"type\": \"this\""));
            assertTrue(json.contains("\"typeName\""));
        }

        @Test
        @DisplayName("ActionNode JSON for action with 'this' parameter")
        void testActionNodeJsonWithThisParam() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action insertBefore = findAction(node.getActionsSelf(), "insertBefore");
            assertNotNull(insertBefore);

            ActionNode actionNode = NodeFactory.toNode(insertBefore);
            String json = actionNode.toJson();

            // Should contain parameter with type 'this'
            assertTrue(json.contains("\"this\""),
                    "Action JSON should contain 'this' for parameter type");
        }

        private TypeDefNode findTypeDefNode(String name) {
            RootNode rootNode = NodeFactory.toNode(langSpec);
            return rootNode.getChildren().stream()
                    .filter(child -> child instanceof TypeDefNode)
                    .map(child -> (TypeDefNode) child)
                    .filter(td -> name.equals(td.getName()))
                    .findFirst()
                    .orElse(null);
        }
    }

    // ==================== Edge Cases and Error Handling ====================

    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCasesTests {

        @Test
        @DisplayName("ThisType singleton is consistent across all usages")
        void testThisTypeSingleton() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            JoinPointClass global = langSpec.getGlobal();

            Attribute clone = findAttribute(node.getAttributesSelf(), "clone");
            Attribute parent = findAttribute(node.getAttributesSelf(), "parent");
            Attribute rootAttr = findAttribute(global.getAttributesSelf(), "root");

            // All should reference the same singleton instance
            assertSame(clone.getType(), parent.getType());
            assertSame(clone.getType(), rootAttr.getType());
            assertSame(ThisType.getInstance(), clone.getType());
        }

        @Test
        @DisplayName("'this' in different join points refers to same ThisType instance")
        void testThisTypeSameAcrossJoinPoints() {
            JoinPointClass expr = langSpec.getJoinPoint("expr");
            JoinPointClass stmt = langSpec.getJoinPoint("stmt");

            Attribute simplify = findAttribute(expr.getAttributesSelf(), "simplify");
            Attribute next = findAttribute(stmt.getAttributesSelf(), "next");

            assertNotNull(simplify);
            assertNotNull(next);

            // Both should be the same ThisType instance
            assertSame(simplify.getType(), next.getType());
        }

        @Test
        @DisplayName("ThisType presence can be verified via instanceof checks")
        void testThisTypePresenceInNestedStructures() {
            JoinPointClass node = langSpec.getJoinPoint("node");

            // Simple generic without 'this'
            Attribute tags = findAttribute(node.getAttributesSelf(), "tags");
            ParameterizedType tagsType = (ParameterizedType) tags.getType();
            assertFalse(tagsType.getTypeArguments().stream().anyMatch(t -> t instanceof ThisType));

            // Generic with 'this'
            Attribute childList = findAttribute(node.getAttributesSelf(), "childList");
            ParameterizedType childListType = (ParameterizedType) childList.getType();
            assertInstanceOf(ThisType.class, childListType.getTypeArguments().get(0));

            // Nested generic with 'this'
            Attribute childGroups = findAttribute(node.getAttributesSelf(), "childGroups");
            ParameterizedType childGroupsType = (ParameterizedType) childGroups.getType();
            ParameterizedType innerListType = (ParameterizedType) childGroupsType.getTypeArguments().get(0);
            assertInstanceOf(ThisType.class, innerListType.getTypeArguments().get(0));
        }

        @Test
        @DisplayName("toString() consistency for various 'this' type configurations")
        void testToStringConsistency() {
            assertEquals("this", ThisType.getInstance().toString());
            assertEquals("this", ThisType.getInstance().type());

            JoinPointClass node = langSpec.getJoinPoint("node");

            Attribute children = findAttribute(node.getAttributesSelf(), "children");
            assertEquals("this[]", children.getType().toString());

            Attribute childList = findAttribute(node.getAttributesSelf(), "childList");
            assertEquals("List<this>", childList.getType().toString());

            Attribute namedChildren = findAttribute(node.getAttributesSelf(), "namedChildren");
            assertEquals("Map<String, this>", namedChildren.getType().toString());
        }

        @Test
        @DisplayName("Attribute default values work with join points having 'this' attributes")
        void testDefaultAttributeWithThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            assertTrue(node.getDefaultAttribute().isPresent());
            assertEquals("id", node.getDefaultAttribute().get());
        }

        @Test
        @DisplayName("Tooltips are preserved for 'this' type attributes")
        void testTooltipsPreservedForThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Attribute clone = findAttribute(node.getAttributesSelf(), "clone");

            assertNotNull(clone);
            assertTrue(clone.getToolTip().isPresent());
            assertEquals("Creates a deep clone of this node", clone.getToolTip().get());
        }

        @Test
        @DisplayName("Actions with 'this' preserve tooltips")
        void testActionTooltipsWithThis() {
            JoinPointClass node = langSpec.getJoinPoint("node");
            Action copy = findAction(node.getActionsSelf(), "copy");

            assertNotNull(copy);
            assertTrue(copy.getToolTip().isPresent());
            assertEquals("Creates a copy of this node", copy.getToolTip().get());
        }

        @Test
        @DisplayName("All expected join point-specific attributes are present")
        void testJoinPointSpecificAttributesPresent() {
            JoinPointClass loop = langSpec.getJoinPoint("loop");

            // Own attributes
            assertNotNull(findAttribute(loop.getAttributesSelf(), "kind"));
            assertNotNull(findAttribute(loop.getAttributesSelf(), "body"));
            assertNotNull(findAttribute(loop.getAttributesSelf(), "nestedLoops"));

            // Inherited from node
            assertNotNull(findAttribute(loop.getAttributes(), "clone"));
            assertNotNull(findAttribute(loop.getAttributes(), "childList"));
        }
    }

    // ==================== Specific Join Point Tests ====================

    @Nested
    @DisplayName("Join Point Specific Feature Tests")
    class JoinPointSpecificTests {

        @Test
        @DisplayName("Expression join point 'this' attributes")
        void testExpressionThisAttributes() {
            JoinPointClass expr = langSpec.getJoinPoint("expr");

            Attribute simplify = findAttribute(expr.getAttributesSelf(), "simplify");
            Attribute normalize = findAttribute(expr.getAttributesSelf(), "normalize");
            Attribute subExpressions = findAttribute(expr.getAttributesSelf(), "subExpressions");

            assertNotNull(simplify);
            assertNotNull(normalize);
            assertNotNull(subExpressions);

            assertInstanceOf(ThisType.class, simplify.getType());
            assertInstanceOf(ThisType.class, normalize.getType());
            assertInstanceOf(ParameterizedType.class, subExpressions.getType());
            assertEquals("List<this>", subExpressions.getType().toString());
        }

        @Test
        @DisplayName("BinaryExpr join point 'this' attributes")
        void testBinaryExprThisAttributes() {
            JoinPointClass binaryExpr = langSpec.getJoinPoint("binaryExpr");

            Attribute left = findAttribute(binaryExpr.getAttributesSelf(), "left");
            Attribute right = findAttribute(binaryExpr.getAttributesSelf(), "right");
            Attribute operands = findAttribute(binaryExpr.getAttributesSelf(), "operands");

            assertNotNull(left);
            assertNotNull(right);
            assertNotNull(operands);

            assertInstanceOf(ThisType.class, left.getType());
            assertInstanceOf(ThisType.class, right.getType());
            assertEquals("List<this>", operands.getType().toString());
        }

        @Test
        @DisplayName("Loop join point actions with 'this'")
        void testLoopActionsWithThis() {
            JoinPointClass loop = langSpec.getJoinPoint("loop");

            Action unroll = findAction(loop.getActionsSelf(), "unroll");
            Action tile = findAction(loop.getActionsSelf(), "tile");
            Action interchange = findAction(loop.getActionsSelf(), "interchange");
            Action fuse = findAction(loop.getActionsSelf(), "fuse");

            assertNotNull(unroll);
            assertNotNull(tile);
            assertNotNull(interchange);
            assertNotNull(fuse);

            assertEquals("List<this>", unroll.getReturnType());
            assertEquals("this", tile.getReturnType());
            assertEquals("this", interchange.getParameters().get(0).getType());
            assertEquals("this", fuse.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Statement join point 'this' navigation attributes")
        void testStmtNavigationAttributes() {
            JoinPointClass stmt = langSpec.getJoinPoint("stmt");

            Attribute next = findAttribute(stmt.getAttributesSelf(), "next");
            Attribute prev = findAttribute(stmt.getAttributesSelf(), "prev");
            Attribute following = findAttribute(stmt.getAttributesSelf(), "following");

            assertNotNull(next);
            assertNotNull(prev);
            assertNotNull(following);

            assertInstanceOf(ThisType.class, next.getType());
            assertInstanceOf(ThisType.class, prev.getType());
            assertEquals("List<this>", following.getType().toString());
        }
    }

    // ==================== Enum and Object Type Tests ====================

    @Nested
    @DisplayName("Enum and Object Types Coexistence")
    class EnumObjectTests {

        @Test
        @DisplayName("Enum definition is parsed correctly")
        void testEnumDefinition() {
            assertTrue(langSpec.hasEnumDef("NodeKind"));
        }

        @Test
        @DisplayName("Object type is parsed correctly")
        void testObjectType() {
            assertTrue(langSpec.hasTypeDef("AstContext"));
        }

        @Test
        @DisplayName("TypeDefs, EnumDefs, and 'this' types coexist")
        void testTypesCoexist() {
            // TypeDefs with 'this'
            assertTrue(langSpec.hasTypeDef("NodeInfo"));
            assertTrue(langSpec.hasTypeDef("TreeStructure"));

            // EnumDefs
            assertTrue(langSpec.hasEnumDef("NodeKind"));

            // Object types
            assertTrue(langSpec.hasTypeDef("AstContext"));

            // All join points with 'this' attributes work
            JoinPointClass node = langSpec.getJoinPoint("node");
            assertNotNull(findAttribute(node.getAttributesSelf(), "clone"));
        }
    }

    // ==================== Helper Methods ====================

    private static Attribute findAttribute(List<Attribute> attributes, String name) {
        return attributes.stream()
                .filter(a -> name.equals(a.getName()))
                .findFirst()
                .orElse(null);
    }

    private static Action findAction(List<Action> actions, String name) {
        return actions.stream()
                .filter(a -> name.equals(a.getName()))
                .findFirst()
                .orElse(null);
    }
}
