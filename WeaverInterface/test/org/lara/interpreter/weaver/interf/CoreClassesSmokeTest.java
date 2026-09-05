package org.lara.interpreter.weaver.interf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.enums.InsertPosition;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Interfaces.DataStore;


class CoreClassesSmokeTest {

    private static class DummyWeaver extends WeaverEngine {
        @Override
        public boolean run(DataStore dataStore) {
            return true;
        }

        @Override
        public String getRoot() {
            return "root";
        }

        @Override
        public JoinPoint2<?, ?> getRootJp() {
            return new DummyJp(this);
        }

        @Override
        public List<WeaverOption> getOptions() {
            return List.of();
        }

        @Override
        public List<AGear> getGears() {
            return List.of();
        }

        @Override
        public boolean implementsEvents() {
            return true;
        }
    }

    private static class DummyJp extends JoinPoint2<DummyJp, DummyJp> {
        public DummyJp(DummyWeaver weaver) {
            super(weaver);
        }

        @Override
        public DummyWeaver getWeaverEngine() {
            return (DummyWeaver) super.getWeaverEngine();
        }

        @Override
        public boolean getSameImpl(DummyJp iJoinPoint) {
            return this == iJoinPoint;
        }

        @Override
        public Object getNodeImpl() {
            return this;
        }

        @Override
        public Stream<DummyJp> getJpChildrenStream() {
            return Stream.empty();
        }

        @Override
        public DummyJp getJpParent() {
            return null;
        }

        @Override
        public DummyJp[] getChildrenImpl() {
            return new DummyJp[0];
        }

        @Override
        public DummyJp[] getDescendantsImpl() {
            return new DummyJp[0];
        }

        @Override
        public DummyJp[] getScopeNodesImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getScopeNodesImpl'");
        }

        @Override
        public DummyJp getParentImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getParentImpl'");
        }

        @Override
        public DummyJp getRootImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getRootImpl'");
        }

        @Override
        public String getCodeImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getCodeImpl'");
        }

        @Override
        public Integer getLineImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getLineImpl'");
        }

        @Override
        public Integer getColumnImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getColumnImpl'");
        }

        @Override
        public boolean getCompareNodesImpl(DummyJp aJoinPoint) {
            throw new UnsupportedOperationException("Unimplemented method 'getCompareNodesImpl'");
        }

        @Override
        public boolean getEqualsImpl(DummyJp jp) {
            throw new UnsupportedOperationException("Unimplemented method 'getEqualsImpl'");
        }

        @Override
        public boolean getInstanceOfImpl(String joinpointClassname) {
            return "joinpoint".equals(joinpointClassname) || get_class().equals(joinpointClassname);
        }

        @Override
        public DummyJp[] insertImpl(InsertPosition position, String code) {
            throw new UnsupportedOperationException("Unimplemented method 'insertImpl'");
        }

        @Override
        public DummyJp[] insertImpl(InsertPosition position, DummyJp joinpoint) {
            throw new UnsupportedOperationException("Unimplemented method 'insertImpl'");
        }

        @Override
        protected IntFunction<DummyJp[]> selfTypeArrayFactory() {
            throw new UnsupportedOperationException("Unimplemented method 'selfTypeArrayFactory'");
        }

        @Override
        protected IntFunction<DummyJp[]> jpTypeArrayFactory() {
            throw new UnsupportedOperationException("Unimplemented method 'jpTypeArrayFactory'");
        }
    }

    @Test
    @DisplayName("WeaverEngine utilities and event trigger interactions")
    void weaverEngineAndEvents() {
        var weaver = new DummyWeaver();
        // Temporary folder lazily created
        assertThat(weaver.hasTemporaryWeaverFolder()).isFalse();
        assertThat(weaver.getTemporaryWeaverFolder()).exists();
        assertThat(weaver.hasTemporaryWeaverFolder()).isTrue();

        // Name and build string is not empty
        assertThat(weaver.getName()).isEqualTo("DummyWeaver");

        // Event trigger usage
        var trigger = new EventTrigger();
        weaver.setEventTrigger(trigger);
        assertThat(weaver.hasListeners()).isFalse();
    }

    @Test
    @DisplayName("JoinPoint utility methods produce outputs and toString contains type")
    void joinPointUtilities() {
        var weaver = new DummyWeaver();
        var jp = new DummyJp(weaver);
        assertThat(jp.getJoinPointTypeImpl()).isEqualTo(jp.get_class());
        assertThat(JoinPoint2.isJoinPoint(jp)).isTrue();
        assertThat(jp.getInstanceOfImpl("joinpoint")).isTrue();
        assertThat(jp.getChildrenImpl()).isEmpty();
        assertThat(jp.getDescendantsImpl()).isEmpty();
        assertThat(jp.getToStringImpl()).contains("Joinpoint");
        assertThat(jp.getDumpImpl()).contains("Joinpoint");
        assertThat(jp.getSelfImpl()).isSameAs(jp);
    }
}
