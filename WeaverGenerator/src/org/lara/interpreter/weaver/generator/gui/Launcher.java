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

package org.lara.interpreter.weaver.generator.gui;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.lara.interpreter.weaver.generator.options.WeaverGeneratorStoreDefinition;
import org.suikasoft.jOptions.app.App;
import org.suikasoft.jOptions.app.AppKernel;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.gui.SimpleGui;
import org.suikasoft.jOptions.gui.panels.app.GuiTab;
import org.suikasoft.jOptions.gui.panels.app.TabProvider;
import org.suikasoft.jOptions.persistence.XmlPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

import pt.up.fe.specs.util.SpecsSystem;

public class Launcher {
    public static void main(String[] args) {

        SpecsSystem.programStandardInit();

        AppKernel kernel = new WeaverGeneratorKernel();

        StoreDefinition definition = new WeaverGeneratorStoreDefinition().getStoreDefinition();

        if (args.length == 0) {

            AppPersistence persistence = new XmlPersistence(definition);

            List<TabProvider> xmlTabs = new ArrayList<>();
            xmlTabs.add(data -> new GuiTab(data) {

                @Serial
                private static final long serialVersionUID = 1L;

                @Override
                public String getTabName() {
                    return "TestTab";
                }

                @Override
                public void exitTab() {
                    System.out.println("I'm moving!" + data);
                }

                @Override
                public void enterTab() {

                    System.out.println("I'ma coming with: " + data);
                }
            });

            SimpleGui gui = new SimpleGui(
                    App.newInstance(definition.getName(), definition, persistence, kernel).setOtherTabs(xmlTabs));

            gui.execute();

        }
    }
}
