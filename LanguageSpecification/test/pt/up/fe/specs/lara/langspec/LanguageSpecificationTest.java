/**
 * Copyright 2020 SPeCS.
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
import static pt.up.fe.specs.lara.langspec.LanguageSpecificationTestResource.ACTION_MODEL;
import static pt.up.fe.specs.lara.langspec.LanguageSpecificationTestResource.ATTRIBUTE_MODEL;
import static pt.up.fe.specs.lara.langspec.LanguageSpecificationTestResource.JOIN_POINT_MODEL;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lara.language.specification.dsl.Action;

import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.xml.XmlDocument;

public class LanguageSpecificationTest {

    @BeforeAll
    public static void init() {
        SpecsSystem.programStandardInit();
    }

    @Test
    public void testSchema() {
        XmlDocument.newInstance(JOIN_POINT_MODEL.toStream(), SchemaResource.JOIN_POINT_SCHEMA.toStream());
        XmlDocument.newInstance(ATTRIBUTE_MODEL.toStream(), SchemaResource.ATTRIBUTE_SCHEMA.toStream());
        XmlDocument.newInstance(ACTION_MODEL.toStream(), SchemaResource.ACTION_SCHEMA.toStream());
    }

    @Test
    public void testParser() {

        var langSpec = LangSpecsXmlParser.parse(JOIN_POINT_MODEL.toStream(), ATTRIBUTE_MODEL.toStream(),
                ACTION_MODEL.toStream());
        // System.out.println("SPEC:\n" + langSpec);
        ResourceProvider parserTestExpected = () -> "pt/up/fe/specs/lara/langspec/test/ParserTestResult.txt";
        // System.out.println("EXPECTED:\n" + parserTestExpected.read());
        // System.out.println("GOT:\n" + langSpec);
        assertEquals(SpecsStrings.normalizeFileContents(parserTestExpected.read(), true),
                SpecsStrings.normalizeFileContents(langSpec.toString(), true));
    }

    @Test
    public void testActionModel() {
        var langSpec = LangSpecsXmlParser.parse(JOIN_POINT_MODEL.toStream(), ATTRIBUTE_MODEL.toStream(),
                ACTION_MODEL.toStream());

        StringBuilder actual = new StringBuilder();

        actual.append("loop own actions:");
        langSpec.getJoinPoint("loop").getActionsSelf().stream()
                .map(Action::getName)
                .forEach(actionName -> actual.append("\n" + actionName));
        actual.append("\n");

        actual.append("loop all actions:");
        langSpec.getJoinPoint("loop").getActions().stream()
                .map(action -> "Name: " + action.getName())
                .forEach(actionName -> actual.append("\n" + actionName));
        actual.append("\n");

        actual.append("body all actions:");
        langSpec.getJoinPoint("body").getActions().stream()
                .map(action -> "Name: " + action.getName())
                .forEach(actionName -> actual.append("\n" + actionName));
        // actions = am.getAllJoinPointActions(jp, jpm);

        ResourceProvider parserTestExpected = () -> "pt/up/fe/specs/lara/langspec/test/ActionModelTest.txt";
        assertEquals(SpecsStrings.normalizeFileContents(parserTestExpected.read(), true),
                SpecsStrings.normalizeFileContents(actual.toString(), true));

    }
}
