/*
 * Copyright 2013 SPeCS.
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
package org.lara.interpreter.weaver.generator.commandline;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.lara.interpreter.weaver.generator.commandline.WeaverGeneratorOptions.GeneratorOption;
import org.lara.interpreter.weaver.generator.generator.BaseGenerator;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java2cpp.JavaImplGenerator;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.actionsmodel.schema.Action;
import org.lara.language.specification.actionsmodel.schema.Parameter;
import org.lara.language.specification.ast.LangSpecNode;
import org.lara.language.specification.ast.NodeFactory;

import pt.up.fe.specs.util.SpecsIo;

public class WeaverGenerator {

    // public static JavaGenerator newJavaGenerator() {
    // return new JavaGenerator();
    // }
    /**
     * 
     * @deprecated change to
     *             {@link WeaverGenerator#generateJava(String, LanguageSpecification, File, String, boolean, Class)}
     */
    @Deprecated
    public static void generate(String weaverName, LanguageSpecification langSpec, File outputDir, String outputPackage,
            boolean abstractGetters) {

        final BaseGenerator generator = new JavaAbstractsGenerator(langSpec).weaverName(weaverName).outputDir(outputDir)
                .setPackage(outputPackage).abstractGetters(abstractGetters);
        printReport(generator);
        generator.generate();
    }

    /**
     * Generate a new weaver, according to the input language specification.
     * 
     * @param weaverName
     *            The name for the new Weaver
     * @param languageSpecification
     *            The language specification
     * @param outputDir
     *            The output directory
     * @param outputPackage
     *            The package for the generated classes
     * @param abstractGetters
     *            Define if the attributes are generated as abstract methods (true) or fields with getters (false)
     * @return true if generated successfully, false otherwise.
     */
    @Deprecated
    public static void generateJava(String weaverName, LanguageSpecification langSpec, File outputDir,
            String outputPackage, boolean abstractGetters) {
        generateJava(weaverName, langSpec, outputDir, outputPackage, abstractGetters, Object.class);
        /*	final BaseGenerator generator = new JavaGenerator(langSpec).weaverName(weaverName).outputDir(outputDir)
        			.setPackage(outputPackage).abstractGetters(abstractGetters);
        	printReport(generator);
        	generator.generate();
        	*/
    }

    /**
     * Generate a new weaver, according to the input language specification.
     * 
     * @param weaverName
     *            The name for the new Weaver
     * @param languageSpecification
     *            The language specification
     * @param outputDir
     *            The output directory
     * @param outputPackage
     *            The package for the generated classes
     * @param abstractGetters
     *            Define if the attributes are generated as abstract methods (true) or fields with getters (false)
     * @param nodeType
     *            Define the base class of the generics for the join points, i.e., <T extends «nodeGenerics»>
     * @return true if generated successfully, false otherwise.
     */
    public static void generateJava(String weaverName, LanguageSpecification langSpec, File outputDir,
            String outputPackage, boolean abstractGetters, Class<?> nodeType) {

        final BaseGenerator generator = new JavaAbstractsGenerator(langSpec)
                .weaverName(weaverName).outputDir(outputDir)
                .setPackage(outputPackage).abstractGetters(abstractGetters).nodeType(nodeType);
        printReport(generator);
        generator.generate();
        generator.print();
    }

    /**
     * Generate a new weaver, according to the input language specification.
     * 
     * @param weaverName
     *            The name for the new Weaver
     * @param languageSpecification
     *            The language specification
     * @param outputDir
     *            The output directory
     * @param outputPackage
     *            The package for the generated classes
     * @param abstractGetters
     *            Define if the attributes are generated as abstract methods (true) or fields with getters (false)
     * @param nodeType
     *            Define the base class of the generics for the join points, i.e., <T extends «nodeGenerics»>
     * @return true if generated successfully, false otherwise.
     */
    public static void generateJava2CPP(String weaverName, LanguageSpecification langSpec, File outputDir,
            String outputPackage, boolean abstractGetters, Class<?> nodeType) {

        final BaseGenerator generator = new JavaImplGenerator(langSpec, "C").weaverName(weaverName).outputDir(outputDir)
                .setPackage(outputPackage).abstractGetters(abstractGetters).nodeType(nodeType);
        printReport(generator);
        generator.generate();
        generator.print();
    }

    /**
     * Generate a new weaver, according to the input language specification.
     * 
     * @param generator
     *            the generator that will generate the weaver implementation
     * @return true if generated successfully, false otherwise.
     */
    private static boolean generate(BaseGenerator generator) {

        // String xmlLocation = languageSpecificationDir.getAbsolutePath();
        try {
            generator.generate();
            return true;
        } catch (final Exception e) {
            System.err.println("Could not generate weaver!");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Print the new weaver in files, according to the generated code.
     * 
     * @param generator
     *            the generator that has the generated code
     * @return true if generated successfully, false otherwise.
     */
    private static boolean print(BaseGenerator generator) {

        // String xmlLocation = languageSpecificationDir.getAbsolutePath();
        try {
            generator.print();
            return true;
        } catch (final Exception e) {
            System.err.println("Could not output the weaver!");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Generate a new weaver, according to the input language specification. <br>
     * Usage: java -jar WeaverGenerator.jar [options] <br>
     * 
     * @param args
     *            List of available arguments:
     *            <table width="100">
     *            <thead>
     *            <tr>
     *            <th>Option</th>
     *            <th>Long Option</th>
     *            <th>Description</th>
     *            </tr>
     *            <thead> <tbody>
     *            <tr>
     *            <td>-h</td>
     *            <td>--help</td>
     *            <td>Print a help message</td>
     *            </tr>
     *            <tr>
     *            <td>-o</td>
     *            <td>--output &ltdir&gt</td>
     *            <td>Change output directory (default: .)</td>
     *            </tr>
     * 
     *            <tr>
     *            <td>-p</td>
     *            <td>--package &ltpackageName&gt</td>
     *            <td>Define the package for the java files</td>
     *            </tr>
     *            <tr>
     *            <td>-w</td>
     *            <td>--weaver &ltclassName&gt</td>
     *            <td>Name for the class (default: MyWeaver)</td>
     *            </tr>
     *            <tr>
     *            <td>-x</td>
     *            <td>--XMLspec &ltdir&gt</td>
     *            <td>Location of the target language specification (default: .)</td>
     *            </tr>
     *            </tbody>
     *            </table>
     */
    /**/
    public static void main(String[] args) {

        final WeaverGeneratorOptions opts = new WeaverGeneratorOptions();

        final CommandLine cmdLine = opts.parse(args);
        if (cmdLine.hasOption(GeneratorOption.H.getOption())) {
            opts.help();
            return;
        }

        File XMLSpecDir;

        if (cmdLine.hasOption(GeneratorOption.X.getOption())) {
            XMLSpecDir = new File(cmdLine.getOptionValue(GeneratorOption.X.getOption()));
        } else {
            XMLSpecDir = GenConstants.getDefaultXMLDir();
        }
        final BaseGenerator generator;
        String optionValue;
        if (cmdLine.hasOption(GeneratorOption.L.getOption())) {
            optionValue = cmdLine.getOptionValue(GeneratorOption.L.getOption());
            if (optionValue.equals("java2cpp")) {
                generator = new JavaImplGenerator(XMLSpecDir);
            } else {
                generator = new JavaAbstractsGenerator(XMLSpecDir);
            }
        } else {
            // Create the JavaAbstractGenerator
            generator = new JavaAbstractsGenerator(XMLSpecDir);
        }

        if (cmdLine.hasOption(GeneratorOption.W.getOption())) {
            optionValue = cmdLine.getOptionValue(GeneratorOption.W.getOption());
            generator.setWeaverName(optionValue);
        }

        if (cmdLine.hasOption(GeneratorOption.P.getOption())) {
            optionValue = cmdLine.getOptionValue(GeneratorOption.P.getOption());
            generator.setOutPackage(optionValue);
        }

        if (cmdLine.hasOption(GeneratorOption.O.getOption())) {
            final File file = new File(cmdLine.getOptionValue(GeneratorOption.O.getOption()));
            generator.setOutDir(file);
        }

        // if (cmdLine.hasOption(GeneratorOption.A.getOption())) {
        // generator.setAbstractGetters(true);
        // }
        if (cmdLine.hasOption(GeneratorOption.F.getOption())) {
            generator.setAbstractGetters(false);
        }
        if (cmdLine.hasOption(GeneratorOption.E.getOption())) {
            generator.setEvents(true);
        }
        // if (cmdLine.hasOption(GeneratorOption.I.getOption())) {
        // generator.setImplMode(true);
        // }

        if (cmdLine.hasOption(GeneratorOption.N.getOption())) {
            optionValue = cmdLine.getOptionValue(GeneratorOption.N.getOption());
            if (optionValue != null) {
                generator.setNodeType(optionValue);
            } else {
                generator.setNodeType(GenConstants.getDefaultNodeType());
            }
        }

        if (cmdLine.hasOption(GeneratorOption.J.getOption())) {
            generator.setJson(true);
        }
        if (cmdLine.hasOption(GeneratorOption.D.getOption())) {
            generator.setDefs(true);
        }

        // if (cmdLine.hasOption(GeneratorOption.G.getOption())) {
        // generator.setShowGraph(true);
        // }

        printReport(generator.getWeaverName(), generator.getOutPackage(), XMLSpecDir, generator.getOutDir(),
                generator.isAbstractGetters(), generator.hasEvents(), generator.hasImplMode(), generator.getNodeType(),
                generator.isJson(), generator.isShowGraph());

        // boolean generated = generate(weaverName, XMLSpecDir, outDir,
        // outPackage, abstractGetters, showGraph);
        final boolean generated = generate(generator);
        if (!generated) {
            System.err.println("The Weaver was not created!");
            return;
        }
        final boolean printed = print(generator);
        if (!printed) {
            System.err.println("The Weaver was not created!");
            return;
        }
        if (generator.isJson()) {
            String packagePath = generator.getOutPackage().replace(".", "/");
            File jsonDir = new File(generator.getOutDir(), packagePath);
            File jsonOutFile = new File(jsonDir, generator.getWeaverName() + ".json");
            printJson(generator, jsonOutFile);
        }
        System.out.println("Weaver successfully created!");
    }

    public static void printJson(final BaseGenerator generator, File jsonOutFile) {
        LanguageSpecification languageSpecification = generator.getLanguageSpecification();
        String ret = "";
        for (final Action action : languageSpecification.getActionModel().getActionsList().getAction()) {

            ret += action.getClazz() + "\t" + action.getName() + "(";
            for (final Parameter parameter : action.getParameter()) {

                ret += "" + parameter.getName() + "[" + parameter.getType() + "], ";
            }
            ret += ")\n";
        }
        // System.out.println(ret);
        LangSpecNode node = NodeFactory.toNode(languageSpecification);
        String json = node.toJson();

        // String json = jw.toJson(languageSpecification);
        SpecsIo.write(jsonOutFile, json);
    }

    private static void printReport(BaseGenerator generator) {
        printReport(generator.getWeaverName(), generator.getOutPackage(), null, generator.getOutDir(),
                generator.isAbstractGetters(), generator.hasEvents(), generator.hasImplMode(), generator.getNodeType(),
                generator.isJson(), generator.isShowGraph());
    }

    private static void printReport(String weaverName, String outPackage, File xMLSpecDir, File outDir,
            boolean abstractGetters, boolean hasEvents, boolean usesImpl, String generics, boolean json,
            boolean showGraph) {
        final StringBuilder report = new StringBuilder();
        report.append("Weaver name:   " + weaverName + "\n");
        report.append("Package:       " + outPackage + "\n");
        if (xMLSpecDir != null) {
            report.append("Lang. Spec:    " + xMLSpecDir + "\n");
        }
        report.append("Output Dir.:   " + outDir + "\n");
        report.append("Add Events: " + hasEvents + "\n");
        report.append("Uses Impl Methods: " + usesImpl + "\n");
        report.append("Abst. Getters: " + abstractGetters + "\n");
        report.append("Node type:     " + (generics == null ? "N/A" : generics) + "\n");
        report.append("Create JSON:   " + json + "\n");
        // report.append("Show Graph: " + showGraph + "\n");
        System.out.println(report);
    }
}
