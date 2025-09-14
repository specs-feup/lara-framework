package org.lara.interpreter.weaver.fixtures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lara.interpreter.weaver.ast.AstMethods;
import org.lara.interpreter.weaver.ast.TreeNodeAstMethods;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.OptionArguments;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.interpreter.weaver.options.WeaverOptionBuilder;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.treenode.ATreeNode;

public class TestWeaverEngine extends WeaverEngine {

    private final List<AGear> gears = new ArrayList<>();
    private final TestJoinPoint rootJp = new TestJoinPoint("root");

    @Override
    public boolean run(DataStore dataStore) {
        // No-op for tests
        return true;
    }

    @Override
    public List<String> getActions() {
        return List.of("insert");
    }

    @Override
    public String getRoot() {
        return "root";
    }

    @Override
    public JoinPoint getRootJp() {
        return rootJp;
    }

    @Override
    public List<WeaverOption> getOptions() {
        DataKey<Boolean> OPT_VERBOSE = KeyFactory.bool("verbose").setLabel("Verbose Mode");
        DataKey<String> OPT_TARGET = KeyFactory.string("target").setLabel("Target");
        return List.of(
                WeaverOptionBuilder.build("v", "verbose", OptionArguments.NO_ARGS, "", "Verbose flag", OPT_VERBOSE),
                WeaverOptionBuilder.build("t", "target", OptionArguments.ONE_ARG, "name", "Target name", OPT_TARGET)
        );
    }

    @Override
    protected LanguageSpecification buildLangSpecs() {
        JoinPointClass base = JoinPoint.getLaraJoinPoint();
        JoinPointClass jp = new JoinPointClass("root");
        jp.setDefaultAttribute("dump");
        LanguageSpecification spec = new LanguageSpecification(jp, null);
        // Register declared join point, otherwise getJoinPoint("root") returns null
        spec.add(jp);
        spec.setRoot(jp);
        spec.setGlobal(base);
        return spec;
    }

    @Override
    public List<AGear> getGears() {
        return Collections.unmodifiableList(gears);
    }

    @Override
    public boolean implementsEvents() {
        return true;
    }

    public void addGear(AGear gear) {
        gears.add(gear);
    }

    @Override
    public AstMethods getAstMethods() {
        // Minimal TreeNodeAstMethods using a dummy ATreeNode hierarchy
        class N extends ATreeNode<N> {
            public N() { super(null); }

            @Override
            protected N copyPrivate() {
                return new N();
            }

            @Override
            public String toContentString() {
                // Minimal content representation for testing purposes
                return "";
            }
        }
        N root = new N();
        N child = new N();
        root.addChild(child);
        return new TreeNodeAstMethods<>(this, N.class,
                node -> rootJp,
                node -> "root",
                node -> node.getChildren());
    }
}
