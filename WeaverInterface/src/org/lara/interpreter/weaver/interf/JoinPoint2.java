package org.lara.interpreter.weaver.interf;

import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.abstracts.joinpoints.ALaraJoinPoint;

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
public abstract class JoinPoint2<Self extends JoinPoint2<Self, Jp>, Jp extends JoinPoint2<?, Jp>>
        extends ALaraJoinPoint<Self, Jp> {

    private final WeaverEngine weaver;

    protected JoinPoint2(WeaverEngine weaver) {
        this.weaver = weaver;
    }

    /**
     * Multi-argument instanceOf.
     */
    public boolean instanceOfImpl(String[] types) {
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
    public abstract boolean same(JoinPoint2<?, ?> other);

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

    public static boolean isJoinPoint(Object value) {
        return value instanceof JoinPoint2;
    }

    /**
     * Returns the join point type.
     */
    public String getJoinPointTypeImpl() {
        return get_class();
    }

    /**
     * Self reference.
     */
    @SuppressWarnings("unchecked")
    public Self getSelfImpl() {
        return (Self) this;
    }

    // ----- Tree navigation defaults -----

    public Stream<Jp> getJpChildrenStream() {
        throw new UnsupportedOperationException(get_class() + ": getJpChildrenStream not implemented");
    }

    public Jp getJpParent() {
        throw new UnsupportedOperationException(get_class() + ": getJpParent not implemented");
    }

    public Stream<Jp> getJpDescendantsStream() {
        return getJpChildrenStream().flatMap(JoinPoint2::getJpDescendantsAndSelfStream);
    }

    public Stream<Jp> getJpDescendantsAndSelfStream() {
        return Stream.concat(Stream.of(asJp()), getJpDescendantsStream());
    }

    @SuppressWarnings("unchecked")
    private Jp asJp() {
        return (Jp) this;
    }

    // ----- Dump -----

    public String getDumpImpl() {
        return dumpPrivate(this, "");
    }

    private static String dumpPrivate(JoinPoint2<?, ?> jp, String prefix) {
        var dump = new StringBuilder();
        dump.append(prefix).append(jp.toString()).append("\n");
        jp.getJpChildrenStream().forEach(child -> dump.append(dumpPrivate(child, prefix + "   ")));
        return dump.toString();
    }

    public String toStringImpl() {
        return "Joinpoint '" + joinPointType() + "'";
    }
}
