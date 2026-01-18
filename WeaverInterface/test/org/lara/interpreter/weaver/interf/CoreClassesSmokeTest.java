package org.lara.interpreter.weaver.interf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;


class CoreClassesSmokeTest {

    private static class DummyWeaver extends WeaverEngine {
        @Override
        public boolean run(DataStore dataStore) {
            return true;
        }

        @Override
        public List<String> getActions() {
            return List.of("a");
        }

        @Override
        public String getRoot() {
            return "root";
        }

        @Override
        public JoinPoint getRootJp() {
            return new DummyJp();
        }

        @Override
        public List<WeaverOption> getOptions() {
            return List.of();
        }

        @Override
        protected LanguageSpecification buildLangSpecs() {
            return new LanguageSpecification(JoinPoint.getLaraJoinPoint(), null);
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

    private static class DummyJp extends JoinPoint {
        @Override
        public boolean same(JoinPoint iJoinPoint) {
            return this == iJoinPoint;
        }

        @Override
        public Object getNode() {
            return this;
        }

        @Override
        public Stream<JoinPoint> getJpChildrenStream() {
            return Stream.empty();
        }

        @Override
        public JoinPoint getJpParent() {
            return null;
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
        assertThat(weaver.getNameAndBuild()).contains("DummyWeaver");

        // Event trigger usage
        var trigger = new EventTrigger();
        weaver.setEventTrigger(trigger);
        assertThat(weaver.hasListeners()).isFalse();
    }

    @Test
    @DisplayName("JoinPoint utility methods produce outputs and toString contains type")
    void joinPointUtilities() {
        var jp = new DummyJp();
        assertThat(jp.getJoinPointType()).isEqualTo(jp.get_class());
        assertThat(JoinPoint.isJoinPoint(jp)).isTrue();
        assertThat(jp.instanceOf("joinpoint")).isTrue();
        assertThat(jp.getJpChildren()).isEmpty();
        assertThat(jp.getJpDescendants()).isEmpty();
        assertThat(jp.toString()).contains("Joinpoint");
        assertThat(jp.getDump()).contains("Joinpoint");
        assertThat(jp.getSelf()).isSameAs(jp);
    }
}
