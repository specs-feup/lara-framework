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

import java.util.Arrays;
import java.util.Optional;

import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.joinpoint.LaraJoinPoint;
import org.lara.interpreter.weaver.utils.FilterExpression;

import tdrc.utils.StringUtils;

public class SelectEvent extends BaseEvent {

    private String aspect_name;
    private String label;
    private String[] pointcutChain;
    private String[] aliases;
    private FilterExpression[][] filters;
    private Optional<LaraJoinPoint> pointcut;

    /**
     * @param stage
     *            when the event occurs
     * @param aspectName
     *            name of the select
     * @param selectName
     *            name of the select
     * @param pointcutChain
     *            chain of selected join points
     * @param aliases
     *            new names for the selected join points
     * @param filters
     *            array of filters for each selected join point
     * @param pointcut2
     *            the resulting pointcut of the selection
     */
    public SelectEvent(Stage stage, String aspectName, String selectLable, String[] pointcutChain, String[] aliases,
            FilterExpression[][] filters, Optional<LaraJoinPoint> pointcut2) {
        super(stage);
        aspect_name = aspectName;
        label = selectLable;
        this.pointcutChain = pointcutChain;
        this.aliases = aliases;
        this.filters = filters;
        pointcut = pointcut2;
    }

    @Override
    public String toString() {
        String ret = super.toString();
        ret += ", aspect " + aspect_name;
        ret += ", select label " + label;
        ret += ", pointcut " + arrayToString(pointcutChain);
        ret += ", aliases " + arrayToString(aliases);
        ret += ", filters " + "{" + StringUtils.join(Arrays.asList(filters), ",") + "}";
        if (pointcut != null) {
            ret += ", result: " + pointcut;
        }
        return ret;
    }

    public String arrayToString(String[] array) {
        String ret = "{" + StringUtils.joinStrings(Arrays.asList(array), ",") + "}";
        return ret + "}";
    }

    /**
     * @return the label of the select
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the pointcut
     */
    public String[] getPointcutChain() {
        return pointcutChain;
    }

    /**
     * @return the aliases
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * @return the filters
     */
    public FilterExpression[][] getFilters() {
        return filters;
    }

    /**
     * @return the resulting pointcut
     */
    public Optional<LaraJoinPoint> getPointcut() {
        return pointcut;
    }

    /**
     * @param selectLabel
     *            the label of the select to set
     */
    protected void setLabel(String selectLabel) {
        label = selectLabel;
    }

    /**
     * @param joinPointChain
     *            the joinPointChain to set
     */
    protected void setPointcutChain(String[] joinPointChain) {
        pointcutChain = joinPointChain;
    }

    /**
     * @param aliases
     *            the aliases to set
     */
    protected void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    /**
     * @param filters
     *            the filters to set
     */
    protected void setFilters(FilterExpression[][] filters) {
        this.filters = filters;
    }

    /**
     * @param jpset
     *            the laraJoinPoint to set
     */
    protected void setPointcut(Optional<LaraJoinPoint> jpset) {
        pointcut = jpset;
    }

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
