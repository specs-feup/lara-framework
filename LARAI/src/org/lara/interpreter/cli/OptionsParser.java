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
/**
 *
 */
package org.lara.interpreter.cli;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.joptions.config.engine.WeaverEngineStoreDefinition;
import org.lara.interpreter.joptions.config.interpreter.LaraiStoreDefinition;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.persistence.XmlPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

import pt.up.fe.specs.util.SpecsIo;

/**
 * @author Tiago
 *
 */
public class OptionsParser {
    private static final int HELP_MAX_WIDTH = 120;

    /**
     * Build LARA options
     */
    public static Collection<Option> buildLaraIOptionGroup() {
        final Option help = OptionsBuilderUtils.newOption(CLIOption.help);
        final Option version = OptionsBuilderUtils.newOption(CLIOption.version);
        final Option javascript = OptionsBuilderUtils.newOption(CLIOption.javascript);
        final Option debug = OptionsBuilderUtils.newOption(CLIOption.debug);
        final Option stack = OptionsBuilderUtils.newOption(CLIOption.stack);

        final Option outDir = OptionsBuilderUtils.newOption(CLIOption.output);
        final Option workDirExtra = OptionsBuilderUtils.newOption(CLIOption.workspace_extra);
        final Option verbose = OptionsBuilderUtils.newOption(CLIOption.verbose);
        final Option argv = OptionsBuilderUtils.newOption(CLIOption.argv);
        final Option main = OptionsBuilderUtils.newOption(CLIOption.main);

        final Option dependencies = OptionsBuilderUtils.newOption(CLIOption.dependencies);

        final Option bundleTags = OptionsBuilderUtils.newOption(CLIOption.bundle_tags);
        final Option restrict = OptionsBuilderUtils.newOption(CLIOption.restrict);
        final Option call = OptionsBuilderUtils.newOption(CLIOption.call);
        final Option jsengine = OptionsBuilderUtils.newOption(CLIOption.jsengine);

        Options options = new Options()
                .addOption(help)
                .addOption(version)
                .addOption(argv)
                .addOption(main)
                .addOption(debug)
                .addOption(stack)
                .addOption(outDir)
                .addOption(workDirExtra)
                .addOption(verbose)
                .addOption(dependencies)
                .addOption(javascript)
                .addOption(bundleTags)
                .addOption(restrict)
                .addOption(call)
                .addOption(jsengine);


        ArrayList<Option> arrayList = new ArrayList<>();
        arrayList.addAll(options.getOptions());
        return arrayList;
    }

    public static CommandLine parse(String[] args, Options options) {

        if (args.length < 1) {
            throw new IllegalArgumentException("LARA aspect file is required.\n" + OptionsParser.getHelp(options));
        }

        try {

            final CommandLineParser parser = new DefaultParser();

            return parser.parse(options, args);

        } catch (final ParseException e) {
            // System.out.println(e.getMessage());
            String help = getHelp(options);
            throw new LaraIException(e.getMessage() + "\n" + help);
        }
    }

    /**
     * Verifies if the given extra options does not exist in the current larai options {@link CLIOption} and adds them
     * to the options collection
     *
     * @param mainOptions
     * @param extraOptions
     */
    public static void addExtraOptions(Collection<Option> mainOptions, List<WeaverOption> extraOptions) {
        for (WeaverOption weaverOption : extraOptions) {

            verifyConflictingOptions(weaverOption);

            Option opt = OptionsBuilderUtils.newOption(weaverOption);
            mainOptions.add(opt);
        }

    }

    private static void verifyConflictingOptions(WeaverOption weaverOption) {
        CLIOption conflictingOption = null;
        if (CLIOption.contains(weaverOption.longOption())) {

            conflictingOption = CLIOption.valueOf(weaverOption.longOption());
        } else if (CLIOption.containsShort(weaverOption.shortOption())) {

            conflictingOption = CLIOption.getArgumentByShortName(weaverOption.shortOption());

        }

        if (conflictingOption != null) {

            String conflict = conflictingOption.name() + "(" + conflictingOption.shortOption() + ") vs";
            conflict += weaverOption.longOption() + "(" + weaverOption.shortOption() + ")";

            throw new LaraIException(
                    "Weaver engine cannot use an option with the same name/letter than existing larai options. Conflicting types: "
                            + conflict);
        }
    }


    public static String getHelp(Options options) {
        return getHelp(options, HelpFormatter.DEFAULT_LEFT_PAD);
    }

    public static String getHelp(Options options, int leftPadding) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null); // So it stays the same order the options are added
        StringWriter sw = new StringWriter();
        String usageSyntax = "<aspect.lara> [options] [-c <file>]  | -c <file>";
        PrintWriter pw = new PrintWriter(sw);
        formatter.printHelp(pw, HELP_MAX_WIDTH,
                usageSyntax,
                "", options, leftPadding, formatter.getDescPadding(), "", false);
        pw.flush();

        var output = sw.toString();

        if (WeaverEngine.isWeaverSet()) {
            var weaver = WeaverEngine.getThreadLocalWeaver();
            output = weaver.getNameAndBuild() + "\n" + output;
        }

        return output;
    }

    public static Collection<Option> buildConfigOptions() {
        List<Option> options = new ArrayList<>();
        options.add(CLIConfigOption.config.buildOption());
        return options;
    }

    public static ExecutionMode getExecMode(String firstArg, CommandLine cmd, Options finalOptions) {
        boolean configPresent = cmd.hasOption(CLIConfigOption.config.getShortOpt());
        boolean guiPresent = cmd.hasOption(CLIConfigOption.gui.getShortOpt());

        // No configuration option means normal execution
        if (!configPresent) {
            if (guiPresent) {
                throw new LaraIException(
                        "Cannot accept GUI mode and execution options: lara file (" + firstArg
                                + ") was given.\n" + getHelp(finalOptions, 3));
            }
            return ExecutionMode.OPTIONS; // for now -g is completely ignored in this case
        }

        // A configuration option in GUI mode plus a normal option means exception
        if (!firstArg.startsWith("-")) { // then we are redefining the lara file
            if (guiPresent) {
                throw new LaraIException(
                        "Cannot accept both configuration in GUI mode and execution options: lara file (" + firstArg
                                + ") was given.\n" + getHelp(finalOptions, 3));
            }
            return ExecutionMode.CONFIG_OPTIONS;
        }
        // for (Option option : mainOptions) {
        for (Option option : finalOptions.getOptions()) {
            if (cmd.hasOption(option.getOpt())) {
                if (guiPresent) {

                    // String configOptionStr = configOption.get().getShortOpt() + "(" + configOption.get().name() +
                    // ")";
                    String mainOption = option.getOpt() + "(" + option.getLongOpt() + ")";
                    throw new LaraIException(
                            "Cannot accept both configuration in GUI mode and execution options: option " /*+ configOptionStr + " vs "*/
                                    + mainOption + "was given.\n");
                } else {
                    return ExecutionMode.CONFIG_OPTIONS;
                }
            }
        }

        // A configuration option means config execution
        return ExecutionMode.CONFIG;
    }

    public enum ExecutionMode {

        /**
         * Execute Weaver with the given options (no configuration file)
         */
        OPTIONS,
        /**
         * Execute Weaver with the given config file (no overriding options)
         */
        CONFIG,
        /**
         * Execute Weaver with the given config file and the overriding options
         */
        CONFIG_OPTIONS,
    }

    /**
     * Verify if it is to launch the GUI without config file
     *
     * @param args
     * @return
     */
    public static boolean guiMode(String[] args) {
        return args.length == 0 ||
                (args.length == 1 && args[0].startsWith("-") && CLIConfigOption.gui.sameAs(args[0]));
    }

    /**
     * Returns a configuration file or an exception if the file does not exist
     *
     * @param cmd
     * @return
     */
    public static File getConfigFile(CommandLine cmd) {
        File file = new File(cmd.getOptionValue(CLIConfigOption.config.getShortOpt()));
        if (!file.exists()) {
            throw new LaraIException("Configuration file does not exist: " + SpecsIo.getCanonicalPath(file));
        }
        return SpecsIo.getCanonicalFile(file);
    }

    public static XmlPersistence getXmlPersistence(StoreDefinition laraiDefinition) {

        XmlPersistence persistence = new XmlPersistence(laraiDefinition);

        persistence.addMappings(Arrays.asList(FileList.class, OptionalFile.class));

        return persistence;
    }

    /**
     * The 'public' StoreDefinition that will be used for the user interface. It is a subset of the complete definition.
     *
     * @param engine
     * @return
     */
    public static StoreDefinition getLaraStoreDefinition(WeaverEngine engine) {
        StoreDefinitionBuilder builder = new StoreDefinitionBuilder(LaraiStoreDefinition.getDefinitionName());
        builder.addNamedDefinition(new LaraiStoreDefinition().getStoreDefinition());
        builder.addNamedDefinition(new WeaverEngineStoreDefinition(engine).getStoreDefinition());
        StoreDefinition laraiDefinition = builder.build();
        return laraiDefinition;
    }
}
