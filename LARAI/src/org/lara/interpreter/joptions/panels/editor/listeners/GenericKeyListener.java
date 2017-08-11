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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

public class GenericKeyListener implements KeyListener {

    private Consumer<KeyEvent> onType;
    private Consumer<KeyEvent> onPress;
    private Consumer<KeyEvent> onRelease;

    public GenericKeyListener() {
	onType = onPress = onRelease = empty();
    }

    public GenericKeyListener onType(Consumer<KeyEvent> listener) {
	onType = listener;
	return this;
    }

    public GenericKeyListener onPressed(Consumer<KeyEvent> listener) {
	onPress = listener;
	return this;
    }

    public GenericKeyListener onRelease(Consumer<KeyEvent> listener) {
	onRelease = listener;
	return this;
    }

    @Override
    public void keyPressed(KeyEvent e) {
	onPress.accept(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
	onRelease.accept(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
	onType.accept(e);
    }

    private static Consumer<KeyEvent> empty() {
	return e -> {
	};
    }

}
