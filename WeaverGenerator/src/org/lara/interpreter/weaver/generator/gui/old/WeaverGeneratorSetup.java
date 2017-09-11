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

import org.lara.interpreter.weaver.generator.gui.old.guitest.GeneratorData;
import org.lara.interpreter.weaver.generator.gui.old.guitest.IoSetup;

import pt.up.fe.specs.guihelper.FieldType;
import pt.up.fe.specs.guihelper.SetupAccess;
import pt.up.fe.specs.guihelper.Base.SetupDefinition;
import pt.up.fe.specs.guihelper.Base.SetupFieldEnum;
import pt.up.fe.specs.guihelper.BaseTypes.SetupData;
import pt.up.fe.specs.guihelper.SetupFieldOptions.SingleSetup;
import pt.up.fe.specs.guihelper.Setups.SimpleIo.SimpleIoData;

/**
 * Setup definition for program WeaverGenerator.
 *
 * @author Tiago Carvalho
 */
public enum WeaverGeneratorSetup implements SetupFieldEnum,SingleSetup {

	AString(FieldType.string), IoFolders(FieldType.setup);

	public static WeaverGeneratorData newData(SetupData setupData) {
		final SetupAccess setup = new SetupAccess(setupData);

		final SimpleIoData data = IoSetup.getGeneralIoData(setup.getSetup(IoFolders));

		return new WeaverGeneratorData(data);
	}

	/**
	 * INSTANCE VARIABLES
	 */
	private final FieldType fieldType;

	// for (PrimType prim : PrimType.values()) {
	// if (prim.toString().equals("_" + type)) {
	// return prim.convert;
	// }
	// }
	private WeaverGeneratorSetup(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	@Override
	public FieldType getType() {
		return fieldType;
	}

	@Override
	public String getSetupName() {
		return "WeaverGenerator";
	}

	public static GeneratorData newGeneratorData(SetupData setupData) {
		// SetupAccess setup = new SetupAccess(setupData);

		// SimpleIoData simpleIo =
		// SimpleIoSetup.getGeneralIoData(setup.getSetup(OutputFolder));

		// File outputFolder = new File(setup.getString(OutputFolder));
		final File outputFolder = null;
		return new GeneratorData(outputFolder);

	}

	@Override
	public SetupDefinition getSetupOptions() {
		if (this == IoFolders) {
			return SetupDefinition.create(IoSetup.class);
		}

		return null;
	}

}
