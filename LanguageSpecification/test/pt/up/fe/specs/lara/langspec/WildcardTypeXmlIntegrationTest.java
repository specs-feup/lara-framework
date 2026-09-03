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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.ParameterizedType;
import org.lara.language.specification.dsl.types.ThisType;

import pt.up.fe.specs.util.providers.ResourceProvider;

/**
 * Comprehensive integration tests for the wildcard type feature in generic types.
 * 
 * <p>Tests cover:
 * <ul>
 *   <li>Basic wildcard types: List&lt;?&gt;, Set&lt;?&gt;</li>
 *   <li>Bounded wildcards: List&lt;? extends this&gt;, List&lt;? super String&gt;</li>
 *   <li>Nested wildcards: Map&lt;?, List&lt;?&gt;&gt;</li>
 *   <li>Wildcard arrays: List&lt;?[]&gt;, Set&lt;? extends this[]&gt;</li>
 *   <li>Multiple wildcards: Map&lt;?, ?&gt;, Map&lt;? extends this, ? super String&gt;</li>
 *   <li>Deeply nested generics with wildcards</li>
 *   <li>Actions returning wildcard types</li>
 *   <li>Actions with wildcard parameters</li>
 *   <li>Combinations of wildcards with 'this' type</li>
 *   <li>Inheritance preservation of wildcard types with 'this'</li>
 * </ul>
 */
@DisplayName("WildcardType XML Integration Tests")
public class WildcardTypeXmlIntegrationTest {
    
    private static final String WILDCARD_BASE_PACKAGE = "pt/up/fe/specs/lara/langspec/wildcards/";

    /**
     * Resource provider enum for wildcard generic test resources.
     */
    public enum WildcardTestResource implements ResourceProvider {
        JOIN_POINT_MODEL("joinPointModel.xml"),
        ATTRIBUTE_MODEL("artifacts.xml"),
        ACTION_MODEL("actionModel.xml");

        private final String resource;

        WildcardTestResource(String resource) {
            this.resource = WILDCARD_BASE_PACKAGE + resource;
        }

        @Override
        public String getResource() {
            return resource;
        }
    }


    private LanguageSpecification parseWildcardSpec() {
        return LangSpecsXmlParser.parse(
                WildcardTestResource.JOIN_POINT_MODEL.toStream(),
                WildcardTestResource.ATTRIBUTE_MODEL.toStream(),
                WildcardTestResource.ACTION_MODEL.toStream(),
                true
        );
    }


    // ==================== Wildcard Generic Types Tests ====================

    @Nested
    @DisplayName("Basic Wildcard Generic Types Parsing")
    class BasicWildcardTests {

        @Test
        @DisplayName("List<?> is parsed correctly")
        void testListWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute wildcardList = findAttribute(node.getAttributesSelf(), "wildcardList");

            assertNotNull(wildcardList);
            assertInstanceOf(ParameterizedType.class, wildcardList.getType());
            ParameterizedType paramType = (ParameterizedType) wildcardList.getType();

            assertEquals("List<?>", paramType.toString());
            assertEquals(1, paramType.getTypeArguments().size());
            assertEquals("?", paramType.getTypeArguments().get(0).toString());
        }

        @Test
        @DisplayName("List<? extends this> is parsed correctly")
        void testListWildcardExtendsThis() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute extendsThisList = findAttribute(node.getAttributesSelf(), "extendsThisList");

            assertNotNull(extendsThisList);
            assertInstanceOf(ParameterizedType.class, extendsThisList.getType());
            ParameterizedType paramType = (ParameterizedType) extendsThisList.getType();

            assertEquals("List<? extends this>", paramType.toString());
            assertEquals(1, paramType.getTypeArguments().size());
            assertEquals("? extends this", paramType.getTypeArguments().get(0).toString());
        }

        @Test
        @DisplayName("Map<String, ?> is parsed correctly")
        void testMapStringWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute mapStringWildcard = findAttribute(node.getAttributesSelf(), "mapStringWildcard");

            assertNotNull(mapStringWildcard);
            assertInstanceOf(ParameterizedType.class, mapStringWildcard.getType());
            ParameterizedType paramType = (ParameterizedType) mapStringWildcard.getType();

            assertEquals("Map<String, ?>", paramType.toString());
            assertEquals(2, paramType.getTypeArguments().size());
            assertEquals("String", paramType.getTypeArguments().get(0).toString());
            assertEquals("?", paramType.getTypeArguments().get(1).toString());
        }

        @Test
        @DisplayName("List<? super String> is parsed correctly")
        void testListWildcardSuperString() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute superStringList = findAttribute(node.getAttributesSelf(), "superStringList");

            assertNotNull(superStringList);
            assertInstanceOf(ParameterizedType.class, superStringList.getType());
            ParameterizedType paramType = (ParameterizedType) superStringList.getType();

            assertEquals("List<? super String>", paramType.toString());
            assertEquals(1, paramType.getTypeArguments().size());
            assertEquals("? super String", paramType.getTypeArguments().get(0).toString());
        }
    }

    // ==================== Nested Wildcards Tests ====================

    @Nested
    @DisplayName("Nested Wildcard Types")
    class NestedWildcardTests {

        @Test
        @DisplayName("Map<?, List<?>> is parsed correctly")
        void testNestedWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute nestedWildcard = findAttribute(node.getAttributesSelf(), "nestedWildcard");

            assertNotNull(nestedWildcard);
            assertInstanceOf(ParameterizedType.class, nestedWildcard.getType());
            ParameterizedType paramType = (ParameterizedType) nestedWildcard.getType();

            assertEquals("Map<?, List<?>>", paramType.toString());
            assertEquals(2, paramType.getTypeArguments().size());
            assertEquals("?", paramType.getTypeArguments().get(0).toString());

            // Inner type should be List<?>
            IType innerType = paramType.getTypeArguments().get(1);
            assertInstanceOf(ParameterizedType.class, innerType);
            ParameterizedType innerParamType = (ParameterizedType) innerType;
            assertEquals("List<?>", innerParamType.toString());
            assertEquals("?", innerParamType.getTypeArguments().get(0).toString());
        }

        @Test
        @DisplayName("List<Map<String, ? extends this>> is parsed correctly")
        void testDeeplyNestedWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute deeplyNestedWildcard = findAttribute(node.getAttributesSelf(), "deeplyNestedWildcard");

            assertNotNull(deeplyNestedWildcard);
            assertInstanceOf(ParameterizedType.class, deeplyNestedWildcard.getType());
            ParameterizedType paramType = (ParameterizedType) deeplyNestedWildcard.getType();

            assertEquals("List<Map<String, ? extends this>>", paramType.toString());

            // Navigate to the innermost type
            ParameterizedType mapType = (ParameterizedType) paramType.getTypeArguments().get(0);
            assertEquals("Map<String, ? extends this>", mapType.toString());
            assertEquals("String", mapType.getTypeArguments().get(0).toString());
            assertEquals("? extends this", mapType.getTypeArguments().get(1).toString());
        }

        @Test
        @DisplayName("Map<String, List<Set<?>>> is parsed correctly (triple nested)")
        void testTripleNestedWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute tripleNested = findAttribute(node.getAttributesSelf(), "tripleNested");

            assertNotNull(tripleNested);
            assertInstanceOf(ParameterizedType.class, tripleNested.getType());
            ParameterizedType paramType = (ParameterizedType) tripleNested.getType();

            assertEquals("Map<String, List<Set<?>>>", paramType.toString());

            // Navigate: Map -> List -> Set -> ?
            ParameterizedType listType = (ParameterizedType) paramType.getTypeArguments().get(1);
            assertEquals("List<Set<?>>", listType.toString());

            ParameterizedType setType = (ParameterizedType) listType.getTypeArguments().get(0);
            assertEquals("Set<?>", setType.toString());
            assertEquals("?", setType.getTypeArguments().get(0).toString());
        }
    }

    // ==================== Wildcard Arrays Tests ====================

    @Nested
    @DisplayName("Wildcard Array Types")
    class WildcardArrayTests {

        @Test
        @DisplayName("List<?[]> is parsed correctly")
        void testWildcardArray() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute wildcardArray = findAttribute(node.getAttributesSelf(), "wildcardArray");

            assertNotNull(wildcardArray);
            assertInstanceOf(ParameterizedType.class, wildcardArray.getType());
            ParameterizedType paramType = (ParameterizedType) wildcardArray.getType();

            assertEquals("List<?[]>", paramType.toString());
            assertEquals(1, paramType.getTypeArguments().size());

            // The type argument should be an array of wildcards
            IType typeArg = paramType.getTypeArguments().get(0);
            assertInstanceOf(ArrayType.class, typeArg);
            assertEquals("?[]", typeArg.toString());
        }

        @Test
        @DisplayName("List<? extends this[]> is parsed correctly")
        void testExtendsThisArray() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute extendsThisArray = findAttribute(node.getAttributesSelf(), "extendsThisArray");

            assertNotNull(extendsThisArray);
            assertInstanceOf(ParameterizedType.class, extendsThisArray.getType());
            ParameterizedType paramType = (ParameterizedType) extendsThisArray.getType();

            assertEquals("List<? extends this[]>", paramType.toString());
        }

        @Test
        @DisplayName("Set<? super String[]> is parsed correctly")
        void testSuperStringArray() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute superStringArray = findAttribute(node.getAttributesSelf(), "superStringArray");

            assertNotNull(superStringArray);
            assertInstanceOf(ParameterizedType.class, superStringArray.getType());
            ParameterizedType paramType = (ParameterizedType) superStringArray.getType();

            assertEquals("Set<? super String[]>", paramType.toString());
        }
    }

    // ==================== Multiple Wildcards Tests ====================

    @Nested
    @DisplayName("Multiple Wildcards in Single Type")
    class MultipleWildcardsTests {

        @Test
        @DisplayName("Map<?, ?> is parsed correctly")
        void testMapMultipleWildcards() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute multipleWildcards = findAttribute(node.getAttributesSelf(), "multipleWildcards");

            assertNotNull(multipleWildcards);
            assertInstanceOf(ParameterizedType.class, multipleWildcards.getType());
            ParameterizedType paramType = (ParameterizedType) multipleWildcards.getType();

            assertEquals("Map<?, ?>", paramType.toString());
            assertEquals(2, paramType.getTypeArguments().size());
            assertEquals("?", paramType.getTypeArguments().get(0).toString());
            assertEquals("?", paramType.getTypeArguments().get(1).toString());
        }

        @Test
        @DisplayName("Map<? extends this, ? super String> is parsed correctly")
        void testMapMixedWildcards() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute mixedWildcards = findAttribute(node.getAttributesSelf(), "mixedWildcards");

            assertNotNull(mixedWildcards);
            assertInstanceOf(ParameterizedType.class, mixedWildcards.getType());
            ParameterizedType paramType = (ParameterizedType) mixedWildcards.getType();

            assertEquals("Map<? extends this, ? super String>", paramType.toString());
            assertEquals(2, paramType.getTypeArguments().size());
            assertEquals("? extends this", paramType.getTypeArguments().get(0).toString());
            assertEquals("? super String", paramType.getTypeArguments().get(1).toString());
        }
    }

    // ==================== Wildcards with 'this' Type Tests ====================

    @Nested
    @DisplayName("Wildcards Combined with 'this' Type")
    class WildcardsWithThisTests {

        @Test
        @DisplayName("Set<? extends this> is parsed correctly")
        void testSetExtendsThis() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute setExtendsThis = findAttribute(node.getAttributesSelf(), "setExtendsThis");

            assertNotNull(setExtendsThis);
            assertInstanceOf(ParameterizedType.class, setExtendsThis.getType());
            ParameterizedType paramType = (ParameterizedType) setExtendsThis.getType();

            assertEquals("Set<? extends this>", paramType.toString());
            assertEquals("? extends this", paramType.getTypeArguments().get(0).toString());
        }

        @Test
        @DisplayName("Map<this, ? super String> combines 'this' and wildcard")
        void testMapThisWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute mapThisWildcard = findAttribute(node.getAttributesSelf(), "mapThisWildcard");

            assertNotNull(mapThisWildcard);
            assertInstanceOf(ParameterizedType.class, mapThisWildcard.getType());
            ParameterizedType paramType = (ParameterizedType) mapThisWildcard.getType();

            assertEquals("Map<this, ? super String>", paramType.toString());
            assertEquals(2, paramType.getTypeArguments().size());
            assertInstanceOf(ThisType.class, paramType.getTypeArguments().get(0));
            assertEquals("? super String", paramType.getTypeArguments().get(1).toString());
        }

        @Test
        @DisplayName("Map<? extends String, this> combines wildcard and 'this'")
        void testMapWildcardThis() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute mapWildcardThis = findAttribute(node.getAttributesSelf(), "mapWildcardThis");

            assertNotNull(mapWildcardThis);
            assertInstanceOf(ParameterizedType.class, mapWildcardThis.getType());
            ParameterizedType paramType = (ParameterizedType) mapWildcardThis.getType();

            assertEquals("Map<? extends String, this>", paramType.toString());
            assertEquals(2, paramType.getTypeArguments().size());
            assertEquals("? extends String", paramType.getTypeArguments().get(0).toString());
            assertInstanceOf(ThisType.class, paramType.getTypeArguments().get(1));
        }

        @Test
        @DisplayName("Map<? extends this, List<? super String>> combines complex types")
        void testComplexNestedWildcardWithThis() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute complexNested = findAttribute(node.getAttributesSelf(), "complexNested");

            assertNotNull(complexNested);
            assertInstanceOf(ParameterizedType.class, complexNested.getType());
            ParameterizedType paramType = (ParameterizedType) complexNested.getType();

            assertEquals("Map<? extends this, List<? super String>>", paramType.toString());

            // First arg: ? extends this
            assertEquals("? extends this", paramType.getTypeArguments().get(0).toString());

            // Second arg: List<? super String>
            ParameterizedType listType = (ParameterizedType) paramType.getTypeArguments().get(1);
            assertEquals("List<? super String>", listType.toString());
            assertEquals("? super String", listType.getTypeArguments().get(0).toString());
        }

        @Test
        @DisplayName("List<? extends List<this>> has wildcard with nested 'this'")
        void testWildcardOfThis() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute wildcardOfThis = findAttribute(node.getAttributesSelf(), "wildcardOfThis");

            assertNotNull(wildcardOfThis);
            assertInstanceOf(ParameterizedType.class, wildcardOfThis.getType());
            ParameterizedType paramType = (ParameterizedType) wildcardOfThis.getType();

            assertEquals("List<? extends List<this>>", paramType.toString());
        }
    }

    // ==================== Actions Returning Wildcard Types ====================

    @Nested
    @DisplayName("Actions Returning Wildcard Types")
    class ActionsReturningWildcardTests {

        @Test
        @DisplayName("Action returning List<?>")
        void testActionReturningListWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action getWildcardList = findAction(node.getActionsSelf(), "getWildcardList");

            assertNotNull(getWildcardList);
            assertEquals("List<?>", getWildcardList.getReturnType());
            assertInstanceOf(ParameterizedType.class, getWildcardList.getType());
            ParameterizedType paramType = (ParameterizedType) getWildcardList.getType();
            assertEquals("?", paramType.getTypeArguments().get(0).toString());
        }

        @Test
        @DisplayName("Action returning Set<? extends this>")
        void testActionReturningExtendsThis() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action getExtendsThis = findAction(node.getActionsSelf(), "getExtendsThis");

            assertNotNull(getExtendsThis);
            assertEquals("Set<? extends this>", getExtendsThis.getReturnType());
            assertInstanceOf(ParameterizedType.class, getExtendsThis.getType());
        }

        @Test
        @DisplayName("Action returning List<? super String>")
        void testActionReturningSuperString() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action getSuperString = findAction(node.getActionsSelf(), "getSuperString");

            assertNotNull(getSuperString);
            assertEquals("List<? super String>", getSuperString.getReturnType());
        }

        @Test
        @DisplayName("Action returning nested wildcard Map<?, List<?>>")
        void testActionReturningNestedWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action getNestedWildcard = findAction(node.getActionsSelf(), "getNestedWildcard");

            assertNotNull(getNestedWildcard);
            assertEquals("Map<?, List<?>>", getNestedWildcard.getReturnType());
        }

        @Test
        @DisplayName("Action returning deeply nested List<Map<String, ? extends this>>")
        void testActionReturningDeeplyNested() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action getDeeplyNested = findAction(node.getActionsSelf(), "getDeeplyNested");

            assertNotNull(getDeeplyNested);
            assertEquals("List<Map<String, ? extends this>>", getDeeplyNested.getReturnType());
        }

        @Test
        @DisplayName("Action returning List<?[]> (wildcard array)")
        void testActionReturningWildcardArray() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action getWildcardArray = findAction(node.getActionsSelf(), "getWildcardArray");

            assertNotNull(getWildcardArray);
            assertEquals("List<?[]>", getWildcardArray.getReturnType());
        }

        @Test
        @DisplayName("Action returning Set<? extends this[]>")
        void testActionReturningExtendsThisArray() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action getExtendsThisArray = findAction(node.getActionsSelf(), "getExtendsThisArray");

            assertNotNull(getExtendsThisArray);
            assertEquals("Set<? extends this[]>", getExtendsThisArray.getReturnType());
        }

        @Test
        @DisplayName("Action returning Map<?, ?>")
        void testActionReturningMultiWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action getMultiWildcard = findAction(node.getActionsSelf(), "getMultiWildcard");

            assertNotNull(getMultiWildcard);
            assertEquals("Map<?, ?>", getMultiWildcard.getReturnType());
        }

        @Test
        @DisplayName("Action returning Map<? extends this, ? super String>")
        void testActionReturningMixedWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action getMixedWildcard = findAction(node.getActionsSelf(), "getMixedWildcard");

            assertNotNull(getMixedWildcard);
            assertEquals("Map<? extends this, ? super String>", getMixedWildcard.getReturnType());
        }
    }

    // ==================== Actions with Wildcard Parameters ====================

    @Nested
    @DisplayName("Actions with Wildcard Parameters")
    class ActionsWithWildcardParamsTests {

        @Test
        @DisplayName("Action with List<?> parameter")
        void testActionWithListWildcardParam() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action processWildcardList = findAction(node.getActionsSelf(), "processWildcardList");

            assertNotNull(processWildcardList);
            assertEquals("void", processWildcardList.getReturnType());
            assertEquals(1, processWildcardList.getParameters().size());

            Parameter param = processWildcardList.getParameters().get(0);
            assertEquals("items", param.getName());
            assertEquals("List<?>", param.getType());
        }

        @Test
        @DisplayName("Action with Set<? extends this> parameter")
        void testActionWithExtendsThisParam() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action processExtendsThis = findAction(node.getActionsSelf(), "processExtendsThis");

            assertNotNull(processExtendsThis);
            assertEquals(1, processExtendsThis.getParameters().size());
            assertEquals("Set<? extends this>", processExtendsThis.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Action with List<? super String> parameter")
        void testActionWithSuperStringParam() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action processSuperString = findAction(node.getActionsSelf(), "processSuperString");

            assertNotNull(processSuperString);
            assertEquals(1, processSuperString.getParameters().size());
            assertEquals("List<? super String>", processSuperString.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Action with nested wildcard parameter Map<?, List<?>>")
        void testActionWithNestedWildcardParam() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action processNestedWildcard = findAction(node.getActionsSelf(), "processNestedWildcard");

            assertNotNull(processNestedWildcard);
            assertEquals(1, processNestedWildcard.getParameters().size());
            assertEquals("Map<?, List<?>>", processNestedWildcard.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Action with deeply nested parameter List<Map<String, ? extends this>>")
        void testActionWithDeeplyNestedParam() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action processDeeplyNested = findAction(node.getActionsSelf(), "processDeeplyNested");

            assertNotNull(processDeeplyNested);
            assertEquals(1, processDeeplyNested.getParameters().size());
            assertEquals("List<Map<String, ? extends this>>", processDeeplyNested.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Action with multiple wildcard parameters")
        void testActionWithMultipleWildcardParams() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action processMultipleWildcards = findAction(node.getActionsSelf(), "processMultipleWildcards");

            assertNotNull(processMultipleWildcards);
            assertEquals(3, processMultipleWildcards.getParameters().size());
            assertEquals("List<?>", processMultipleWildcards.getParameters().get(0).getType());
            assertEquals("Set<? extends this>", processMultipleWildcards.getParameters().get(1).getType());
            assertEquals("Map<?, ? super String>", processMultipleWildcards.getParameters().get(2).getType());
        }
    }

    // ==================== Actions with Wildcard Return AND Parameters ====================

    @Nested
    @DisplayName("Actions with Wildcard Return Types and Parameters")
    class ActionsWithWildcardReturnAndParamsTests {

        @Test
        @DisplayName("Action returning List<? extends this> with Set<?> parameter")
        void testTransformWildcard() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action transformWildcard = findAction(node.getActionsSelf(), "transformWildcard");

            assertNotNull(transformWildcard);
            assertEquals("List<? extends this>", transformWildcard.getReturnType());
            assertEquals(1, transformWildcard.getParameters().size());
            assertEquals("Set<?>", transformWildcard.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Action returning Map<?, ?> with two wildcard parameters")
        void testMergeWildcards() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action mergeWildcards = findAction(node.getActionsSelf(), "mergeWildcards");

            assertNotNull(mergeWildcards);
            assertEquals("Map<?, ?>", mergeWildcards.getReturnType());
            assertEquals(2, mergeWildcards.getParameters().size());
            assertEquals("Map<? extends this, ?>", mergeWildcards.getParameters().get(0).getType());
            assertEquals("Map<?, ? super String>", mergeWildcards.getParameters().get(1).getType());
        }

        @Test
        @DisplayName("Action returning Map<this, ? extends this> with List<? extends this> parameter")
        void testCombineWithThis() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Action combineWithThis = findAction(node.getActionsSelf(), "combineWithThis");

            assertNotNull(combineWithThis);
            assertEquals("Map<this, ? extends this>", combineWithThis.getReturnType());
            assertEquals(1, combineWithThis.getParameters().size());
            assertEquals("List<? extends this>", combineWithThis.getParameters().get(0).getType());

            // Verify return type structure
            ParameterizedType returnType = (ParameterizedType) combineWithThis.getType();
            assertInstanceOf(ThisType.class, returnType.getTypeArguments().get(0));
            assertEquals("? extends this", returnType.getTypeArguments().get(1).toString());
        }
    }

    // ==================== Inheritance Tests for Wildcards with 'this' ====================

    @Nested
    @DisplayName("Inheritance Preservation of Wildcard Types with 'this'")
    class InheritanceTests {

        @Test
        @DisplayName("Wildcard with 'this' is preserved in inherited attribute")
        void testWildcardThisPreservedInInheritedAttribute() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            JoinPointClass expr = wildcardSpec.getJoinPoint("expr");

            // Find extendsThisList on node (where it's defined)
            Attribute extendsOnNode = findAttribute(node.getAttributesSelf(), "extendsThisList");
            assertNotNull(extendsOnNode);
            assertEquals("List<? extends this>", extendsOnNode.getType().toString());

            // Find it on expr (inherited)
            Attribute extendsOnExpr = findAttribute(expr.getAttributes(), "extendsThisList");
            assertNotNull(extendsOnExpr);

            // Should still be the same type string (late-bound)
            assertEquals("List<? extends this>", extendsOnExpr.getType().toString());
        }

        @Test
        @DisplayName("Expr has its own wildcard attribute with 'this'")
        void testExprHasOwnWildcardAttribute() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass expr = wildcardSpec.getJoinPoint("expr");

            Attribute exprSpecific = findAttribute(expr.getAttributesSelf(), "exprSpecific");
            assertNotNull(exprSpecific);
            assertEquals("Set<? extends this>", exprSpecific.getType().toString());
        }

        @Test
        @DisplayName("Expr inherits all node's wildcard attributes")
        void testExprInheritsNodeWildcardAttributes() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass expr = wildcardSpec.getJoinPoint("expr");

            // Should have access to all of node's wildcard attributes via inheritance
            assertNotNull(findAttribute(expr.getAttributes(), "wildcardList"));
            assertNotNull(findAttribute(expr.getAttributes(), "extendsThisList"));
            assertNotNull(findAttribute(expr.getAttributes(), "mapStringWildcard"));
            assertNotNull(findAttribute(expr.getAttributes(), "multipleWildcards"));
            assertNotNull(findAttribute(expr.getAttributes(), "nestedWildcard"));
        }

        @Test
        @DisplayName("Expr-specific action returns wildcard with 'this'")
        void testExprActionReturnsWildcardWithThis() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass expr = wildcardSpec.getJoinPoint("expr");

            Action exprWildcard = findAction(expr.getActionsSelf(), "exprWildcard");
            assertNotNull(exprWildcard);
            assertEquals("Set<? extends this>", exprWildcard.getReturnType());
        }
    }

    // ==================== Attributes with Parameters Tests ====================

    @Nested
    @DisplayName("Attributes with Wildcard Parameters")
    class AttributesWithParametersTests {

        @Test
        @DisplayName("Attribute returning wildcard with simple parameter")
        void testAttributeWithSimpleParameter() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute filterByType = findAttribute(node.getAttributesSelf(), "filterByType");

            assertNotNull(filterByType);
            assertEquals("List<? extends this>", filterByType.getType().toString());
            assertEquals(1, filterByType.getParameters().size());
            assertEquals("typeName", filterByType.getParameters().get(0).getName());
            assertEquals("String", filterByType.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Attribute returning wildcard with wildcard parameter")
        void testAttributeWithWildcardParameter() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute findMatching = findAttribute(node.getAttributesSelf(), "findMatching");

            assertNotNull(findMatching);
            assertEquals("Set<?>", findMatching.getType().toString());
            assertEquals(1, findMatching.getParameters().size());
            assertEquals("criteria", findMatching.getParameters().get(0).getName());
            assertEquals("Map<String, ?>", findMatching.getParameters().get(0).getType());
        }

        @Test
        @DisplayName("Attribute with multiple wildcard 'this' parameters")
        void testAttributeWithMultipleWildcardThisParameters() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute searchBetween = findAttribute(node.getAttributesSelf(), "searchBetween");

            assertNotNull(searchBetween);
            assertEquals("List<? extends this>", searchBetween.getType().toString());
            assertEquals(2, searchBetween.getParameters().size());
            assertEquals("start", searchBetween.getParameters().get(0).getName());
            assertEquals("? extends this", searchBetween.getParameters().get(0).getType());
            assertEquals("end", searchBetween.getParameters().get(1).getName());
            assertEquals("? extends this", searchBetween.getParameters().get(1).getType());
        }

        @Test
        @DisplayName("Attribute with complex wildcard parameters")
        void testAttributeWithComplexWildcardParameters() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute transformWith = findAttribute(node.getAttributesSelf(), "transformWith");

            assertNotNull(transformWith);
            assertEquals("Map<?, ? super String>", transformWith.getType().toString());
            assertEquals(2, transformWith.getParameters().size());
            assertEquals("transformer", transformWith.getParameters().get(0).getName());
            assertEquals("List<? extends this>", transformWith.getParameters().get(0).getType());
            assertEquals("options", transformWith.getParameters().get(1).getName());
            assertEquals("Map<String, ?>", transformWith.getParameters().get(1).getType());
        }

        @Test
        @DisplayName("Attribute with contravariant wildcard parameter (? super this)")
        void testAttributeWithContravariantWildcardParameter() {
            LanguageSpecification wildcardSpec = parseWildcardSpec();
            JoinPointClass node = wildcardSpec.getJoinPoint("node");
            Attribute collectInto = findAttribute(node.getAttributesSelf(), "collectInto");

            assertNotNull(collectInto);
            assertEquals("List<?>", collectInto.getType().toString());
            assertEquals(1, collectInto.getParameters().size());
            assertEquals("target", collectInto.getParameters().get(0).getName());
            assertEquals("List<? super this>", collectInto.getParameters().get(0).getType());
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
