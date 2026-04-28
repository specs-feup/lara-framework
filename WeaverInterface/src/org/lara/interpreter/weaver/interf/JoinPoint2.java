package org.lara.interpreter.weaver.interf;

import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.abstracts.joinpoints.AJoinpoint;
import pt.up.fe.specs.jsengine.node.UndefinedValue;
import pt.up.fe.specs.util.treenode.ATreeNode;

import java.util.stream.Stream;

/**
 * New base class for join points.
 * <p>
 * This replaces the old {@link JoinPoint} class. Generated abstract classes extend this
 * (via an intermediate generated {@code ABaseJoinPoint} or {@code AJoinPoint}).
 * <p>
 * Key differences from the old {@link JoinPoint}:
 * <ul>
 *   <li>No delegation chain -- behavior is defined directly in the hierarchy</li>
 *   <li>Methods driven by {@link BaseJoinPointSpec} -- if the spec adds a new attribute,
 *       generated code will require this class to implement it</li>
 * </ul>
 */
public abstract class JoinPoint2<Self extends JoinPoint2<Self>> extends AJoinpoint<Self> {

    private final WeaverEngine weaver;

    protected JoinPoint2(WeaverEngine weaver) {
        this.weaver = weaver;
    }

    // ----- Core identity (built-in, not from spec) -----

    /**
     * Returns the underlying AST node.
     */
    public abstract ATreeNode getNode();


    /**
     * Multi-argument instanceOf.
     */
    public boolean instanceOf(String[] types) {
        for (var type : types) {
            if (instanceOf(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compares two join points for identity.
     */
    public abstract boolean same(JoinPoint2<?> other);

    // ----- Weaver access -----

    public WeaverEngine getWeaverEngine() {
        return weaver;
    }

    protected EventTrigger eventTrigger() {
        return weaver.getEventTrigger();
    }

    protected boolean hasListeners() {
        return weaver.hasListeners();
    }

    // ----- Common utilities -----

    public static Object getUndefinedValue() {
        return UndefinedValue.getUndefined();
    }

    public static boolean isJoinPoint(Object value) {
        return value instanceof JoinPoint2;
    }

    /**
     * Returns the join point type.
     */
    @Override
    public String getJoinPointType() {
        return get_class();
    }

    /**
     * Self reference.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Self getSelf() {
        return (Self) this;
    }

    // ----- Tree navigation defaults -----

    public Stream<JoinPoint2> getJpChildrenStream() {
        throw new UnsupportedOperationException(get_class() + ": getJpChildrenStream not implemented");
    }

    public JoinPoint2 getJpParent() {
        throw new UnsupportedOperationException(get_class() + ": getJpParent not implemented");
    }

    public Stream<JoinPoint2> getJpDescendantsStream() {
        return getJpChildrenStream().flatMap(JoinPoint2::getJpDescendantsAndSelfStream);
    }

    public Stream<JoinPoint2> getJpDescendantsAndSelfStream() {
        return Stream.concat(Stream.of(this), getJpDescendantsStream());
    }

    // ----- Dump -----

    public String getDump() {
        return dump(this, "");
    }

    public static String dump(JoinPoint2<?> jp, String prefix) {
        var dump = new StringBuilder();
        dump.append(prefix).append(jp.toString()).append("\n");
        jp.getJpChildrenStream().forEach(child -> dump.append(dump(child, prefix + "   ")));
        return dump.toString();
    }

    @Override
    public String toString() {
        return "Joinpoint '" + getJoinPointType() + "'";
    }
}
