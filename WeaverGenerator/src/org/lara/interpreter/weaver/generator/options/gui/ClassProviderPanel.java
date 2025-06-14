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

package org.lara.interpreter.weaver.generator.options.gui;

import org.lara.interpreter.weaver.generator.options.utils.ClassProvider;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.gui.panels.option.GenericStringPanel;

public class ClassProviderPanel extends GenericStringPanel<ClassProvider> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ClassProviderPanel(DataKey<ClassProvider> key, DataStore data) {
        super(key, data);
    }

    @Override
    public ClassProvider getValue() {
        String stringValue = getText().trim();
        if (stringValue.isEmpty()) {
            return ClassProvider.newInstance();
        }

        return getKey().getDecoder().get().decode(stringValue);
    }

    @Override
    public <ET extends ClassProvider> void setValue(ET value) {
        setText(value.getCustomClass().getName());
    }

}
