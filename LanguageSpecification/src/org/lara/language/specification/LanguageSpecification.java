/**
 * Copyright 2013 SPeCS Research Group.
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

package org.lara.language.specification;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.lara.language.specification.actionsmodel.ActionModel;
import org.lara.language.specification.actionsmodel.schema.Action;
import org.lara.language.specification.artifactsmodel.ArtifactsModel;
import org.lara.language.specification.artifactsmodel.schema.Attribute;
import org.lara.language.specification.artifactsmodel.schema.Parameter;
import org.lara.language.specification.exception.LanguageSpecificationException;
import org.lara.language.specification.graph.JPMGraph;
import org.lara.language.specification.joinpointmodel.JoinPointModel;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.lara.language.specification.joinpointmodel.schema.Select;
import org.xml.sax.SAXException;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.providers.ResourceProvider;

/**
 * Class containing the language specification in terms of join point model, artifacts and the available actions
 * 
 * @author Tiago
 * 
 */
public class LanguageSpecification {

    public static final String ACTION_MODEL_NAME = "actionModel";
    public static final String JOIN_POINT_MODEL_NAME = "joinPointModel";
    public static final String ARTIFACTS_NAME = "artifacts";
    public static final String XML_EXTENSION = ".xml";
    /**
     * Information regarding the target language <br>
     * TODO: Make the fields final
     */
    private JoinPointModel jpModel;
    private ArtifactsModel artifacts;
    private ActionModel actionModel;

    private LanguageSpecification(JoinPointModel jpModel, ArtifactsModel artifacts, ActionModel actionModel) {
	this.artifacts = artifacts;
	this.jpModel = jpModel;
	this.actionModel = actionModel;
	this.artifacts.sanitizeByJoinPointModel(jpModel); // Required to access
							  // attributes of
							  // extending join
							  // points
    }

    /**
     * Creates a language specification instance with the files contained in the folder 'specDir'
     * 
     * @param specDir
     *            the source folder of the language specification, should include 3 files:
     *            {@value #JOIN_POINT_MODEL_NAME} {@value #XML_EXTENSION}, {@value #ARTIFACTS_NAME}
     *            {@value #XML_EXTENSION} and {@value #ACTION_MODEL_NAME} {@value #XML_EXTENSION}
     * @param validate
     * @return
     */
    public static LanguageSpecification newInstance(File specDir, boolean validate) {

	if (!specDir.exists() || !specDir.isDirectory()) {
	    throw new RuntimeException("Language Specification directory is invalid: " + specDir.getAbsolutePath());
	}

	try {
	    // Language specification files
	    final File artifactsFile = getSpecificationFile(specDir,
		    LanguageSpecification.ARTIFACTS_NAME + LanguageSpecification.XML_EXTENSION);
	    final File jpModelFile = getSpecificationFile(specDir,
		    LanguageSpecification.JOIN_POINT_MODEL_NAME + LanguageSpecification.XML_EXTENSION);
	    final File actionModelFile = getSpecificationFile(specDir,
		    LanguageSpecification.ACTION_MODEL_NAME + LanguageSpecification.XML_EXTENSION);

	    return new LanguageSpecification(new JoinPointModel(jpModelFile, validate),
		    new ArtifactsModel(artifactsFile, validate), ActionModel.newInstance(actionModelFile, validate));
	} catch (final Exception e) {
	    throw new LanguageSpecificationException(
		    "Could not create a Language Specification from folder '" + specDir + "'", e);
	}
	/*
	 * // Instantiations setArtifacts(new ArtifactsModel(artifactsFile,
	 * validate)); setJpModel(new JoinPointModel(jpModelFile, validate));
	 * setActionModel(new ActionModel(actionModelFile, validate));
	 */

    }

    /*
     * public LanguageSpecification(InputStream joinPointModelResource,
     * InputStream artifactsModelResource, InputStream actionModelResource,
     * boolean validate) throws JAXBException, SAXException, XMLParseException {
     * this(new StreamSource(joinPointModelResource), new
     * StreamSource(artifactsModelResource), new StreamSource(
     * actionModelResource), validate); }
     */
    /*
     * public LanguageSpecification(Source joinPointModelResource, Source
     * artifactsModelResource, Source actionModelResource, boolean validate)
     * throws JAXBException, SAXException, XMLParseException { setArtifacts(new
     * ArtifactsModel(artifactsModelResource, "default", validate));
     * setJpModel(new JoinPointModel(joinPointModelResource, "default",
     * validate)); setActionModel(new ActionModel(actionModelResource,
     * "default", validate)); }
     */
    // public static LanguageSpecification
    // newInstance(LanguageSpecificationResources resources) {
    /**
     * Creates a language specification instance with the files contained in a given package (resource)
     * 
     * @param packageName
     *            the name of the package containing the language specification, should include 3 files:
     *            {@value #JOIN_POINT_MODEL_NAME} {@value #XML_EXTENSION}, {@value #ARTIFACTS_NAME}
     *            {@value #XML_EXTENSION} and {@value #ACTION_MODEL_NAME} {@value #XML_EXTENSION}
     * @param validate
     * @return
     */
    public static LanguageSpecification newInstance(String packageName, boolean validate) {

	final String resourceRoot = SpecsStrings.packageNameToResource(packageName);
	/*
	 * if (!packageName.endsWith("/")) { packageName += "/"; }
	 */
	final String joinPointModelResource = resourceRoot + LanguageSpecification.JOIN_POINT_MODEL_NAME
		+ LanguageSpecification.XML_EXTENSION;
	final String artifactsModelResource = resourceRoot + LanguageSpecification.ARTIFACTS_NAME
		+ LanguageSpecification.XML_EXTENSION;
	final String actionModelResource = resourceRoot + LanguageSpecification.ACTION_MODEL_NAME
		+ LanguageSpecification.XML_EXTENSION;

	return LanguageSpecification.newInstance(() -> joinPointModelResource, () -> artifactsModelResource,
		() -> actionModelResource, validate);
	/*
	 * Source jpmSource = new
	 * StreamSource(IoUtils.resourceToStream(joinPointModelResource));
	 * Source armSource = new
	 * StreamSource(IoUtils.resourceToStream(artifactsModelResource));
	 * Source acmSource = new
	 * StreamSource(IoUtils.resourceToStream(actionModelResource));
	 * 
	 * LanguageSpecification langSpec = null; try { langSpec = new
	 * LanguageSpecification(jpmSource, armSource, acmSource, false); }
	 * catch (Exception e) { LoggingUtils.msgWarn(
	 * "Problem parsing default weaver", e);
	 * 
	 * } return langSpec;
	 */
    }

    /*
     * public static LanguageSpecification newInstance(String
     * joinPointModelResource, String artifactsModelResource, String
     * actionModelResource, boolean validate) { return newInstance(() ->
     * joinPointModelResource, () -> artifactsModelResource, () ->
     * actionModelResource, validate); }
     */

    /**
     * Creates a language specification instance based on three {@link ResourceProvider} for join point , artifacts and
     * action model, respectively
     * 
     * @param packageName
     *            the name of the package containing the language specification, should include 3 files:
     *            {@value #JOIN_POINT_MODEL_NAME} {@value #XML_EXTENSION}, {@value #ARTIFACTS_NAME}
     *            {@value #XML_EXTENSION} and {@value #ACTION_MODEL_NAME} {@value #XML_EXTENSION}
     * @param validate
     * @return
     */
    public static LanguageSpecification newInstance(ResourceProvider joinPointModelResource,
	    ResourceProvider artifactsModelResource, ResourceProvider actionModelResource, boolean validate) {

	try (InputStream joinpointStream = SpecsIo.resourceToStream(joinPointModelResource);
		InputStream artifactsStream = SpecsIo.resourceToStream(artifactsModelResource)) {

	    final JoinPointModel jpModel = new JoinPointModel(new StreamSource(joinpointStream), "default", validate);
	    final ArtifactsModel artifactsModel = new ArtifactsModel(new StreamSource(artifactsStream), "default",
		    validate);
	    final ActionModel actionModel = ActionModel.newInstance(actionModelResource, "default", validate);

	    return new LanguageSpecification(jpModel, artifactsModel, actionModel);
	} catch (final Exception e) {
	    throw new LanguageSpecificationException("Could not create Language Specification from resources", e);
	}
    }

    /**
     * Provide enumeration with the resources, in the following order inside the enum: <br>
     * - join point model<br>
     * - artifacts model<br>
     * - action model<br>
     * 
     * @param resourceClass
     * @return
     */
    /*
     * public static <K extends Enum<K> & ResourceProvider>
     * LanguageSpecification newInstance(Class<K> resourceClass, boolean
     * validate) {
     * 
     * List<K> enums = EnumUtils.extractValuesV2(resourceClass);
     * 
     * if (enums.size() < 3) { throw new RuntimeException("Given enumeration '"
     * + resourceClass +
     * "' needs at least 3 elements, by this order: join point model, artifacts model, action model"
     * ); }
     * 
     * // Add the first three enums InputStream joinpointStream =
     * IoUtils.resourceToStream(enums.get(0)); InputStream artifactsStream =
     * IoUtils.resourceToStream(enums.get(1)); InputStream actionStream =
     * IoUtils.resourceToStream(enums.get(2));
     * 
     * LanguageSpecification languageSpecification = null; try {
     * languageSpecification = new LanguageSpecification(joinpointStream,
     * artifactsStream, actionStream, validate); } catch (Exception e) { throw
     * new RuntimeException(e); }
     * 
     * return languageSpecification; }
     */
    /**
     * @param xmlSourceDir
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private static File getSpecificationFile(File xmlSourceDir, String specificationFileName)
	    throws IOException, ParserConfigurationException, SAXException {
	File specFile;
	specFile = new File(xmlSourceDir, specificationFileName);

	if (!specFile.exists()) {
	    throw new IOException("The specification file " + specificationFileName + " could not be found in: "
		    + xmlSourceDir.getAbsolutePath());
	}
	return specFile;
    }

    @Override
    public String toString() {
	return "########## Language Specification ##########\n" + jpModel + artifacts + actionModel;
    }

    /**
     * @return the artifacts
     */
    public ArtifactsModel getArtifacts() {
	return artifacts;
    }

    /**
     * @param artifacts
     *            the artifacts to set
     */
    public void setArtifacts(ArtifactsModel artifacts) {
	this.artifacts = artifacts;
    }

    /**
     * @return the jpModel
     */
    public JoinPointModel getJpModel() {
	return jpModel;
    }

    /**
     * @param jpModel
     *            the jpModel to set
     */
    public void setJpModel(JoinPointModel jpModel) {
	this.jpModel = jpModel;
    }

    /**
     * @return the actionModel
     */
    public ActionModel getActionModel() {
	return actionModel;
    }

    /**
     * @param actionModel
     *            the actionModel to set
     */
    public void setActionModel(ActionModel actionModel) {
	this.actionModel = actionModel;
    }

    public JPMGraph createGraph(String graphName, File outputDir, String outputPackage) {
	final JPMGraph graph = new JPMGraph("TestWeaver");
	for (final JoinPointType joinPoint : jpModel.getJoinPointList().getJoinpoint()) {
	    final String clazz = joinPoint.getClazz();
	    final JoinPointType extend = (JoinPointType) joinPoint.getExtends();
	    if (!joinPoint.equals(extend)) {

		graph.addExtend(clazz, extend.getClazz());
	    }

	    joinPoint.getSelect().forEach(select -> {

		String alias = null;
		final JoinPointType superJoinPoint = (JoinPointType) select.getClazz();
		final String aliasText = select.getAlias();
		if (!aliasText.equals(superJoinPoint.getClazz())) {
		    alias = aliasText;
		}

		graph.addSelect(alias, clazz, superJoinPoint.getClazz());
	    });
	}
	//
	// graph.addNode("File", new JPNodeInfo("File"));
	// graph.addNode("Function", new JPNodeInfo("Function"));
	// graph.addNode("Node", new JPNodeInfo("Function"));
	// graph.addConnection("File", "Function", JPEdgeInfo.newSelects("File",
	// "Function"));
	// graph.addConnection("Function", "Node",
	// JPEdgeInfo.newExtends("Function", "Node"));
	// // System.out.println(graph);
	return graph;
    }

    /**
     * Print all information regarding each join point
     * 
     * @param engine
     */
    public void print() {
	JoinPointModel jpModel = getJpModel();
	ArtifactsModel artifacts = getArtifacts();
	ActionModel actionModel = getActionModel();
	List<JoinPointType> joinpoints = jpModel.getJoinPointList().getJoinpoint();
	for (JoinPointType joinPointType : joinpoints) {
	    String clazz = joinPointType.getClazz();
	    JoinPointType extend = (JoinPointType) joinPointType.getExtends();
	    System.out.print("joinpoint " + clazz);
	    if (!extend.equals(joinPointType)) {
		System.out.print(" extends " + extend.getClazz());
	    }
	    System.out.println(" {");

	    List<Attribute> artifact = artifacts.getAttributes(clazz);

	    System.out.println("\n\t// artifacts");
	    // System.out.println("\tartifacts {");
	    if (artifact != null) {
		for (Attribute attribute : artifact) {
		    System.out.print("\t" + attribute.getType() + " " + attribute.getName());
		    List<Parameter> parameters = attribute.getParameter();
		    if (parameters != null && !parameters.isEmpty()) {
			System.out.print("(");
			String params = parameters.stream()
				.map(param -> param.getType() + " " + param.getType())
				.collect(Collectors.joining(", "));
			System.out.print(params + ")");
		    }
		    System.out.println();
		}
	    }
	    // System.out.println("\t}");

	    List<Select> selects = jpModel.getAllSelects(joinPointType);

	    System.out.println("\n\tselects {");
	    // if (selects != null) {
	    for (Select select : selects) {
		JoinPointType clazz2 = (JoinPointType) select.getClazz();
		String jpSelect = clazz2.getClazz();
		System.out.print("\t\t" + jpSelect);
		if (!jpSelect.equals(select.getAlias())) {
		    System.out.print(" as " + select.getAlias());
		}
		System.out.println();

	    }
	    // }

	    System.out.println("\t}");

	    List<Action> actions = actionModel.getAllJoinPointActions(joinPointType,
		    jpModel);

	    System.out.println("\n\tactions {");
	    for (Action action : actions) {

		List<org.lara.language.specification.actionsmodel.schema.Parameter> parameters = action
			.getParameter();
		String params = "";
		if (parameters != null && !parameters.isEmpty()) {
		    params = parameters.stream()
			    .map(param -> param.getType() + " " + param.getType())
			    .collect(Collectors.joining(", "));

		}

		System.out.println("\t\t" + action.getName() + "(" + params + ")");
	    }
	    System.out.println("\t\tinsert [ before | after | replace ] [ %{<code>}% | '<code>' ]");
	    System.out.println("\t\tdef <attribute> = <expr>");
	    System.out.println("\t}");

	    System.out.println("}\n");

	}
    }
}