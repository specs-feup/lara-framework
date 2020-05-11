import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.actionsmodel.ActionModel;
import org.lara.language.specification.actionsmodel.schema.Action;
import org.lara.language.specification.exception.SchemaValidationException;
import org.lara.language.specification.joinpointmodel.JoinPointModel;
import org.lara.language.specification.joinpointmodel.constructor.JoinPointModelConstructor;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.lara.language.specification.joinpointmodel.schema.JoinPointsList;
import org.lara.language.specification.resources.LanguageSpecificationResources;
import org.xml.sax.SAXException;

import pt.up.fe.specs.util.SpecsIo;
import tdrc.utils.MarshalUtils;
import tdrc.utils.Pair;
import tdrc.utils.PairList;

/**
 * Copyright 2015 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class TestLanguageSpecification {

    private static final File LANG_SPEC_DIR = new File("language");

    @Test
    public void testXSD() throws JAXBException, SAXException, IOException {

        final File jpmXML = new File(TestLanguageSpecification.LANG_SPEC_DIR,
                LanguageSpecification.JOIN_POINT_MODEL_NAME + LanguageSpecification.XML_EXTENSION);

        try (final InputStream iS = SpecsIo
                .resourceToStream(LanguageSpecificationResources.JoinPointModelSchema.getResource());) {
            JoinPointsList jpl = MarshalUtils.unmarshal(new StreamSource(jpmXML), "test", iS, JoinPointsList.class,
                    JoinPointModelConstructor.JoinPointModelObjectFactory, true);
            System.out.println(jpl.toString());
        }
    }

    @Test
    public void testJoinPointModel() throws JAXBException, SAXException, XMLParseException, IOException {
        final File jpmXML = new File(TestLanguageSpecification.LANG_SPEC_DIR,
                LanguageSpecification.JOIN_POINT_MODEL_NAME + LanguageSpecification.XML_EXTENSION);

        try {
            final JoinPointModel jpm = new JoinPointModel(jpmXML, true);

            PairList<String, String> path = jpm.getPath("function");
            assertEquals(2, path.size());
            System.out.println("Function:" + path);

            final Pair<String, String> jp = path.last();
            final String lastType = jp.getRight();

            path = jpm.getPath(lastType, "test", true);
            System.out.println("Test from function:" + path);
            assertEquals(2, path.size());
            path = jpm.getPath("all");
            System.out.println("select all:" + path);
            assertEquals(0, path.size());

        } catch (final SchemaValidationException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    @Test
    public void testActionModel() throws JAXBException, SAXException, XMLParseException, IOException {
        final File jpmXML = new File(TestLanguageSpecification.LANG_SPEC_DIR,
                LanguageSpecification.JOIN_POINT_MODEL_NAME + LanguageSpecification.XML_EXTENSION);
        final File aXML = new File(TestLanguageSpecification.LANG_SPEC_DIR,
                LanguageSpecification.ACTION_MODEL_NAME + LanguageSpecification.XML_EXTENSION);
        try {
            final JoinPointModel jpm = new JoinPointModel(jpmXML, true);
            final ActionModel am = ActionModel.newInstance(aXML, true);
            List<Action> actions = am.getJoinPointOwnActions("loop");
            System.out.println("Action pertaining to loop: ");
            assertEquals(1, actions.size());
            for (final Action action : actions) {
                System.out.print(action.getName() + ", ");
            }
            System.out.println();

            actions = am.getJoinPointActions("loop");
            System.out.println("actions: " + actions);
            assertEquals(8, actions.size());
            System.out.println("All actions for loop: ");
            for (final Action action : actions) {
                System.out.print(action.getName() + ", ");
            }
            System.out.println();

            actions = am.getActionsForAll();
            assertEquals(4, actions.size());
            System.out.println("All actions: ");
            for (final Action action : actions) {
                System.out.print(action.getName() + ", ");
            }
            System.out.println();

            final JoinPointType jp = jpm.getJoinPoint("body");
            actions = am.getAllJoinPointActions(jp, jpm);
            assertEquals(5, actions.size());
            System.out.println("All actions for body, including of super type scope: ");
            for (final Action action : actions) {
                System.out.print(action.getName() + ", ");
            }
            System.out.println();
        } catch (final SchemaValidationException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

}
