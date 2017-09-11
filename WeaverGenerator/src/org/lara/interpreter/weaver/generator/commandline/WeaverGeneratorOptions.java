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
package org.lara.interpreter.weaver.generator.commandline;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;

class WeaverGeneratorOptions extends Options {
    private static HelpFormatter formatter = new HelpFormatter();

    /**
     * 
     */
    private static final long serialVersionUID = -7738963410049098076L;

    /**
     * Arguments for options
     * 
     * @author Tiago Carvalho
     * 
     */
    private enum ArgOption {
        NO_ARGS,
        ONE_ARG,
        SEVERAL_ARGS,
        OPTIONAL_ARG,
        OPTIONAL_ARGS;
    }

    protected enum GeneratorOption {
        H("h", "help"),
        W("w", "weaver"),

        X("x", "XMLspec"),
        L("l", "language"),
        O("o", "output"),
        P("p", "package"),
        // A("a", "abstractGetters"), //replaced with -f
        F("f", "fields"),
        E("e", "events"),
        // I("i", "implMode"),
        // G("g", "graph"),
        N("n", "nodeType"),
        J("j", "json"),;
        private String option;
        private String longOption;

        private GeneratorOption(String option, String longOption) {
            this.option = option;
            this.longOption = longOption;
        }

        public String getOption() {
            return option;
        }

        public String getLongOption() {
            return option;
        }

    }

    protected WeaverGeneratorOptions() {
        final Option help = new Option(GeneratorOption.H.option, GeneratorOption.H.longOption, false,
                "print this message");

        final Option weaver = newOption("className", GeneratorOption.W, ArgOption.ONE_ARG,
                "Name for the class (default: " + GenConstants.getDefaultWeaverName() + ")");

        final Option xmlDir = newOption("dir", GeneratorOption.X, ArgOption.ONE_ARG,
                "location of the target language specification (default: " + GenConstants.getDefaultXMLDir() + ")");

        final Option language = newOption("language", GeneratorOption.L, ArgOption.ONE_ARG,
                "define in which language the code will be generated (default: Java)");

        final Option outDir = newOption("dir", GeneratorOption.O, ArgOption.ONE_ARG,
                "change output directory (default: " + GenConstants.getDefaultOutputDir() + ")");

        final Option packDir = newOption("packageName", GeneratorOption.P, ArgOption.ONE_ARG,
                "define the package for the java files");

        // final Option abstractGetters = newOption(null, GeneratorOption.A, ArgOption.NO_ARGS,
        // "don't use fields for attributes and and define getter methods as abstract"); //Replaced with
        // GeneratorOption.F
        final Option fields = newOption(null, GeneratorOption.F, ArgOption.NO_ARGS,
                "use fields for attributes");

        final Option events = newOption(null, GeneratorOption.E, ArgOption.NO_ARGS,
                "add code that trigger events such as action execution and attributes access");
        // final Option implMode = newOption(null, GeneratorOption.I, ArgOption.NO_ARGS,
        // "Wrap use of attributes with \"Impl\" methods");

        final Option nodeType = newOption("base", GeneratorOption.N, ArgOption.OPTIONAL_ARG,
                "use generics for the Join points, extending the given class (optional)");

        final Option toJson = newOption(null, GeneratorOption.J, ArgOption.NO_ARGS,
                "Output a json file of the language specification");
        // final Option showGraph = newOption(null, GeneratorOption.G, ArgOption.NO_ARGS,
        // "Show a graph of the join point hierarchy (default: " + GenConstants.getDefaultShowGraph() + ")");

        addOption(help);
        addOption(weaver);
        addOption(xmlDir);
        addOption(language);
        addOption(outDir);
        addOption(packDir);
        // addOption(abstractGetters);
        addOption(fields);
        addOption(events);
        // addOption(implMode);
        addOption(nodeType);
        addOption(toJson);
        // addOption(showGraph);
    }

    protected CommandLine parse(String[] args) {

        try {

            final CommandLineParser parser = new DefaultParser();

            return parser.parse(this, args);

        } catch (final ParseException e) {
            // System.out.println(e.getMessage());
            help();
            throw new RuntimeException(e);
        }

    }

    protected void help() {
        WeaverGeneratorOptions.formatter.printHelp("java -jar WeaverGenerator.jar [options]", this);
    }

    /**
     * Create a new Option with all the description
     * 
     * @param argName
     * @param shortOpt
     * @param longOpt
     * @param argOption
     * @param description
     * @return
     */
    protected static Option newOption(String argName, GeneratorOption shortOpt, ArgOption argOption,
            String description) {

        Builder builder = Option.builder(shortOpt.getOption());

        builder.argName(argName);
        builder.longOpt(shortOpt.longOption);
        builder.desc(description);

        switch (argOption) {
        case ONE_ARG:
            builder.hasArg();
            break;
        case SEVERAL_ARGS:
            builder.hasArgs();
            break;
        case OPTIONAL_ARG:
            builder.hasArg();
            builder.optionalArg(true);
            break;
        case OPTIONAL_ARGS:
            builder.hasArgs();
            builder.optionalArg(true);
            break;
        default:
            break;
        }
        return builder.build();
    }
}
