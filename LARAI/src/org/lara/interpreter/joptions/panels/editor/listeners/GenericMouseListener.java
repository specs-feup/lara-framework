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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public class GenericMouseListener implements MouseListener {

    private Consumer<MouseEvent> onClick;
    private Consumer<MouseEvent> onPress;
    private Consumer<MouseEvent> onRelease;
    private Consumer<MouseEvent> onEntered;
    private Consumer<MouseEvent> onExited;

    public GenericMouseListener() {
	onClick = onPress = onRelease = onEntered = onExited = empty();
    }

    public static GenericMouseListener click(Consumer<MouseEvent> listener) {

	return new GenericMouseListener().onClick(listener);
    }

    public GenericMouseListener onClick(Consumer<MouseEvent> listener) {
	onClick = listener;
	return this;
    }

    public GenericMouseListener onPressed(Consumer<MouseEvent> listener) {
	onPress = listener;
	return this;
    }

    public GenericMouseListener onRelease(Consumer<MouseEvent> listener) {
	onRelease = listener;
	return this;
    }

    public GenericMouseListener onEntered(Consumer<MouseEvent> listener) {
	onEntered = listener;
	return this;
    }

    public GenericMouseListener onExited(Consumer<MouseEvent> listener) {
	onExited = listener;
	return this;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	onClick.accept(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
	onPress.accept(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	onRelease.accept(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
	onEntered.accept(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
	onExited.accept(e);
    }

    private static Consumer<MouseEvent> empty() {
	return e -> {
	};
    }

}
