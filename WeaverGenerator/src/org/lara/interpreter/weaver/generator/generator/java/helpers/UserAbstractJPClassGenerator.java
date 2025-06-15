/**
 * Copyright 2015 SPeCS.
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

package org.lara.interpreter.weaver.generator.generator.java.helpers;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.members.Method;

/**
 * Generates an abstract class that can be edited by the developer. This class
 * can be used for changes/extensions that
 * are global to the join points.
 *
 */
public class UserAbstractJPClassGenerator extends GeneratorHelper {

    protected UserAbstractJPClassGenerator(JavaAbstractsGenerator javaGenerator) {
        super(javaGenerator);
    }

    @Override
    public JavaClass generate() {
        return generateUserClass();
    }

    /**
     * Generate an abstract class that can be edited by the developer. This class
     * can be used for changes/extensions
     * that are global to the join points
     * 
     * @param javaGenerator
     * @param sanitizedOutPackage
     * @param enums
     * @return
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator) {
        final UserAbstractJPClassGenerator gen = new UserAbstractJPClassGenerator(javaGenerator);
        return gen.generate();
    }

    /**
     * Generate an abstract class that can be edited by the developer. This class
     * can be used for changes/extensions
     * that are global to the join points
     * 
     * @return
     */
    private JavaClass generateUserClass() {
        // Create the abstract class using the name of the weaver
        String classname = GenConstants.abstractPrefix() + javaGenerator.getWeaverName() + GenConstants.interfaceName();

        final JavaClass abstJPClass = new JavaClass(classname, javaGenerator.getAbstractUserJoinPointClassPackage());
        abstJPClass.setSuperClass(javaGenerator.getaJoinPointType());
        abstJPClass.add(Modifier.ABSTRACT);
        abstJPClass.appendComment(
                "Abstract class which can be edited by the developer. This class will not be overwritten." + ln());
        abstJPClass.add(JDocTag.AUTHOR, GenConstants.getAUTHOR());
        abstJPClass.setSuperClass(javaGenerator.getaJoinPointType());
        // and add the following default implementation of the method
        // "compareNodes"
        // protected boolean compareNodes(AJoinPoint aJoinPoint) {
        // ..... return this.getNode().equals(aJoinPoint.getNode());
        // }
        final Method compareNodes = GeneratorUtils.generateCompareNodes(javaGenerator.getaJoinPointType());
        compareNodes.add(Annotation.OVERRIDE);
        abstJPClass.add(compareNodes);

        return abstJPClass;
    }
}
