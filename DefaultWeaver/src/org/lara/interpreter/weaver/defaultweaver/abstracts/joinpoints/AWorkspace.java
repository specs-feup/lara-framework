package org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints;

import org.lara.interpreter.exception.ActionException;
import org.lara.interpreter.weaver.defaultweaver.abstracts.ADefaultWeaverJoinPoint;

/**
 * Auto-Generated class for join point AWorkspace
 * This class is overwritten by the Weaver Generator.
 * 
 * 
 * @author Lara Weaver Generator
 */
public abstract class AWorkspace extends ADefaultWeaverJoinPoint {

    /**
     * 
     */
    public void reportImpl() {
        throw new UnsupportedOperationException(get_class()+": Action report not implemented ");
    }

    /**
     * 
     */
    public final void report() {
        try {
        	this.reportImpl();
        } catch(Exception e) {
        	throw new ActionException(get_class(), "report", e);
        }
    }

    /**
     * Returns the join point type of this class
     * @return The join point type
     */
    @Override
    public final String get_class() {
        return "workspace";
    }
}
