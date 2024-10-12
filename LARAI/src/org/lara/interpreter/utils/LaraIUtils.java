/*
 * Copyright 2013 SPeCS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */
package org.lara.interpreter.utils;

import static org.lara.interpreter.weaver.defaultweaver.specification.DefaultWeaverResource.ACTIONS;
import static org.lara.interpreter.weaver.defaultweaver.specification.DefaultWeaverResource.ARTIFACTS;
import static org.lara.interpreter.weaver.defaultweaver.specification.DefaultWeaverResource.JOINPOINTS;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.lara.interpreter.cli.CLIOption;
import org.lara.interpreter.cli.OptionsParser;
import org.lara.language.specification.LanguageSpecification;
import larai.LaraI;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.utilities.JarPath;

public class LaraIUtils {

    public final static String SPACE = "\t";

    public static enum Statements {
        VARDECL,
        FNDECL,
        GFNDECL,
        EXPR,
        BLOCK,
        IF,
        LOOP,
        CONTINUE,
        BREAK,
        RETURN,
        YIELD,
        YIELD_STAR,
        WITH,
        SWITCH,
        THROW,
        TRY,
        EXIT,
        SELECT,
        APPLY,
        SCRIPTIMPORT,
        JSIMPORT,
        LARAIMPORT;
    }

    public static String getSpace(int depth) {
        if (depth <= 0) {
            return "";
        }
        return String.format(String.format("%%0%dd", depth), 0).replace("0", LaraIUtils.SPACE);
    }

    public static enum Operators {
        UNION_JOIN("+"),
        NATURAL_JOIN("::"),
        BITAND_JOIN("&"),
        ADD("+"),
        SUB("-"),
        MULT("*"),
        DIV("/"),
        MOD("%"),
        SHL(
                "<<"),
        SHR(">>"),
        USHR(">>>"),
        OR("||"),
        AND("&&"),
        LT("<"),
        LTE("<="),
        GT(">"),
        GTE(">="),
        INSTANCEOF(
                "instanceof"),
        IN("in"),
        EQ("=="),
        NEQ("!="),
        SEQ("==="),
        NSEQ("!=="),
        MATCH("~="),
        BITAND("&"),
        BITXOR("^"),
        BITOR("|"),
        COMMA(","),
        POS("+"),
        NEG("-"),
        NOT("!"),
        INV("~"),
        INCP(
                "++"),
        DECP("--"),
        ASSIGN("="),
        ASSIGN_ADD("+="),
        ASSIGN_SUB("-="),
        ASSIGN_MULT(
                "*="),
        ASSIGN_DIV("/="),
        ASSIGN_MOD("%="),
        ASSIGN_SHL(
                "<<="),
        ASSIGN_SHR(">>="),
        ASSIGN_USHR(">>>="),
        ASSIGN_BITAND(
                "&="),
        ASSIGN_BITXOR("^="),
        ASSIGN_BITOR("|="),
        TYPEOF(
                "typeof "),
        DELETE("delete "),
        VOID("void ");

        private String op;

        Operators(String op) {
            setOp(op);
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getOp() {
            return op;
        }

        public static String getOpString(String operEnum) {
            return valueOf(operEnum.toUpperCase()).op;
        }

        public static Operators getOpTag(String text) {
            if (text != null) {
                for (final Operators b : Operators.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public static boolean contains(String text) {
            if (text != null) {
                for (final Operators b : Operators.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static boolean printHelp(CommandLine cmd, Options options) {
        if (cmd.hasOption(CLIOption.help.shortOption())) {
            System.out.println(OptionsParser.getHelp(options));
            return true;
        }
        if (cmd.hasOption(CLIOption.version.shortOption())) {
            System.out.println(LaraI.LARAI_VERSION_TEXT);

            var implVersion = SpecsSystem.getBuildNumber();
            if (implVersion == null) {
                implVersion = "<build number not found>";
            }

            System.out.println("Build: " + implVersion);

            return true;
        }
        return false;
    }

    /**
     * Enables lazy initialization of jarParth
     *
     * @author Joao Bispo
     *
     */
    private static class JarPathHolder {
        public static final String instance = new JarPath(LaraI.class, LaraI.PROPERTY_JAR_PATH).buildJarPath();

    }

    public static String getJarFoldername() {
        return JarPathHolder.instance;
    }

    /**
     * Creates the default language specification
     *
     * @return
     */
    public static LanguageSpecification createDefaultLanguageSpecification() {
        return LanguageSpecification.newInstance(JOINPOINTS, ARTIFACTS, ACTIONS, false);
    }

}
