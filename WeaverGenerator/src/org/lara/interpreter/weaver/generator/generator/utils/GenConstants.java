/**
 * Copyright 2015 SPeCS.
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

package org.lara.interpreter.weaver.generator.generator.utils;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.specs.generators.java.types.JavaType;

import java.io.File;

public class GenConstants {

    private static final String IMPLEMENTATION_SUFIX = "Impl";
    private static final File defaultXmlDir = new File(".");
    private static final File defaultOutputDir = new File(".");
    private static final String defaultWeaverName = "MyWeaver";
    private static final String defaultPackage = "";
    private static final boolean defaultAbstractGetters = true;
    private static final boolean defaultEvents = false;
    private static final boolean defaultImplMode = true;
    private static final boolean defaultShowGraph = false;
    private static final String defaultNodeType = Object.class.getCanonicalName();
    private static final String AUTHOR = "Lara Weaver Generator";

    private static final String GET_CLASS_NAME = "get_class";
    private static final String INSTANCE_OF_NAME = "instanceOf";
    private static final String WITH_TRY_PREFIX = "WithTryCatch";
    private static final String LINK_TAG = "[[LINK]]";
    private static final String WEAVER_NAME_TAG = "[[WEAVERNAME]]";
    private static final String WEAVER_TEXT_HEADER_LOCATION = "org/lara/interpreter/weaver/generator/WeaverTextHeader.txt";
    // private static final String rootName = "ProgramRoot";

    private static final String interfaceName = JoinPoint.class.getSimpleName();
    // private static String globalJPInfo = "Common";
    private static final String abstractPrefix = "A";
    private static final String ENTITY = "entities";
    private static final JavaType joinPointInterfaceType = new JavaType(JoinPoint.class);
    private static final String ARRAY_METHOD_SUFIX = "Array" + IMPLEMENTATION_SUFIX;
    private static final String NATIVE_ARRAY_VAR_NAME = "nativeArray";
    private static final String GET_WEAVER_ENGINE_NAME = "getWeaverEngine";
    private static final String SET_WEAVER_ENGINE_NAME = "setWeaverEngine";

    public static JavaType getJoinPointInterfaceType() {

        return GenConstants.joinPointInterfaceType;
    }

    public static String getClassName() {
        return GenConstants.GET_CLASS_NAME;
    }

    public static String linkTag() {
        return GenConstants.LINK_TAG;
    }

    public static String weaverNameTag() {
        return GenConstants.WEAVER_NAME_TAG;
    }

    public static String weaverTextHeaderLocation() {
        return GenConstants.WEAVER_TEXT_HEADER_LOCATION;
    }

    public static String interfaceName() {
        return GenConstants.interfaceName;
    }

    public static String abstractPrefix() {
        return GenConstants.abstractPrefix;
    }

    public static String entity() {
        return GenConstants.ENTITY;
    }

    public static File getDefaultXMLDir() {
        return GenConstants.defaultXmlDir;
    }

    public static File getDefaultOutputDir() {
        return GenConstants.defaultOutputDir;
    }

    public static String getDefaultWeaverName() {
        return GenConstants.defaultWeaverName;
    }

    public static String getDefaultPackage() {
        return GenConstants.defaultPackage;
    }

    public static boolean getDefaultAbstractGetters() {
        return GenConstants.defaultAbstractGetters;
    }

    public static boolean getDefaultEvents() {
        return GenConstants.defaultEvents;
    }

    public static boolean getDefaultImplMode() {
        return GenConstants.defaultImplMode;
    }

    public static boolean getDefaultShowGraph() {
        return GenConstants.defaultShowGraph;
    }

    public static String getDefaultNodeType() {
        return GenConstants.defaultNodeType;
    }

    public static String getAUTHOR() {
        return GenConstants.AUTHOR;
    }

    public static String getArrayMethodSufix() {
        return GenConstants.ARRAY_METHOD_SUFIX;
    }

    public static String getNativeArrayVarName() {
        return GenConstants.NATIVE_ARRAY_VAR_NAME;
    }

    /**
     * @return the withTryPrefix
     */
    public static String getWithTryPrefix() {
        return GenConstants.WITH_TRY_PREFIX;
    }

    public static String getImplementationSufix() {
        return GenConstants.IMPLEMENTATION_SUFIX;
    }

    public static String getInstanceOfName() {
        return INSTANCE_OF_NAME;
    }

    public static String getWeaverEngineMethodName() {
        return GenConstants.GET_WEAVER_ENGINE_NAME;
    }

    public static String setWeaverEngineMethodName() {
        return GenConstants.SET_WEAVER_ENGINE_NAME;
    }

    public static String withImpl(String string) {
        return string + IMPLEMENTATION_SUFIX;
    }

    public static String enums() {
        return "enums";
    }

}
