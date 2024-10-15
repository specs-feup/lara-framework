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
package org.lara.interpreter.weaver.joinpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.lara.interpreter.weaver.interf.JoinPoint;

/**
 * Encapsulation of a {@link JoinPoint} to define a selection, containing the current {@link JoinPoint} reference,
 * alias, children, parent and a boolean defining if this join point is a leaf (has no childs, i.e., end of chain)
 *
 * @author Tiago
 *
 */
public class LaraJoinPoint {

    /**
     * ALIAS used in LARA. Ex: "function", "f1" instead of "function", etc.
     */
    private String classAlias;

    /**
     * join point parent in the pointcut chain
     */
    private LaraJoinPoint _jp_parent_;

    /**
     * List of join point children in the join point chain
     */
    private List<LaraJoinPoint> children;

    /**
     * Defines if the join point is in the end of the join point chain
     */
    private boolean leaf;

    /**
     * The Reference to a join point
     */
    private JoinPoint _jp_reference_;

    // private LaraJoinPoint laraJoinPoint;

    // //////////////////////////METHODS////////////////////////////
    /**
     * Generate an empty join point to be used as root.
     *
     * @return
     */
    public static LaraJoinPoint createRoot() {
        return new LaraJoinPoint();
    }

    /**
     * Empty constructor. Used to create a join point element that is used as root.
     */
    public LaraJoinPoint() {
        leaf = false;
        children = new ArrayList<>();
    }

    /**
     * Constructor based on a existing join point, containing the attributes and the Object reference
     */
    public LaraJoinPoint(JoinPoint jp) {
        leaf = false;
        children = new ArrayList<>();
        // laraJoinPoint = this;
        setReference(jp);
    }

    /**
     * Add a child to the join point children
     *
     * @param jp
     *            the child to add
     */
    public void addChild(LaraJoinPoint jp) {
        children.add(jp);
        jp.setParent(this);
        setLeaf(false);
    }

    /**
     * Add all children in the collection to the join point children
     *
     * @param jp
     *            the collection of children to add
     */
    public void addChildren(Collection<LaraJoinPoint> jpC) {
        for (final LaraJoinPoint jp : jpC) {
            addChild(jp);
        }
    }

    /**
     * Remove a child from the join point children
     *
     * @param jp
     *            the child to remove
     * @return true if this list contained the specified join point
     */
    public boolean removeChild(LaraJoinPoint jp) {
        return children.remove(jp);
    }

    /**
     * Removes the child in the specific position
     *
     * @param index
     *            the position of the child to be removed
     * @return the join point at the specified position
     */
    public LaraJoinPoint removeChild(int index) {
        return children.remove(index);
    }

    /**
     * Returns the join point in the specific position
     *
     * @param index
     *            index of the join point to return
     * @return the join point at the specified position in the children list
     */
    public LaraJoinPoint getChild(int index) {
        return children.get(index);
    }

    /**
     * @return the name
     */
    public String getClassAlias() {
        return classAlias;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setClassAlias(String classAlias) {
        this.classAlias = classAlias;
    }

    /**
     * @return the parent
     */
    public LaraJoinPoint getParent() {
        return _jp_parent_;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(LaraJoinPoint parent) {
        _jp_parent_ = parent;
    }

    /**
     * @return the children
     */
    public List<LaraJoinPoint> getChildren() {
        return children;
    }

    /**
     * @return the children with the given alias
     */
    public List<LaraJoinPoint> getChildren(String alias) {
        return children.stream().filter(c -> c.getClassAlias().equals(alias)).collect(Collectors.toList());
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * @param children
     *            the children to set
     */
    public void setChildren(List<LaraJoinPoint> children) {
        this.children = children;
    }

    /**
     * @param isLeaf
     *            the isLeaf to set
     */
    public void setLeaf(boolean isLeaf) {
        leaf = isLeaf;
    }

    /**
     * @return the isLeaf
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * @return the reference
     */
    public JoinPoint getReference() {
        // TODO: In Graal mode, return a JavaScript object which emulates nashorn behaviour when accessing attributes
        return _jp_reference_;
    }

    /**
     * @param reference
     *            the reference to set
     */
    public void setReference(JoinPoint reference) {
        _jp_reference_ = reference;
    }

    @Override
    public String toString() {
        return toString("");
    }

    private String toString(String space) {
        final String space2 = space + "\t";

        String ret = "\n" + space + "JoinPoint {";
        ret += "\n" + space2 + "type : " + getReference().get_class();
        ret += "\n" + space2;
        if (classAlias == null) {
            ret += "LARA Root for Select";
        } else {
            ret += "alias: " + classAlias;
        }

        if (_jp_parent_ != null) {
            ret += "\n" + space2 + "parent: " + (_jp_parent_.classAlias == null ? "none" : _jp_parent_.classAlias);
        }
        if (!leaf) {
            ret += "\n" + space2 + "children: [";
            for (final LaraJoinPoint ljp : children) {
                ret += ljp.toString(space2 + "\t");
            }
            ret += "\n" + space2 + "]";
        }
        ret += "\n" + space + "}";
        return ret;
    }

    /**
     * Create a LaraJoinPoint clone for this instance, including cloned children
     */
    @Override
    public LaraJoinPoint clone() {

        final LaraJoinPoint clone = cleanClone();

        for (final LaraJoinPoint child : children) {

            final LaraJoinPoint clonedChild = child.clone();
            clone.addChild(clonedChild);
        }

        return clone;
    }

    /**
     * Create a LaraJoinPoint clone for this instance, with no children
     */
    public LaraJoinPoint cleanClone() {
        final LaraJoinPoint clone = new LaraJoinPoint(_jp_reference_);
        clone.setClassAlias(classAlias);
        clone.setLeaf(leaf);
        clone.setParent(null);
        return clone;
    }

    /**
     * Get the leaves on the join point chain. If this is a leave, then adds himself
     *
     * @return a list containing all the leaves in the join point chain
     */
    public List<LaraJoinPoint> getLeaves() {

        final List<LaraJoinPoint> leaves = new ArrayList<>();
        this.getLeaves(leaves);
        return leaves;
    }

    /**
     * Auxiliary function that complements the getLeaves() function
     *
     * @param leaves
     *            the list to use to add leaves
     */
    private void getLeaves(List<LaraJoinPoint> leaves) {
        if (isLeaf()) {

            leaves.add(this);
        } else {

            for (final LaraJoinPoint child : children) {

                child.getLeaves(leaves);
            }
        }
    }

    public List<String> getJoinPointChain() {
        final List<String> chain = new ArrayList<>();
        getJoinPointChain(chain);
        return chain;
    }

    private void getJoinPointChain(List<String> chain) {
        if (chain != null) {
            chain.add(classAlias);
        }
        if (!isLeaf()) {
            children.get(0).getJoinPointChain(chain);
        }
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    // public LaraJoinPoint getLaraJoinPoint() {
    // return laraJoinPoint;
    // }
    //
    // public void setLaraJoinPoint(LaraJoinPoint laraJoinPoint) {
    // this.laraJoinPoint = laraJoinPoint;
    // }
}
