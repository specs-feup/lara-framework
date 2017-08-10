/* Generated By:JJTree: Do not edit this line. ASTAspectDef.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.LaraC;
import larac.objects.Enums.Types;
import larac.objects.SymbolTable;
import larac.objects.Variable;

public class ASTAspectDef extends SimpleNode {
    private String name;
    private String referenceName;
    private LaraC lara;
    private int selectCount = 0;
    private int applyCount = 0;
    private int conditionCount = 0;
    private ASTSelect lastSelectWithNoLabel = null;
    private ASTApply lastApplyWithNoLabel = null;
    private ASTAroundApply lastBeforeWithNoLabel = null;
    private final SymbolTable<String, ASTSelect> selects = new SymbolTable<>();
    private ASTCheck check;
    private ASTInitialize initialize;
    private ASTFinalize finalize;
    private final ArrayList<SimpleNode> applies = new ArrayList<>();
    private final SymbolTable<String, ASTCondition> conditions = new SymbolTable<>();
    private Variable staticVar;
    private final ArrayList<Integer> nodeList = new ArrayList<>();
    private LinkedHashMap<String, Variable> inputs = new LinkedHashMap<>();

    private SymbolTable<String, Variable> outputs = new SymbolTable<>();
    private SymbolTable<String, Variable> localVars = new SymbolTable<>();
    private ASTStatic Static;
    private ASTInput inputsNode;
    private ASTOutput outputsNode;

    private static enum StatementJump {
        ASTAroundApply,
        ASTCondition,
        ASTInput,
        ASTStatic,
        ASTInitialize,
        ASTFinalize,
        ASTCheck,
        ASTOutput;
        public static boolean contains(String member) {

            if (member != null) {
                for (final StatementJump b : StatementJump.values()) {
                    if (member.equalsIgnoreCase(b.name())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public ASTApply findApplyByName(String name) {
        for (final SimpleNode apply : applies) {
            if (apply instanceof ASTApply) {
                if (apply.value.toString().equals(name)) {
                    return (ASTApply) apply;
                }
            }
        }
        return null;
    }

    public ArrayList<SimpleNode> getApplies() {
        return applies;
    }

    public ASTAspectDef(int id) {
        super(id);
    }

    public ASTAspectDef(LARAEcmaScript p, int id) {
        super(p, id);
    }

    public void setName(String image) {
        name = image;
        value = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object organize(Object obj) {
        if (getChildren() == null) {
            return null;
        }

        for (int i = 0; i < getChildren().length; i++) {
            final SimpleNode node = getChild(i);
            if (!StatementJump.contains(node.getClass().getSimpleName())) {
                nodeList.add(i);
            }
            node.organize(this);

        }
        for (final String var : localVars.keySet()) {
            if (inputs.containsKey(var) || outputs.containsKey(var)
                    || (Static != null && Static.getLocalVars().containsKey(var))) {
                throw newException("Duplicate local var: \"" + var + "\"on aspectdef \"" + getName() + "\"");
            }
        }

        // for(int i: pos){
        // ((SimpleNode)getChildren()[i]).organize(this,nullSelect);
        // }

        if (lastBeforeWithNoLabel != null) {
            getLara().warnln("No apply for the before statement. This statement will not take place.");
        }
        return null;
    }

    public int getSelectCount() {
        return selectCount++;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    /**
     * @return the lara
     */
    @Override
    public LaraC getLara() {
        if (lara == null) {
            return super.getLara();
        }
        return lara;
    }

    /**
     * @param lara
     *            the lara to set
     */
    public void setLara(LaraC lara) {
        this.lara = lara;
    }

    public int getApplyCount() {
        return applyCount++;
    }

    public void setApplyCount(int applyCount) {
        this.applyCount = applyCount;
    }

    public ASTSelect getLastSelectWithNoLabel() {
        return lastSelectWithNoLabel;
    }

    public void setLastSelectWithNoLabel(ASTSelect lastSelectWithNoLabel) {
        this.lastSelectWithNoLabel = lastSelectWithNoLabel;
    }

    public ASTApply getLastApplyWithNoLabel() {
        return lastApplyWithNoLabel;
    }

    public void setLastApplyWithNoLabel(ASTApply lastApplyWithNoLabel) {
        this.lastApplyWithNoLabel = lastApplyWithNoLabel;
    }

    public ASTSelect putSelect(String string, ASTSelect astSelect) {
        return selects.put(string, astSelect);
    }

    public ASTSelect getSelect(String string) {
        return selects.get(string);
    }

    public String printIR() {

        lara.println("aspectdef \"" + getName() + "\"");
        lara.println("Inputs:");
        lara.println("---------------------------------------------------");
        for (final Variable var : inputs.values()) {
            var.printIR();
        }
        lara.println("Outputs:");
        lara.println("---------------------------------------------------");
        for (final Variable var : outputs.values()) {
            var.printIR();
        }
        lara.println("---------------------------------------------------");
        lara.println("Local Variables:");
        lara.println("---------------------------------------------------");
        for (final Variable var : localVars.values()) {
            var.printIR();
        }
        lara.println("---------------------------------------------------");
        return null;
    }

    public int compare(ASTAspectDef o) {
        if (name.equals(o.getName())) {
            return 0;
        }
        return -1;
    }

    /*
     * @Override public boolean equals(Object obj) { if (obj instanceof
     * ASTAspectDef) return equals((ASTAspectDef) obj); return
     * super.equals(obj); }
     */
    /*
     * public boolean equals(ASTAspectDef o) { if (name.equals(o.getName()))
     * return true; return false; }
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ASTAspectDef other = (ASTAspectDef) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public void putApply(ASTApply astApply) {
        applies.add(astApply);
    }

    public ASTCondition putCondition(String string, ASTCondition astCondition) {
        return conditions.put(string, astCondition);
    }

    public HashMap<String, Variable> getInputs() {
        return inputs;
    }

    public void setInputs(LinkedHashMap<String, Variable> inputs) {
        this.inputs = inputs;
    }

    public HashMap<String, Variable> getOutputs() {
        return outputs;
    }

    public void setOutputs(SymbolTable<String, Variable> outputs) {
        this.outputs = outputs;
    }

    public int getConditionCount() {
        return conditionCount++;
    }

    public ASTCondition getCondition(String name) {
        return conditions.get(name);
    }

    @Override
    public void toXML(Document doc, Element root) {
        final Element aspectEl = doc.createElement("aspect");
        aspectEl.setAttribute("name", lara.getPrefix() + getName());
        aspectEl.setAttribute("coord", getCoords());
        root.appendChild(aspectEl);

        final Element parameterEl = doc.createElement("parameters");

        if (inputsNode != null) {
            final Element inputEl = doc.createElement("input");
            parameterEl.appendChild(inputEl);
            createInputXMLDecl(inputsNode.getChild(0), inputs, doc, inputEl);
        }
        if (outputsNode != null) {
            final Element outputEl = doc.createElement("output");
            parameterEl.appendChild(outputEl);
            createInputXMLDecl(outputsNode.getChild(0), outputs, doc, outputEl);
        }
        if (parameterEl.getChildNodes().getLength() > 0) {
            aspectEl.appendChild(parameterEl);
        }
        if (Static != null) {
            Static.toXML(doc, aspectEl);
        }
        if (initialize != null) {
            initialize.toXML(doc, aspectEl);
        }
        if (check != null) {
            check.toXML(doc, aspectEl);
        }

        for (final int i : nodeList) {
            ((SimpleNode) children[i]).toXML(doc, aspectEl);
        }
        /*
         * for(ASTSelect select: selects.values()) select.toXML(doc,aspectEl);
         * for(SimpleNode apply: applies) apply.toXML(doc,aspectEl);
         */
        if (finalize != null) {
            finalize.toXML(doc, aspectEl);
        }
    }

    /**
     * @param localVars
     *            the localVars to set
     */
    public void setLocalVars(SymbolTable<String, Variable> localVars) {
        this.localVars = localVars;
    }

    /**
     * @return the localVars
     */
    public HashMap<String, Variable> getLocalVars() {
        return localVars;
    }

    @Override
    public Variable lookup(String var) {
        if (inputs.containsKey(var)) {
            return inputs.get(var);
        }
        if (outputs.containsKey(var)) {
            return outputs.get(var);
        }

        if (initialize != null) {
            final HashMap<String, Variable> initVars = ((SimpleNode) initialize.getChildren()[0]).getHMVars();
            if (initVars.containsKey(var)) {
                return initVars.get(var);
            }
        }

        if (localVars.containsKey(var)) {
            return localVars.get(var);
        }

        if (Static != null) {
            final Variable retVar = Static.lookupStatic(var);

            if (retVar != null) {
                return retVar;
            }
        }

        return ((SimpleNode) parent).lookup(var);
    }

    @Override
    public Variable lookupNoError(String var) {
        if (inputs.containsKey(var)) {
            return inputs.get(var);
        }
        if (outputs.containsKey(var)) {
            return outputs.get(var);
        }
        if (initialize != null) {

            final HashMap<String, Variable> initVars = ((SimpleNode) initialize.getChildren()[0]).getHMVars();

            if (initVars.containsKey(var)) {
                return initVars.get(var);
            }

        }

        if (localVars.containsKey(var)) {
            return localVars.get(var);
        }
        if (Static != null) {
            final Variable retVar = Static.lookupNoErrorStatic(var);
            if (retVar != null) {
                return retVar;
            }
        }

        return ((SimpleNode) parent).lookupNoError(var);
    }

    public boolean containsCondition(String name2) {
        return conditions.containsKey(name2);
    }

    @Override
    public HashMap<String, Variable> getHMVars() {
        return localVars;
    }

    /**
     * @param initialize
     *            the initialize to set
     */
    public void setInitialize(ASTInitialize initialize) {
        this.initialize = initialize;
    }

    /**
     * @return the initialize
     */
    public ASTInitialize getInitialize() {
        return initialize;
    }

    /**
     * @param finalize
     *            the finalize to set
     */
    public void setFinalize(ASTFinalize finalize) {
        this.finalize = finalize;
    }

    /**
     * @return the finalize
     */
    public ASTFinalize getFinalize() {
        return finalize;
    }

    /**
     * @param lastbeforeWithNoLabel
     *            the lastbeforeWithNoLabel to set
     */
    public void setLastbeforeWithNoLabel(ASTAroundApply lastbeforeWithNoLabel) {
        lastBeforeWithNoLabel = lastbeforeWithNoLabel;
    }

    /**
     * @return the lastbeforeWithNoLabel
     */
    public ASTAroundApply getLastbeforeWithNoLabel() {
        final ASTAroundApply ret = lastBeforeWithNoLabel;
        lastBeforeWithNoLabel = null;
        return ret;
    }

    public ASTApply getLastApplyDecl() {
        for (int i = applies.size() - 1; i > -1; i--) {
            if (applies.get(i) instanceof ASTApply) {
                return (ASTApply) applies.get(i);
            }
        }
        return null;
    }

    /**
     * @param check
     *            the check to set
     */
    public void setCheck(ASTCheck check) {
        this.check = check;
    }

    /**
     * @return the check
     */
    public ASTCheck getCheck() {
        return check;
    }

    public void setStatic(ASTStatic astStatic) {
        Static = astStatic;
    }

    public ASTStatic getStatic() {
        return Static;
    }

    public void createAspect(LaraC lara) {
        this.lara = lara;
        lara.aspectIR().addAspectDef(name, this);
        if (getChildren() == null) {
            return;
        }
        /*
        	for (Node child : getChildren()) {
        	    if (child instanceof ASTInput ||
        		    child instanceof ASTOutput) {
        		((SimpleNode) child).organize(this);
        	    }
        	}
        */
    }

    public Variable getStaticVar() {
        return staticVar;
    }

    public void createVar() {
        String nameWithPrefix = lara.getPrefix() + name;
        // final ASTAllocationExpression alloc = newAllocExpr(nameWithPrefix);

        staticVar = new Variable(nameWithPrefix, Types.AspectSTATIC);
        // staticVar.setInitialize(alloc);
    }

    public String getCompleteName() {
        return lara.getPrefix() + name;
    }

    /**
     * @param referenceName
     *            the referenceName to set
     */
    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    /**
     * @return the referenceName
     */
    public String getReferenceName() {
        return referenceName;
    }

    public ASTInput getInputsNode() {
        return inputsNode;
    }

    public void setInputsNode(ASTInput inputsNode) {
        this.inputsNode = inputsNode;
    }

    public ASTOutput getOutputsNode() {
        return outputsNode;
    }

    public void setOutputsNode(ASTOutput outputsNode) {
        this.outputsNode = outputsNode;
    }
}
/*
 * JavaCC - OriginalChecksum=598b7423db12e3e7552f7955c92efea9 (do not edit this
 * line)
 */
