/**
 * Copyright 2026 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.generator.generator.java.utils;

import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;

/**
 * A JavaClass that supports the CRTP (Curiously Recurring Template Pattern) for polymorphic "this" types.
 * 
 * <p>This class extends JavaClass to add class-level type parameters while keeping the 
 * file name clean. The standard JavaClass uses the name for both the class declaration
 * and the file name, which prevents using type parameters like {@code ANode<Self extends ANode<Self>>}.</p>
 * 
 * <p>ALL classes get the CRTP type parameter for consistency and future extensibility:</p>
 * <pre>
 * CrtpJavaClass javaC = new CrtpJavaClass("ANode", package);
 * // Generates: public abstract class ANode&lt;Self extends ANode&lt;Self&gt;&gt; extends ASuperClass&lt;Self&gt;
 * 
 * CrtpJavaClass javaC = new CrtpJavaClass("ALoop", package);
 * // Generates: public abstract class ALoop&lt;Self extends ALoop&lt;Self&gt;&gt; extends AStmt&lt;Self&gt;
 * </pre>
 */
public class CrtpJavaClass extends JavaClass {

    /** The name used for the CRTP type parameter */
    public static final String SELF_TYPE_PARAMETER = "Self";

    private String superClassTypeArg;
    private boolean addTypeArgToSuperClass = true;

    /**
     * Creates a CRTP-enabled JavaClass.
     * 
     * <p>All classes get the CRTP type parameter {@code <Self extends ClassName<Self>>} for
     * consistency and to allow any class to be extended in the future.</p>
     *
     * @param name the base class name (without type parameters, e.g., "ANode")
     * @param classPackage the class package
     */
    public CrtpJavaClass(String name, String classPackage) {
        super(name, classPackage);
        this.superClassTypeArg = null;
    }

    /**
     * Creates a CRTP-enabled JavaClass with modifier.
     * 
     * <p>All classes get the CRTP type parameter {@code <Self extends ClassName<Self>>} for
     * consistency and to allow any class to be extended in the future.</p>
     *
     * @param name the base class name (without type parameters, e.g., "ANode")
     * @param classPackage the class package
     * @param modifier the class modifier
     */
    public CrtpJavaClass(String name, String classPackage, Modifier modifier) {
        super(name, classPackage, modifier);
        this.superClassTypeArg = null;
    }
    
    /**
     * Sets whether to add a type argument to the superclass.
     * 
     * <p>Use this when the superclass doesn't support type parameters (e.g., the base JoinPoint class).</p>
     *
     * @param addTypeArg true to add type argument, false to omit it
     */
    public void setAddTypeArgToSuperClass(boolean addTypeArg) {
        this.addTypeArgToSuperClass = addTypeArg;
    }

    /**
     * Sets the superclass with a type argument for CRTP.
     * 
     * <p>All classes now use "Self" as the type argument to maintain consistency:</p>
     * <pre>setSuperClassWithTypeArg("AParent") // Uses "Self" as type arg</pre>
     *
     * @param superClassName the simple name of the superclass (used for documentation, actual superclass set via setSuperClass)
     */
    public void setSuperClassWithTypeArg(String superClassName) {
        this.superClassTypeArg = SELF_TYPE_PARAMETER;
    }

    /**
     * Overrides the default setSuperClass to track that we may need type args.
     * 
     * @param superClass the superclass type
     */
    @Override
    public void setSuperClass(JavaType superClass) {
        super.setSuperClass(superClass);
        // If we haven't explicitly set a type arg, always use Self (all classes use CRTP)
        if (superClassTypeArg == null) {
            superClassTypeArg = SELF_TYPE_PARAMETER;
        }
    }

    /**
     * Generates the Java class code with CRTP type parameters.
     * 
     * <p>All classes are generated with the CRTP pattern:</p>
     * <pre>public abstract class ANode&lt;Self extends ANode&lt;Self&gt;&gt; extends AParent&lt;Self&gt;</pre>
     * <pre>public abstract class ALoop&lt;Self extends ALoop&lt;Self&gt;&gt; extends AStmt&lt;Self&gt;</pre>
     *
     * @param indentation the indentation level
     * @return the generated Java class code
     */
    @Override
    public StringBuilder generateCode(int indentation) {
        final StringBuilder classGen = generateClassHeader(indentation);

        classGen.append("class ");
        classGen.append(getName());
        
        // All classes get the CRTP type parameter
        classGen.append("<");
        classGen.append(SELF_TYPE_PARAMETER);
        classGen.append(" extends ");
        classGen.append(getName());
        classGen.append("<");
        classGen.append(SELF_TYPE_PARAMETER);
        classGen.append(">>");

        // Add extends clause with type argument
        JavaType superClass = getSuperClass();
        if (superClass != null && !superClass.equals(JavaTypeFactory.getObjectType())) {
            classGen.append(" extends ");
            classGen.append(superClass.getSimpleType());
            // Add type argument to superclass (only if enabled and we have a type arg)
            if (addTypeArgToSuperClass && superClassTypeArg != null) {
                classGen.append("<");
                classGen.append(superClassTypeArg);
                classGen.append(">");
            }
        }

        addImplements(classGen);
        classGen.append(" {" + ln() + ln());

        addFields(indentation, classGen);
        addConstructors(indentation, classGen);

        addMethods(indentation, classGen);

        classGen.append(generateClassTail(indentation));
        return classGen;
    }
}
