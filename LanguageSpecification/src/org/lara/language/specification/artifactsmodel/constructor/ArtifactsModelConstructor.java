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

package org.lara.language.specification.artifactsmodel.constructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.lara.language.specification.IModel;
import org.lara.language.specification.artifactsmodel.schema.Artifact;
import org.lara.language.specification.artifactsmodel.schema.ArtifactsList;
import org.lara.language.specification.artifactsmodel.schema.Attribute;
import org.lara.language.specification.artifactsmodel.schema.EnumDef;
import org.lara.language.specification.artifactsmodel.schema.Global;
import org.lara.language.specification.artifactsmodel.schema.ObjectFactory;
import org.lara.language.specification.artifactsmodel.schema.Parameter;
import org.lara.language.specification.artifactsmodel.schema.TypeDef;
import org.lara.language.specification.resources.LanguageSpecificationResources;
import org.xml.sax.SAXException;

import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsIo;
import tdrc.utils.MarshalUtils;

public class ArtifactsModelConstructor implements IModel {
    private static final String ArtifactsModelPackageName = ObjectFactory.class.getPackage().getName();
    private final static QName _Artifacts_QNAME = new QName("", "artifacts");
    protected ArtifactsList artifactsList;
    protected Map<String, Attribute> globalAttributes;
    protected ObjectFactory objFactory;
    // left extends right
    protected Map<String, String> joinPointHierarchy;

    /**
     * Create a new instance of the {@link ArtifactsModelConstructor} according to an xml file specification, plus an
     * xsd schema for validation
     * 
     * @param attributesModelFile
     * @param schema
     * @throws JAXBException
     * @throws SAXException
     * @throws XMLParseException
     * @throws IOException
     */
    public ArtifactsModelConstructor(File attributesModelFile, boolean validate)
            throws JAXBException, SAXException, XMLParseException, IOException {
        this(new StreamSource(attributesModelFile), attributesModelFile.getName(), validate);
    }

    /**
     * Create a new Join Point Model Representation from an XML file
     * 
     * @param joinPointModelFile
     *            the XML file to parse
     * @param validate
     *            use the built-in schema to validate the XML file
     * @throws JAXBException
     *             If the XML is not validated
     * @throws SAXException
     * @throws IOException
     * @throws XMLParseException
     *             If the XML is badly formatted
     */
    public ArtifactsModelConstructor(Source artifactsSource, String sourceName, boolean validate)
            throws JAXBException, SAXException, IOException {
        try (final InputStream iS = SpecsIo
                .resourceToStream(LanguageSpecificationResources.ArtifactsModelSchema.getResource());) {

            setArtifactsList(MarshalUtils.unmarshal(artifactsSource, sourceName, iS, ArtifactsList.class,
                    ArtifactsModelConstructor.ArtifactsModelPackageName, validate));
        }
    }

    /**
     * Create a new instance with a predefined list of artifacts
     * 
     * @param artifactsList
     */
    public ArtifactsModelConstructor(ArtifactsList artifactsList) {

        setArtifactsList(artifactsList);
    }

    /**
     * Create a new instance with an empty artifacts list
     */
    public ArtifactsModelConstructor() {

        setArtifactsList(getObjFactory().createArtifactsList());
    }

    /**
     * Add a new {@link Parameter} in an attribute, in an artifact
     * 
     * @param artifactName
     * @return
     */
    public boolean add(String paramName, String paramType, String attributeName, String artifactName) {

        final Artifact artifact = getArtifact(artifactName);
        if (artifact != null) {

            final Attribute attribute = getAttribute(attributeName, artifact);
            if (attribute != null) {
                final Parameter parameter = objFactory.createParameter();
                parameter.setName(paramName);
                parameter.setType(paramType);
                attribute.getParameter().add(parameter);
            }

        }
        return false;
    }

    /**
     * Add a new {@link Attribute} in an artifact
     * 
     * @param artifactName
     * @return
     */
    public boolean add(String attributeName, String attributeType, String artifactName) {

        final Artifact artifact = getArtifact(artifactName);
        if (artifact != null) {

            final Attribute attribute = objFactory.createAttribute();
            attribute.setName(attributeName);
            attribute.setType(attributeType);
            artifact.getAttribute().add(attribute);
        }
        return false;
    }

    /**
     * Add a new {@link TypeDef} to the artifact model
     * 
     * @param artifactName
     * @return
     */
    public boolean addObject(String objectName, Attribute... params) {

        final TypeDef TypeDef = objFactory.createTypeDef();
        TypeDef.setName(objectName);
        if (params != null) {

            for (final Attribute param : params) {

                TypeDef.getAttribute().add(param);
            }
        }

        return false;
    }

    /**
     * Return a declared object with the given name
     * 
     * @return
     */
    public TypeDef getObject(String objName) {

        for (final TypeDef obj : artifactsList.getObject()) {

            if (obj.getName().equals(objName)) {
                return obj;
            }
        }

        return null;
    }

    /**
     * Return a declared object with the given name
     * 
     * @return
     */
    public EnumDef getEnum(String enumName) {

        for (final EnumDef obj : artifactsList.getEnum()) {

            if (obj.getName().equals(enumName)) {
                return obj;
            }
        }

        return null;
    }

    /**
     * Verifies if an object exists with the given name
     * 
     * @return
     */
    public boolean hasObject(String objName) {

        return getObject(objName) != null;
    }

    /**
     * Return a list of declared objects
     * 
     * @return
     */
    public List<TypeDef> getTypeDefs() {
        List<TypeDef> allTypeDefs = SpecsCollections.newArrayList();
        allTypeDefs.addAll(artifactsList.getObject());
        allTypeDefs.addAll(artifactsList.getTypedef());
        return artifactsList.getObject();
    }

    public List<EnumDef> getEnumDefs() {
        return artifactsList.getEnum();
    }

    public boolean hasEnumDef(String type) {
        return getEnum(type) != null;
    }

    /**
     * Add a new {@link Artifact}
     * 
     * @param artifact
     * @return
     */
    public boolean add(String artifactType) {
        final Artifact artifact = objFactory.createArtifact();
        artifact.setClazz(artifactType);
        return add(artifact);
    }

    /**
     * Add a new {@link Artifact}
     * 
     * @param artifact
     * @return
     */
    public boolean add(Artifact artifact) {

        return artifactsList.getArtifact().add(artifact);
    }

    /**
     * Generate an XML representation of the Artifacts list
     * 
     * @param oStream
     * @throws JAXBException
     */
    @Override
    public void toXML(OutputStream oStream) throws JAXBException {

        MarshalUtils.marshal(artifactsList, ArtifactsList.class, ArtifactsModelConstructor.ArtifactsModelPackageName,
                ArtifactsModelConstructor._Artifacts_QNAME, oStream);
    }

    /**
     * Get a specific attribute from an {@link Artifact}
     * 
     * @param attributeType
     * @param artifact
     * @return the named attribute or null if the artifact does not contain the attribute
     */
    protected Attribute getAttribute(String attributeType, Artifact artifact) {

        if (globalAttributes.containsKey(attributeType)) {

            return globalAttributes.get(attributeType);
        }

        for (final Attribute attribute : artifact.getAttribute()) {

            if (attribute.getName().equals(attributeType)) {
                return attribute;
            }
        }

        final String parent = joinPointHierarchy.get(artifact.getClazz());
        if (parent != null && !parent.equals(artifact.getClazz())) {
            final Artifact parentArtifact = getArtifact(parent);
            return getAttribute(attributeType, parentArtifact);
        }
        return null;
    }

    /**
     * Get all attributes of a named Artifact
     * 
     * @param artifactType
     * @return the attributes pertaining to an artifact or null if the artifact does not exist
     */
    public List<Attribute> getAttributes(String artifactType) {

        for (final Artifact artifact : artifactsList.getArtifact()) {

            if (artifact.getClazz().equals(artifactType)) {
                final List<Attribute> attributes = new ArrayList<>(artifact.getAttribute());
                // attributes.addAll(globalAttributes.values());
                return attributes;
            }
        }
        return null;
    }

    /**
     * Get an Artifact instance of a certain name
     * 
     * @param artifactType
     * @return the named artifact or null if the artifact does not exist
     */
    public Artifact getArtifact(String artifactType) {

        for (final Artifact artifact : artifactsList.getArtifact()) {

            if (artifact.getClazz().equals(artifactType)) {
                return artifact;
            }
        }
        return null;
    }

    /**
     * Get an Artifact instance of a certain name or the extended artifact
     * 
     * @param artifactType
     * @return the named artifact or null if the artifact does not exist
     */
    public Artifact getArtifactRecursively(String artifactType) {

        final Artifact artifact = getArtifact(artifactType);
        if (artifact == null && joinPointHierarchy.containsKey(artifactType)) {

            final String parentType = joinPointHierarchy.get(artifactType);

            if (!parentType.equals(artifactType)) {
                return getArtifactRecursively(parentType);
            }
        }

        return artifact;
    }

    /**
     * Get all attributes pertaining to the artifact, including inheritted attributes
     * 
     * @param artifactType
     * @return the named artifact or null if the artifact does not exist
     */
    public List<Attribute> getAttributesRecursively(String artifactType) {

        List<Attribute> attributes = new ArrayList<>();

        fillAttributesListRecursively(artifactType, attributes);

        attributes.addAll(globalAttributes.values());

        return attributes;

    }

    private void fillAttributesListRecursively(String artifactType, List<Attribute> attributes) {

        final Artifact artifact = getArtifactRecursively(artifactType);
        if (artifact != null) {
            attributes.addAll(artifact.getAttribute());

            final String parentType = joinPointHierarchy.get(artifactType);

            if (!parentType.equals(artifactType)) {
                fillAttributesListRecursively(parentType, attributes);
            }
        }
    }

    /**
     * Converts the join point model into a tree-type String
     * 
     * @return
     */
    @Override
    public String toString() {
        String ret = "";
        ret += "----------- Artifacts -----------\n";
        if (artifactsList.getGlobal() != null) {
            ret += "GLOBAL\n";
            for (final Attribute attribute : artifactsList.getGlobal().getAttribute()) {

                ret += "\t" + attribute.getName() + "(" + attribute.getType() + ")\n";
                for (final Parameter param : attribute.getParameter()) {

                    ret += "\t\t" + param.getName() + "(" + param.getType() + ")\n";
                }
            }
        }

        for (final Artifact artifact : artifactsList.getArtifact()) {

            ret += artifact.getClazz() + "\n";
            for (final Attribute attribute : artifact.getAttribute()) {

                ret += "\t" + attribute.getName() + "(" + attribute.getType() + ")\n";
                for (final Parameter param : attribute.getParameter()) {

                    ret += "\t\t" + param.getName() + "(" + param.getType() + ")\n";
                }
            }
        }
        if (!artifactsList.getObject().isEmpty()) {

            ret += "\n----------- Objects -----------\n";
            for (final TypeDef TypeDef : artifactsList.getObject()) { // Maintaining for backwards compatibility

                ret += TypeDef.getName() + "\n";
                for (final Attribute attribute : TypeDef.getAttribute()) {

                    ret += "\t" + attribute.getName() + "(" + attribute.getType() + ")\n";
                    for (final Parameter param : attribute.getParameter()) {

                        ret += "\t\t" + param.getName() + "(" + param.getType() + ")\n";
                    }
                }
            }
            for (final TypeDef TypeDef : artifactsList.getTypedef()) {

                ret += TypeDef.getName() + "\n";
                for (final Attribute attribute : TypeDef.getAttribute()) {

                    ret += "\t" + attribute.getName() + "(" + attribute.getType() + ")\n";
                    for (final Parameter param : attribute.getParameter()) {

                        ret += "\t\t" + param.getName() + "(" + param.getType() + ")\n";
                    }
                }
            }
        }
        return ret;
    }

    /**
     * @return the artifactsList
     */
    public ArtifactsList getArtifactsList() {
        return artifactsList;
    }

    /**
     * @param artifactsList
     *            the artifactsList to set
     */
    public void setArtifactsList(ArtifactsList artifactsList) {
        this.artifactsList = artifactsList;
        sanitizeAndMap();
    }

    /**
     * Sanitize the artifacts model and create mappings between artifacts and attributes
     */
    private void sanitizeAndMap() {
        joinPointHierarchy = new HashMap<>();

        if (artifactsList.getGlobal() == null) {

            artifactsList.setGlobal(new Global());
        }
        final List<Attribute> globalAttributesList = artifactsList.getGlobal().getAttribute();
        final int initCapacity = globalAttributesList.size();
        globalAttributes = new HashMap<>(initCapacity);
        for (final Attribute attr : globalAttributesList) {

            globalAttributes.put(attr.getName(), attr);
        }
    }

    /**
     * @return the objFactory
     */
    public ObjectFactory getObjFactory() {
        if (objFactory == null) {
            objFactory = new ObjectFactory();
        }
        return objFactory;
    }

    /**
     * @param objFactory
     *            the objFactory to set
     */
    public void setObjFactory(ObjectFactory objFactory) {
        this.objFactory = objFactory;
    }

    @Override
    public boolean contains(String name) {

        return getArtifact(name) != null;
    }

    @Override
    public boolean contains(String name, String subname) {
        final Artifact artifact = getArtifact(name);
        if (artifact == null) {
            return false;
        }
        if (artifactsList.getGlobal() != null) {

            if (globalAttributes.containsKey(subname)) {
                return true;
            }
        }
        return getAttribute(subname, artifact) != null;
    }
}
