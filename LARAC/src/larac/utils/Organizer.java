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

package larac.utils;

import larac.objects.Enums.Types;
import larac.utils.xml.entity.ActionArgument;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.dsl.types.TypeDef;
import tdrc.utils.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Methods for organization of the LARA AST.
 *
 * @author jbispo
 *
 */
public class Organizer {

    private static final String CODE_ARGUMENT_NAME = "code";
    private static final String POSITION_ARGUMENT_NAME = "position";
    private static final String VALUE_ARGUMENT_NAME = "value";
    private static final String ATTRIBUTE_ARGUMENT_NAME = "attribute";

    private final LanguageSpecification langSpec;

    public Organizer(LanguageSpecification langSpec) {
        this.langSpec = langSpec;
    }

    public Map<String, ActionArgument> createActionParameters(final Action act) {
        Map<String, ActionArgument> args = new LinkedHashMap<>();
        for (var param : act.getParameters()) {

            final ActionArgument actionArgument = new ActionArgument(param.getDeclaration().getName(),
                    param.getDeclaration().getType().getType(), this);

            var defaultValue = param.getDefaultValue();
            if (!defaultValue.isEmpty()) {
                actionArgument.setValue(defaultValue);
            }
            args.put(param.getDeclaration().getName(), actionArgument);
        }
        return args;
    }

    public Types getConvertedType(String typeStr) {

        final Types type = Types.value(typeStr);
        if (type != null) {
            return type;
        }
        if (typeStr.contains("[]")) {
            return Types.Array;
        }

        // if the object declaration exist in the artifacts
        if (langSpec.hasTypeDef(typeStr)) {
            return Types.Object;
        }
        // if it is a join point class
        if (langSpec.hasJoinPointName(typeStr)) {
            return Types.Joinpoint;
        }

        // If it does not exist, throw an exception with the error message and
        // the possible
        // types that can be used
        final StringBuilder message = new StringBuilder("Could not convert type '" + type + "'. Available types: ");

        final StringBuilder availableTypes = reportAvailableTypes();
        message.append(availableTypes);

        throw new RuntimeException(message.toString());
    }

    private StringBuilder reportAvailableTypes() {
        final StringBuilder message = new StringBuilder("\n\t Primitives: ");
        message.append(StringUtils.join(Arrays.asList(Types.values()), ", "));
        // message.append(", Object, Array, Map, Template, Joinpoint");

        final Collection<TypeDef> objects = langSpec.getTypeDefs().values();
        if (!objects.isEmpty()) {

            message.append("\n\t Defined types: ");
            final String objectsString = StringUtils.join(objects, TypeDef::getName, ", ");
            message.append(objectsString);
        }

        var joinpoints = langSpec.getJoinPoints().values();
        if (!joinpoints.isEmpty()) {

            message.append("\n\t Join point types: ");
            final String jpsString = StringUtils.join(joinpoints, JoinPointClass::getName, ", ");
            message.append(jpsString);
        }
        return message;
    }

    public Map<String, ActionArgument> createInsertParameters() {
        final Map<String, ActionArgument> args = new LinkedHashMap<>();

        final ActionArgument when = new ActionArgument(POSITION_ARGUMENT_NAME, "string", this);
        args.put(POSITION_ARGUMENT_NAME, when);
        final ActionArgument code = new ActionArgument(CODE_ARGUMENT_NAME, "template", this);
        args.put(CODE_ARGUMENT_NAME, code);
        return args;
    }

    public Map<String, ActionArgument> createDefParameters() {
        final Map<String, ActionArgument> args = new LinkedHashMap<>();

        final ActionArgument attribute = new ActionArgument(ATTRIBUTE_ARGUMENT_NAME, "string", this);
        args.put(ATTRIBUTE_ARGUMENT_NAME, attribute);
        final ActionArgument value = new ActionArgument(VALUE_ARGUMENT_NAME, "Object", this);
        args.put(VALUE_ARGUMENT_NAME, value);
        return args;
    }
}
