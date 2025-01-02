/* Generated By:JJTree: Do not edit this line. ASTPointcut.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import larac.LaraC;
import larac.exceptions.LARACompilerException;
import larac.exceptions.LaraException;
import larac.objects.Enums.Types;
import larac.objects.Variable;
import org.dojo.jsl.parser.ast.utils.LARACConstantPool;
import org.lara.language.specification.dsl.Select;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import utils.SelectionPath;
import utils.Selector;

import java.util.List;

public class ASTPointcut extends SimpleNode {
    public String reference;
    private String type;
    private Variable var;
    private boolean validateChain = true;

    public ASTPointcut(int id) {
        super(id);
    }

    public ASTPointcut(LARAEcmaScript p, int id) {
        super(p, id);
    }

    public void setReference(String string) {

        if (!string.startsWith("$")) {
            throw new LaraException(
                    "Id \"" + string + "\" for join point \"" + value + "\" must start with '$'");
        }

        reference = string;

    }

    public String getReference() {
        return reference;
    }

    /**
     * @return the var
     */
    public Variable getVar() {
        return var;
    }

    /**
     * @param var the var to set
     */
    public void setVar(Variable var) {
        this.var = var;
    }

    @Override
    public Object organize(Object obj) {
        final ASTSelect select = ((ASTSelect) obj);
        final ASTAspectDef aspdef = this.getAspectDefForDeclStmt("Select");
        // final ASTAspectDef aspdef = (ASTAspectDef) (select.parent);
        final LaraC lara = aspdef.getLara();
        // final JoinPointModel joinPointModel = lara.languageSpec().getJpModel();
        // This type was set previously, I hope!
        // setType(joinPointModel.getLastPointcutType());
        var = new Variable(reference, Types.Joinpoint);
        select.putPointcut(reference, this);
        if (getChildren() == null) {
            return null;
        }
        final SimpleNode firstChild = (SimpleNode) getChildren()[0];
        SimpleNode pointcutNode = null;

        if (firstChild instanceof ASTPointcutFilters) {
            final ASTPointcutFilters pcp = (ASTPointcutFilters) firstChild;
            pcp.organize(this, type, lara);

            if (getChildren().length > 1) {
                pointcutNode = (SimpleNode) getChildren()[1];
            } else {
                return null;
            }
        } else {
            pointcutNode = firstChild;
        }

        final String jpType = getType();
        final String selectName = pointcutNode.value.toString();
        // final PairList<String, String> path2 = joinPointModel.getPath(jpType, selectName, validateChain);
        final SelectionPath selPath = new Selector(lara.languageSpec()).selectionPath(jpType, selectName,
                validateChain);
        // final SelectionPathV2 selPath = joinPointModel.selectionPath(jpType, selectName, validateChain);

        // System.out.println("##########################################");
        // System.out.println("[DEBUG]" + path2);
        // System.out.println("[DEBUG]" + selPath);
        // System.out.println("##########################################");

        if ((!selPath.hasPath() || selPath.getPath().isEmpty()) && validateChain) {
            throw newException(
                    "The following pointcut chain is not correct: \"" + value + "\"->\"" + pointcutNode.value + "\"");
        }

        if (selPath.hasSecondaryPath()) {
            getLara().warnln("More than one path was found for select: " + selPath.toString());

        }
        List<Select> path = selPath.getReversedPath();

        ASTPointcut pointcutNode2 = (ASTPointcut) pointcutNode;
        if (path != null) {
            if (path.size() > 1) {
                int i = getChildren().length - 1;
                SimpleNode nextParent = this;
                for (int j = 0; j < path.size() - 1; j++) {
                    final ASTPointcut pc = new ASTPointcut(id);
                    final String pcName = path.get(j).getSelectName();
                    // final String pcName = path.get(j).getAlias();
                    // final String pcType = path.get(j).getClazz().getClazz();
                    final String pcType = path.get(j).getClazz().getName();
                    pc.jjtSetValue(pcName);
                    pc.setReference("$" + pcName + LARACConstantPool.HIDDEN_TAG + select.getHiddenCount());
                    pc.setType(pcType);
                    nextParent.associateChild(pc, i);
                    nextParent = pc;
                    i = 0;
                }
                nextParent.associateChild(pointcutNode, i);
            }
            if (!path.isEmpty()) {
                // pointcutNode2.setType(path.get(path.size() - 1).getClazz().getClazz());
                pointcutNode2.setType(path.get(path.size() - 1).getClazz().getName());
            } else {
                pointcutNode2.setType(selectName);
            }
        } else {
            pointcutNode2.setValidateChain(false);
        }
        // ASTPointcut pointcutNode2 = (ASTPointcut) pointcutNode;
        // if (path != null) {
        // if (path.size() > 1) {
        // int i = getChildren().length - 1;
        // SimpleNode nextParent = this;
        // for (int j = 0; j < path.size() - 1; j++) {
        // final ASTPointcut pc = new ASTPointcut(id);
        // final String pcName = path.get(j).getLeft();
        // final String pcType = path.get(j).getRight();
        // pc.jjtSetValue(pcName);
        // pc.setReference("$" + pcName + LARACConstantPool.HIDDEN_TAG + select.getHiddenCount());
        // pc.setType(pcType);
        // nextParent.associateChild(pc, i);
        // nextParent = pc;
        // i = 0;
        // }
        // nextParent.associateChild(pointcutNode, i);
        // }
        // if (!path.isEmpty()) {
        // pointcutNode2.setType(path.last().getRight());
        // } else {
        // pointcutNode2.setType(selectName);
        // }
        // } else {
        // pointcutNode2.setValidateChain(false);
        // }
        pointcutNode.organize(obj);
        return null;
    }

    @Override
    public Object organizeFirst(Object obj, int i) {
        final ASTSelect select = ((ASTSelect) obj);

        // final ASTAspectDef aspdef = (ASTAspectDef) (select.parent);
        final ASTAspectDef aspdef = this.getAspectDefForDeclStmt("Select");

        final LaraC lara = aspdef.getLara();
        // final JoinPointModel joinPointModel = lara.languageSpec().getJpModel();

        // if (!joinPointModel.contains(value.toString())) {
        if (!lara.languageSpec().hasJoinPointName(value.toString())) {

            final Variable var = lookup(value.toString());

            if (var.isNotFound() && !getLara().getOptions().isDocumentationMode()) {
                throw new LARACompilerException(
                        "The join point \"" + value + "\" does not exist in the join point model");
            }
            // getLara().warnln("Using variable '" + value + "' to create select " + select.label
            // + ": Auto-complete could not be used. Pointcut chain has to be complete and attributes must be
            // defined.");
            type = LARACConstantPool.USER_DEFINED;
            // Not knowing this join point type, try to validate the rest of the chain, starting with the next join
            // point
            if (getChildren() != null) {

                final SimpleNode firstChild = (SimpleNode) getChildren()[0];
                if (firstChild instanceof ASTPointcutFilters) {
                    throw new LARACompilerException(
                            "Cannot use join point filters in the variable used as starting join point");
                }
                if (!(firstChild instanceof ASTPointcut)) {
                    throw new LARACompilerException(
                            "Only a pointcut can be used when using a variable as starting join point. Type used: "
                                    + firstChild.toString());
                }
                ASTPointcut childAsPointCut = (ASTPointcut) firstChild;
                childAsPointCut.setType(firstChild.value.toString());
                childAsPointCut.setValidateChain(false);
                childAsPointCut.organize(obj); // TODO- if the next child cannot be found in the join point model then
                // do not
                // validate the child. This should be recursive for the rest of the chain,
                // until a valid join point is found
            }

            return null;
            // getLara().warnln("Could not solve select for join point
            // "+value+"");
            // throw newException("The join point \"" + value + "\" does not
            // exist in the join point model");
        }
        // final PairList<String, String> path2 = joinPointModel.getPath(value.toString());
        final SelectionPath selPath = new Selector(lara.languageSpec()).selectionPath(value.toString());
        // final SelectionPath selPath = joinPointModel.selectionPath(value.toString());
        //
        // System.out.println("##########################################");
        // System.out.println("[DEBUG]" + path2);
        // System.out.println("[DEBUG]" + selPath);
        // System.out.println("##########################################");

        if (!selPath.hasPath() || selPath.getPath().isEmpty()) {
            throw new LARACompilerException("No path exists for was found for join point '" + value + "'");
        }

        if (selPath.hasSecondaryPath()) {
            getLara().warnln("More than one path was found for select: " + selPath.toString());
        }
        List<Select> path = selPath.getReversedPath();

        for (int j = 0; j < path.size() - 1; j++) {
            final ASTPointcut pc = new ASTPointcut(id);
            final String pcName = path.get(j).getSelectName();
            // final String pcName = path.get(j).getAlias();
            // final String pcType = path.get(j).getClazz().getClazz();
            final String pcType = path.get(j).getClazz().getName();
            pc.jjtSetValue(pcName);
            pc.setReference("$" + pcName + LARACConstantPool.HIDDEN_TAG + select.getHiddenCount());
            pc.setType(pcType);
            ((SimpleNode) parent).associateChild(pc, i);
            pc.associateChild(this, 0);
            i = 0;
        }
        // setType(path.get(path.size() - 1).getClazz().getClazz());
        setType(path.get(path.size() - 1).getClazz().getName());

        // if (path == null || path.isEmpty()) {
        // throw new LARACompilerException("No path exists for was found for join pont '" + value + "'");
        // }
        //
        // for (int j = 0; j < path.size() - 1; j++) {
        // final ASTPointcut pc = new ASTPointcut(id);
        // final String pcName = path.get(j).getLeft();
        // final String pcType = path.get(j).getRight();
        // pc.jjtSetValue(pcName);
        // pc.setReference("$" + pcName + LARACConstantPool.HIDDEN_TAG + select.getHiddenCount());
        // pc.setType(pcType);
        // ((SimpleNode) parent).associateChild(pc, i);
        // pc.associateChild(this, 0);
        // i = 0;
        // }
        // setType(path.last().getRight());
        this.organize(obj);
        return null;
    }

    private void setValidateChain(boolean b) {
        validateChain = b;
    }

    @Override
    public Variable lookupLastJPVariable() {
        if (children == null) {
            return var;
        }
        if (children.length == 1) {
            if ((children[0] instanceof ASTPointcutFilters)) {
                return var;
            }
            return ((SimpleNode) children[0]).lookupLastJPVariable();
        }
        return ((SimpleNode) children[1]).lookupLastJPVariable();
    }

    @Override
    public String toString() {
        return LARAEcmaScriptTreeConstants.jjtNodeName[id]
                + (value != null ? " [" + reference + "," + value + "]" : "");
    }

    @Override
    public void toXML(Document doc, Element parent) {
        final Element nameExprEl = doc.createElement("expression");
        parent.appendChild(nameExprEl);
        Element nameLitEl;
        if (type != null && type.equals(LARACConstantPool.USER_DEFINED)) {
            // nameExprEl.setAttribute("desc", USER_DEFINED);
            nameLitEl = doc.createElement("id");
            nameLitEl.setAttribute("name", value.toString());
        } else {

            nameLitEl = doc.createElement("literal");
            nameLitEl.setAttribute("value", value.toString());
            nameLitEl.setAttribute("type", Types.String.toString());

        }

        nameExprEl.appendChild(nameLitEl);

        final Element idExprEl = doc.createElement("expression");
        parent.appendChild(idExprEl);
        final Element idLitEl = doc.createElement("literal");

        idLitEl.setAttribute("value", getReference().substring(1));
        idLitEl.setAttribute("type", Types.String.toString());
        idExprEl.appendChild(idLitEl);
        /*
         * Element classExprEl = doc.createElement("expression");
         * parent.appendChild(classExprEl); Element classLitEl =
         * doc.createElement("literal"); classLitEl.setAttribute("value", type);
         * classLitEl.setAttribute("type", Types.String.toString());
         * classExprEl.appendChild(classLitEl);
         */
        final Element filterExprEl = doc.createElement("expression");
        parent.appendChild(filterExprEl);
        if (getChildren() == null) {
            return;
        }
        if (((SimpleNode) getChildren()[0]) instanceof ASTPointcutFilters) {
            ((SimpleNode) getChildren()[0]).toXML(doc, filterExprEl);
            if (getChildren().length > 1) {
                ((SimpleNode) getChildren()[1]).toXML(doc, parent);
            }
        } else {
            ((SimpleNode) getChildren()[0]).toXML(doc, parent);
        }

    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean hasFilter() {
        if (getChildren() == null) {
            return false;
        }
        if (getChildren().length == 2) {
            return true;
        }
        if (getChildren()[0] instanceof ASTPointcutFilters) {
            return true;
        }
        return ((SimpleNode) getChildren()[0]).hasFilter();
    }

    @Override
    public Element getFilterElement(Document doc) {

        // Element filterEl = doc.createElement("TODO-FILTER");
        if (getChildren() == null) {
            return null;
        }
        if (getChildren().length == 2) {
            final ASTPointcutFilters props = (ASTPointcutFilters) getChildren()[0];
            final Element propsEl = props.getFilterElement(doc);
            final Element childPropsEl = ((SimpleNode) getChildren()[1]).getFilterElement(doc);
            if (childPropsEl != null) {
                final Element andEl = doc.createElement("AND");
                andEl.appendChild(propsEl);
                andEl.appendChild(childPropsEl);
                return andEl;
            }
            return propsEl;
        }
        if (getChildren()[0] instanceof ASTPointcutFilters) {
            final ASTPointcutFilters props = (ASTPointcutFilters) getChildren()[0];
            return props.getFilterElement(doc);
        }
        return ((SimpleNode) getChildren()[0]).getFilterElement(doc);

    }
}

/*
 * JavaCC - OriginalChecksum=fe9e94dd85a2ee269ee705d34a1bed04 (do not edit this
 * line)
 */
