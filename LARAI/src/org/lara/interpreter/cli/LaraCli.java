/**
 * Copyright 2018 SPeCS.
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

package org.lara.interpreter.cli;

import java.util.Collection;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.lara.interpreter.weaver.interf.WeaverEngine;

public class LaraCli {

    /**
     * Builds the list of command-line interface options available to the given weaver.
     * 
     * @param weaverEngine
     * @return
     */
    public static Options getCompleteOptions(WeaverEngine weaverEngine) {
        Collection<Option> configOptions = OptionsParser.buildConfigOptions();
        Collection<Option> mainOptions = OptionsParser.buildLaraIOptionGroup();
        OptionsParser.addExtraOptions(mainOptions, weaverEngine.getOptions());

        Options completeOptions = new Options();

        configOptions.forEach(completeOptions::addOption); // So the config options appear on the top
        mainOptions.forEach(completeOptions::addOption);

        return completeOptions;
    }
}
