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
 * specific language governing permissions and limitations under the License.
 */
package org.lara.interpreter.weaver.generator.commandline;

import org.apache.commons.cli.*;
import org.apache.commons.cli.Option.Builder;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;

import java.io.PrintWriter;
import java.io.Serial;
import java.io.StringWriter;

class WeaverGeneratorOptions extends Options {
    private static HelpFormatter formatter = new HelpFormatter();

    @Serial
    private static final long serialVersionUID = -7738963410049098076L;

    private enum ArgOption {
        NO_ARGS,
        ONE_ARG,
        SEVERAL_ARGS,
        OPTIONAL_ARG,
        OPTIONAL_ARGS
    }

    protected enum GeneratorOption {
        H("h", "help"),
        W("w", "weaver"),
        X("x", "XMLspec"),
        O("o", "output"),
        P("p", "package"),
        F("f", "fields"),
        E("e", "events"),
        N("n", "nodeType"),
        J("j", "json"),
        C("c", "concrete");

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
            return longOption;
        }

    }

    protected WeaverGeneratorOptions() {
        final Option help = new Option(GeneratorOption.H.option, GeneratorOption.H.longOption, false,
                "print this message");

        final Option weaver = newOption("className", GeneratorOption.W, ArgOption.ONE_ARG,
                "Name for the class (default: " + GenConstants.getDefaultWeaverName() + ")");

        final Option xmlDir = newOption("dir", GeneratorOption.X, ArgOption.ONE_ARG,
                "location of the target language specification (default: " + GenConstants.getDefaultXMLDir() + ")");

        final Option outDir = newOption("dir", GeneratorOption.O, ArgOption.ONE_ARG,
                "change output directory (default: " + GenConstants.getDefaultOutputDir() + ")");

        final Option packDir = newOption("packageName", GeneratorOption.P, ArgOption.ONE_ARG,
                "define the package for the java files");

        final Option fields = newOption(null, GeneratorOption.F, ArgOption.NO_ARGS,
                "use fields for attributes");

        final Option events = newOption(null, GeneratorOption.E, ArgOption.NO_ARGS,
                "add code that trigger events such as action execution and attributes access");

        final Option nodeType = newOption("base", GeneratorOption.N, ArgOption.OPTIONAL_ARG,
                "use generics for the Join points, extending the given class (optional)");

        final Option toJson = newOption(null, GeneratorOption.J, ArgOption.NO_ARGS,
                "Output a json file of the language specification");
        final Option concreteClasses = newOption("concreteClasses", GeneratorOption.C, ArgOption.ONE_ARG,
                "Generate the concrete classes, using a linear hierarchy");

        addOption(help);
        addOption(weaver);
        addOption(xmlDir);
        addOption(outDir);
        addOption(packDir);
        addOption(fields);
        addOption(events);
        addOption(nodeType);
        addOption(toJson);
        addOption(concreteClasses);
    }

    protected CommandLine parse(String[] args) {

        try {

            final CommandLineParser parser = new DefaultParser();

            return parser.parse(this, args);

        } catch (final ParseException e) {
            help();
            throw new RuntimeException(e);
        }

    }

    protected void help() {
        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);

        WeaverGeneratorOptions.formatter.printHelp(writer, WeaverGeneratorOptions.formatter.getWidth(),
                "java -jar WeaverGenerator.jar [options]", null, this,
                WeaverGeneratorOptions.formatter.getLeftPadding(), WeaverGeneratorOptions.formatter.getDescPadding(),
                null);
        writer.flush();
        System.out.print(buffer.toString());
    }

    /**
     * Create a new Option with all the description
     *
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
