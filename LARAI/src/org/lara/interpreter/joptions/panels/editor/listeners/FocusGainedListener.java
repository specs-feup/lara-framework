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

package org.lara.interpreter.joptions.panels.editor.listeners;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

public class FocusGainedListener implements FocusListener {

    private final Consumer<FocusEvent> gainFocusListener;

    public FocusGainedListener(Consumer<FocusEvent> gainFocusListener) {
	super();
	this.gainFocusListener = gainFocusListener;
    }

    @Override
    public void focusGained(FocusEvent e) {
	gainFocusListener.accept(e);
    }

    @Override
    public void focusLost(FocusEvent e) {

    }

}
