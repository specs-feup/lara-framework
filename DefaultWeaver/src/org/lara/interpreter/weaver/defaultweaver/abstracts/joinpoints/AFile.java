package org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints;

import org.lara.interpreter.exception.AttributeException;
import org.lara.interpreter.weaver.defaultweaver.abstracts.ADefaultWeaverJoinPoint;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;

/**
 * Auto-Generated class for join point AFile
 * This class is overwritten by the Weaver Generator.
 * 
 * 
 * @author Lara Weaver Generator
 */
public abstract class AFile extends ADefaultWeaverJoinPoint {

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
        	return this.getAbsolutePathImpl();
        } catch(Exception e) {
        	throw new AttributeException(get_class(), "absolutePath", e);
        }
    }

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
        	return this.getNameImpl();
        } catch(Exception e) {
        	throw new AttributeException(get_class(), "name", e);
        }
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
        ABSOLUTEPATH("absolutePath"),
        NAME("name");
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
