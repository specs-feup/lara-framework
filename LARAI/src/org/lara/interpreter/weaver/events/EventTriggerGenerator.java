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

package org.lara.interpreter.weaver.events;

import java.util.List;

import org.lara.interpreter.Interpreter;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.interf.events.Stage;

import pt.up.fe.specs.lara.aspectir.Argument;
import pt.up.fe.specs.lara.aspectir.Aspect;
import pt.up.fe.specs.lara.aspectir.Parameter;
import tdrc.utils.StringUtils;

public class EventTriggerGenerator {

    public static final String ASPECT_CALLER_PROPERTY_NAME = "__called_by__";
    public static final String IS_ASPECT = "__is_aspect__";

    public static void triggerAspectBegin(Aspect asp, StringBuilder aspectConstructor, boolean hasInput,
            int indentLevel) {

        final List<Parameter> params = hasInput ? asp.parameters.input.parameters : null;
        triggerAspect(Stage.BEGIN, asp.name, params, aspectConstructor, indentLevel);
    }

    public static void triggerAspectEnd(Aspect asp, StringBuilder aspectConstructor, boolean hasOutput,
            int indentLevel) {
        final List<Parameter> params = hasOutput ? asp.parameters.output.parameters : null;
        triggerAspect(Stage.END, asp.name, params, aspectConstructor, indentLevel);
    }

    public static void triggerAspect(Stage stage, String aspName, List<Parameter> params,
            StringBuilder aspectConstructor, int indentLevel) {
        aspectConstructor.append(LaraIUtils.getSpace(indentLevel) + MasterWeaver.GET_EVENT_TRIGGER);
        aspectConstructor.append("." + EventTrigger.TRIGGER_ASPECT_EVENT);
        aspectConstructor.append("(" + stage.toCode());
        aspectConstructor.append(", '" + aspName + "'");
        aspectConstructor.append(", this." + EventTriggerGenerator.ASPECT_CALLER_PROPERTY_NAME);
        aspectConstructor.append(", [");
        final StringBuilder args = new StringBuilder(", [");
        if (params != null) {
            aspectConstructor.append("'" + params.get(0).name + "'");
            args.append("this." + params.get(0).name);
            for (int i = 1; i < params.size(); i++) {
                aspectConstructor.append(", '" + params.get(i).name + "'");
                args.append(", this." + params.get(i).name);
            }
        }
        args.append("]");
        aspectConstructor.append("]");
        aspectConstructor.append(args);
        aspectConstructor.append(", this.exception");
        aspectConstructor.append(");\n");
    }

    public static void triggerAction(Stage stage, String actionName, String target,
            List<Argument> params, StringBuilder aspectConstructor, int indentLevel, Interpreter interp) {
        aspectConstructor.append(LaraIUtils.getSpace(indentLevel) + MasterWeaver.GET_EVENT_TRIGGER);
        aspectConstructor.append("." + EventTrigger.TRIGGER_ACTION_EVENT);
        aspectConstructor.append("(" + stage.toCode());
        aspectConstructor.append(", '" + actionName + "'");
        aspectConstructor.append(", " + target);
        aspectConstructor.append(", [");
        // StringBuilder args = new StringBuilder(", [");
        if (!params.isEmpty()) {

            // if(params.get(0).name != null){ //For now, calls with named
            // arguments are passed only the values, in the
            // given order
            //
            aspectConstructor.append(interp.getJavascriptString(params.get(0), 0));
            for (int i = 1; i < params.size(); i++) {
                aspectConstructor.append(",");
                aspectConstructor.append(interp.getJavascriptString(params.get(i), 0));
            }
        }
        // args.append("]");
        aspectConstructor.append("]");
        // aspectConstructor.append(args);
        // aspectConstructor.append(", this.exception");
        // aspectConstructor.append(");\n");
        aspectConstructor.append(")");
    }

    public static void triggerApply(Stage stage, String aspect_name, String label, String select_label, int indentLevel,
            StringBuilder aspectConstructor, List<String> pointcutChainNames) {
        aspectConstructor.append(LaraIUtils.getSpace(indentLevel) + MasterWeaver.GET_EVENT_TRIGGER);
        aspectConstructor.append("." + EventTrigger.TRIGGER_APPLY_EVENT);
        aspectConstructor.append("(" + stage.toCode());
        aspectConstructor.append(", '" + aspect_name + "'");
        aspectConstructor.append(", '" + label + "'");
        aspectConstructor.append(", '" + select_label + "'");
        aspectConstructor.append(", [" + StringUtils.joinStrings(pointcutChainNames, ", ") + "]");
        aspectConstructor.append(");\n");
    }

    public static void triggerApply(Stage stage, String aspect_name, String label, String select_label, int indentLevel,
            StringBuilder aspectConstructor, String rootName) {
        aspectConstructor.append(LaraIUtils.getSpace(indentLevel) + MasterWeaver.GET_EVENT_TRIGGER);
        aspectConstructor.append("." + EventTrigger.TRIGGER_APPLY_EVENT);
        aspectConstructor.append("(" + stage.toCode());
        aspectConstructor.append(", '" + aspect_name + "'");
        aspectConstructor.append(", '" + label + "'");
        aspectConstructor.append(", '" + select_label + "'");
        // aspectConstructor.append(", (" + select_label + "!=null?" + select_label + ".laraJoinPoint:null)");
        aspectConstructor.append(", " + select_label);// + "[" + rootName + "]"); // [0].reference
        aspectConstructor.append(");\n");
    }

}
