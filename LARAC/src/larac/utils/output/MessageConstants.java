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
package larac.utils.output;

import java.io.File;

/**
 * Class containing all the predefined messages used on Lara
 * 
 * @author Tiago Carvalho
 * 
 */
public class MessageConstants {

    // Ai ai, this should not be a global
    public static int order = 1;

    public static final String HELP_DESC = "Shows this message";
    public static final String DEBUG_DESC = "Enter debug mode, i.e., reply all the possible information";
    public static final String DOC_DESC = "Compiles in documentation mode (e.g., does not process imports)";
    public static final String VERSION_DESC = "Shows the version of the LARA language and the Front-End";
    public static final String SHOWXML_DESC = "Show Aspect-IR in the console";
    public static final String OUTPUT_DESC = "Change Output directory of resulting files. Default: ." + File.separator;
    public static final String XMLSPEC_DESC = "Change language specification directory. Default: ." + File.separator;
    public static final String LANGUAGE_DESC = "Change the concerning programming language. Default: C";
    public static final String VERBOSE_DESC = "Change the message level from 0(none) to 3(all). Default: 3";
    public static final String STREAM_DESC = "Change the output stream. Default: java.lang.System.out";
    public static final String INCLUDE_DESC = "Include a list of folders containing lara aspect files. Separate paths with ';'";
    public static final String RESOURCE_DESC = "Include a list of resources that reference lara aspect files. Separate paths with ';'";

    public static final String USAGE = "usage: java -jar larac.jar (<file.lara> | -gen <WeaverName>) [-<option> <optionParam>+] | -h"
            + "\noptions:" + "\n Option\t\tShort\tInput\t\tDescription" + "\n -help \t\t-h \t\t\tShows this message"
            + "\n -version  \t-v \t\t\tShows the version of the LARA language and the Front-End"
            + "\n -gen  \t\t-g  \t<WeaverName>\tGenerate a weaver interface implementation for the Language Specification"
            + "\n -xml  \t\t-x \t\t\tShow Aspect-IR in the console"
            + "\n -log  \t\t  \t\t\tAll the output goes to a .log file"
            + "\n -outdir  \t-od \t<path>\t\tChange Output directory of resulting files. Default: ." + File.separator
            + "\n -workdir \t-wd \t<path>\t\tChange Output working directory. Default: ." + File.separator
            + "\n -xmldir \t-xd \t<path>\t\tChange source of the XML input files. Default: ." + File.separator
            + "\n -lang \t\t-l \t<language>\tChange the concerning programming language. Default: C"
            + "\n -verbose \t-vb \t<level>\t\tChange the message level from 0(none) to 3(all). Default: 3" + "\n";

    public static final String FILE_WRITEN = "Files created successfully for ";
    public static final String FILE_WRITING = "Writing file to ";
    public static final String FILE_READ = "Reading: ";
    public static final String NAME_SEPARATOR = "$";// "_";
    public static final String OVERLINE = " -==================================================-";
    public static final String UNDERLINE = MessageConstants.OVERLINE;

    public static final String LARA_VERSION = "Lara language version: 2.0";

    public static final String LANGUAGE_DEFAULT = "C";
    public static final String HOME_DIR = "." + File.separator;
    public static final String PARENT_HOME_DIR = "." + MessageConstants.HOME_DIR;
    public static final String OUT_DIR = MessageConstants.PARENT_HOME_DIR + "out" + File.separator;
    public static final String XML_DIR = MessageConstants.PARENT_HOME_DIR + "resources" + File.separator;
    public static final String WORK_DIR = MessageConstants.PARENT_HOME_DIR + "program" + File.separator;
    public static final String DEFAULT_TARGET = "__DEFAULT__";
    public static final int INDENT = 2;
    public static String space = "";
    private static String spaceStep = "    ";

    public static String getNotFoundMessage(String type) {
        return type + " not found: ";
    }

    public static void addSpace() {
        MessageConstants.space += MessageConstants.spaceStep;
    }

    public static void removeSpace() {
        MessageConstants.space = MessageConstants.space.replace(MessageConstants.spaceStep, "");
    }

    public static final String LARAC_HELP_EXEC = "java -jar larac.jar <larafile> [options]";
}
