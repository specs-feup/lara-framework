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

package org.lara.interpreter.joptions.panels.editor.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.lara.interpreter.joptions.panels.editor.listeners.FocusGainedListener;
import org.lara.interpreter.joptions.panels.editor.tabbed.SourceTextArea;

public class ButtonTabComponent extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // private final EditorTab tab;
    private final JLabel label;

    public ButtonTabComponent(final SourceTextArea tab) {
        // unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (tab == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        // this.tab = tab;
        setOpaque(false);

        // make JLabel read titles from JTabbedPane
        label = new JLabel(tab.getTextArea().getFileName());
        add(label);

        label.addFocusListener(new FocusGainedListener(e -> tab.requestFocus()));

        label.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                dispatch(e);
                Component parent = ButtonTabComponent.this;

                do {
                    // System.out.println(parent.getClass());
                    parent = parent.getParent();
                } while (parent != null);
            }

            private void dispatch(MouseEvent e) {
                JTabbedPane ancestorOfClass = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class,
                        ButtonTabComponent.this);
                ancestorOfClass.dispatchEvent(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                dispatch(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dispatch(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                dispatch(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                dispatch(e);
            }
        });

        /*label.addMouseListener(TabbedContextMenu.newMouseAdapter(tab.getTabbedParent()));
        label.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
        
        	switch (e.getButton()) {
        	case MouseEvent.BUTTON1:
        	    doFocus(e);
        	    break;
        	default:
        	    break;
        	}
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
        	switch (e.getButton()) {
        	case MouseEvent.BUTTON2:
        	    tab.close();
        	    break;
        	default:
        	    break;
        	}
        }
        
        private void doFocus(MouseEvent e) {
        	tab.getTabbedParent().moveTab(tab);
        
        }
        
        });
        */
        // add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        // tab button
        JButton button = new CloseButton(x -> tab.close());
        add(button);
        // add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    public void setTitle(String title) {
        label.setText(title);
    }

}
