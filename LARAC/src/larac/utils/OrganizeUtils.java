/*
 * Copyright 2013 SPeCS.
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dojo.jsl.parser.ast.ASTExpressionStatement;
import org.dojo.jsl.parser.ast.LARAEcmaScript;
import org.dojo.jsl.parser.ast.ParseException;
import org.dojo.jsl.parser.ast.SimpleNode;
import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.actionsmodel.schema.Action;
import org.lara.language.specification.actionsmodel.schema.Parameter;
import org.lara.language.specification.artifactsmodel.schema.TypeDef;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import larac.exceptions.LARACompilerException;
import larac.objects.Enums;
import larac.objects.Enums.Types;
import larac.utils.xml.entity.ActionArgument;
import tdrc.utils.StringUtils;

public class OrganizeUtils {
    private static final String VALUE_ARGUMENT_NAME = "value";

    private static final String ATTRIBUTE_ARGUMENT_NAME = "attribute";
    private static final String CODE_ARGUMENT_NAME = "code";
    private static final String POSITION_ARGUMENT_NAME = "position";
    public String space = "";
    public static final String IDENT_STR = "  ";
    public String fileName = "";
    public int condNum = 0;
    public int applyNum = 0;
    public int codeNum = 0;

    public static ArrayList<Node> getDirectChilds(Element joinPoint, String tag) {
        final ArrayList<Node> childs = new ArrayList<>();
        final NodeList nodeList = joinPoint.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals(tag)) {
                childs.add(nodeList.item(i));
            }
        }
        return childs;
    }

    public static ArrayList<Node> getDescendantNodes(Element el, ArrayList<String> tags, int pos) {
        final String tag = tags.get(pos++);
        final ArrayList<Node> nodes = getDirectChilds(el, tag);
        if (tags.size() == pos) {
            return nodes;
        }
        final ArrayList<Node> finalNodes = new ArrayList<>();
        for (final Node n : nodes) {
            final Element elChild = (Element) n;
            finalNodes.addAll(getDescendantNodes(elChild, tags, pos));
        }
        return finalNodes;
    }

    @Deprecated
    public static Map<String, ActionArgument> createPerformActionParameters(LanguageSpecification langSpec,
            String method) {
        final Action act = langSpec.getActionModel().getAction(method);
        return createActionParameters(act, langSpec);
    }

    public static Map<String, ActionArgument> createActionParameters(final Action act,
            LanguageSpecification langSpec) {
        final Map<String, ActionArgument> args = new LinkedHashMap<>();
        for (final Parameter param : act.getParameter()) {

            final ActionArgument actionArgument = new ActionArgument(param.getName(), param.getType(), langSpec);
            if (param.getDefault() != null) {
                actionArgument.setValue(param.getDefault());
            }
            args.put(param.getName(), actionArgument);
        }
        return args;
    }

    public static Map<String, ActionArgument> createInsertParameters(LanguageSpecification spec) {
        final Map<String, ActionArgument> args = new LinkedHashMap<>();

        final ActionArgument when = new ActionArgument(OrganizeUtils.POSITION_ARGUMENT_NAME, "string", spec);
        args.put(OrganizeUtils.POSITION_ARGUMENT_NAME, when);
        final ActionArgument code = new ActionArgument(OrganizeUtils.CODE_ARGUMENT_NAME, "template", spec);
        args.put(OrganizeUtils.CODE_ARGUMENT_NAME, code);
        return args;
    }

    public static Map<String, ActionArgument> createOutputActionParameters(LanguageSpecification spec) {
        final Map<String, ActionArgument> args = new LinkedHashMap<>();
        final ActionArgument code = new ActionArgument(OrganizeUtils.CODE_ARGUMENT_NAME, "template", spec);
        args.put(OrganizeUtils.CODE_ARGUMENT_NAME, code);
        return args;
    }

    public static Map<String, ActionArgument> createDefParameters(LanguageSpecification spec) {
        final Map<String, ActionArgument> args = new LinkedHashMap<>();

        final ActionArgument attribute = new ActionArgument(OrganizeUtils.ATTRIBUTE_ARGUMENT_NAME, "string", spec);
        args.put(OrganizeUtils.ATTRIBUTE_ARGUMENT_NAME, attribute);
        final ActionArgument value = new ActionArgument(OrganizeUtils.VALUE_ARGUMENT_NAME, "Object", spec);
        args.put(OrganizeUtils.VALUE_ARGUMENT_NAME, value);
        return args;
    }

    /**
     * <parameter name="attribute" type="string"/> <parameter name="value" type= "Object"/>
     */

    private static final Pattern CODE_REGEX = Pattern.compile(Enums.INSERT_SYMBOL_REGEX);

    public static List<String> getTagValues(final String str) {
        final List<String> tagValues = new ArrayList<>();
        final Matcher matcher = OrganizeUtils.CODE_REGEX.matcher(str);
        while (matcher.find()) {
            tagValues.add(matcher.group(1));
        }
        return tagValues;
    }

    public static String firstCharToUpper(String string) {
        return charToUpper(string, 0);
    }

    public static String charToUpper(String string, int pos) {
        if (pos < 0 || pos >= string.length()) {
            throw new StringIndexOutOfBoundsException(pos);
        }
        String ret = string.substring(0, pos);
        ret += string.substring(pos, pos + 1).toUpperCase();
        ret += string.substring(pos + 1);
        return ret;
    }

    /**
     * Create an element, inside the document, as an undefined literal type, as child of the given element
     * 
     * @param doc
     * @param parent
     */
    public static void createLiteralUndefined(Document doc, Element parent) {
        final Element litEl = doc.createElement("literal");
        litEl.setAttribute("type", Types.Undefined.toString());
        litEl.setAttribute("value", Types.Undefined.toString());
        parent.appendChild(litEl);
    }

    public static Types getConvertedType(String typeStr, LanguageSpecification spec) {
        // String original = type;
        // First remove array dimension

        final Types type = Types.value(typeStr);
        if (type != null) {
            return type;
        }
        if (typeStr.contains("[]")) {
            return Types.Array;
        }

        // if the object declaration exist in the artifacts
        if (spec.getArtifacts().hasObject(typeStr)) {
            return Types.Object;
        }
        // if it is a join point class
        if (spec.getJpModel().contains(typeStr)) {
            return Types.Joinpoint;
        }

        // If it does not exist, throw an exception with the error message and
        // the possible
        // types that can be used
        final StringBuilder message = new StringBuilder("Could not convert type '" + type + "'. Available types: ");

        final StringBuilder availableTypes = reportAvailableTypes(spec);
        message.append(availableTypes);

        throw new RuntimeException(message.toString());
    }

    private static StringBuilder reportAvailableTypes(LanguageSpecification languageSpecification) {
        final StringBuilder message = new StringBuilder("\n\t Primitives: ");
        message.append(StringUtils.join(Arrays.asList(Types.values()), ", "));
        // message.append(", Object, Array, Map, Template, Joinpoint");

        final List<TypeDef> objects = languageSpecification.getArtifacts().getTypeDefs();
        if (!objects.isEmpty()) {

            message.append("\n\t Defined types: ");
            final String objectsString = StringUtils.join(objects, TypeDef::getName, ", ");
            message.append(objectsString);
        }

        final List<JoinPointType> joinpoints = languageSpecification.getJpModel().getJoinPointList().getJoinpoint();
        if (!joinpoints.isEmpty()) {

            message.append("\n\t Join point types: ");
            final String jpsString = StringUtils.join(joinpoints, JoinPointType::getClazz, ", ");
            message.append(jpsString);
        }
        return message;
    }

    /**
     * Parse an arbitrary string as an expression statement and returns the expression itself
     * 
     * @param code
     *            the code to parse
     * @return the expression node
     */
    public static SimpleNode parseExpression(String code) {
        InputStream is = new ByteArrayInputStream(code.getBytes());
        LARAEcmaScript parser = new LARAEcmaScript(is);
        try {

            ASTExpressionStatement parseExpression = parser.parseExpression(code);
            if (parseExpression.jjtGetNumChildren() == 0) {
                throw new RuntimeException("Expression is empty");
            }
            if (parseExpression.jjtGetNumChildren() != 1) {
                throw new RuntimeException("More than one expression was given");
            }
            return parseExpression.getChild(0);
            // return parseExpression;
        } catch (ParseException e) {
            RuntimeException simpleE = new RuntimeException(e.getMessage());
            throw new LARACompilerException("Problems when parsing code parameter: " + code, simpleE);

        } catch (Exception e) {

            throw new LARACompilerException("Problems when parsing code parameter: " + code, e);
        }

    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getCondNum() {
        return condNum;
    }

    public void setCondNum(int condNum) {
        this.condNum = condNum;
    }

    public int getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(int applyNum) {
        this.applyNum = applyNum;
    }

    public int getCodeNum() {
        return codeNum;
    }

    public void setCodeNum(int codeNum) {
        this.codeNum = codeNum;
    }

    public static String getValueArgumentName() {
        return OrganizeUtils.VALUE_ARGUMENT_NAME;
    }

    public static String getAttributeArgumentName() {
        return OrganizeUtils.ATTRIBUTE_ARGUMENT_NAME;
    }

    public static String getCodeArgumentName() {
        return OrganizeUtils.CODE_ARGUMENT_NAME;
    }

    public static String getPositionArgumentName() {
        return OrganizeUtils.POSITION_ARGUMENT_NAME;
    }

    public static String getIdentStr() {
        return OrganizeUtils.IDENT_STR;
    }

    public static Pattern getCodeRegex() {
        return OrganizeUtils.CODE_REGEX;
    }
}
