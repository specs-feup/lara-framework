/**
 * Copyright 2013 SPeCS Research Group.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

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

import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @deprecated tests LangSpecV1
 */
public class LangSpecTester {

    private static final File LANG_SPEC_DIR = new File("../../lara-framework/LanguageSpecification/language");

    public static void main(String[] args) {
        System.out.println("Starting Application");

        try {
            testXSD();
            testJoinPointModel();
            testActionModel();
            // boolean validate = true;
            // // LanguageSpecification langSpec = new
            // LanguageSpecification(LANG_SPEC_DIR, validate);
            // PairList<String, String> path =
            // langSpec.getJpModel().getPath("all");
            // System.out.println(path);

            // Attribute attr = langSpec.getArtifacts().getAttribute("function",
            // "file_name");
            // System.out.println(attr.getName() +","+attr.getType());
            // xsd_dir = createValidationFolder(VALIDATION_PROPERTY);
            // System.out.println("INIT");
            // long begin = System.nanoTime();
            // boolean validate = true;
            // LanguageSpecification langSpec = new
            // LanguageSpecification(LANG_SPEC_DIR,validate);
            // long end = System.nanoTime();
            // System.out.println("Total time expended: " +
            // ParseUtils.parseTime(end - begin));
            //
            // System.out.println("SEARCHING");
            // PairList<String, String> path =
            // langSpec.getJpModel().getPath("function");
            // path =
            // langSpec.getJpModel().getPath(path.last().getRight(),"var");
            // System.out.println(path);
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // testJoinPointModel();
        // testAttributes();
        // testActions();
    }

    private static void testXSD() throws JAXBException, SAXException, IOException {

        final File jpmXML = new File(LangSpecTester.LANG_SPEC_DIR,
                LanguageSpecification.JOIN_POINT_MODEL_NAME + LanguageSpecification.XML_EXTENSION);

        try (final InputStream iS = SpecsIo
                .resourceToStream(LanguageSpecificationResources.JoinPointModelSchema.getResource());) {
            MarshalUtils.unmarshal(new StreamSource(jpmXML), "test", iS, JoinPointsList.class,
                    JoinPointModelConstructor.JoinPointModelObjectFactory, true);
        }
    }

    private static void testJoinPointModel() throws JAXBException, SAXException, XMLParseException, IOException {
        final File jpmXML = new File(LangSpecTester.LANG_SPEC_DIR,
                LanguageSpecification.JOIN_POINT_MODEL_NAME + LanguageSpecification.XML_EXTENSION);

        try {
            final JoinPointModel jpm = new JoinPointModel(jpmXML, true);

            PairList<String, String> path = jpm.getPath("function");
            System.out.println("Function:" + path);

            final Pair<String, String> jp = path.last();
            final String lastType = jp.getRight();

            path = jpm.getPath(lastType, "test", true);
            System.out.println("Test from function:" + path);

            path = jpm.getPath("all");
            System.out.println("select all:" + path);

        } catch (final SchemaValidationException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void testActionModel() throws JAXBException, SAXException, XMLParseException, IOException {
        final File jpmXML = new File(LangSpecTester.LANG_SPEC_DIR,
                LanguageSpecification.JOIN_POINT_MODEL_NAME + LanguageSpecification.XML_EXTENSION);
        final File aXML = new File(LangSpecTester.LANG_SPEC_DIR,
                LanguageSpecification.ACTION_MODEL_NAME + LanguageSpecification.XML_EXTENSION);
        try {
            final JoinPointModel jpm = new JoinPointModel(jpmXML, true);
            final ActionModel am = ActionModel.newInstance(aXML, true);
            List<Action> actions = am.getJoinPointOwnActions("loop");
            System.out.println("Action pertaining to loop: ");
            for (final Action action : actions) {
                System.out.print(action.getName() + ", ");
            }
            System.out.println();

            actions = am.getJoinPointActions("loop");
            System.out.println("All actions for loop: ");
            for (final Action action : actions) {
                System.out.print(action.getName() + ", ");
            }
            System.out.println();

            actions = am.getActionsForAll();
            System.out.println("All actions: ");
            for (final Action action : actions) {
                System.out.print(action.getName() + ", ");
            }
            System.out.println();

            final JoinPointType jp = jpm.getJoinPoint("body");
            actions = am.getAllJoinPointActions(jp, jpm);
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
    /*
     * private static void testAttributes() throws JAXBException, SAXException,
     * XMLParseException { File xml = new File(LANG_SPEC_DIR,
     * ATTRIBUTES_MODEL_XML_NAME); File schema = new File(XSD_DIR,
     * ATTRIBUTES_MODEL_SCHEMA_NAME);
     *
     * try { ArtifactsModel am = new ArtifactsModel(xml, schema);
     * System.out.println(am);
     *
     * } catch (SchemaValidationException e) {
     * System.err.println(e.getMessage()); System.exit(-1); } }
     *
     * private static void testActions() throws JAXBException, SAXException,
     * XMLParseException { File xml = new File(LANG_SPEC_DIR,
     * ACTION_MODEL_XML_NAME); File schema = new File(XSD_DIR,
     * ACTION_MODEL_SCHEMA_NAME);
     *
     * try { ActionModel am = new ActionModel(xml, schema);
     * System.out.println(am);
     *
     * } catch (SchemaValidationException e) {
     * System.err.println(e.getMessage()); System.exit(-1); } }
     */

    // private static File createValidationFolder(String jarLocProperty) throws
    // IOException {
    // String jarFolder = new JarPath(Main.class,
    // jarLocProperty).buildJarPath();
    // System.out.println("JARFOLDER "+jarFolder);
    // ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
    // System.out.println("JARFOLDER "+protectionDomain);
    // CodeSource codeSource = protectionDomain.getCodeSource();
    // System.out.println("JARFOLDER "+codeSource);
    // URL location = codeSource.getLocation();
    // System.out.println("JARFOLDER "+location);
    // String path = location.getPath();
    // System.out.println("JARFOLDER "+path);
    // File testJar = new File(path);
    // System.out.println("TEST "+testJar);
    // File jarDir = new File(jarFolder);
    // if (!jarDir.exists()) {
    // throw new IOException("Could not reach jar location!");
    // }
    // File valDir = new File(jarDir, XSD_FOLDER_NAME);
    // if (!valDir.exists()) {
    // valDir.mkdir();
    // }
    // exportSchemas(valDir);
    // return valDir;
    // }

    // private static void exportSchemas(File resourcesPath) {
    //
    // for (LanguageSpecificationResources resource :
    // LanguageSpecificationResources.values()) {
    // InputStream iS = IoUtils.resourceToStream(resource.getResource());
    // copy(iS,System.out);
    // }
    // }
    //
    // public static long copy(InputStream is, OutputStream os) {
    // byte[] buf = new byte[8192];
    // long total = 0;
    // int len = 0;
    // try {
    // while (-1 != (len = is.read(buf))) {
    // os.write(buf, 0, len);
    // total += len;
    // }
    // } catch (IOException ioe) {
    // throw new RuntimeException("error reading stream", ioe);
    // }
    // return total;
    // }
}