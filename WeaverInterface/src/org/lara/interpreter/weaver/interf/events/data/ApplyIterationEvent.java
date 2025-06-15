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

import java.util.List;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;

import tdrc.utils.StringUtils;

public class ApplyIterationEvent extends BaseEvent {

    private String aspect_name;
    private String label;
    private String select_label;
    private List<JoinPoint> pointcutChain;

    public ApplyIterationEvent(Stage stage, String aspect_name, String label, String select_label,
            List<JoinPoint> pointcutChain) {
        super(stage);
        this.aspect_name = aspect_name;
        this.label = label;
        this.select_label = select_label;
        this.pointcutChain = pointcutChain;
    }

    @Override
    public String toString() {
        String ret = super.toString();
        ret += ", aspect " + aspect_name;
        ret += ", apply label " + label;
        ret += ", select " + select_label;
        ret += ", pointcut chain: " + StringUtils.join(pointcutChain, JoinPoint::get_class, ",");
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
    public List<JoinPoint> getPointcutChain() {
        return pointcutChain;
    }

    /**
     * @return the aspect_name
     */
    public String getAspect_name() {
        return aspect_name;
    }

    /**
     * @param aspect_name the aspect_name to set
     */
    protected void setAspect_name(String aspect_name) {
        this.aspect_name = aspect_name;
    }

}
