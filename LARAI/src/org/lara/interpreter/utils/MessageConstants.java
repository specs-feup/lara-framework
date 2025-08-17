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
package org.lara.interpreter.utils;

import java.io.File;

/**
 * Class containing all the predefined messages used on Lara
 * 
 * @author Tiago Carvalho
 * 
 */
public class MessageConstants {
	public static int order = 1;
	private static final String USAGE = "usage: java -jar larai.jar <file.xml> [-<option> <optionParam>+] | -h"
			+ "\noptions:" + "\n Option\t\tShort\tInput\t\tDescription" + "\n -help \t\t-h \t\t\tShows this message"
			+ "\n -version  \t-v \t\t\tShows the version of the LARA interpreter"
			+ "\n -javascript\t-js \t\t\tShow the resulting javascript in the console"
			+ "\n -outdir  \t-od \t<path>\t\tChange Output directory of resulting files. Default: ." + File.separator
			+ "\n -workdir \t-wd \t<path>\t\tChange Output working directory. Default: ." + File.separator
			+ "\n -verbose \t-vb \t<level>\t\tChange the message level from 0(none) to 3(all). Default: 3" + "\n";

	public static String getUsage() {
		return USAGE;
	}
	private static final String HOME_DIR = "." + File.separator;
	private static final String PARENT_HOME_DIR = "." + HOME_DIR;
	private static final String OUT_DIR = PARENT_HOME_DIR + "out" + File.separator;
	private static final String WORK_DIR = PARENT_HOME_DIR + "program" + File.separator;
	private static final String DEFAULT_TARGET = "__DEFAULT__";
	private static final String BRANCH_STR = "\\_ ";

	public static String getHomeDir() {
		return HOME_DIR;
	}

	public static String getParentHomeDir() {
		return PARENT_HOME_DIR;
	}

	public static String getOutDir() {
		return OUT_DIR;
	}

	public static String getWorkDir() {
		return WORK_DIR;
	}

	public static String getDefaultTarget() {
		return DEFAULT_TARGET;
	}

	public static String getBranchStr() {
		return BRANCH_STR;
	}

	public static final String getElapsedTimeMessage(long timeMillis) {
		return getElapsedTimeMessage(timeMillis, "Elapsed Time");
	}

	public static final String getElapsedTimeMessage(long timeMillis, String text) {
		StringBuilder sb = new StringBuilder(larac.utils.output.MessageConstants.getUnderline() + "\n");
		sb.append("  " + text + ": " + timeMillis + "ms\n");
		sb.append(larac.utils.output.MessageConstants.getOverline() + "");
		return sb.toString();
	}

	public static final String getHeaderMessage(int order, String text) {
		StringBuilder sb = new StringBuilder(larac.utils.output.MessageConstants.getUnderline() + "\n");
		sb.append("  " + order + ". " + text + "\n");
		sb.append(larac.utils.output.MessageConstants.getOverline() + "");
		return sb.toString();
	}

	public static final String getMessage(String text) {
		StringBuilder sb = new StringBuilder(larac.utils.output.MessageConstants.getUnderline() + "\n");
		sb.append("   " + text + "\n");
		sb.append(larac.utils.output.MessageConstants.getOverline() + "");
		return sb.toString();
	}
}
