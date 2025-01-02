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

import larai.LaraI;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringEscapeUtils;
import org.lara.interpreter.cli.CLIOption;
import org.lara.interpreter.cli.OptionsParser;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;
import pt.up.fe.specs.lara.aspectir.Base;
import pt.up.fe.specs.lara.langspec.LangSpecsXmlParser;
import pt.up.fe.specs.lara.loc.LaraLoc;
import pt.up.fe.specs.lara.loc.LaraStats;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.utilities.JarPath;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.lara.interpreter.weaver.defaultweaver.specification.DefaultWeaverResource.*;

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
        // TODO: Why validate is false?
        return LangSpecsXmlParser.parse(JOINPOINTS, ARTIFACTS, ACTIONS, false);
    }

    public static void appendComment(Base base, final StringBuilder ret) {
        String comment = base.comment;
        if (comment != null && !comment.isEmpty()) {
            String unescapeJava = StringEscapeUtils.unescapeHtml4(comment.toString());
            ret.append(unescapeJava);
        }
    }

    public static Map<String, LaraStats> getLaraLoc(WeaverEngine engine, DataStore args) {
        // Collect LARA files and folders
        List<File> laraFiles = getLaraFiles(args);
        return new LaraLoc(engine).getStats(laraFiles);
    }

    /**
     * Returns all the LARA files defined as inputs in the arguments.
     *
     * @param args
     * @return
     */
    public static List<File> getLaraFiles(DataStore args) {
        // Collect LARA files and folders
        List<File> laraPaths = new ArrayList<>();
        laraPaths.add(args.get(LaraiKeys.LARA_FILE));
        args.get(LaraiKeys.INCLUDES_FOLDER).forEach(path -> laraPaths.add(path));

        return SpecsIo.getFiles(laraPaths, true, Arrays.asList("lara"));
    }

}
