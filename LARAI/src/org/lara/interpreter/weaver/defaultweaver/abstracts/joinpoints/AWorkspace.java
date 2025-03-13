package org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints;

import java.util.List;
import org.lara.interpreter.weaver.interf.SelectOp;
import org.lara.interpreter.weaver.interf.events.Stage;
import java.util.Optional;
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
        	if(hasListeners()) {
        		eventTrigger().triggerAction(Stage.BEGIN, "report", this, Optional.empty());
        	}
        	this.reportImpl();
        	if(hasListeners()) {
        		eventTrigger().triggerAction(Stage.END, "report", this, Optional.empty());
        	}
        } catch(Exception e) {
        	throw new ActionException(get_class(), "report", e);
        }
    }

    /**
     * 
     */
    @Override
    protected final void fillWithActions(List<String> actions) {
        super.fillWithActions(actions);
        actions.add("void report()");
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
