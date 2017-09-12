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

public class OldWeaverGenerator {
	/*
	 * /** Generate a new weaver, according to the input language specification.
	 * The language specification folder should contain the three required
	 * files: joinPointModel.xml, artifacts.xml and actionModel.xml
	 * 
	 * @param weaverName The name for the new Weaver
	 * 
	 * @param languageSpecificationDir The location of the language
	 * specification
	 * 
	 * @param outputDir The output directory
	 * 
	 * @param outputPackage The package for the generated classes
	 * 
	 * @param showGraph
	 * 
	 * @return true if generated successfully, false otherwise.
	 *
	 * public static boolean generate(String weaverName, File
	 * languageSpecificationDir, File outputDir, String outputPackage, boolean
	 * abstractGetters, boolean showGraph) {
	 * 
	 * // String xmlLocation = languageSpecificationDir.getAbsolutePath(); try {
	 * LanguageSpecification languageSpecification; languageSpecification =
	 * LanguageSpecification.newInstance(languageSpecificationDir, true);
	 * 
	 * generate(weaverName, languageSpecification, outputDir, outputPackage,
	 * abstractGetters); if (showGraph) { //
	 * languageSpecification.createGraph(outputDir, outputPackage); } return
	 * true; } catch (Exception e) { System.err.println(
	 * "Could not generate weaver!"); e.printStackTrace(); } return false; }
	 * 
	 * /** Generate a new weaver, according to the {@link JPModel}, {@link
	 * Artifacts} and {@link ActionModel}.
	 * 
	 * @param weaverName The name for the new Weaver
	 * 
	 * @param languageSpecificationDir The location of the language
	 * specification
	 * 
	 * @param outputDir The output directory
	 * 
	 * @param outputPackage The package for the generated classes
	 * 
	 * @param abstractGetters If true, getters are defined as abstract and the
	 * fields are not generated
	 * 
	 * @return true if generated successfully, false otherwise.
	 *
	 * public static void generate(String weaverName, LanguageSpecification
	 * langSpec, File outputDir, String outputPackage, boolean abstractGetters)
	 * {
	 * 
	 * // Assign required package names to be constructed String jpClassPackage
	 * = outputPackage.isEmpty() ? "" : outputPackage + "."; String
	 * weaverPackage = outputPackage; String aWeaverPackage = jpClassPackage +
	 * "abstracts.weaver"; String aJPPackage = weaverPackage + ".abstracts";
	 * jpClassPackage += "abstracts.joinpoints"; List<JavaEnum> enums = new
	 * ArrayList<>();
	 * 
	 * // Generate the user defined objects, which are seen in LARA List<String>
	 * definedObjects =
	 * generateUserDefinedObjects(langSpec.getArtifacts().getObjects(),
	 * outputPackage, outputDir);
	 * 
	 * // Abstract class containing all the global elements JavaClass
	 * abstrJPClass = generateAbstractJoinPointClass(langSpec, jpClassPackage,
	 * GenConstants.i, enums, abstractGetters);
	 * 
	 * String superClass = abstrJPClass.getName();
	 * 
	 * // Create a class which can be defined by the user JavaClass userClass =
	 * generateUserClass(superClass, aJPPackage, GenConstants.interfaceName(),
	 * weaverName); userClass.addImport(jpClassPackage + "." +
	 * abstrJPClass.getName()); superClass = userClass.getName();
	 * 
	 * List<JavaClass> classes = generateClasses(langSpec, jpClassPackage,
	 * superClass, enums, abstractGetters);
	 * 
	 * // Generate the Weaver abstraction class JavaClass weaverAbstractClass =
	 * WeaverAbstractGenerator.generate(weaverName, aWeaverPackage, aJPPackage,
	 * langSpec); Utils.generateToFile(outputDir, weaverAbstractClass, true);
	 * 
	 * // Generate the Weaver implementation JavaClass weaverImplClass =
	 * createWeaverImplementation(weaverName, weaverPackage,
	 * weaverAbstractClass, interfaceName, "A" +
	 * Utils.firstCharToUpper(langSpec.getJpModel().getRoot().getClazz()));
	 * boolean generatedWeaverImpl = Utils.generateToFile(outputDir,
	 * weaverImplClass, false); if (!generatedWeaverImpl) System.out.println(
	 * "Note: java class for the weaver '" + weaverName +
	 * "' was not created because the file already exist on the path!");
	 * 
	 * Utils.generateToFile(outputDir, abstrJPClass, true);
	 * Utils.generateToFile(outputDir, userClass, false); for (JavaClass javaC :
	 * classes) { if (javaC.getSuperClass().equals(userClass.getName()))
	 * javaC.addImport(aJPPackage + "." + userClass.getName()); for (Field f :
	 * javaC.getFields()) if (definedObjects.contains(f.getType())) {
	 * 
	 * javaC.addImport(outputPackage + "." + ENTITY + "." + f.getType()); }
	 * 
	 * Utils.generateToFile(outputDir, javaC, true); } for (JavaEnum javaE :
	 * enums) { Utils.generateToFile(outputDir, javaE, true); } }
	 * 
	 * /** Generate a new weaver, according to the input language specification.
	 * <br> Usage: java -jar WeaverGenerator.jar [options] <br>
	 * 
	 * @param args List of available arguments: <table width="100"> <thead> <tr>
	 * <th>Option</th> <th>Long Option</th> <th>Description</th> </tr> <thead>
	 * <tbody> <tr> <td>-h</td> <td>--help</td> <td>Print a help message</td>
	 * </tr> <tr> <td>-o</td> <td>--output &ltdir&gt</td> <td>Change output
	 * directory (default: .)</td> </tr>
	 * 
	 * <tr> <td>-p</td> <td>--package &ltpackageName&gt</td> <td>Define the
	 * package for the java files</td> </tr> <tr> <td>-w</td> <td>--weaver
	 * &ltclassName&gt</td> <td>Name for the class (default: MyWeaver)</td>
	 * </tr> <tr> <td>-x</td> <td>--XMLspec &ltdir&gt</td> <td>Location of the
	 * target language specification (default: .)</td> </tr> </tbody> </table>
	 *
	 * public static void main(String[] args) {
	 * 
	 * WeaverGeneratorOptions opts = new WeaverGeneratorOptions();
	 * 
	 * CommandLine cmdLine = opts.parse(args); if
	 * (cmdLine.hasOption(GeneratorOption.H.getOption())) { opts.help(); return;
	 * }
	 * 
	 * if (cmdLine.hasOption(GeneratorOption.W.getOption())) { weaverName =
	 * cmdLine.getOptionValue(GeneratorOption.W.getOption()); } else weaverName
	 * = GenConstants.getDefaultWeaverName(); if
	 * (cmdLine.hasOption(GeneratorOption.P.getOption())) { outPackage =
	 * cmdLine.getOptionValue(GeneratorOption.P.getOption()); } else outPackage
	 * = GenConstants.getDefaultPackage();
	 * 
	 * if (cmdLine.hasOption(GeneratorOption.X.getOption())) { XMLSpecDir = new
	 * File(cmdLine.getOptionValue(GeneratorOption.X.getOption())); } else
	 * XMLSpecDir = GenConstants.getDefaultxmldir();
	 * 
	 * if (cmdLine.hasOption(GeneratorOption.O.getOption())) { outDir = new
	 * File(cmdLine.getOptionValue(GeneratorOption.O.getOption())); } else
	 * outDir = GenConstants.getDefaultOutputDir();
	 * 
	 * if (cmdLine.hasOption(GeneratorOption.A.getOption())) { abstractGetters =
	 * true; } else abstractGetters = GenConstants.getDefaultAbstractGetters();
	 * 
	 * if (cmdLine.hasOption(GeneratorOption.N.getOption())) { String nodeOpt =
	 * cmdLine.getOptionValue(GeneratorOption.N.getOption()); if (nodeOpt !=
	 * null) nodeGenerics = nodeOpt; else nodeGenerics =
	 * GenConstants.getDefaultNode(); } else nodeGenerics =
	 * GenConstants.getDefaultNode();
	 * 
	 * if (cmdLine.hasOption(GeneratorOption.G.getOption())) { showGraph = true;
	 * } else showGraph = GenConstants.getDefaultShowGraph();
	 * 
	 * printReport(weaverName, outPackage, XMLSpecDir, outDir, showGraph);
	 * 
	 * boolean generated = generate(weaverName, XMLSpecDir, outDir, outPackage,
	 * abstractGetters, showGraph); if (!generated) System.err.println(
	 * "The Weaver was not created!"); else System.out.println(
	 * "Weaver successfully created!"); }
	 * 
	 * private static void printReport(String weaverName, String outPackage,
	 * File xMLSpecDir, File outDir, boolean showGraph) { StringBuilder report =
	 * new StringBuilder(); report.append("Weaver name: " + weaverName + "\n");
	 * report.append("Package:     " + outPackage + "\n"); report.append(
	 * "Lang. Spec:  " + xMLSpecDir + "\n"); report.append("Output Dir.: " +
	 * outDir + "\n"); report.append("Show Graph: " + showGraph + "\n");
	 * System.out.println(report); }
	 */
}
