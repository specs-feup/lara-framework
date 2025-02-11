/**
 * Copyright 2020 SPeCS.
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

package utils;

import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.dsl.Select;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Selector {

    private final LanguageSpecification langSpec;
    private boolean alreadyFound;

    public Selector(LanguageSpecification langSpec) {
        this.langSpec = langSpec;
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
        // final JoinPointType source = getJoinPoint(joinPointList, sourceJP);
        var source = langSpec.getJoinPoint(sourceJP);
        var selPath = new SelectionPath(source, targetJP);

        if (source == null) {
            if (!validatePath) {
                // System.out.println("SEL PATH 1: " + selPath);
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
            // System.out.println("SEL PATH 2: " + selPath);
            return selPath;
        } else {

            path = selectWithMiddlePath(targetJP, source, selPath);
            if (!path.isEmpty()) {
                selPath.setPath(path);
            }
        }
        // System.out.println("SEL PATH 3: " + selPath);
        return selPath;

    }

    /**
     * Selects a subsequent join point from the given join point
     *
     * @param jp             the base join point
     * @param joinPointClass the selected join point
     * @return a {@link Select} if the name occurs, null otherwise.
     */
    public Select select(JoinPointClass jp, String joinPointClass) {
        return jp.getSelect(joinPointClass).orElse(null);

        // Global selects not supported
        // if (globalSelects.containsKey(joinPointClass)) {
        // return globalSelects.get(joinPointClass);
        // }

        // Verify the selects pertaining to the join point
        // for (final Select select : jp.getSelects()getSelect()) {
        //
        // if (select.getAlias().equals(joinPointClass)) {
        // return select;
        // }
        // }
        // // If the join point does not contain, verify the super type
        // // (recursively!)
        // if (!jp.getExtends().equals(jp)) {
        //
        // jp = (JoinPointType) jp.getExtends();
        // return select(jp, joinPointClass);
        // }
        // return null;
    }

    private Stack<Select> selectWithMiddlePath(String targetJP, JoinPointClass source,
                                               SelectionPath selPath) {
        Stack<Select> path = new Stack<>();

        final Set<JoinPointClass> ignoreSet = new HashSet<>();
        ignoreSet.add(source);
        // for (final Select select : source.getSelect()) {
        for (final Select select : source.getSelects()) {
            // final JoinPointType next = select.getClazz();
            final JoinPointClass next = select.getClazz();
            final Stack<Select> pathAux = selectionPathAux(next, targetJP, new ArrayList<JoinPointClass>(),
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
                        // boolean firstWOalias = first.getAlias().equals(first.getClazz().getClazz());
                        // boolean secondWOAlias = second.getAlias().equals(second.getClazz().getClazz());
                        boolean firstWOalias = first.getSelectName().equals(first.getClazz().getName());
                        boolean secondWOAlias = second.getSelectName().equals(second.getClazz().getName());
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

        // if (path.isEmpty() && !source.getExtends().equals(source)) {
        // If extends another join point
        if (path.isEmpty() && source.getExtend().isPresent()) {

            // source = (JoinPointType) source.getExtends();
            source = source.getExtend().get();
            return selectWithMiddlePath(targetJP, source, selPath);
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
     * @param selPath
     * @return
     */
    private Stack<Select> selectionPathAux(JoinPointClass current, String joinPointName,
                                           ArrayList<JoinPointClass> visitedList, Set<JoinPointClass> ignoreSet, SelectionPath selPath) {

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

        // System.out.println("SELECTS: " + current.getSelects());
        for (final Select select : current.getSelects()) {
            final JoinPointClass next = getJoinPoint(select);
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
     * Searches for a join point with the same description as the {@link Select} element
     *
     * @param jp     the list of join points
     * @param select the selected join point to search
     * @return a {@link JoinPointClass} if the select exists in the join point model, null otherwise.
     */
    public static JoinPointClass getJoinPoint(Select select) {
        final JoinPointClass joinPointType = select.getClazz();
        return joinPointType;
    }

    /**
     * Get the path from an existent join point root (inclusive) to the target join point (inclusive)
     *
     * @param tag the join point target
     * @return the first path to the join point (Depth First Search)
     */
    public SelectionPath selectionPath(String joinPointName) {
        // System.out.println("SELECT PATH 1: from root (" + langSpec.getRoot() + ") to " + joinPointName);
        // final JoinPointClass joinPointRoot = getRoot();
        final JoinPointClass joinPointRoot = langSpec.getRoot();
        SelectionPath selPath = new SelectionPath(joinPointRoot, joinPointName);
        Stack<Select> path = new Stack<>();
        alreadyFound = false;
        // Verify if the join point exist
        // Warning: the join point to select may be an alias for a selectable
        // join point
        // E.g.: select input end -> the input join point is an alias for 'var'
        // Hence, if this getter returns null it may not be an error!
        // if (joinPointName.equals(joinPointList.getRootAlias())) {
        if (joinPointName.equals(langSpec.getRootAlias())) {
            Select e = new Select(joinPointRoot, langSpec.getRootAlias());
            // e.setAlias(langSpec.getRootAlias());
            // e.setClazz(joinPointRoot);
            path.add(e);
            selPath.setPath(path);
            // System.out.println("SEL PATH 4: " + selPath);
            return selPath;
        }

        final Set<JoinPointClass> ignoreSet = new HashSet<>();

        path = selectionPathAux(joinPointRoot, joinPointName, new ArrayList<JoinPointClass>(), ignoreSet, selPath);
        // System.out.println("PATH: " + path);
        if (!path.isEmpty()) {
            Select e = new Select(joinPointRoot, langSpec.getRootAlias());
            // e.setAlias(joinPointList.getRootAlias());
            // e.setClazz(joinPointRoot);
            path.add(e);
            selPath.setPath(path);
            if (selPath.hasSecondaryPath()) {
                selPath.getSecondaryPath().get().add(e);
            }
        }
        // System.out.println("PATH FINAL: " + selPath);
        // System.out.println("SEL PATH 5: " + selPath);
        return selPath;
    }
}
