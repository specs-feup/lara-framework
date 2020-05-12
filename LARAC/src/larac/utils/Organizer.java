/**
 * Copyright 2020 SPeCS.
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

package larac.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.lara.language.specification.dsl.types.TypeDef;

import larac.objects.Enums.Types;
import larac.utils.xml.entity.ActionArgument;
import tdrc.utils.StringUtils;

/**
 * Methods for organization of the LARA AST.
 * 
 * @author jbispo
 *
 */
public class Organizer {

    private final LanguageSpecificationV2 langSpec;

    public Organizer(LanguageSpecificationV2 langSpec) {
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

}
