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

package org.lara.language.specification.artifactsmodel;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

import org.lara.language.specification.artifactsmodel.constructor.ArtifactsModelConstructor;
import org.lara.language.specification.artifactsmodel.schema.Artifact;
import org.lara.language.specification.artifactsmodel.schema.ArtifactsList;
import org.lara.language.specification.artifactsmodel.schema.Attribute;
import org.lara.language.specification.joinpointmodel.JoinPointModel;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.xml.sax.SAXException;

import pt.up.fe.specs.util.SpecsCollections;

public class ArtifactsModel extends ArtifactsModelConstructor {

    /**
     * Create a new instance of the {@link ArtifactsModel} according to an xml file specification, plus an xsd schema
     * for validation
     * 
     * @param attributesModelFile
     * @param validate
     * @throws JAXBException
     * @throws SAXException
     * @throws XMLParseException
     * @throws IOException
     */
    public ArtifactsModel(File attributesModelFile, boolean validate)
            throws JAXBException, SAXException, XMLParseException, IOException {
        super(attributesModelFile, validate);

    }

    /**
     * Create a new instance of the {@link ArtifactsModel} according to an stream source specification, plus an xsd
     * schema for validation
     * 
     * @param attributesModelFile
     * @param validate
     * @throws JAXBException
     * @throws SAXException
     * @throws XMLParseException
     * @throws IOException
     */
    public ArtifactsModel(Source attributesModel, String sourceName, boolean validate)
            throws JAXBException, SAXException, XMLParseException, IOException {

        super(attributesModel, sourceName, validate);

    }

    /**
     * Create a new instance with a predefined list of artifacts
     * 
     * @param artifactsList
     */
    public ArtifactsModel(ArtifactsList artifactsList) {

        super(artifactsList);

    }

    /**
     * Create a new instance with an empty artifacts list
     */
    public ArtifactsModel() {

        super();

    }

    /**
     * This method sanitizes the artifacts according to the join point model, allowing the use of join point hierarchy
     * for the attributes
     * 
     * @param jpm
     */
    public void sanitizeByJoinPointModel(JoinPointModel jpm) {
        // For each join point: associate the parent type
        for (final JoinPointType jp : jpm.getJoinPointList().getJoinpoint()) {
            final Object _extends = jp.getExtends();
            if (_extends != null) {
                // if (!jp.getClazz().equals(((JoinPointType) _extends).getClazz())) { //change code to accept this
                // condition!
                // System.out.println("HAPPENING FOR: " + jp.getClazz());
                joinPointHierarchy.put(jp.getClazz(), ((JoinPointType) _extends).getClazz());
                // }
            }
        }
    }

    /**
     * Get a specific attribute from an artifact
     * 
     * @param artifactType
     * @param attributeName
     * @return the {@link Attribute} instance, or null if the artifact or the attribute does not exist
     */
    public Attribute getAttribute(String artifactType, String attributeName) {

        final Artifact artifact = getArtifact(artifactType);

        if (artifact == null) {
            if (joinPointHierarchy.containsKey(artifactType)) {
                final String parentArtifact = joinPointHierarchy.get(artifactType);
                return getAttribute(parentArtifact, attributeName);
            }

            return null;
        }
        return getAttribute(attributeName, artifact);
    }

    /**
     * Get a specific attribute from an artifact, searching the super types if necessary
     * 
     * @param artifactType
     * @param attributeName
     * @return the {@link Attribute} instance, or null if the artifact or the attribute does not exist
     */
    public Attribute getAttributeRecursively(String artifactType, String attributeName) {

        final Artifact artifact = getArtifact(artifactType);

        if (artifact == null) {
            if (joinPointHierarchy.containsKey(artifactType)) {
                final String parentArtifact = joinPointHierarchy.get(artifactType);
                return getAttribute(parentArtifact, attributeName);
            }

            return null;
        }
        return getAttribute(attributeName, artifact);
    }

    /**
     * Get a specific attribute from an artifact
     * 
     * @param artifactType
     * @param attributeName
     * @return the list of {@link Attribute} instance that are definable, or null if the artifact or the attribute does
     *         not exist
     */
    public List<Attribute> getAllDefinableAttributes(String artifactType) {

        Artifact artifact = getArtifact(artifactType);

        if (artifact == null) {
            if (joinPointHierarchy.containsKey(artifactType)) {
                final String parentArtifact = joinPointHierarchy.get(artifactType);
                if (!parentArtifact.equals(artifactType)) {
                    return getAllDefinableAttributes(parentArtifact);
                } else {
                    Predicate<? super Attribute> hasDefs = a -> !a.getDef().isEmpty();
                    return globalAttributes.values().stream().filter(hasDefs).collect(Collectors.toList());
                }
            }
            return null;
        }
        List<Attribute> definables = SpecsCollections.newArrayList();
        Predicate<? super Attribute> hasDefs = a -> !a.getDef().isEmpty();
        globalAttributes.values().stream().filter(hasDefs).forEach(definables::add);
        artifact.getAttribute().stream().filter(hasDefs).forEach(definables::add);

        String parent = joinPointHierarchy.get(artifact.getClazz());
        while (parent != null && !parent.equals(artifact.getClazz())) {
            artifact = getArtifact(parent);
            if (artifact == null) {
                throw new RuntimeException("Unnexpected problem with join point hierarchy: join point " + parent
                        + " does not exist in the model");
            }
            artifact.getAttribute().stream().filter(hasDefs).forEach(definables::add);
            parent = joinPointHierarchy.get(artifact.getClazz());
        }
        return definables;

    }

    /**
     * Get a specific attribute from an artifact
     * 
     * @param artifactType
     * @param attributeName
     * @return the list of {@link Attribute} instance that are definable, or null if the artifact or the attribute does
     *         not exist
     */
    public List<Attribute> getOwnDefinableAttributes(String artifactType) {
        Artifact artifact = getArtifact(artifactType);

        if (artifact == null) {
            if (joinPointHierarchy.containsKey(artifactType)) {
                return Collections.emptyList();
            }
            return null;
        }
        return getOwnDefinableAttributes(artifact);
    }

    public List<Attribute> getOwnDefinableAttributes(Artifact artifact) {

        Predicate<? super Attribute> hasDefs = a -> !a.getDef().isEmpty();
        return artifact.getAttribute().stream().filter(hasDefs).collect(Collectors.toList());
    }

}
