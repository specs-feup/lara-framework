package org.lara.interpreter.weaver.fixtures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lara.interpreter.weaver.ast.AstMethods;
import org.lara.interpreter.weaver.ast.TreeNodeAstMethods;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.OptionArguments;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.interpreter.weaver.options.WeaverOptionBuilder;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.treenode.ATreeNode;

public class TestWeaverEngine extends WeaverEngine {

    private final List<AGear> gears = new ArrayList<>();

    // Minimal Tree node model for AST bridge
    private static class N extends ATreeNode<N> {
        public N() {
            super(null);
        }

        @Override
        protected N copyPrivate() {
            return new N();
        }

        @Override
        public String toContentString() {
            return "";
        }
    }

    private final N astRoot;
    private final N astChild;
    private final TestJoinPoint rootJp;

    public TestWeaverEngine() {
        // Build a tiny AST
        this.astRoot = new N();
        this.astChild = new N();
        this.astRoot.addChild(astChild);

        // Root JP is backed by the AST root node
        this.rootJp = new TestJoinPoint(this, "root", this.astRoot);
    }

    @Override
    public boolean run(DataStore dataStore) {
        // No-op for tests
        return true;
    }

    @Override
    public String getRoot() {
        return "root";
    }

    @Override
    public TestJoinPoint getRootJp() {
        return rootJp;
    }

    @Override
    public List<WeaverOption> getOptions() {
        DataKey<Boolean> OPT_VERBOSE = KeyFactory.bool("verbose").setLabel("Verbose Mode");
        DataKey<String> OPT_TARGET = KeyFactory.string("target").setLabel("Target");
        return List.of(
                WeaverOptionBuilder.build("v", "verbose", OptionArguments.NO_ARGS, "", "Verbose flag", OPT_VERBOSE),
                WeaverOptionBuilder.build("t", "target", OptionArguments.ONE_ARG, "name", "Target name", OPT_TARGET));
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
        // Minimal TreeNodeAstMethods using the persistent AST hierarchy
        return new TreeNodeAstMethods<>(this, N.class,
                node -> rootJp,
                node -> "root",
                node -> node == astRoot ? List.of(astChild) : node.getChildren());
    }
}
