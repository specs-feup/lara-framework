package org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints;

import java.util.List;

import org.lara.interpreter.weaver.defaultweaver.abstracts.ADefaultWeaverJoinPoint;
import org.lara.interpreter.weaver.interf.JoinPoint;

/**
 * Auto-Generated class for join point AWorkspace
 * This class is overwritten by the Weaver Generator.
 * 
 * 
 * @author Lara Weaver Generator
 */
public abstract class AWorkspace extends ADefaultWeaverJoinPoint {

    /**
     * Method used by the lara interpreter to select folders
     * @return 
     */
    public abstract List<? extends AFolder> selectFolder();

    /**
     * 
     */
    @Override
    public final List<? extends JoinPoint> select(String selectName) {
        List<? extends JoinPoint> joinPointList;
        switch(selectName) {
        	case "folder": 
        		joinPointList = selectFolder();
        		break;
        	default:
        		joinPointList = super.select(selectName);
        		break;
        }
        return joinPointList;
    }

    /**
     * 
     */
    @Override
    protected final void fillWithAttributes(List<String> attributes) {
        super.fillWithAttributes(attributes);
    }

    /**
     * 
     */
    @Override
    protected final void fillWithSelects(List<String> selects) {
        super.fillWithSelects(selects);
        selects.add("folder");
    }

    /**
     * 
     */
    @Override
    protected final void fillWithActions(List<String> actions) {
        super.fillWithActions(actions);
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
