/* Generated By:JJTree: Do not edit this line. ASTStart.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import java.util.ArrayList;
import java.util.Iterator;

import larac.LaraC;
import larac.objects.Variable;

public class ASTStart extends SimpleNode {
    private LaraC lara;

    public ASTStart(int id) {
        super(id);
    }

    public ASTStart(LARAEcmaScript p, int id) {
        super(p, id);
    }

    @Override
    public Object organize(Object obj) {
        lara = (LaraC) obj;
        var declList = new ArrayList<SimpleNode>();
        final ArrayList<ASTAspectDef> aspectsList = new ArrayList<>();
        if (getChildren() == null) {
            return null;
        }
        for (final SimpleNode n : getSimpleNodeChildren()) {
            if (!(n instanceof ASTAspectDef)) {
                // n.declareGlobal(lara);
                declList.add(n); // delay declaring global symbols
                // ((SimpleNode) n).organize(obj);
            } else {
                aspectsList.add((ASTAspectDef) n);
            }
        }
        for (final ASTAspectDef asp : aspectsList) {
            asp.createAspect(lara);
            asp.createVar();
        }

        // declare global symbols after aspects have been created
        for (var n : declList) {
            n.declareGlobal(lara);
        }

        for (final ASTAspectDef asp : aspectsList) {
            asp.organize(obj);
            for (final SimpleNode sn : asp.getApplies()) {
                ((ASTApply) sn).organizeAfter();
            }
        }
        return null;
    }

    @Override
    public Variable lookup(String var) {
        ASTAspectDef asp = lara.aspectIR().getAspectDef(var);
        if (asp != null) {
            return asp.getStaticVar();
        }
        asp = lara.aspectIR().getImportedAspectDef(var);
        if (asp != null) {
            return asp.getStaticVar();
        }

        if (lara.aspectIR().containsGlobal(var)) {
            SimpleNode global = lara.aspectIR().getGlobal(var);
            return new Variable(var, global.getExpressionType());
        }

        final Variable variableNotFound = new Variable(var);
        variableNotFound.setNotFound(true);
        return variableNotFound;
    }

    public void getMatchingAspectDef(ArrayList<ASTAspectDef> aspects, String name) {
        final Iterator<ASTAspectDef> it = aspects.iterator();
        while (it.hasNext()) {
            final ASTAspectDef asp = it.next();
            if (!asp.getCompleteName().contains(name)) {
                it.remove();
            }
        }
    }

    @Override
    public Variable lookupNoError(String var) {
        ASTAspectDef asp = lara.aspectIR().getAspectDef(var);

        if (asp != null) {
            return asp.getStaticVar();
        }
        asp = lara.aspectIR().getImportedAspectDef(var);
        if (asp != null) {
            return asp.getStaticVar();
        }

        return new Variable(var);
    }

    @Override
    public LaraC getLara() {
        return lara;
    }

    public void setLara(LaraC lara) {
        this.lara = lara;
    }

    @Override
    public String toSource(int indentation) {
        if (getChildren() == null) {
            return "";
        }
        String source = "";

        for (final Node n : getChildren()) {
            source += ((SimpleNode) n).toSource(indentation) + "\n";
        }
        return source;
    }

}
/*
 * JavaCC - OriginalChecksum=8071fc6e50a502f262cc0461c29fb06b (do not edit this
 * line)
 */
