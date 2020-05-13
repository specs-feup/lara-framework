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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.lara.interpreter.joptions.panels.editor.EditorPanel;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.Declaration;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.Select;

import pt.up.fe.specs.util.swing.GenericActionListener;

public class LanguageSpecificationSideBar extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final LanguageSpecificationV2 langSpec;
    private JComboBox<JoinPointClass> joinPoints;

    private final EditorPanel editor;

    private JPanel rootPanel;

    private DefaultListModel<Attribute> attributes;
    private JoinPointClass currentExtend = null;
    private DefaultListModel<Select> selects;
    private DefaultListModel<Action> actions;
    private DefaultListModel<Select> selectedBy;
    private static final Color SELECTION_COLOR = Color.LIGHT_GRAY;
    private static final int preferedWidth = 350;
    // private static final int preferedListHeight = 140;
    private static final int listCharMaxWidth = 260;

    private JPanel inner;
    private JPanel joinPointPanel;
    private JPanel extendsPanel;
    private JButton extendsButton;

    /**
     * This constructor exists for compatibility purposes
     * 
     * @param editor
     * @param langSpec
     */
    // public LanguageSpecificationSideBar(EditorPanel editor, LanguageSpecification langSpec) {
    // this(editor, JoinPointFactory.fromOld(langSpec));
    // }

    public LanguageSpecificationSideBar(EditorPanel editor, LanguageSpecificationV2 langSpec) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(4, 0, 5, 0));
        // setBackground(Colors.BLUE_GREY);
        Dimension preferredSize = this.getPreferredSize();
        preferredSize.setSize(preferedWidth, preferredSize.getHeight());
        this.setPreferredSize(preferredSize);
        this.langSpec = langSpec;
        this.editor = editor;

        addHeader();
        addLists();
        init();

    }

    private void addHeader() {
        JPanel header = new JPanel(new BorderLayout());

        add(header, BorderLayout.NORTH);
        JPanel topHeader = new JPanel(new BorderLayout());
        JLabel comp = new JLabel("Language Specification", SwingConstants.CENTER);
        comp.setBorder(new EmptyBorder(5, 0, 5, 0));
        // JLabel comp = new JLabel("<html><div style='text-align: center;te'>Language Specification</div></html>");
        comp.setFont(comp.getFont().deriveFont(Font.BOLD, 14));
        // comp.setVerticalAlignment(SwingConstants.CENTER);
        topHeader.add(comp, BorderLayout.CENTER);
        rootPanel = new JPanel(new FlowLayout());
        topHeader.add(rootPanel, BorderLayout.SOUTH);

        header.add(topHeader, BorderLayout.NORTH);

        joinPointPanel = new JPanel(new BorderLayout());
        joinPoints = new JComboBox<>();
        extendsPanel = new JPanel();
        joinPointPanel.add(joinPoints, BorderLayout.CENTER);
        header.add(joinPointPanel);
    }

    private void addLists() {
        inner = new JPanel(new GridBagLayout());
        add(inner, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;

        c.gridy++;
        addLabel(c, "Attributes");

        c.gridy++;
        attributes = new DefaultListModel<>();
        addList(attributes, new AttributeCellRenderer(), c);

        c.gridy++;
        addLabel(c, "Selects");

        c.gridy++;
        selects = new DefaultListModel<>();
        addList(selects, new SelectCellRenderer(), c);

        c.gridy++;
        addLabel(c, "Actions");

        c.gridy++;
        actions = new DefaultListModel<>();
        addList(actions, new ActionCellRenderer(), c);

        c.gridy++;
        addLabel(c, "Selected by");

        c.gridy++;
        selectedBy = new DefaultListModel<>();
        addList(selectedBy, new SelectedByCellRenderer(), c);
    }

    private void addLabel(GridBagConstraints c, String text) {
        JLabel comp = new JLabel(text);
        comp.setFont(comp.getFont().deriveFont(Font.BOLD, 11));
        comp.setBorder(new EmptyBorder(9, 0, 3, 0));
        inner.add(comp, c);

        // JLabel comp = new JLabel("Language Specification", SwingConstants.CENTER);
        // JLabel comp = new JLabel("<html><div style='te xt-align: center;te'>Language Specification</div></html>");
        // comp.setFont(comp.getFont().deriveFont(Font.BOLD, 13));
    }

    private <T> void addList(ListModel<T> model, ListCellRenderer<T> renderer, GridBagConstraints c) {
        JList<T> jList = new JList<>();
        jList.setModel(model);
        jList.setCellRenderer(renderer);
        jList.setSelectionBackground(LanguageSpecificationSideBar.SELECTION_COLOR);
        JScrollPane comp = new JScrollPane(jList);

        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        inner.add(comp, c);
        c.weighty = 0;
    }

    private void init() {
        initRoot();
        initExtends();
        initJoinPoints();
        joinPoints.setSelectedItem(langSpec.getRoot());
    }

    private void initJoinPoints() {

        joinPoints.addItem(langSpec.getGlobal());
        for (JoinPointClass joinPoint : langSpec.getJoinPoints().values()) {
            joinPoints.addItem(joinPoint);
        }
        joinPoints.addActionListener(
                new GenericActionListener(e -> updateJPInfo((JoinPointClass) joinPoints.getSelectedItem())));

    }

    private void updateJPInfo(JoinPointClass selectedItem) {
        // Clear lists
        attributes.removeAllElements();
        selects.removeAllElements();
        actions.removeAllElements();
        selectedBy.removeAllElements();
        // Populate lists
        selectedItem.getAttributes().forEach(attributes::addElement);
        selectedItem.getSelects().forEach(selects::addElement);
        selectedItem.getActions().forEach(actions::addElement);
        selectedItem.getSelectedBy().forEach(selectedBy::addElement);

        if (selectedItem.hasExtend()) {
            extendsPanel.setVisible(true);
            currentExtend = selectedItem.getExtend().get();
            extendsButton.setText(currentExtend.getName());
        } else {
            extendsPanel.setVisible(false);
            currentExtend = null;
            extendsButton.setText("N/A");
        }
        revalidate();
    }

    private void initRoot() {
        JLabel jLabel = new JLabel("Root: ");
        JoinPointClass root = langSpec.getRoot();
        String name = root.getName();
        if (!langSpec.getRootAlias().isEmpty()) {
            name = langSpec.getRootAlias() + "(" + name + ")";
        }
        JButton button = new JButton(" " + name + " ");
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEtchedBorder());
        button.addActionListener(new GenericActionListener(e -> joinPoints.setSelectedItem(root)));

        rootPanel.add(jLabel);
        rootPanel.add(button);
        // rootPanel.setPreferredSize(new Dimension(50, 50));

    }

    private void initExtends() {
        JLabel jLabel = new JLabel("Extends: ");
        extendsButton = new JButton("N/A");
        extendsButton.setContentAreaFilled(false);
        extendsButton.setBorder(BorderFactory.createEtchedBorder());
        extendsButton.addActionListener(new GenericActionListener(e -> {
            if (currentExtend != null) {
                joinPoints.setSelectedItem(currentExtend);
            }
        }));
        extendsPanel.add(jLabel);
        extendsPanel.add(extendsButton);
        joinPointPanel.add(extendsPanel, BorderLayout.EAST);
        // rootPanel.setPreferredSize(new Dimension(50, 50));

    }

    public LanguageSpecificationV2 getLangSpec() {
        return langSpec;
    }

    public EditorPanel getEditor() {
        return editor;
    }

    static class AttributeCellRenderer extends JLabel implements ListCellRenderer<Attribute> {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public AttributeCellRenderer() {
            setVisible(true);
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Attribute> list, Attribute value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Declaration declaration = value.getDeclaration();
            String toHtml = toHtml(declaration);
            String text = "<html>" + toHtml;
            if (!value.getParameters().isEmpty()) {
                text += value.getParameters().stream().map(d -> toHtml(d)).collect(Collectors.joining(", ", "(", ")"));
            }
            text += "</html>";
            setText(text);
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            // setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            setToolTipText(value.getToolTip().orElse(null));
            return this;
        }

    }

    static class ActionCellRenderer extends JLabel implements ListCellRenderer<Action> {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public ActionCellRenderer() {
            setVisible(true);
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Action> list, Action value, int index,
                boolean isSelected, boolean cellHasFocus) {
            // String text = "<html><b>" + value.getName() + "</b>";
            Declaration declaration = value.getDeclaration();
            String toHtml = toHtml(declaration);
            String text = "<html><body style='width: " + listCharMaxWidth + "px'>" + toHtml;
            text += value.getParameters().stream().map(d -> toHtml(d)).collect(Collectors.joining(", ", "(", ")"));
            text += "</body></html>";
            setText(text);
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            // setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            setToolTipText(value.getToolTip().orElse("Documentation not available"));
            return this;
        }

    }

    static class SelectCellRenderer extends JLabel implements ListCellRenderer<Select> {
        // static {
        // ToolTipManager.sharedInstance().setInitialDelay(1000);
        // }

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public SelectCellRenderer() {
            setVisible(true);
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Select> list, Select value, int index,
                boolean isSelected, boolean cellHasFocus) {
            String name = value.getAlias();
            String ofType = "";
            String temp = value.getClazz().getName();
            if (name.isEmpty()) {

                name = temp;
            } else {
                ofType = ": " + temp + "";
            }

            String text = "<html><b><font color=\"#7f0055\">" + name + "</b>" + ofType + "</html>";
            setText(text);
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            // setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());

            setToolTipText(value.getToolTip().orElse(null));

            return this;
        }

    }

    static class SelectedByCellRenderer extends JLabel implements ListCellRenderer<Select> {
        // static {
        // ToolTipManager.sharedInstance().setInitialDelay(1000);
        // }

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public SelectedByCellRenderer() {
            setVisible(true);
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Select> list, Select value, int index,
                boolean isSelected, boolean cellHasFocus) {

            String text = selectedBytoHtml(value);
            setText(text);
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            // setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());

            setToolTipText(value.getToolTip().orElse(null));

            return this;
        }

    }

    private static String toHtml(Declaration declaration) {
        String toHtml = "<b><font color=\"#7f0055\">" + declaration.getType() + "</font> </b>"
                + declaration.getName();
        return toHtml;
    }

    private static String selectedBytoHtml(Select select) {
        JoinPointClass selector = select.getSelector();
        String alias = select.getAlias();
        if (alias.isEmpty()) {
            alias = select.getClazz().getName();
        }
        String toHtml = "<html><b><font color=\"#7f0055\">" + selector.getName() + "</font> </b> as <b>"
                + alias + "</b></html>";
        return toHtml;
    }

    private static String toHtml(Parameter parameter) {
        Declaration declaration = parameter.getDeclaration();
        String defaultValue = parameter.getDefaultValue();
        String toHtml = toHtml(declaration);
        if (!defaultValue.isEmpty()) {
            toHtml += " = " + defaultValue;
        }
        return toHtml;
    }

    public void resizeForScrollBar() {
        // Dimension size = inner.getSize();
        // this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        // hiddenPanel.setVisible(true);
        validate();
        repaint();
        // size.setSize(size.getWidth() - 20, size.getHeight());
        // inner.setSize(size);
        // inner.revalidate();
    }
}
