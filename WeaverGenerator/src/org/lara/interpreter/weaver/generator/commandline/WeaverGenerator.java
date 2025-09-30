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

import org.apache.commons.cli.CommandLine;
import org.lara.interpreter.weaver.generator.commandline.WeaverGeneratorOptions.GeneratorOption;
import org.lara.interpreter.weaver.generator.generator.BaseGenerator;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.ast.LangSpecNode;
import org.lara.language.specification.ast.NodeFactory;
import pt.up.fe.specs.util.DotRenderFormat;
import pt.up.fe.specs.util.SpecsGraphviz;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsSystem;

import java.io.File;
import java.util.Objects;

public class WeaverGenerator {
    /**
     * Generate a new weaver, according to the input language specification.
     *
     * @param generator the generator that will generate the weaver implementation
     * @return true if generated successfully, false otherwise.
     */
    private static boolean generate(BaseGenerator generator) {
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
     * @param generator the generator that has the generated code
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
     * @param args List of available arguments:
     *             <table width="100">
     *             <thead>
     *             <tr>
     *             <th>Option</th>
     *             <th>Long Option</th>
     *             <th>Description</th>
     *             </tr>
     *             <thead> <tbody>
     *             <tr>
     *             <td>-h</td>
     *             <td>--help</td>
     *             <td>Print a help message</td>
     *             </tr>
     *             <tr>
     *             <td>-o</td>
     *             <td>--output &ltdir&gt</td>
     *             <td>Change output directory (default: .)</td>
     *             </tr>
     *
     *             <tr>
     *             <td>-p</td>
     *             <td>--package &ltpackageName&gt</td>
     *             <td>Define the package for the java files</td>
     *             </tr>
     *             <tr>
     *             <td>-w</td>
     *             <td>--weaver &ltclassName&gt</td>
     *             <td>Name for the class (default: MyWeaver)</td>
     *             </tr>
     *             <tr>
     *             <td>-x</td>
     *             <td>--XMLspec &ltdir&gt</td>
     *             <td>Location of the target language specification (default:
     *             .)</td>
     *             </tr>
     *             </tbody>
     *             </table>
     */
    /**/
    public static void main(String[] args) {
        SpecsSystem.programStandardInit();
        System.exit(run(args));
    }

    public static int run(String[] args) {
        final WeaverGeneratorOptions opts = new WeaverGeneratorOptions();

        final CommandLine cmdLine;
        try {
            cmdLine = opts.parse(args);
        } catch (RuntimeException e) {
            reportException(e);
            return 1;
        }

        if (cmdLine.hasOption(GeneratorOption.H.getOption())) {
            opts.help();
            return 0;
        }

        try {
            final BaseGenerator generator = buildGenerator(cmdLine);

            printReport(generator);

            if (!generate(generator)) {
                System.err.println("The Weaver was not created!");
                return 1;
            }

            if (!print(generator)) {
                System.err.println("The Weaver was not created!");
                return 1;
            }

            if (generator.isJson() && !emitJsonArtifacts(generator)) {
                return 1;
            }

            System.out.println("Weaver successfully created!");
            return 0;
        } catch (RuntimeException e) {
            reportException(e);
            return 1;
        }
    }

    private static BaseGenerator buildGenerator(CommandLine cmdLine) {
        final File XMLSpecDir;
        if (cmdLine.hasOption(GeneratorOption.X.getOption())) {
            XMLSpecDir = new File(cmdLine.getOptionValue(GeneratorOption.X.getOption()));
        } else {
            XMLSpecDir = GenConstants.getDefaultXMLDir();
        }

        final BaseGenerator generator = new JavaAbstractsGenerator(XMLSpecDir);

        if (cmdLine.hasOption(GeneratorOption.W.getOption())) {
            final String optionValue = cmdLine.getOptionValue(GeneratorOption.W.getOption());
            generator.setWeaverName(optionValue);
        }

        if (cmdLine.hasOption(GeneratorOption.P.getOption())) {
            final String optionValue = cmdLine.getOptionValue(GeneratorOption.P.getOption());
            generator.setOutPackage(optionValue);
        }

        if (cmdLine.hasOption(GeneratorOption.O.getOption())) {
            final File file = new File(cmdLine.getOptionValue(GeneratorOption.O.getOption()));
            generator.setOutDir(file);
        }

        if (cmdLine.hasOption(GeneratorOption.F.getOption())) {
            generator.setAbstractGetters(false);
        }

        if (cmdLine.hasOption(GeneratorOption.E.getOption())) {
            generator.setEvents(true);
        }

        if (cmdLine.hasOption(GeneratorOption.N.getOption())) {
            final String optionValue = cmdLine.getOptionValue(GeneratorOption.N.getOption());
            generator.setNodeType(Objects.requireNonNullElseGet(optionValue, GenConstants::getDefaultNodeType));
        }

        if (cmdLine.hasOption(GeneratorOption.J.getOption())) {
            generator.setJson(true);
        }

        if (cmdLine.hasOption(GeneratorOption.C.getOption())) {
            generator.setConcreteClassesPrefix(cmdLine.getOptionValue(GeneratorOption.C.getOption()));
        }

        return generator;
    }

    private static boolean emitJsonArtifacts(BaseGenerator generator) {
        try {
            String packagePath = generator.getOutPackage().replace(".", "/");
            File jsonDir = new File(generator.getOutDir(), packagePath);
            File jsonOutFile = new File(jsonDir, generator.getWeaverName() + ".json");
            printJson(generator, jsonOutFile);

            // Also generate the graph
            File dotOutFile = new File(jsonDir, generator.getWeaverName() + ".dotty");
            var graphName = generator.getWeaverName() + "_join_point_hierarchy";
            var graph = generator.getLanguageSpecification().toHierarchyDiagram(graphName);
            SpecsIo.write(dotOutFile, graph);

            // If dot available, generate PNG and SVG
            if (SpecsGraphviz.isDotAvailable()) {
                SpecsGraphviz.renderDot(dotOutFile, DotRenderFormat.PNG);
                SpecsGraphviz.renderDot(dotOutFile, DotRenderFormat.SVG);
            }

            return true;
        } catch (Exception e) {
            System.err.println("The Weaver was not created!");
            e.printStackTrace();
            return false;
        }
    }

    private static void reportException(RuntimeException e) {
        var message = e.getMessage();
        if ((message == null || message.isBlank()) && e.getCause() != null) {
            message = e.getCause().getMessage();
        }
        if (message == null || message.isBlank()) {
            message = e.toString();
        }
        System.err.println(message);
    }

    public static void printJson(final BaseGenerator generator, File jsonOutFile) {
        var languageSpecification = generator.getLanguageSpecification();

        LangSpecNode node = NodeFactory.toNode(languageSpecification);
        String json = node.toJson();

        SpecsIo.write(jsonOutFile, json);
    }

    private static void printReport(BaseGenerator generator) {
        printReport(generator.getWeaverName(), generator.getOutPackage(), null, generator.getOutDir(),
                generator.isAbstractGetters(), generator.hasEvents(), generator.hasImplMode(), generator.getNodeType(),
                generator.isJson(), generator.isShowGraph(), generator.getConcreteClassesPrefix());
    }

    private static void printReport(String weaverName, String outPackage, File xMLSpecDir, File outDir,
            boolean abstractGetters, boolean hasEvents, boolean usesImpl, String generics, boolean json,
            boolean showGraph, String concreteClassesPrefix) {
        final StringBuilder report = new StringBuilder();
        report.append("Weaver name:       " + weaverName + "\n");
        report.append("Package:           " + outPackage + "\n");
        if (xMLSpecDir != null) {
            report.append("Lang. Spec:        " + xMLSpecDir + "\n");
        }
        report.append("Output Dir.:       " + outDir + "\n");
        report.append("Add Events:        " + hasEvents + "\n");
        report.append("Uses Impl Methods: " + usesImpl + "\n");
        report.append("Abst. Getters:     " + abstractGetters + "\n");
        report.append("Node type:         " + (generics == null ? "N/A" : generics) + "\n");
        report.append("Create JSON:       " + json + "\n");

        if (concreteClassesPrefix != null) {
            report.append("Concrete classes with prefix:   " + concreteClassesPrefix + "\n");
        }

        System.out.println(report);
    }
}
