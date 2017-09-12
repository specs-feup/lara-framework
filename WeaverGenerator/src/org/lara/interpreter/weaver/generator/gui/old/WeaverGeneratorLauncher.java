/*
 * Copyright 2015 SPeCS.
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

package org.lara.interpreter.weaver.generator.gui.old;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.up.fe.specs.guihelper.App;
import pt.up.fe.specs.guihelper.AppDefaultConfig;
import pt.up.fe.specs.guihelper.AppSource;
import pt.up.fe.specs.guihelper.GuiHelperUtils;
import pt.up.fe.specs.guihelper.Base.SetupDefinition;
import pt.up.fe.specs.guihelper.BaseTypes.SetupData;
import pt.up.fe.specs.guihelper.gui.SimpleGui;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.properties.SpecsProperty;

/**
 * Launches the program WeaverGenerator.
 *
 * @author Tiago Carvalho
 */
public class WeaverGeneratorLauncher implements AppDefaultConfig, AppSource {

	private final static String DEFAULT_CONFIG = "default.config";
	private final static List<String> baseResourceFiles = Arrays.asList();

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		SpecsSystem.programStandardInit();
		SpecsProperty.ShowStackTrace.applyProperty("true");

		SpecsIo.resourceCopy(getResources());

		final WeaverGeneratorLauncher app = new WeaverGeneratorLauncher();

		if (args.length > 0) {
			GuiHelperUtils.trySingleConfigMode(args, app);
			return;
		}

		final SimpleGui gui = new SimpleGui(app);

		gui.setTitle("WeaverGenerator v0.1");
		gui.execute();
	}

	public static List<String> getResources() {
		final List<String> resources = new ArrayList<>();
		resources.addAll(WeaverGeneratorLauncher.baseResourceFiles);
		resources.addAll(SpecsProperty.getResources());
		return resources;
	}

	@Override
	public int execute(File setupFile) throws InterruptedException {
		final SetupData setupData = GuiHelperUtils.loadData(setupFile);

		// WeaverGeneratorGlobalData globalData =
		// WeaverGeneratorGlobalSetup.getData();

		WeaverGeneratorData data = null;
		try {
			data = WeaverGeneratorSetup.newData(setupData);
		} catch (final Exception e) {
			SpecsLogs.msgWarn("Exception while building configuration data.", e);
			return -1;
		}

		if (data == null) {
			SpecsLogs.msgWarn("Configuration data is null.");
			return -1;
		}

		final WeaverGeneratorExecutor appBody = new WeaverGeneratorExecutor(data);

		final int result = appBody.execute();
		SpecsLogs.msgInfo("Done");

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.up.fe.specs.guihelper.AppSource#newInstance()
	 */
	@Override
	public App newInstance() {
		return new WeaverGeneratorLauncher();
	}

	@Override
	public SetupDefinition getEnumKeys() {
		return SetupDefinition.create(WeaverGeneratorSetup.class);
	}

	@Override
	public String defaultConfigFile() {
		return WeaverGeneratorLauncher.DEFAULT_CONFIG;
	}

}