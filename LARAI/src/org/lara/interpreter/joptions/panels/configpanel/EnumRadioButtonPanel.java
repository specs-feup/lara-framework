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

package org.lara.interpreter.joptions.panels.configpanel;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.gui.KeyPanel;

public class EnumRadioButtonPanel<T extends Enum<T>> extends KeyPanel<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // private final CheckboxGroup lngGrp;
    private final Map<T, JRadioButton> radioButtons;
    private final ButtonGroup group;

    public EnumRadioButtonPanel(DataKey<T> key, DataStore data) {
	super(key, data);

	T[] enumConstants = key.getValueClass().getEnumConstants();
	radioButtons = new HashMap<>();
	// lngGrp = new CheckboxGroup();

	// add(new JLabel("test"));
	group = new ButtonGroup();

	for (T constant : enumConstants) {
	    JRadioButton radioButton = new JRadioButton(constant.name(), false);
	    radioButtons.put(constant, radioButton);
	    group.add(radioButton);
	    add(radioButton);
	}

	if (key.getDefault().isPresent()) {

	    T t = key.getDefault().get();
	    if (radioButtons.containsKey(t)) {

		radioButtons.get(t).setSelected(true);
		// checkBoxes.get(t).setSelected(true);
		// lngGrp.setSelectedCheckbox(checkBoxes.get(t));
		return;
	    }
	}
	// if you create checkboxes and add to group,they become radio buttons
	// java = new Checkbox("Java", lngGrp, true);
	// cpp = new Checkbox("C++", lngGrp, false);
	// vb = new Checkbox("VB", lngGrp, false);

	// setLayout(new FlowLayout(FlowLayout.LEFT));
	FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
	setLayout(flowLayout);
    }

    @Override
    public T getValue() {
	Optional<T> selected = radioButtons.keySet().stream().filter(t -> radioButtons.get(t).isSelected())
		.findFirst();
	if (selected.isPresent()) {
	    return selected.get();
	}
	return null;
    }

    @Override
    public <ET extends T> void setValue(ET value) {
	if (radioButtons.containsKey(value)) {
	    radioButtons.get(value).setSelected(true);
	    // lngGrp.setSelectedCheckbox(radioButtons.get(value));
	}

    }

}
