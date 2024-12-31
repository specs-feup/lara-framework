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

package pt.up.fe.specs.lara.commonlang.generator;

import org.apache.commons.lang3.StringUtils;
import org.lara.language.specification.ast.LangSpecNode;
import org.lara.language.specification.ast.NodeFactory;
import org.lara.language.specification.dsl.JoinPointClass;
import pt.up.fe.specs.lara.commonlang.LaraCommonLang;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.Replacer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates the LARA files that implement the language specification hierarchy.
 *
 * @author JBispo
 */
public class CommonLangGenerator {

    private final File outputFolder;

    public static List<ResourceProvider> getGeneratedResources() {
        List<ResourceProvider> resources = new ArrayList<>();

        for (var jp : LaraCommonLang.getLanguageSpecification().getAllJoinPoints()) {
            // Exclude joinpoint to keep previous behaviour
            // TODO: Check if this exception is really needed
            if (jp.getName().equals("joinpoint")) {
                continue;
            }

            resources.add(() -> getJpResource(jp.getName()));
        }

        return resources;
    }

    public CommonLangGenerator(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public void generate() {
        // Read lang spec
        var langSpec = LaraCommonLang.getLanguageSpecification();

        var globalAttrs = langSpec.getGlobal().getAttributes();

        generateJpBase(globalAttrs);

        for (var jp : langSpec.getAllJoinPoints()) {

            // Exclude joinpoint to keep previous behaviour
            // TODO: Check if this exception is really needed
            if (jp.getName().equals("joinpoint")) {
                continue;
            }

            System.out.println("JP: " + "weaver/jp/" + jp.getName() + ".lara");

            generate(jp);
        }

        generateIndex();

        // Generate JSON

        LangSpecNode node = NodeFactory.toNode(langSpec);
        String json = node.toJson();

        File jsonFile = new File(outputFolder, "LaraCommonLanguage.json");
        System.out.println("JSON: " + jsonFile);
        SpecsIo.write(jsonFile, json);
    }

    private void generateIndex() {
        var jpIndexStr = "";

        final String importTemplate = "import weaver.jp.<SUPER_JP>;\n";

        jpIndexStr += importTemplate.replace("<SUPER_JP>", "JoinPoint");


        // TODO: Is it needed to exclude joinpoint?
        var jps = LaraCommonLang.getLanguageSpecification().getAllJoinPoints().stream()
                .filter(jp -> !jp.getName().equals("joinpoint"))
                .toList();

        for (var jp : jps) {
            jpIndexStr += importTemplate.replace("<SUPER_JP>", getJoinPointClassName(jp.getName()));
        }

        // classMapper
        var classMapperTemplate = new Replacer(
                () -> "pt/up/fe/specs/lara/commonlang/generator/ClassMapperTemplate.txt");

        var jpsMapping = jps.stream()
                .map(jp -> getJoinPointClassName(jp.getName()))
                .map(jp -> "	'" + jp + "': " + jp)
                .reduce((jp1, jp2) -> jp1 + ",\n" + jp2).get();
        classMapperTemplate.replace("<MAPPING>", jpsMapping);
        jpIndexStr += classMapperTemplate.toString();

        var laraResource = "weaver/jp/JoinPointIndex.lara";
        var laraFile = new File(outputFolder, laraResource);
        SpecsIo.write(laraFile, jpIndexStr);

    }

    private void generateJpBase(List<org.lara.language.specification.dsl.Attribute> globAtts) {

        var jpBase = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/JpBase.txt");
        var jpName = "JoinPoint";
        var jpBaseStr = jpBase.toString();

        for (var globAttr : globAtts) {
            var att = globAttr.getName();

            // if att is already defined do not create template
            var attFunc = String.format("JoinPoint.prototype.%s = function(", att);
            var attParamsFunc = String.format("Object.defineProperty(JoinPoint.prototype, \'%s\', {", att);

            if (jpBaseStr.contains(attFunc) || jpBaseStr.contains(attParamsFunc))
                continue;

            var attTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/AttTemplate.txt");
            if (!globAttr.getParameters().isEmpty()) {
                attTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/AttParamTemplate.txt");
                var params = globAttr.getParameters().stream().map(param -> param.getName())
                        .reduce((param1, param2) -> param1 + "," + param2).get();
                attTemplate.replace("<PARAMS>", params);
            }
            attTemplate.replace("<THIS_JP>", jpName);
            attTemplate.replace("<ATT>", att);
            jpBaseStr += attTemplate.toString();
        }

        var laraResource = "weaver/jp/" + jpName + ".lara";
        var laraFile = new File(outputFolder, laraResource);
        SpecsIo.write(laraFile, jpBaseStr);

    }

    public static String getJoinPointClassName(String jpName) {
        // Upper case first letter
        return StringUtils.capitalize(jpName) + "Jp";
    }


    private void generate(JoinPointClass jpType) {

        var atts = jpType.getAttributes();

        var jpTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/JpTemplate.txt");

        var jpName = jpType.getName();
        var jpClassName = getJoinPointClassName(jpName);
        var superClassName = jpType.getExtend()
                .map(JoinPointClass::getName)
                .map(name -> !name.equals("joinpoint") ? getJoinPointClassName(name) : "JoinPoint").orElseThrow();

        var types = getJpTypes(jpType);
        var typesSet = new StringBuilder();
        for (var type : types) {
            typesSet.append("_lara_dummy_ = ").append(jpClassName).append("._JP_TYPES.add('" + type + "');\n");
        }

        jpTemplate.replace("<THIS_JP>", jpClassName);
        jpTemplate.replace("<SUPER_JP>", superClassName);
        jpTemplate.replace("<JP_TYPE>", jpName);
        jpTemplate.replace("<TYPES_SET>", typesSet);

        // System.out.println("TEMPLATE:\n" + jpTemplate);

        var jpTemplateStr = jpTemplate.toString();

        for (var att : atts) {
            if (isAttDefinedInSuper(jpType, att))
                continue;

            var attTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/AttTemplate.txt");
            if (!att.getParameters().isEmpty()) {
                attTemplate = new Replacer(() -> "pt/up/fe/specs/lara/commonlang/generator/AttParamTemplate.txt");
                var params = att.getParameters().stream().map(param -> param.getName())
                        .reduce((param1, param2) -> param1 + "," + param2).get();
                attTemplate.replace("<PARAMS>", params);
            }
            attTemplate.replace("<THIS_JP>", jpClassName);
            attTemplate.replace("<ATT>", att.getName());
            jpTemplateStr += attTemplate.toString();
        }

        var laraResource = getJpResource(jpName);
        var laraFile = new File(outputFolder, laraResource);
        SpecsIo.write(laraFile, jpTemplateStr);
    }

    public static List<String> getJpTypes(JoinPointClass jpType) {
        var types = new ArrayList<String>();
        var current = jpType;
        boolean stop = false;

        while (!stop) {
            types.add(current.getName());
            var superJp = current.getExtend();
            if (superJp.isEmpty()) {
                stop = true;
            } else {
                current = superJp.get();
            }
        }

        // Finally add base
        //types.add("joinpoint");

        return types;
    }

    // classes written in javascript
    public static List<String> classesInJS = Arrays
            .asList("DeclJp", "FunctionJp", "StmtJp", "JoinPoint");

    public static String getJpResource(String jpName) {
        var jpClassName = getJoinPointClassName(jpName);
        // check if has JS definition in list
        // System.err.println("getJpResource::" + jpClassName);
        // if (classesInJS.contains(jpClassName)) return "weaver/jp/" + jpClassName + ".js";
        return "weaver/jp/" + jpClassName + ".lara";
    }

    public static Boolean isAttDefinedInSuper(JoinPointClass jpType, org.lara.language.specification.dsl.Attribute att) {
        var superJpTry = jpType.getExtend();

        // TODO: Is the joinpoint check needed?
        //if (superJpTry.isEmpty() || superJpTry.get().equals("joinpoint"))
        if (superJpTry.isEmpty()) {
            return false;
        }
        var superJp = superJpTry.get();


        var atts = superJp.getAttributes();

        if (atts.stream().anyMatch(attElem -> attElem.getName().equals(att.getName()))) {
            return true;
        }

        return isAttDefinedInSuper(superJp, att);

    }

}
