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

package org.lara.language.specification.joinpointmodel.constructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.lara.language.specification.IModel;
import org.lara.language.specification.joinpointmodel.schema.GlobalJoinPoints;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.lara.language.specification.joinpointmodel.schema.JoinPointsList;
import org.lara.language.specification.joinpointmodel.schema.ObjectFactory;
import org.lara.language.specification.joinpointmodel.schema.Select;
import org.lara.language.specification.resources.LanguageSpecificationResources;
import org.xml.sax.SAXException;

import pt.up.fe.specs.util.SpecsIo;
import tdrc.utils.MarshalUtils;
import tdrc.utils.Pair;
import tdrc.utils.PairList;
import tdrc.utils.StringUtils;
import utils.SelectionPath;

/**
 * Utility class to use over the language specification
 * 
 * @author Tiago
 * 
 */
public class JoinPointModelConstructor implements IModel {

    private final static QName _JoinPoints_QNAME = new QName("", "joinpoints");
    public static final String JoinPointModelPackageName = ObjectFactory.class.getPackage().getName();

    private boolean alreadyFound; // Auxiliary field
    protected JoinPointsList joinPointList;
    protected Map<String, JoinPointType> joinPoints;
    protected Map<JoinPointType, List<String>> selects;
    protected Map<String, Select> globalSelects;

    /**
     * Create a new Join Point Model Representation with a join point list
     * 
     * @param jps
     */
    public JoinPointModelConstructor(JoinPointsList jps) {
        setJoinPointList(jps);
        sanitizeAndMap();
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
     * @throws XMLParseException
     *             If the XML is badly formatted
     * @throws IOException
     */
    public JoinPointModelConstructor(File joinPointModelFile, boolean validate)
            throws JAXBException, SAXException, XMLParseException, IOException {
        this(new StreamSource(joinPointModelFile), joinPointModelFile.getAbsolutePath(), validate);
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
     * @throws XMLParseException
     *             If the XML is badly formatted
     * @throws IOException
     */
    public JoinPointModelConstructor(Source joinPointModelSource, String sourceName, boolean validate)
            throws JAXBException, SAXException, XMLParseException, IOException {
        try (InputStream iS = SpecsIo
                .resourceToStream(LanguageSpecificationResources.JoinPointModelSchema.getResource());) {
            joinPointList = MarshalUtils.unmarshal(joinPointModelSource, sourceName, iS, JoinPointsList.class,
                    JoinPointModelConstructor.JoinPointModelPackageName, validate);
            sanitizeAndMap();
        }
    }

    /**
     * Generate an XML representation of the Join Point Model
     * 
     * @param oStream
     * @throws JAXBException
     */
    @Override
    public void toXML(OutputStream oStream) throws JAXBException {

        MarshalUtils.marshal(joinPointList, JoinPointsList.class, JoinPointModelConstructor.JoinPointModelPackageName,
                JoinPointModelConstructor._JoinPoints_QNAME, oStream);
    }

    /**
     * Get the path from an existent join point root (inclusive) to the target join point (inclusive)
     * 
     * @param tag
     *            the join point target
     * @return the first path to the join point (Depth First Search)
     */
    public SelectionPath selectionPath(String joinPointName) {
        final JoinPointType joinPointRoot = getRoot();
        SelectionPath selPath = new SelectionPath(joinPointRoot, joinPointName);
        Stack<Select> path = new Stack<>();
        alreadyFound = false;
        // Verify if the join point exist
        // Warning: the join point to select may be an alias for a selectable
        // join point
        // E.g.: select input end -> the input join point is an alias for 'var'
        // Hence, if this getter returns null it may not be an error!
        if (joinPointName.equals(joinPointList.getRootAlias())) {
            Select e = new Select();
            e.setAlias(joinPointList.getRootAlias());
            e.setClazz(joinPointRoot);
            path.add(e);
            selPath.setPath(path);
            return selPath;
        }

        final Set<JoinPointType> ignoreSet = new HashSet<>();

        path = selectionPathAux(joinPointRoot, joinPointName, new ArrayList<JoinPointType>(), ignoreSet, selPath);

        if (!path.isEmpty()) {
            Select e = new Select();
            e.setAlias(joinPointList.getRootAlias());
            e.setClazz(joinPointRoot);
            path.add(e);
            selPath.setPath(path);
            if (selPath.hasSecondaryPath()) {
                selPath.getSecondaryPath().get().add(e);
            }
        }

        return selPath;
    }

    /**
     * Auxiliary method to get the path to a join point
     * 
     * @param current
     * @param joinPointName
     * @param visitedList
     * @param ignoreSet
     * @param selPath
     * @return
     */
    private Stack<Select> selectionPathAux(JoinPointType current, String joinPointName,
            ArrayList<JoinPointType> visitedList, Set<JoinPointType> ignoreSet, SelectionPath selPath) {

        Stack<Select> path = new Stack<>();

        if (ignoreSet.contains(current) || visitedList.contains(current)) {

            return path;
        }

        final Select selected = select(current, joinPointName);
        if (selected != null) {

            path.push(selected);
            return path;
        }

        visitedList.add(current);

        for (final Select select : current.getSelect()) {
            final JoinPointType next = getJoinPoint(select);
            Stack<Select> pathAux = selectionPathAux(next, joinPointName, visitedList, ignoreSet, selPath);
            if (!pathAux.isEmpty()) {
                pathAux.add(select);
                if (selPath.hasSecondaryPath()) {
                    selPath.getSecondaryPath().get().add(select);
                }
                if (!path.isEmpty()) {
                    if (!alreadyFound) {
                        selPath.setSecondaryPath(pathAux);
                        selPath.setTieBreakReason("use the first path found (from a depth first search)");
                        // System.out.println(
                        // "More than one path for inital join point '" + joinPointName + "'. Two of then are: ");
                        // Function<Pair<String, String>, String> converted = p -> {
                        // if (p.getLeft().equals(p.getRight())) {
                        // return p.getLeft();
                        // }
                        // return "(" + p.getRight() + " as " + p.getLeft() + ")";
                        // };
                        //
                        // System.out.println("\t1. " + StringUtils.join(path, converted, "."));
                        // System.out.println("\t2. " + StringUtils.join(pathAux, converted, "."));
                        //
                        // System.out.println("Select uses a depth first search. Will use 1.");
                        alreadyFound = true;
                    }
                    break;
                }
                path = pathAux;
            } else {
                ignoreSet.add(next);
            }
        }
        visitedList.remove(current);

        return path;
    }

    /**
     * Returns the path between a source join point (exclusive) and a destination join point (inclusive). The Pairs are
     * the select alias and the type of the selected join point
     * 
     * @param sourceJP
     * @param targetJP
     * @return
     */
    public SelectionPath selectionPath(String sourceJP, String targetJP, boolean validatePath) {

        alreadyFound = false;
        // Verify if the join point exist
        final JoinPointType source = getJoinPoint(joinPointList, sourceJP);
        SelectionPath selPath = new SelectionPath(source, targetJP);

        if (source == null) {
            if (!validatePath) {
                return selPath;
            }
            throw new RuntimeException(
                    "The source join point type '" + sourceJP + "' does not exist in the join point model.");
        }
        Stack<Select> path = new Stack<>();
        final Select selected = select(source, targetJP);
        if (selected != null) {

            path.push(selected);
            selPath.setPath(path);
            return selPath;
        } else {

            path = selectWithMiddlePath(targetJP, source, selPath);
            if (!path.isEmpty()) {
                selPath.setPath(path);
            }
        }

        return selPath;

    }

    private Stack<Select> selectWithMiddlePath(String targetJP, JoinPointType source,
            SelectionPath selPath) {
        Stack<Select> path = new Stack<>();

        final Set<JoinPointType> ignoreSet = new HashSet<>();
        ignoreSet.add(source);
        for (final Select select : source.getSelect()) {
            final JoinPointType next = select.getClazz();
            final Stack<Select> pathAux = selectionPathAux(next, targetJP, new ArrayList<JoinPointType>(),
                    ignoreSet, selPath);
            if (!pathAux.isEmpty()) {
                pathAux.push(select);
                if (!path.isEmpty()) {
                    if (!alreadyFound) {
                        // System.out.println(
                        // "More than one path for inital join point '" + targetJP + "'. Two of then are: ");
                        // System.out.println("\t1. " + path + "->" + targetJP);
                        // System.out.println("\t2. " + pathAux + "->" + targetJP);

                        int pos = 1;
                        Select first = path.get(0);
                        Select second = pathAux.get(0);
                        while (first.equals(second)) {// && pos < max) { <-- this should not happen in these conditions
                            first = path.get(pos);
                            second = pathAux.get(pos);
                            pos++;
                        }
                        boolean firstWOalias = first.getAlias().equals(first.getClazz().getClazz());
                        boolean secondWOAlias = second.getAlias().equals(second.getClazz().getClazz());
                        if (first.getClazz().equals(second.getClazz())) {
                            selPath.setTieBreakReason(
                                    "use the one with specific join point type (i.e. without label)");
                            if (!firstWOalias && secondWOAlias) {
                                selPath.setSecondaryPath(path);
                                path = pathAux;
                            } else {
                                selPath.setSecondaryPath(pathAux);
                            }
                        } else {
                            selPath.setTieBreakReason(
                                    "use the first path found as primary (from a depth first search)");
                            selPath.setSecondaryPath(pathAux);
                        }
                        alreadyFound = true;
                    }
                    break;
                }
                path = pathAux;
            } else {
                ignoreSet.add(next);
            }
        }
        if (path.isEmpty() && !source.getExtends().equals(source)) {

            source = (JoinPointType) source.getExtends();
            return selectWithMiddlePath(targetJP, source, selPath);
        }
        return path;
    }

    /**
     * Get the path from an existent join point root (inclusive) to the target join point (inclusive)
     * 
     * @param tag
     *            the join point target
     * @return the first path to the join point (Depth First Search)
     */
    public PairList<String, String> getPath(String joinPointName) {
        PairList<String, String> path = new PairList<>();
        alreadyFound = false;
        // Verify if the join point exist
        // Warning: the join point to select may be an alias for a selectable
        // join point
        // E.g.: select input end -> the input join point is an alias for 'var'
        // Hence, if this getter returns null it may not be an error!
        final JoinPointType joinPointRoot = getRoot();
        if (joinPointName.equals(joinPointList.getRootAlias())) {

            path.add(joinPointName, joinPointRoot.getClazz());
            return path;
        }

        final Set<JoinPointType> ignoreSet = new HashSet<>();

        path = getPathAux(joinPointRoot, joinPointName, new ArrayList<JoinPointType>(), ignoreSet);

        if (!path.isEmpty()) {
            path.add(0, new Pair<>(joinPointList.getRootAlias(), joinPointRoot.getClazz()));
        }

        return path;
    }

    /**
     * Returns the path between a source join point (exclusive) and a destination join point (inclusive). The Pairs are
     * the select alias and the type of the selected join point
     * 
     * @param sourceJP
     * @param targetJP
     * @return
     */
    public PairList<String, String> getPath(String sourceJP, String targetJP, boolean validatePath) {
        PairList<String, String> path = new PairList<>();
        alreadyFound = false;
        // Verify if the join point exist
        final JoinPointType source = getJoinPoint(joinPointList, sourceJP);

        if (source == null) {
            if (!validatePath) {
                return null;
            }
            throw new RuntimeException(
                    "The source join point type '" + sourceJP + "' does not exist in the join point model.");
        }

        final Select selected = select(source, targetJP);
        if (selected != null) {

            path.add(selected.getAlias(), getJoinPointClass(selected));
            return path;
        }

        path = selectWithMiddlePath(targetJP, source);

        return path;

    }

    private PairList<String, String> selectWithMiddlePath(String targetJP, JoinPointType source) {
        PairList<String, String> path = new PairList<>();

        final Set<JoinPointType> ignoreSet = new HashSet<>();
        ignoreSet.add(source);
        for (final Select select : source.getSelect()) {
            final JoinPointType next = getJoinPoint(select);
            final PairList<String, String> pathAux = getPathAux(next, targetJP, new ArrayList<JoinPointType>(),
                    ignoreSet);
            if (!pathAux.isEmpty()) {
                pathAux.add(0, new Pair<>(select.getAlias(), getJoinPointClass(select)));
                if (!path.isEmpty()) {

                    if (!alreadyFound) {

                        System.out.println(
                                "More than one path for inital join point '" + targetJP + "'. Two of then are: ");
                        System.out.println("\t1. " + path + "->" + targetJP);
                        System.out.println("\t2. " + pathAux + "->" + targetJP);

                        int pos = 1;
                        Pair<String, String> first = path.get(0);
                        Pair<String, String> second = pathAux.get(0);
                        while (first.equals(second)) {// && pos < max) { <-- this should not happen in these conditions
                            first = path.get(pos);
                            second = pathAux.get(pos);
                            pos++;
                        }
                        boolean firstWOalias = first.getLeft().equals(first.getRight());
                        boolean secondWOAlias = second.getLeft().equals(second.getRight());
                        if (first.getRight().equals(second.getRight())) {
                            if (firstWOalias && !secondWOAlias) { // first does not have an alias!
                                System.out
                                        .println("Select considers the one with specific join point type. Will use 1.");
                            } else if (!firstWOalias && secondWOAlias) {
                                System.out
                                        .println("Select considers the one with specific join point type. Will use 2.");
                                path = pathAux;
                            }
                        } else {
                            System.out.println("Select uses a depth first search. Will use 1.");
                        }
                        alreadyFound = true;
                    }
                    break;
                }
                path = pathAux;
            } else {
                ignoreSet.add(next);
            }
        }
        if (path.isEmpty() && !source.getExtends().equals(source)) {

            source = (JoinPointType) source.getExtends();
            return selectWithMiddlePath(targetJP, source);
        }

        return path;

    }

    /**
     * Auxiliary method to get the path to a join point
     * 
     * @param current
     * @param joinPointName
     * @param visitedList
     * @param ignoreSet
     * @return
     */
    private PairList<String, String> getPathAux(JoinPointType current, String joinPointName,
            ArrayList<JoinPointType> visitedList, Set<JoinPointType> ignoreSet) {

        PairList<String, String> path = new PairList<>();

        if (ignoreSet.contains(current) || visitedList.contains(current)) {

            return path;
        }

        final Select selected = select(current, joinPointName);
        if (selected != null) {

            path.add(selected.getAlias(), getJoinPointClass(selected));
            return path;
        }

        visitedList.add(current);

        for (final Select select : current.getSelect()) {
            final JoinPointType next = getJoinPoint(select);
            final PairList<String, String> pathAux = getPathAux(next, joinPointName, visitedList, ignoreSet);
            if (!pathAux.isEmpty()) {
                pathAux.add(0, new Pair<>(select.getAlias(), getJoinPointClass(select)));
                if (!path.isEmpty()) {

                    if (!alreadyFound) {
                        System.out.println(
                                "More than one path for inital join point '" + joinPointName + "'. Two of then are: ");
                        Function<Pair<String, String>, String> converted = p -> {
                            if (p.getLeft().equals(p.getRight())) {
                                return p.getLeft();
                            }
                            return "(" + p.getRight() + " as " + p.getLeft() + ")";
                        };

                        System.out.println("\t1. " + StringUtils.join(path, converted, "."));
                        System.out.println("\t2. " + StringUtils.join(pathAux, converted, "."));

                        System.out.println("Select uses a depth first search. Will use 1.");
                        alreadyFound = true;
                    }
                    break;
                }
                path = pathAux;
            } else {
                ignoreSet.add(next);
            }
        }
        visitedList.remove(current);

        return path;
    }

    // UPDATED!

    /**
     * Return the root of a join point model
     * 
     * @return a {@link JoinPointType} which is selectable as root
     */
    public JoinPointType getRoot() {
        return (JoinPointType) joinPointList.getRootClass();
    }

    /**
     * Returns a join point description with a specific type
     * 
     * @param joinPointType
     *            the join point type to search
     * @return a {@link JoinPointType} if the type occurs, null otherwise.
     */
    public JoinPointType getJoinPoint(String joinPointType) {
        return joinPoints.get(joinPointType);
    }

    /**
     * Returns a join point description with a specific type
     * 
     * @param jps
     *            a list of join points
     * @param joinPointType
     *            the join point type to search
     * @return a {@link JoinPointType} if the type occurs, null otherwise.
     */
    public static JoinPointType getJoinPoint(JoinPointsList jps, String joinPointType) {
        for (final JoinPointType jp : jps.getJoinpoint()) {

            if (jp.getClazz().equals(joinPointType)) {
                return jp;
            }
        }
        return null;
    }

    /**
     * Selects a subsequent join point from the given join point
     * 
     * @param jp
     *            the base join point
     * @param joinPointClass
     *            the selected join point
     * @return a {@link Select} if the name occurs, null otherwise.
     */
    public Select select(JoinPointType jp, String joinPointClass) {
        if (globalSelects.containsKey(joinPointClass)) {
            return globalSelects.get(joinPointClass);
        }
        // Verify the selects pertaining to the join point
        for (final Select select : jp.getSelect()) {

            if (select.getAlias().equals(joinPointClass)) {
                return select;
            }
        }
        // If the join point does not contain, verify the super type
        // (recursively!)
        if (!jp.getExtends().equals(jp)) {

            jp = (JoinPointType) jp.getExtends();
            return select(jp, joinPointClass);
        }
        return null;
    }

    /**
     * Searches for a join point with the same description as the {@link Select} element
     * 
     * @param jp
     *            the list of join points
     * @param select
     *            the selected join point to search
     * @return a {@link JoinPointType} if the select exists in the join point model, null otherwise.
     */
    public static JoinPointType getJoinPoint(Select select) {
        final JoinPointType joinPointType = select.getClazz();
        return joinPointType;
    }

    /**
     * Searches for a join point with the same description as the {@link Select} element
     * 
     * @param jp
     *            the list of join points
     * @param select
     *            the selected join point to search
     * @return a {@link JoinPointType} if the select exists in the join point model, null otherwise.
     */
    public static String getJoinPointClass(Select select) {
        return getJoinPointClass(select.getClazz());
    }

    /**
     * Auxiliary method to convert the field Object, from xsd classes, that should be of type JoinPointType, and
     * retrieves the class name of the join point type
     * 
     * @param joinPointType
     * @return
     */
    public static String getJoinPointClass(Object joinPointType) {
        final JoinPointType joinPointTypeCasted = (JoinPointType) joinPointType;
        return joinPointTypeCasted.getClazz();
    }

    /**
     * Sanitize the join point model by creating the types defined in the join point but not created as join point
     * 
     * @param joinPointsList
     */
    private void sanitizeAndMap() {
        joinPoints = new HashMap<>();
        selects = new MapAList<>();
        final ObjectFactory factory = new ObjectFactory();

        if (joinPointList.getRootAlias() == null) {
            final Object rootClass = joinPointList.getRootClass();
            joinPointList.setRootAlias(getJoinPointClass(rootClass));
        }

        if (joinPointList.getGlobal() == null) {
            joinPointList.setGlobal(factory.createGlobalJoinPoints());
        }
        globalSelects = new HashMap<>(joinPointList.getGlobal().getSelect().size());
        // name2type
        for (final Select sel : joinPointList.getGlobal().getSelect()) {
            if (sel.getAlias() == null) {
                sel.setAlias(getJoinPointClass(sel));
            }
            globalSelects.put(sel.getAlias(), sel);
            // String joinPointName = sel.getClazz();
            // createIfInexistent(joinPointList, factory, joinPointName); <--
            // Already forcing join point class creation
            // in xsd
        }

        // int jpListSize = joinPointList.getJoinpoint().size();
        // for (int i = 0; i < jpListSize; i++) {

        for (final JoinPointType jp : joinPointList.getJoinpoint()) {
            joinPoints.put(jp.getClazz(), jp);
            // if (jp.getExtends() != null) { <-- Already forcing join point
            // class creation in xsd
            //
            // createIfInexistent(joinPointList, factory, jp.getExtends());
            // } else
            if (jp.getExtends() == null) {

                jp.setExtends(jp);
            }
            final List<String> selectable = new ArrayList<>();
            for (final Select sel : jp.getSelect()) {
                if (sel.getAlias() == null) {
                    sel.setAlias(getJoinPointClass(sel));
                }

                selectable.add(sel.getAlias());

                // String joinPointName = sel.getClazz();
                // createIfInexistent(joinPointList, factory, joinPointName);<--
                // Already forcing join point class
                // creation in xsd
            }
            selects.put(jp, selectable);
        }
    }

    /**
     * Add a new JoinPoint if the specified name does not exist
     * 
     * @param jps
     * @param factory
     * @param newJoinPoints
     * @param joinPointType
     */
    @Deprecated
    static void createIfInexistent(JoinPointsList jps, ObjectFactory factory, String joinPointType) {
        JoinPointType superType = getJoinPoint(jps, joinPointType);
        if (superType == null) {

            superType = factory.createJoinPointType();
            superType.setClazz(joinPointType);
            jps.getJoinpoint().add(superType);
        }
    }

    /**
     * Converts the join point model into a tree-type String
     * 
     * @param jpList
     *            the list of join points to print
     * @return
     */
    @Override
    public String toString() {
        String ret = "----------- Join Point Model -----------\n";
        final String rootClass = getJoinPointClass(joinPointList.getRootClass());
        final String rootAlias = joinPointList.getRootAlias();
        ret += "\tRoot: " + rootAlias;
        if (!rootClass.equals(rootAlias)) {
            ret += " of type " + rootClass;
        }
        ret += "\n";

        final GlobalJoinPoints globalJP = joinPointList.getGlobal();
        if (globalJP != null && !globalJP.getSelect().isEmpty()) {
            ret += "Global JoinPoints:\n";
            for (final Select sel : globalJP.getSelect()) {
                final String selClass = getJoinPointClass(sel);
                final String selAlias = sel.getAlias();
                ret += "\t" + selAlias;
                if (!selClass.equals(selAlias)) {
                    ret += " (" + selClass + ")";
                }
                ret += "\n";
            }
        }

        for (final JoinPointType jp : joinPointList.getJoinpoint()) {

            String jpClass = jp.getClazz();
            final String jpExtends = getJoinPointClass(jp.getExtends());

            ret += jpClass;
            if (!jpClass.equals(jpExtends)) {

                ret += " (" + jpExtends + ")";
            }
            ret += "\n";

            for (final Select sel : jp.getSelect()) {

                final String jpAlias = sel.getAlias();
                jpClass = getJoinPointClass(sel);

                ret += "\t" + jpAlias;
                if (!jpClass.equals(jpAlias)) {

                    ret += " (" + jpClass + ")";
                }
                ret += "\n";
            }
        }
        return ret;
    }

    /**
     * Compare a {@link Select} to a a {@link JoinPointType}. They will be equal if the selected type equals the join
     * point name
     * 
     * @param sel
     *            the select to compare
     * @param jp
     *            the join point to verify it equals to the select
     * @return true if the select type equals the join point type; false otherwise
     */
    public static boolean equals(Select sel, JoinPointType jp) {

        return sel.getClazz().equals(jp);

    }

    // /**
    // * Verify if a join point contains another selective join point
    // *
    // * @param current
    // * the join point in which we intend to verify the select
    // * @param selected
    // * the join point to select in the current join point
    // * @return true if the current join point have the selected join point;
    // false otherwise.
    // */
    // public boolean selects(JoinPointType current, JoinPointType selected) {
    // if (globalSelects.containsKey(selected.getClazz()))
    // return true;
    // for (Select sel : current.getSelect()) {
    //
    // if (equals(sel, selected))
    // return true;
    // }
    //
    // // If the join point does not contain, verify the super type
    // (recursively!)
    // if (current.getExtends() != null &&
    // !current.getExtends().equals(current.getClazz())) {
    //
    // current = getJoinPoint(current.getExtends());
    // return selects(current, selected);
    // }
    //
    // return false;
    //
    // }

    public JoinPointsList getJoinPointList() {
        return joinPointList;
    }

    public void setJoinPointList(JoinPointsList joinPointList) {
        this.joinPointList = joinPointList;
    }

    @Override
    public boolean contains(String name) {

        final boolean isJoinPoint = joinPoints.containsKey(name);

        if (isJoinPoint) {
            return true;
        }

        for (final JoinPointType jp : joinPointList.getJoinpoint()) {

            if (selects.get(jp).contains(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsJoinPoint(String name) {

        return joinPoints.containsKey(name);

    }

    @Override
    public boolean contains(String name, String subname) {

        if (!contains(name)) {
            return false;
        }
        return select(getJoinPoint(name), subname) != null;
    }

    /**
     * Verify if the given Join point is a super type of any other Join Point
     * 
     * @param name
     * @return
     */
    public boolean isSuper(JoinPointType joinPoint) {

        for (final JoinPointType jp : joinPointList.getJoinpoint()) {

            if (!jp.equals(joinPoint) && jp.getExtends().equals(joinPoint)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSuper(String joinPointName) {

        final JoinPointType joinPoint = joinPoints.get(joinPointName);
        if (joinPoint == null) {
            throw new RuntimeException("The join point does not exist in the specification: " + joinPointName);
        }

        return isSuper(joinPoint);
    }
}
