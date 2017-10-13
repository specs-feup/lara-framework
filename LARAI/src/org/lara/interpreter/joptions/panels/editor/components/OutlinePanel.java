/**
 * Copyright 2017 SPeCS.
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
import java.awt.Image;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.BadLocationException;

import org.dojo.jsl.parser.ast.ASTAspectDef;
import org.dojo.jsl.parser.ast.ASTAssignmentExpression;
import org.dojo.jsl.parser.ast.ASTCodeDef;
import org.dojo.jsl.parser.ast.ASTFunctionDeclaration;
import org.dojo.jsl.parser.ast.ASTFunctionExpression;
import org.dojo.jsl.parser.ast.ASTIdentifier;
import org.dojo.jsl.parser.ast.ASTVariableDeclaration;
import org.dojo.jsl.parser.ast.Node;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.lara.interpreter.joptions.panels.editor.EditorPanel;

import pt.up.fe.specs.tools.lara.logging.LaraLog;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;

public class OutlinePanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 4026258311800595450L;

    private final EditorPanel editor;

    private DefaultListModel<OutlineElement> listModel;
    private JList<OutlineElement> list;
    private JPanel inner;

    private static final Color SELECTION_COLOR = Color.LIGHT_GRAY;
    private static final int preferedWidth = 350;

    public OutlinePanel(EditorPanel editor) {
        super(new BorderLayout());

        Dimension preferredSize = getPreferredSize();
        preferredSize.setSize(preferedWidth, preferredSize.getHeight());
        setPreferredSize(preferredSize);

        this.editor = editor;

        inner = new JPanel(new BorderLayout());
        add(inner, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();

        list = new JList<>(listModel);
        list.setCellRenderer(new OutlineRenderer());
        list.setSelectionBackground(SELECTION_COLOR);
        inner.add(list, BorderLayout.CENTER);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addListSelectionListener(this::selectionHandler);
    }

    private void selectionHandler(ListSelectionEvent e) {

        // if (listModel.isEmpty()) {
        // return;
        // }
        OutlineElement element = list.getSelectedValue();
        if (element == null) {
            return;
        }
        int line = element.getLine();

        try {
            TextEditorPane laraTextArea = editor.getTabsContainer().getCurrentTab().getTextArea();
            laraTextArea.setCaretPosition(laraTextArea.getLineStartOffset(line - 1));
        } catch (BadLocationException e1) {

        }
    }

    public void setElements(List<ASTAspectDef> aspectList, List<ASTFunctionDeclaration> functionList,
            List<ASTCodeDef> codedefList, List<ASTFunctionExpression> expressionList) {

        List<OutlineElement> elements = OutlineElement.buildOutlineElementList(aspectList, functionList, codedefList,
                expressionList);

        listModel.removeAllElements();

        elements.forEach(listModel::addElement);

        revalidate();
    }

    static class OutlineRenderer implements ListCellRenderer<OutlineElement> {

        private JLabel label;

        public OutlineRenderer() {
            label = new JLabel();
            label.setVisible(true);
            label.setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends OutlineElement> list, OutlineElement value,
                int index,
                boolean isSelected, boolean cellHasFocus) {

            label.setText(value.getHtml());
            label.setIcon(value.getIcon());
            label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

            return label;
        }

    }

    enum OutlineElementType {
        ASPECT,
        FUNCTION,
        CODEDEF
    }

    static class OutlineElement {

        private final String name;
        private final int line;
        private final OutlineElementType type;

        public OutlineElement(String name, int line, OutlineElementType type) {
            this.name = name;
            this.line = line;
            this.type = type;
        }

        public int getLine() {
            return line;
        }

        public String getHtml() {

            String html = "<html>";
            html += name + " &nbsp ";
            html += "<span style=\"color:gray\">" + line + "</span>";
            html += "</html>";

            return html;
        }

        public Icon getIcon() {
            Image img;
            try {
                switch (type) {
                case ASPECT:
                    img = ImageIO.read(SpecsIo.resourceToStream("larai/resources/img/aspect.png"));
                    return new ImageIcon(img);
                case FUNCTION:
                    img = ImageIO.read(SpecsIo.resourceToStream("larai/resources/img/function.png"));
                    return new ImageIcon(img);
                case CODEDEF:
                    img = ImageIO.read(SpecsIo.resourceToStream("larai/resources/img/codedef.png"));
                    return new ImageIcon(img);

                default:
                    img = ImageIO.read(SpecsIo.resourceToStream("larai/resources/img/run.gif"));
                    return new ImageIcon(img);

                }
            } catch (IOException e) {
                SpecsLogs.msgWarn("Could not load the needed icon resource");
            }
            return null;
        }

        static List<OutlineElement> buildOutlineElementList(List<ASTAspectDef> aspectList,
                List<ASTFunctionDeclaration> functionList,
                List<ASTCodeDef> codedefList, List<ASTFunctionExpression> expressionList) {

            List<OutlineElement> list = SpecsCollections.newArrayList();

            aspectList.forEach(a -> list.add(
                    new OutlineElement(a.getName(), a.jjtGetFirstToken().beginLine, OutlineElementType.ASPECT)));
            functionList.forEach(a -> list.add(
                    new OutlineElement(a.getFuncName(), a.jjtGetFirstToken().beginLine, OutlineElementType.FUNCTION)));
            codedefList.forEach(a -> list.add(
                    new OutlineElement(a.getName(), a.jjtGetFirstToken().beginLine, OutlineElementType.CODEDEF)));

            expressionList.stream()
                    .map(OutlineElement::convert)
                    .filter(a -> a != null)
                    .forEach(list::add);

            Comparator<OutlineElement> alpha = (e1, e2) -> e1.name.toLowerCase().compareTo(e2.name.toLowerCase());
            return list.stream().sorted(alpha).collect(Collectors.toList());
        }

        static OutlineElement convert(ASTFunctionExpression expr) {

            // Optional<ASTVariableDeclaration> varDecl = expr.getAncestorOfType(ASTVariableDeclaration.class);
            Node parent = expr.jjtGetParent();

            if (parent instanceof ASTVariableDeclaration) {

                ASTVariableDeclaration varDecl = (ASTVariableDeclaration) parent;

                final ASTIdentifier id = (ASTIdentifier) varDecl.getChildren()[0];
                final String varName = id.getName();

                return new OutlineElement(varName, varDecl.jjtGetFirstToken().beginLine, OutlineElementType.FUNCTION);
            }
            if (parent instanceof ASTAssignmentExpression) {
                try {
                    ASTAssignmentExpression assignment = (ASTAssignmentExpression) parent;
                    String source = assignment.getChild(0).toSource();
                    return new OutlineElement(source, expr.jjtGetFirstToken().beginLine,
                            OutlineElementType.FUNCTION);
                } catch (Exception e) {
                    LaraLog.debug("Could not create outline element for a function expression: " + e.getMessage());
                }
            }

            return null;
        }
    }
}
