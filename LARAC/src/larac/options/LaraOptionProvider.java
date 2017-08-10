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
package larac.options;

import larac.options.optionprovider.ArgOption;
import larac.options.optionprovider.Descriptor;
import larac.options.optionprovider.OptionProvider;
import larac.options.optionprovider.OptionUtils;
import larac.utils.output.MessageConstants;
import pt.up.fe.specs.util.providers.KeyProvider;

public enum LaraOptionProvider implements OptionProvider, KeyProvider<Descriptor> {

    /* Options with no arguments */
    // Show help message
    help(OptionUtils.newDescriptor("help", "h", ArgOption.NO_ARGS, MessageConstants.HELP_DESC)),
    // Show version
    version(OptionUtils.newDescriptor("version", "v", ArgOption.NO_ARGS, MessageConstants.VERSION_DESC)),
    // Display the Aspect-IR
    aspectir(OptionUtils.newDescriptor("aspectir", "a", ArgOption.NO_ARGS, MessageConstants.SHOWXML_DESC)),
    // Display all available information in the console
    debug(OptionUtils.newDescriptor("debug", "d", ArgOption.NO_ARGS, MessageConstants.DEBUG_DESC)),

    /* Options with one argument */
    // Target Language
    language(OptionUtils.newDescriptor("language", "l", ArgOption.ONE_ARG, "language", MessageConstants.LANGUAGE_DESC)),
    // Specification Directory
    xmlspec(OptionUtils.newDescriptor("xmlspec", "x", ArgOption.ONE_ARG, "dir", MessageConstants.XMLSPEC_DESC)),
    // Output directory for the created files
    output(OptionUtils.newDescriptor("output", "o", ArgOption.ONE_ARG, "dir", MessageConstants.OUTPUT_DESC)),
    // Outputs to a file with the given name
    stream(OptionUtils.newDescriptor("stream", "s", ArgOption.ONE_ARG, "file", MessageConstants.STREAM_DESC)),
    // Verbose level
    verbose(OptionUtils.newDescriptor("verbose", "b", ArgOption.ONE_ARG, "level", MessageConstants.VERBOSE_DESC)),
    // include aspects in the given directories (separated by ';')
    include(OptionUtils.newDescriptor("include", "i", ArgOption.ONE_ARG, "dir(;dir)*", MessageConstants.INCLUDE_DESC)),
    resource(OptionUtils.newDescriptor("resource", "r", ArgOption.ONE_ARG, "resource(;resource)*",
            MessageConstants.RESOURCE_DESC)),;

    private Descriptor descriptor;

    LaraOptionProvider(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public Descriptor getKey() {
        return getDescriptor();
    }

    @Override
    public Descriptor getDescriptor() {
        return descriptor;
    }
}
