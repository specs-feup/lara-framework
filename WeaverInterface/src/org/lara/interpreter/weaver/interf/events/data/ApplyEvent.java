/**
 * Copyright 2014 SPeCS Research Group.
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

package org.lara.interpreter.weaver.interf.events.data;

import java.util.Optional;

import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.joinpoint.LaraJoinPoint;

public class ApplyEvent extends BaseEvent {

    private String aspect_name;
    private String label;
    private String select_label;
    private Optional<LaraJoinPoint> jpset;

    // private JoinPoint currentJoinPoint;

    /**
     * @param stage
     * @param label
     * @param select_label
     * @param root
     */
    public ApplyEvent(Stage stage, String aspect_name, String label, String select_label,
	    Optional<LaraJoinPoint> root) {
	super(stage);
	this.aspect_name = aspect_name;
	this.label = label;
	this.select_label = select_label;
	jpset = root;
    }

    @Override
    public String toString() {
	String ret = super.toString();
	ret += ", aspect " + aspect_name;
	ret += ", apply label " + label;
	ret += ", select " + select_label;
	if (jpset != null) {
	    ret += ", join point set: " + jpset;
	}
	return ret;
    }

    // /**
    // * @param stage
    // * @param applyName
    // * @param selectName
    // * @param select
    // */
    // public ApplyEvent(Stage stage, String applyName, String selectName,
    // JoinPoint currentJoinPoint) {
    // super(stage);
    // this.applyName = applyName;
    // this.selectName = selectName;
    // this.currentJoinPoint = currentJoinPoint;
    // }

    /**
     * @return the applyName
     */
    public String getLabel() {
	return label;
    }

    /**
     * @return the selectName
     */
    public String getSelect_label() {
	return select_label;
    }

    /**
     * @return the select
     */
    public Optional<LaraJoinPoint> getJpset() {
	return jpset;
    }

    /**
     * @param applyName
     *            the applyName to set
     */
    protected void setApplyName(String applyName) {
	label = applyName;
    }

    /**
     * @param selectName
     *            the selectName to set
     */
    protected void setSelectName(String selectName) {
	select_label = selectName;
    }

    /**
     * @param select
     *            the select to set
     */
    protected void setSelect(Optional<LaraJoinPoint> select) {
	jpset = select;
    }

    // /**
    // * @return the currentJoinPoint
    // */
    // public JoinPoint getCurrentJoinPoint() {
    // return currentJoinPoint;
    // }
    //
    // /**
    // * @param currentJoinPoint
    // * the currentJoinPoint to set
    // */
    // protected void setCurrentJoinPoint(JoinPoint currentJoinPoint) {
    // this.currentJoinPoint = currentJoinPoint;
    // }

    /**
     * @return the aspect_name
     */
    public String getAspect_name() {
	return aspect_name;
    }

    /**
     * @param aspect_name
     *            the aspect_name to set
     */
    protected void setAspect_name(String aspect_name) {
	this.aspect_name = aspect_name;
    }

}
