/**
 * Copyright 2013 SPeCS Research Group.
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

package larac.options.optionprovider;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import larac.exceptions.LARACompilerException;
import pt.up.fe.specs.util.SpecsEnums;
import pt.up.fe.specs.util.providers.KeyProvider;

/**
 * Utility class for Options creation and parsing.
 * 
 * @author Tiago
 * 
 */
public class OptionUtils {

    /**
     * Parse and create a {@link CommandLine} for the given input arguments for the {@link Options} available
     * 
     * @param options
     * @param args
     * @param cmdLineSyntax
     * @return
     */
    public static CommandLine parseOptions(Options options, String[] args, String cmdLineSyntax) {

	try {

	    final CommandLineParser parser = new DefaultParser();
	    return parser.parse(options, args);

	} catch (final ParseException e) {
	    String message = e.getMessage() + "\n";
	    final HelpFormatter formatter = new HelpFormatter();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintWriter pw = new PrintWriter(baos);
	    formatter.printHelp(pw, formatter.getWidth(), cmdLineSyntax, "Options:", options,
		    formatter.getLeftPadding(),
		    formatter.getDescPadding(), null, false);
	    pw.flush();
	    String usage = new String(baos.toByteArray(), StandardCharsets.UTF_8);
	    // System.out.println("->" + usage);
	    message += usage;
	    throw new LARACompilerException("when processing options", new LARACompilerException(message));
	    // System.out.println(message);
	    // help(cmdLineSyntax, options);
	}
	// return null;
    }

    /**
     * Print a help message of how to use the application and its options
     * 
     * @param cmdLineSyntax
     *            the syntax one should use to execute the application
     * @param options
     *            the options of the application
     */
    public static void help(String cmdLineSyntax, Options options) {

	final HelpFormatter formatter = new HelpFormatter();
	formatter.printHelp(cmdLineSyntax, options);
    }

    /**
     * Create a new {@link Options} instance containing the enumerated options in optionsEnum (implementing
     * {@link OptionProvider}).
     * 
     * @param optionsEnum
     *            an enum implementing {@link OptionProvider} and {@link KeyProvider}&lt;{@link Descriptor}&gt;
     * @return
     */
    public static <K extends Enum<K> & OptionProvider & KeyProvider<Descriptor>> Options optionsBuilder(
	    Class<K> optionsEnum) {

	final Options options = new Options();
	final List<Descriptor> descriptors = SpecsEnums.getKeys(optionsEnum);

	for (final Descriptor desc : descriptors) {
	    final Option newOpt = newOption(desc);
	    options.addOption(newOpt);
	}
	return options;
    }

    /**
     * Build an option according to a {@link Descriptor}
     * 
     * @param descriptor
     * @return
     */
    public static Option newOption(Descriptor descriptor) {
	final String shortName = descriptor.getShortName();

	Builder builder;

	if (shortName == null) {
	    builder = Option.builder();
	} else {
	    builder = Option.builder(shortName);
	}

	builder.longOpt(descriptor.getName());
	switch (descriptor.getNumOfArguments()) {
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
	    builder.optionalArg(true);
	    builder.hasArgs();
	    break;
	default:
	    break;
	}

	final String argumentName = descriptor.getArgumentName();
	if (argumentName != null) {
	    builder.argName(argumentName);
	}

	builder.desc(descriptor.getDescription());

	return builder.build();
    }

    /**
     * Create a new Option Descriptor (see {@link Descriptor})
     * 
     * @param name
     * @param shortName
     * @param numOfArguments
     * @param description
     * @return
     */
    public static Descriptor newDescriptor(String name, String shortName, ArgOption numOfArguments,
	    String description) {
	return new Descriptor(name, shortName, numOfArguments, description);
    }

    /**
     * Create a new Option Descriptor, with an argument description (see {@link Descriptor})
     * 
     * @param name
     * @param shortName
     * @param numOfArguments
     * @param argumentName
     * @param description
     * @return
     */
    public static Descriptor newDescriptor(String name, String shortName, ArgOption numOfArguments, String argumentName,
	    String description) {
	return new Descriptor(name, shortName, numOfArguments, description);
    }

    /**
     * Get the descriptor of an option with a specific shortName
     * 
     * @param options
     *            Enum, implementing {@link OptionProvider}, containing the options
     * @param shortName
     *            the short version of an option to search
     * @return a {@link Descriptor} containing the option description
     */
    public static <K extends Enum<K> & OptionProvider & KeyProvider<Descriptor>> Descriptor getDescriptionByShortName(
	    Class<K> options, String shortName) {

	final List<Descriptor> descriptors = SpecsEnums.getKeys(options);

	for (final Descriptor desc : descriptors) {
	    if (desc.getShortName().equals(shortName)) {
		return desc;
	    }
	}
	return null;
    }

    /**
     * Get the enum referent to an option with a specific shortName
     * 
     * @param optionsEnum
     *            Enum, implementing {@link OptionProvider}, containing the options
     * @param shortName
     *            the short version of an option to search
     * @return the enum containing the option description
     */
    public static <K extends Enum<K> & OptionProvider & KeyProvider<Descriptor>> K getOptionByShortName(
	    Class<K> optionsEnum, String shortName) {
	final K[] optionsList = optionsEnum.getEnumConstants();
	for (final K opt : optionsList) {
	    if (opt.getDescriptor().getShortName().equals(shortName)) {
		return opt;
	    }
	}
	return null;
    }

    /**
     * Verifies if an option exists in a given Enum implementing {@link OptionProvider}
     * 
     * @param options
     *            Enum containing the options
     * @param shortName
     *            the short version of an option to search
     * @return a {@link Descriptor} containing the option description
     */
    public static <K extends Enum<K> & OptionProvider & KeyProvider<Descriptor>> boolean contains(Class<K> options,
	    String optionName) {

	final List<Descriptor> descriptors = SpecsEnums.getKeys(options);

	for (final Descriptor desc : descriptors) {
	    if (desc.getName().equals(optionName)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Verifies if an option exists in a given Enum implementing {@link OptionProvider}
     * 
     * @param optionsEnum
     *            Enum containing the options
     * @param shortName
     *            the short version of an option to search
     * @return a {@link Descriptor} containing the option description
     */
    public static <K extends Enum<K> & OptionProvider & KeyProvider<Descriptor>> K getOptionByName(Class<K> optionsEnum,
	    String optionName) {

	final K[] optionsList = optionsEnum.getEnumConstants();
	for (final K opt : optionsList) {
	    if (opt.getDescriptor().getName().equals(optionName)) {
		return opt;
	    }
	}
	return null;
    }
}
