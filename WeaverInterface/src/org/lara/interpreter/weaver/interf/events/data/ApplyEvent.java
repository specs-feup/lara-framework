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

}
