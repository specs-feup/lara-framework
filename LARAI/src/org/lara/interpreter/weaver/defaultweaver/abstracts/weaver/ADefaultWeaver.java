package org.lara.interpreter.weaver.defaultweaver.abstracts.weaver;

import org.lara.interpreter.weaver.LaraWeaverEngine;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract Weaver Implementation for DefaultWeaver<br>
 * Since the generated abstract classes are always overwritten, their implementation should be done by extending those abstract classes with user-defined classes.<br>
 * The abstract class {@link org.lara.interpreter.weaver.defaultweaver.abstracts.ADefaultWeaverJoinPoint} can be used to add user-defined methods and fields which the user intends to add for all join points and are not intended to be used in LARA aspects.
 * The implementation of the abstract methods is mandatory!
 * @author Lara C.
 */
public abstract class ADefaultWeaver extends LaraWeaverEngine {

    /**
     * Get the list of available actions in the weaver
     * 
     * @return list with all actions
     */
    @Override
    public final List<String> getActions() {
        String[] weaverActions= {"report"};
        return Arrays.asList(weaverActions);
    }

    /**
     * Returns the name of the root
     * 
     * @return the root name
     */
    @Override
    public final String getRoot() {
        return "workspace";
    }

    /**
     * Does the generated code implements events?
     * 
     * @return true if implements events, false otherwise
     */
    @Override
    public final boolean implementsEvents() {
        return true;
    }
}
