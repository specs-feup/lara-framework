package org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.lara.interpreter.exception.AttributeException;
import org.lara.interpreter.weaver.defaultweaver.abstracts.ADefaultWeaverJoinPoint;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;

/**
 * Auto-Generated class for join point AFile
 * This class is overwritten by the Weaver Generator.
 * 
 * 
 * @author Lara Weaver Generator
 */
public abstract class AFile extends ADefaultWeaverJoinPoint {

    /**
     * Get value on attribute name
     * @return the attribute's value
     */
    public abstract String getNameImpl();

    /**
     * Get value on attribute name
     * @return the attribute's value
     */
    public final Object getName() {
        try {
        	if(hasListeners()) {
        		eventTrigger().triggerAttribute(Stage.BEGIN, this, "name", Optional.empty());
        	}
        	String result = this.getNameImpl();
        	if(hasListeners()) {
        		eventTrigger().triggerAttribute(Stage.END, this, "name", Optional.ofNullable(result));
        	}
        	return result!=null?result:getUndefinedValue();
        } catch(Exception e) {
        	throw new AttributeException(get_class(), "name", e);
        }
    }

    /**
     * Get value on attribute absolutePath
     * @return the attribute's value
     */
    public abstract String getAbsolutePathImpl();

    /**
     * Get value on attribute absolutePath
     * @return the attribute's value
     */
    public final Object getAbsolutePath() {
        try {
        	if(hasListeners()) {
        		eventTrigger().triggerAttribute(Stage.BEGIN, this, "absolutePath", Optional.empty());
        	}
        	String result = this.getAbsolutePathImpl();
        	if(hasListeners()) {
        		eventTrigger().triggerAttribute(Stage.END, this, "absolutePath", Optional.ofNullable(result));
        	}
        	return result!=null?result:getUndefinedValue();
        } catch(Exception e) {
        	throw new AttributeException(get_class(), "absolutePath", e);
        }
    }

    /**
     * Method used by the lara interpreter to select functions
     * @return 
     */
    public abstract List<? extends AFunction> selectFunction();

    /**
     * 
     */
    @Override
    public final List<? extends JoinPoint> select(String selectName) {
        List<? extends JoinPoint> joinPointList;
        switch(selectName) {
        	case "function": 
        		joinPointList = selectFunction();
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
        attributes.add("name");
        attributes.add("absolutePath");
    }

    /**
     * 
     */
    @Override
    protected final void fillWithSelects(List<String> selects) {
        super.fillWithSelects(selects);
        selects.add("function");
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
        return "file";
    }
    /**
     * 
     */
    protected enum FileAttributes {
        NAME("name"),
        ABSOLUTEPATH("absolutePath");
        private String name;

        /**
         * 
         */
        private FileAttributes(String name){
            this.name = name;
        }
        /**
         * Return an attribute enumeration item from a given attribute name
         */
        public static Optional<FileAttributes> fromString(String name) {
            return Arrays.asList(values()).stream().filter(attr -> attr.name.equals(name)).findAny();
        }

        /**
         * Return a list of attributes in String format
         */
        public static List<String> getNames() {
            return Arrays.asList(values()).stream().map(FileAttributes::name).collect(Collectors.toList());
        }

        /**
         * True if the enum contains the given attribute name, false otherwise.
         */
        public static boolean contains(String name) {
            return fromString(name).isPresent();
        }
    }
}
