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

package org.lara.interpreter.joptions.panels.editor.utils;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.joptions.panels.editor.EditorPanel;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.gui.panels.app.ProgramPanel;
import org.suikasoft.jOptions.gui.panels.app.TabbedPane;

public class LaraWorker extends ApplicationWorker<DataStore> {

    private final EditorPanel editor;
    private Optional<File> toOpen;

    public LaraWorker(EditorPanel editor) {
        this.editor = editor;
        toOpen = Optional.empty();
    }

    @Override
    protected Callable<Integer> getTask(DataStore setup) {
        TabbedPane tabbedPane = (TabbedPane) SwingUtilities.getAncestorOfClass(TabbedPane.class, editor);
        ProgramPanel program = SearchUtils.findFirstComponentOfType(tabbedPane, ProgramPanel.class);
        // program.execute();

        Callable<Integer> callable = () -> {
            return executeLARA(setup, program);
        };
        return callable;
    }

    private int executeLARA(DataStore setup, ProgramPanel program) {
        int execute = program.getApplication().getKernel().execute(setup);
        if (execute == 0) {

            Optional<OptionalFile> metrics = setup.getTry(LaraiKeys.METRICS_FILE);
            if (metrics.isPresent()) {
                OptionalFile optionalFile = metrics.get();
                if (optionalFile.isUsed()) {
                    toOpen = Optional.of(optionalFile.getFile());
                }
            }
        }
        return execute;
    }

    @Override
    protected void onStart() {
        editor.setStopButton();
    }

    @Override
    protected void onEnd() {
        if (toOpen.isPresent()) {
            editor.getTabsContainer().open(toOpen.get());
            toOpen = Optional.empty();
        }
        editor.setPlayButton();
        editor.getTabsContainer().getCurrentTab().refresh();
    }

}
