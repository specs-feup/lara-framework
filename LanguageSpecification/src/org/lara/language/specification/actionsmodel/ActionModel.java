/**
 * Copyright 2013 SPeCS Research Group.
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

package org.lara.language.specification.actionsmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.lara.language.specification.IModel;
import org.lara.language.specification.actionsmodel.schema.Action;
import org.lara.language.specification.actionsmodel.schema.ActionsList;
import org.lara.language.specification.actionsmodel.schema.ObjectFactory;
import org.lara.language.specification.actionsmodel.schema.Parameter;
import org.lara.language.specification.joinpointmodel.JoinPointModel;
import org.lara.language.specification.joinpointmodel.constructor.JoinPointModelConstructor;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.lara.language.specification.resources.LanguageSpecificationResources;
import org.xml.sax.SAXException;

import pt.up.fe.specs.util.SpecsFactory;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.providers.ResourceProvider;
import tdrc.utils.MarshalUtils;

public class ActionModel implements IModel {
    private static final String ActionsModelPackageName = ObjectFactory.class.getPackage().getName();
    private final static QName _Actions_QNAME = new QName("", "actions");
    private ActionsList actionsList;
    private ObjectFactory objFactory;

    /**
     * Create a new instance of the {@link ActionModel} according to an xml file specification, plus an xsd schema for
     * validation
     *
     * @param actionsModelFile
     * @param schema
     * @throws JAXBException
     * @throws SAXException
     * @throws XMLParseException
     */
    /*
     * public ActionModel(File actionsModelFile, boolean validate) throws
     * JAXBException, SAXException, XMLParseException {
     *
     * this(new StreamSource(actionsModelFile), actionsModelFile.getName(),
     * validate); }
     */

    /*
     * public ActionModel(ResourceProvider actions, boolean validate) {
     *
     *
     * try(InputStream actionStream = IoUtils.resourceToStream(actions); new
     * StreamSource(actionStream)) {
     *
     * }
     *
     * ActionModel actionModel = new ActionModel(new StreamSource(actionStream),
     * "default", validate);
     *
     *
     * }
     */

    public static ActionModel newInstance(File actionsModelFile, boolean validate) {
        // Don't know how StreamSource works.
        // Using InputStream directly, so that we can be sure the InputStream
        // gets closed
        try (InputStream actionsStream = new FileInputStream(actionsModelFile)) {
            return newInstance(new StreamSource(actionsStream), actionsModelFile.getName(), validate);
        } catch (final Exception e) {
            throw new RuntimeException("Could not create Action Model from file", e);
        }

    }

    public static ActionModel newInstance(ResourceProvider actions, String sourceName, boolean validate) {
        try (InputStream actionStream = SpecsIo.resourceToStream(actions)) {
            final StreamSource actionSource = new StreamSource(actionStream);
            return newInstance(actionSource, sourceName, validate);
        } catch (final Exception e) {
            throw new RuntimeException("Could not create Action Model from resource", e);
        }
    }

    private static ActionModel newInstance(Source actionModelSource, String sourceName, boolean validate) {
        if (validate) {
            return newInstanceWithValidation(actionModelSource, sourceName);
        }

        // Create new ActionModel without validation
        try {
            return new ActionModel(MarshalUtils.unmarshal(actionModelSource, sourceName, null, ActionsList.class,
                    ActionModel.ActionsModelPackageName, false));
        } catch (final Exception e) {
            throw new RuntimeException("Could not create Action Model", e);
        }
    }

    private static ActionModel newInstanceWithValidation(Source joinPointModelSource, String sourceName) {
        try (InputStream iS = SpecsIo
                .resourceToStream(LanguageSpecificationResources.ActionModelSchema.getResource())) {

            return new ActionModel(MarshalUtils.unmarshal(joinPointModelSource, sourceName, iS, ActionsList.class,
                    ActionModel.ActionsModelPackageName, true));

        } catch (final Exception e) {
            throw new RuntimeException("Could not create Action Model with validation", e);
        }
    }

    /*
     * public ActionModel(Source joinPointModelSource, String sourceName,
     * boolean validate) throws JAXBException, SAXException {
     *
     * InputStream iS = null; if (validate) { iS =
     * IoUtils.resourceToStream(LanguageSpecificationResources.ActionModelSchema
     * .getResource()); } this.actionsList =
     * MarshalUtils.unmarshal(joinPointModelSource, sourceName, iS,
     * ActionsList.class, ActionsModelPackageName); this.sanitize(); }
     */
    private void sanitize() {

        for (final Action action : actionsList.getAction()) {
            if (action.getClazz() == null) {
                action.setClazz("*"); // By default, an action pertains to all
                // join points ("*")
            }
        }
    }

    /**
     * Create a new instance with a predefined list of artifacts
     *
     * @param actionsList
     */
    public ActionModel(ActionsList actionsList) {
        setActionsList(actionsList);

        sanitize();
    }

    /**
     * Create a new instance with an empty artifacts list
     */
    public ActionModel() {

        setActionsList(getObjFactory().createActionsList());
    }

    /**
     * Returns a list of specific actions pertaining to a join point. Excludes global actions
     *
     * @param joinPointType
     *            the join point owner of the actions
     * @return
     */
    public List<Action> getJoinPointOwnActions(String joinPointType) {
        final List<Action> actions = new ArrayList<>();
        for (final Action action : actionsList.getAction()) {
            final String jpType = action.getClazz();

            if (jpType.equals("*")) {
                continue;
            }

            final boolean actionPertainsToJoinPoint = doesActionPertainsToJoinPoint(joinPointType, action);
            if (actionPertainsToJoinPoint) {

                actions.add(action);
            }
        }
        return actions;
    }

    /**
     * Returns a list of actions pertaining to a join point. Includes global actions
     *
     * @param joinPointType
     *            the join point owner of the actions
     * @return
     */
    public List<Action> getJoinPointActions(String joinPointType) {
        final List<Action> actions = new ArrayList<>();
        for (final Action action : actionsList.getAction()) {
            final boolean actionPertainsToJoinPoint = doesActionPertainsToJoinPoint(joinPointType, action);

            if (actionPertainsToJoinPoint) {

                actions.add(action);
            }
        }

        // Add default insert action
        final Action insertAct = new Action();
        insertAct.setName("insert");
        insertAct.setClazz("*");
        // action 'insert' returns an array of join points
        insertAct.setReturn("Joinpoint[]");
        final Parameter positionParam = new Parameter();
        positionParam.setName("position");
        positionParam.setType("String");
        insertAct.getParameter().add(positionParam);
        final Parameter codeParam = new Parameter();
        codeParam.setName("code");
        codeParam.setType("String");
        insertAct.getParameter().add(codeParam);
        actions.add(insertAct);

        // Add default insert action overload that receives a JoinPoint
        final Action insertActJp = new Action();
        insertActJp.setName("insert");
        insertActJp.setClazz("*");
        // action 'insert' returns an array of join points
        insertActJp.setReturn("Joinpoint[]");
        final Parameter positionParam2 = new Parameter();
        positionParam2.setName("position");
        positionParam2.setType("String");
        insertActJp.getParameter().add(positionParam2);
        final Parameter codeParam2 = new Parameter();
        codeParam2.setName("code");
        codeParam2.setType("JoinpointInterface");
        insertActJp.getParameter().add(codeParam2);
        actions.add(insertActJp);

        // Add default define (def) action
        final Action defAct = new Action();
        defAct.setName("def");
        defAct.setClazz("*");
        final Parameter attParam = new Parameter();
        attParam.setName("attribute");
        attParam.setType("String");
        defAct.getParameter().add(attParam);
        final Parameter valueParam = new Parameter();
        valueParam.setName("value");
        valueParam.setType("Object");
        defAct.getParameter().add(valueParam);
        actions.add(defAct);

        return actions;
    }

    /*
     * Method insertMethod = new Method(void.class.getSimpleName(), "insert");
     * insertMethod.addArgument("String", "position");
     * insertMethod.addArgument("String", "code"); insertMethod.appendCode(
     * "throw new UnsupportedOperationException(\"Join point \"+" +
     * GET_CLASS_NAME + "()+\": Action insert not implemented \");");
     * abstJPClass.add(insertMethod);
     *
     * Method defMethod = new Method(void.class.getSimpleName(), "def");
     * defMethod.addArgument("String", "attribute");
     * defMethod.addArgument("Object", "value"); defMethod.appendCode(
     * "throw new UnsupportedOperationException(\"Join point \"+" +
     * GET_CLASS_NAME + "()+\": Action def not implemented \");");
     * abstJPClass.add(defMethod);
     *
     */

    /**
     * Returns a list of all actions pertaining to a join point. Includes global actions and actions pertaining to the
     * super join point
     *
     * @param joinPointType
     *            the join point owner of the actions
     * @return
     */
    public List<Action> getAllJoinPointActions(JoinPointType joinPointType, JoinPointModel jpm) {
        final List<Action> actions = new ArrayList<>();
        final List<String> jpTypes = new ArrayList<>();
        jpTypes.add(joinPointType.getClazz());
        String parent = JoinPointModelConstructor.getJoinPointClass(joinPointType.getExtends());

        while (parent != null && parent != joinPointType.getClazz()) {
            jpTypes.add(parent);
            joinPointType = jpm.getJoinPoint(parent);
            parent = JoinPointModelConstructor.getJoinPointClass(joinPointType.getExtends());
        }
        for (final Action action : actionsList.getAction()) {
            final boolean actionPertainsToJoinPoint = doesActionPertainsToJoinPoint(jpTypes, action);
            if (actionPertainsToJoinPoint) {

                actions.add(action);
            }
        }
        return actions;
    }

    private static boolean doesActionPertainsToJoinPoint(String jpClass, Action action) {
        final String jpClassesForActionStr = action.getClazz();
        final String[] jpClassesForAction = jpClassesForActionStr.split(",");
        final List<String> classesInAction = Arrays.asList(jpClassesForAction);

        final boolean actionPertainsToJoinPoint = jpClassesForActionStr.equals("*")
                || classesInAction.contains(jpClass);
        return actionPertainsToJoinPoint;
    }

    private static boolean doesActionPertainsToJoinPoint(List<String> jpTypes, Action action) {
        final String jpClassesForActionStr = action.getClazz();
        final String[] jpClassesForAction = jpClassesForActionStr.split(",");
        final List<String> classesInAction = Arrays.asList(jpClassesForAction);

        final boolean actionPertainsToJoinPoint = jpClassesForActionStr.equals("*")
                || !Collections.disjoint(jpTypes, classesInAction);
        return actionPertainsToJoinPoint;
    }

    /**
     * Returns a list of actions pertaining to all join points.
     *
     * @return
     */
    public List<Action> getActionsForAll() {
        final List<Action> actions = new ArrayList<>();
        for (final Action action : actionsList.getAction()) {
            final String jpType = action.getClazz();
            if (jpType.equals("*")) {

                actions.add(action);
            }
        }
        return actions;
    }

    /**
     * <b>NOTE:</b> This method should not be used as we accept method overloading. Please use
     * {@link ActionModel#getActions}. <br>
     * Get an {@link Action} with the specified name.
     *
     * @param actionName
     * @return the {@link Action}; or null if there is no action with the specified name
     */
    @Deprecated
    public Action getAction(String actionName) {

        for (final Action act : actionsList.getAction()) {

            if (act.getName().equals(actionName)) {
                return act;
            }
        }
        return null;

    }

    /**
     * Returns a list of actions with the given name
     *
     * @param actionName
     * @return
     */
    public List<Action> getActions(String actionName) {
        List<Action> actions = SpecsFactory.newArrayList();
        for (final Action act : actionsList.getAction()) {

            if (act.getName().equals(actionName)) {
                actions.add(act);
            }
        }

        return actions;
    }

    /**
     * Get a specific parameter
     *
     * @param parameterName
     * @param act
     * @return
     */
    public Parameter getParameter(String parameterName, Action act) {

        for (final Parameter param : act.getParameter()) {

            if (param.getName().equals(parameterName)) {
                return param;
            }
        }

        return null;
    }

    /**
     * Generate an XML representation of the Artifacts list
     *
     * @param oStream
     * @throws JAXBException
     */
    @Override
    public void toXML(OutputStream oStream) throws JAXBException {

        MarshalUtils.marshal(actionsList, ActionsList.class, ActionModel.ActionsModelPackageName,
                ActionModel._Actions_QNAME, oStream);
    }

    /**
     * @return the objFactory
     */
    public ObjectFactory getObjFactory() {
        if (objFactory == null) {
            objFactory = new ObjectFactory();
        }
        return objFactory;
    }

    /**
     * @param objFactory
     *            the objFactory to set
     */
    public void setObjFactory(ObjectFactory objFactory) {
        this.objFactory = objFactory;
    }

    /**
     * @return the actionsList
     */
    public ActionsList getActionsList() {
        return actionsList;
    }

    /**
     * @param actionsList
     *            the actionsList to set
     */
    public void setActionsList(ActionsList actionsList) {
        this.actionsList = actionsList;
    }

    @Override
    public boolean contains(String name) {

        return !getActions(name).isEmpty();
    }

    @Override
    public boolean contains(String name, String subname) {
        for (Action action : getActions(name)) {
            if (getParameter(subname, action) != null) {

                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String ret = "----------- Action Model -----------\n";
        for (final Action action : actionsList.getAction()) {

            ret += action.getName() + "\n";
            for (final Parameter parameter : action.getParameter()) {

                ret += "\t" + parameter.getName() + "(" + parameter.getType() + ")";
                if (parameter.getDefault() != null) {
                    ret += " = " + parameter.getDefault();
                }
                ret += "\n";
            }
        }
        return ret;
    }
}
