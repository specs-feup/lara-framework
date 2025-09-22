/**
 * Copyright 2016 SPeCS.
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

package org.lara.interpreter.weaver.options;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.suikasoft.jOptions.Datakey.DataKey;

public class WeaverOptionBuilder {

    private final DefaultWeaverOption weaverOption;

    public WeaverOptionBuilder() {
        weaverOption = new DefaultWeaverOption();
    }

    /**
     * Build a WeaverOption that has no arguments
     *
     */
    public static WeaverOption build(String shortOption, String longOption, String description,
            DataKey<?> dataKey) {
        WeaverOptionBuilder builder = new WeaverOptionBuilder()
                .shortOption(shortOption)
                .longOption(longOption)
                .description(description)
                .args(OptionArguments.NO_ARGS)
                .dataKey(dataKey);
        return builder.build();
    }

    /**
     * Build a WeaverOption with the given information
     *
     */
    public static WeaverOption build(String shortOption, String longOption, OptionArguments args, String argName,
            String description,
            DataKey<?> dataKey) {
        WeaverOptionBuilder builder = new WeaverOptionBuilder()
                .shortOption(shortOption)
                .longOption(longOption)
                .args(args)
                .argName(argName)
                .description(description)
                .dataKey(dataKey);
        return builder.build();
    }

    public WeaverOption build() {
        return weaverOption;
    }

    public WeaverOptionBuilder shortOption(String shortOption) {
        weaverOption.shortOption = shortOption;
        return this;
    }

    public WeaverOptionBuilder longOption(String longOption) {
        weaverOption.longOption = longOption;
        return this;
    }

    public WeaverOptionBuilder description(String description) {
        weaverOption.description = description;
        return this;
    }

    public WeaverOptionBuilder args(OptionArguments args) {
        weaverOption.args = args;
        return this;
    }

    public WeaverOptionBuilder argName(String argName) {
        weaverOption.argName = argName;
        return this;
    }

    public WeaverOptionBuilder dataKey(DataKey<?> dataKey) {
        weaverOption.dataKey = dataKey;
        return this;
    }

    private static class DefaultWeaverOption implements WeaverOption {

        private String shortOption;
        private String longOption;
        private String description;
        private OptionArguments args;
        private String argName;
        private DataKey<?> dataKey;

        private DefaultWeaverOption() {
            shortOption = "";
            longOption = "";
            description = "";
            args = OptionArguments.NO_ARGS;
            argName = "arg";
            dataKey = null;
        }

        @Override
        public String shortOption() {
            return shortOption;
        }

        @Override
        public String longOption() {
            return longOption;
        }

        @Override
        public String description() {
            return description;
        }

        @Override
        public OptionArguments args() {
            return args;
        }

        @Override
        public String argName() {
            return argName;
        }

        @Override
        public DataKey<?> dataKey() {
            return dataKey;
        }

        @Override
        public String toString() {
            return description;
        }

    }

    public static WeaverOption build(DataKey<?> dataKey) {
        return build(null, dataKey.getName(), dataKey.getLabel(), dataKey);
    }

    public static <T extends Enum<T>> List<WeaverOption> enum2List(Class<T> anEnum,
            Function<? super T, WeaverOption> mapper) {

        List<T> enumList = Arrays.asList(anEnum.getEnumConstants());
        return enumList.stream().map(mapper).collect(Collectors.toList());

    }

}
