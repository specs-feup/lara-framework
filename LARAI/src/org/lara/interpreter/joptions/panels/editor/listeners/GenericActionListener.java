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

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;

public class GenericActionListener extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Consumer<ActionEvent> consumer;

    public static GenericActionListener newInstance(Consumer<ActionEvent> consumer) {
	return new GenericActionListener(consumer);
    }

    public GenericActionListener(Consumer<ActionEvent> consumer) {
	this.consumer = consumer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	consumer.accept(e);
    }

}
