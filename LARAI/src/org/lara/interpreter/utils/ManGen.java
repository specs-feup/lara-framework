/**
 * Copyright 2019 SPeCS.
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

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.lara.interpreter.cli.OptionsParser;
import org.lara.interpreter.weaver.interf.WeaverEngine;

import pt.up.fe.specs.util.SpecsIo;

public class ManGen {

    private static final String QUOTE = "\"";
    private static final String SPACE = " ";
    private static final String NL = "\n"; // Linux only?
    private static final String SLASH = "\\";
    private static final String MAN_SECTION = "1";

    private String name;
    private String shortDesc;
    private String version;
    private String header;
    private WeaverEngine engine;
    private String longDesc;
    private List<String> synopses;

    public ManGen(String name) {

        this.name = name;
        this.shortDesc = "";
        this.version = "";
        this.header = "";
        this.engine = null;
        this.synopses = new ArrayList<String>();
    }

    public String generate() {

        StringBuilder builder = new StringBuilder();

        builder.append(getHeaderSection());
        builder.append(getNameSection());
        builder.append(getSynopsisSection());
        builder.append(getDescriptionSection());
        builder.append(getOptionsSection());

        return builder.toString();
    }

    private String getOptionsSection() {

        StringBuilder builder = new StringBuilder(section("OPTIONS"));

        Collection<Option> configOptions = OptionsParser.buildConfigOptions();
        Collection<Option> mainOptions = OptionsParser.buildLaraIOptionGroup();

        if (this.engine != null) {
            OptionsParser.addExtraOptions(mainOptions, engine.getOptions());
        }

        Options completeOptions = new Options();
        configOptions.forEach(completeOptions::addOption); // So the config options appear on the top
        mainOptions.forEach(completeOptions::addOption);

        for (Option option : completeOptions.getOptions()) {

            builder.append(optionCode(option));
        }

        builder.append(NL);
        builder.append(NL);

        return builder.toString();
    }

    private String getSynopsisSection() {

        StringBuilder builder = new StringBuilder();

        if (!this.synopses.isEmpty()) {

            builder.append(section("SYNOPSIS"));

            builder.append(synopses.get(0).stripTrailing());

            for (int i = 1; i < this.synopses.size(); i++) {

                builder.append(NL + ".br" + NL);
                builder.append(synopses.get(i).stripTrailing());
            }
        }

        builder.append(NL);
        builder.append(NL);

        return builder.toString();
    }

    private String getDescriptionSection() {

        StringBuilder builder = new StringBuilder();

        if (!this.longDesc.isBlank()) {

            builder.append(section("DECRIPTION"));

            String newName = NL + bold(this.name) + NL;
            String newLongDesc = this.longDesc.replace(this.name, newName);
            newLongDesc = newLongDesc.replace(NL + SPACE, NL);
            builder.append(newLongDesc);
        }

        builder.append(NL);
        builder.append(NL);

        return builder.toString();
    }

    private String getHeaderSection() {

        StringBuilder builder = new StringBuilder(".TH");

        builder.append(SPACE);

        builder.append(this.name);

        builder.append(SPACE);

        builder.append(MAN_SECTION);

        builder.append(SPACE);

        builder.append(getDate());

        builder.append(SPACE);

        builder.append(getVersion());

        builder.append(SPACE);

        builder.append(getHeader());

        builder.append(NL);
        builder.append(NL);

        return builder.toString();
    }

    private String getHeader() {

        if (this.header.isBlank()) {
            return QUOTE + this.name + SPACE + "man page" + QUOTE;
        } else {
            return this.header;
        }
    }

    private String getDate() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();

        return QUOTE + dtf.format(now) + QUOTE;
    }

    private String getNameSection() {

        StringBuilder builder = new StringBuilder(section("NAME"));
        builder.append(this.name);

        if (!this.shortDesc.isBlank()) {

            builder.append(SPACE);
            builder.append(SLASH);
            builder.append("-");
            builder.append(SPACE);
            builder.append(this.shortDesc);
        }

        builder.append(NL);
        builder.append(NL);

        return builder.toString();
    }

    private String getVersion() {

        if (this.version.isBlank()) {
            return "\"\"";
        } else {
            return QUOTE + this.name + " version " + this.version + QUOTE;
        }
    }

    /*
     * HELPERS
     */

    private static String optionCode(Option option) {

        StringBuilder builder = new StringBuilder(".TP");
        builder.append(NL);

        if (option.hasArg()) {

            builder.append(".BI");
            builder.append(SPACE);
            builder.append("-");
            builder.append(option.getOpt());
            builder.append(SPACE);
            builder.append(SLASH);
            builder.append(SPACE);
            builder.append(option.getArgName().replace(SPACE, SLASH + SPACE));

            if (option.hasLongOpt()) {

                builder.append(SPACE);
                builder.append(SLASH);
                builder.append("fR");
                builder.append(",");
                builder.append(SLASH);
                builder.append(SPACE);
                builder.append(SLASH);
                builder.append("fB");
                builder.append("--");
                builder.append(option.getLongOpt());
                builder.append(SPACE);
                builder.append(SLASH);
                builder.append(SPACE);
                builder.append(option.getArgName().replace(SPACE, SLASH + SPACE));
            }
        } else {

            builder.append(".B");
            builder.append(SPACE);
            builder.append("-");
            builder.append(option.getOpt());

            if (option.hasLongOpt()) {

                builder.append(SLASH);
                builder.append("fR");
                builder.append(",");
                builder.append(SLASH);
                builder.append(SPACE);
                builder.append(SLASH);
                builder.append("fB");
                builder.append("--");
                builder.append(option.getLongOpt());
            }
        }
        builder.append(NL);

        builder.append(option.getDescription());
        builder.append(NL);

        return builder.toString();
    }

    private static String bold(String string) {

        return ".B" + SPACE + string;
    }

    private static String section(String string) {
        return ".SH" + SPACE + string + NL;
    }

    private static String italic(String string) {
        return ".I" + SPACE + string;
    }

    public static String arg(String string) {

        return italic(string) + NL;
    }

    public static String optArg(String string) {

        return ".RI" + SPACE + "[" + string + "]" + NL;
    }

    public static String flag(String flag) {

        return ".B" + SPACE + flag + NL;
    }

    public static String flag(String flag, String value) {

        return ".BI" + SPACE + flag + SPACE + QUOTE + SPACE + value + QUOTE + NL;
    }

    public static String optFlag(String flag) {

        return ".RB" + SPACE + "[" + SPACE + QUOTE + flag + QUOTE + SPACE + "]" + NL;
    }

    public static String optFlag(String flag, String value) {

        return ".RB" + SPACE + "[" + SPACE + QUOTE + flag + QUOTE + NL + ".IR" + SPACE + value + SPACE + "]" + NL;
    }

    /*
     * SETTERS
     */

    public ManGen setVersion(String version) {

        this.version = version;

        return this;
    }

    public ManGen setHeader(String header) {

        this.header = header;

        return this;
    }

    public ManGen setShortDesc(String shortDesc) {

        this.shortDesc = shortDesc;

        return this;
    }

    public ManGen setLongDesc(String longDesc) {

        this.longDesc = longDesc;

        return this;
    }

    public ManGen setLongDesc(File file) {

        this.longDesc = SpecsIo.read(file);

        return this;
    }

    public ManGen addSynopsis(String syn) {

        this.synopses.add(bold(this.name) + NL + syn);

        return this;
    }
}
