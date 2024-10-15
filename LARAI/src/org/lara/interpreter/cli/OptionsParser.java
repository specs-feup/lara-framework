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
        final Option workDir = OptionsBuilderUtils.newOption(CLIOption.workspace);
        final Option workDirExtra = OptionsBuilderUtils.newOption(CLIOption.workspace_extra);
        final Option verbose = OptionsBuilderUtils.newOption(CLIOption.verbose);
        final Option argv = OptionsBuilderUtils.newOption(CLIOption.argv);
        // final Option argw = OptionsBuilderUtils.newOption(CLIOption.argw);
        final Option main = OptionsBuilderUtils.newOption(CLIOption.main);
        final Option tools = OptionsBuilderUtils.newOption(CLIOption.tools);
        final Option report = OptionsBuilderUtils.newOption(CLIOption.report);

        final Option log = OptionsBuilderUtils.newOption(CLIOption.log);

        final Option metrics = OptionsBuilderUtils.newOption(CLIOption.metrics);

        final Option restrict = OptionsBuilderUtils.newOption(CLIOption.restrict);
        final Option jsengine = OptionsBuilderUtils.newOption(CLIOption.jsengine);
        final Option jarpaths = OptionsBuilderUtils.newOption(CLIOption.jarpaths);

        Options options = new Options()
                .addOption(help)
                .addOption(version)
                .addOption(argv)
                // .addOption(argw)
                .addOption(main)
                .addOption(debug)
                .addOption(stack)
                .addOption(outDir)
                .addOption(workDir)
                .addOption(workDirExtra)
                .addOption(verbose)
                .addOption(tools)
                .addOption(report)
                .addOption(javascript)
                .addOption(log)
                .addOption(metrics)
                .addOption(restrict)
                .addOption(jsengine)
                .addOption(jarpaths);

        // final Option weaver = newOption(CLIOption.weaver, "className", ArgOption.ONE_ARG,
        // "change the target weaver (default: " + LaraI.DEFAULT_WEAVER + ")");
        // final Option xml = newOption(CLIOption.XMLspec, "dir", ArgOption.ONE_ARG,
        // "location of the target language specification");
        // .addOption(weaver)
        // .addOption(xml)

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

    /*
    public void help() {
    	this.formatter.printHelp("java -jar larai.jar (<larafile>.lara|<aspIR>.xml) [options]", this.opts);
    }
    */
    public static String getHelp(Options options) {
        return getHelp(options, HelpFormatter.DEFAULT_LEFT_PAD);
    }

    public static String getHelp(Options options, int leftPadding) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null); // So it stays the same order the options are added
        StringWriter sw = new StringWriter();
        String usageSyntax = "<aspect.lara> [options] [-c <file>]  | -c <file>";
        PrintWriter pw = new PrintWriter(sw);
        if (CLIConfigOption.ALLOW_GUI) {
            usageSyntax += " [-g]";
        }
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
    /*
    public static String getHelp() {
    	return getHelp(buildLaraIOptions());
    }
    */

    public static Collection<Option> buildConfigOptions() {

        List<Option> options = new ArrayList<>();
        options.add(CLIConfigOption.config.buildOption());

        return options;
    }

    // public static ExecutionMode getExecMode(String firstArg, CommandLine cmd, Collection<Option> mainOptions,
    // Options finalOptions) {
    public static ExecutionMode getExecMode(String firstArg, CommandLine cmd, Options finalOptions) {
        // validateExclusiveOptions(cmd, configOptions, options);
        // Optional<CLIConfigOption> configOption = Arrays.asList(CLIConfigOption.values()).stream()
        // .filter(opt -> cmd.hasOption(opt.getShortOpt()))
        // .findFirst();

        boolean configPresent = cmd.hasOption(CLIConfigOption.config.getShortOpt());

        // No configuration option means normal execution
        if (!configPresent) {
            return ExecutionMode.OPTIONS; // for now -g is completely ignored in this case
        }

        // A configuration option in GUI mode plus a normal option means exception
        if (!firstArg.startsWith("-")) { // then we are redefining the lara file
            return ExecutionMode.CONFIG_OPTIONS;
        }
        // for (Option option : mainOptions) {
        for (Option option : finalOptions.getOptions()) {
            if (cmd.hasOption(option.getOpt())) {
                return ExecutionMode.CONFIG_OPTIONS;
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
        /**
         * Open GUI with the overriding options (means creating a temporary config file)
         */
        // OPTIONS_GUI //FUTURE WORK!
        /**
         * Unit testing mode
         */
        // UNIT_TEST
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
        return new StoreDefinitionBuilder(LaraiStoreDefinition.getDefinitionName())
            .addNamedDefinition(new LaraiStoreDefinition().getStoreDefinition())
            .addNamedDefinition(new WeaverEngineStoreDefinition(engine).getStoreDefinition())
            .build();
    }

    // /**
    // *
    // * @param engine
    // * @return
    // */
    // public static StoreDefinition getLaraStoreDefinitionComplete(WeaverEngine engine) {
    // return getLaraStoreDefinition(engine, true);
    // }
    //
    // private static StoreDefinition getLaraStoreDefinition(WeaverEngine engine, boolean isComplete) {
    // StoreDefinitionBuilder builder = new StoreDefinitionBuilder(LaraiStoreDefinition.getDefinitionName());
    // builder.addNamedDefinition(new LaraiStoreDefinition().getStoreDefinition());
    // builder.addNamedDefinition(new WeaverEngineStoreDefinition(engine).getStoreDefinition());
    //
    // if (isComplete) {
    // builder.addDefinition(LaraiKeys.STORE_DEFINITION_EXTRA);
    // }
    //
    // StoreDefinition laraiDefinition = builder.build();
    // return laraiDefinition;
    // }
}
