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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.panels.editor.EditorPanel;
import org.lara.interpreter.joptions.panels.editor.listeners.FileTreeCellRenderer;
import org.lara.interpreter.joptions.panels.editor.listeners.GenericKeyListener;
import org.lara.interpreter.joptions.panels.editor.listeners.GenericMouseListener;
import org.lara.interpreter.joptions.panels.editor.utils.Factory;

public class Explorer extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final EditorPanel parent;
    private final DefaultTreeModel treeModel;
    private final JTree tree;
    final FileSystemView fileSystemView;
    private final DefaultMutableTreeNode root;
    private final JPopupMenu popupMenu;
    private File lastDirectory;
    private WorkDirNode workspaceNode;
    private DefaultMutableTreeNode outputNode;

    public Explorer(EditorPanel parent) {
        super(new BorderLayout());
        // setBackground(Colors.BLUE_GREY);
        this.parent = parent;
        fileSystemView = FileSystemView.getFileSystemView();

        // the File tree
        root = new DefaultMutableTreeNode("Workspace");
        treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);
        initTree();
        JScrollPane treeScroll = new JScrollPane(tree);
        // tree.setCellRenderer(new DefaultTreeCellRenderer());

        popupMenu = new ExplorerPopup(this);
        // Dimension preferredSize = treeScroll.getPreferredSize();
        Dimension widePreferred = new Dimension(
                200,
                // (int) preferredSize.getHeight());
                800);
        // (int) preferredSize2.height);
        treeScroll.setPreferredSize(widePreferred);
        add(treeScroll, BorderLayout.CENTER);
        lastDirectory = new File(".");
        // setMainWorkspace(lastDirectory);
        addDefaultProjects(lastDirectory);

        DevUtils.getDevProjects(this);
    }

    private void addDefaultProjects(File source) {
        addProject(source, "Aspects", true, false);
        workspaceNode = new WorkDirNode("Workspace");
        outputNode = new FileNode(new File("Output"));

        root.add(workspaceNode);
        root.add(outputNode);

    }

    private void initTree() {

        tree.addKeyListener(new GenericKeyListener().onPressed(e -> {
            if (e.getKeyCode() == KeyEvent.VK_F5) {
                refresh();
            }
        }));
        tree.setRootVisible(false);
        tree.setEditable(false);
        // tree.addTreeSelectionListener(treeSelectionListener);
        tree.setCellRenderer(new FileTreeCellRenderer(this));
        tree.expandRow(0);

        tree.setVisibleRowCount(15);

        MouseListener onRelease = GenericMouseListener.click(this::clicked)
                .onRelease(this::released);
        tree.addMouseListener(onRelease);
        tree.addTreeSelectionListener(e -> {
            TreePath path = e.getPath();
            Object lastPathComponent = path.getLastPathComponent();
            if (lastPathComponent instanceof FileNode) {
                FileNode node = (FileNode) lastPathComponent;
                if (node.getFile().isDirectory()) {
                    if (node.isLeaf()) {
                        showChildren(node, false, 3);
                    } else {
                        updateChildren(node, 3);
                    }
                }
            } else if (lastPathComponent instanceof WorkDirNode) {
                DefaultMutableTreeNode wdNode = (DefaultMutableTreeNode) lastPathComponent;
                for (int i = 0; i < wdNode.getChildCount(); i++) {
                    updateChildren((FileNode) wdNode.getChildAt(i), 3);
                }
            }
            tree.scrollPathToVisible(path);
        });

    }

    public void setMainWorkspace(File source) {
        if (!source.isDirectory()) {
            source = source.getParentFile();
        }
        setMain(source);

    }

    public void refreshAllExceptMain() {
        for (int i = 3; i < root.getChildCount(); i++) {
            reload((FileNode) root.getChildAt(i));
        }
    }

    public void refreshAll() {
        for (int i = 0; i < root.getChildCount(); i++) {
            reload((FileNode) root.getChildAt(i));
        }
    }

    public void refresh() {
        // Enumeration<TreePath> expandedDescendants = tree.getExpandedDescendants(new TreePath(root));
        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath treePath : selectionPaths) {
                Object lastPathComponent2 = treePath.getLastPathComponent();
                if (lastPathComponent2 instanceof WorkDirNode) {
                    WorkDirNode wdNode = (WorkDirNode) lastPathComponent2;
                    for (int i = 0; i < wdNode.getChildCount(); i++) {
                        reload((FileNode) wdNode.getChildAt(i));
                    }
                } else {
                    FileNode lastPathComponent = (FileNode) lastPathComponent2;
                    reload(lastPathComponent);
                }
            }
        }
        // treeModel.nodeStructureChanged(root); // this works
        // tree.expandRow(0); // together with this

        // while (expandedDescendants.hasMoreElements()) {
        // TreePath path = expandedDescendants.nextElement();
        // tree.expandPath(path);
        // }
        // tree.setSelectionPaths(selectionPaths);
    }

    private void reload(FileNode lastPathComponent) { // for now an update removes all child and adds new
        updateChildren(lastPathComponent, 2);
    }

    private void setMain(File source) { // this method will only be called when source is guaranteed existent
        // if (root.isLeaf()) {
        // addProject(source, "Aspects", true, false);
        // } else {
        FileNode sourceNode = newFileNode(source, "Aspects", false);

        showChildren(sourceNode, true, 2);
        FileNode old = (FileNode) root.getChildAt(0);
        if (old.getFile().equals(source)) {
            return;
        }
        Enumeration<TreePath> expandedDescendants = tree.getExpandedDescendants(new TreePath(root));
        treeModel.removeNodeFromParent(old);
        treeModel.insertNodeInto(sourceNode, root, 0);
        treeModel.nodeStructureChanged(root);
        TreePath newPath = new TreePath(treeModel.getPathToRoot(sourceNode));
        tree.expandPath(newPath);

        if (expandedDescendants != null) {
            while (expandedDescendants.hasMoreElements()) {
                TreePath path = expandedDescendants.nextElement();

                tree.expandPath(path);
            }
        }

        // }

    }

    private void reloadInserted(int position) {
        treeModel.nodesWereInserted(root, new int[] { position });
    }

    private static FileNode newFileNode(File source, String comment) {
        return newFileNode(source, comment, true);
    }

    private static FileNode newFileNode(File source, String comment, boolean isCloseable) {

        FileNode node = new FileNode(source, comment, isCloseable);
        return node;
    }

    void addProject(File file, String comment, boolean collapse, boolean isCloseable) {
        FileNode fileNode = addFileWithChildren(file, comment, root, collapse, isCloseable, 2);
        reloadInserted(root.getChildCount() - 1);
        TreeNode[] nodePath = treeModel.getPathToRoot(fileNode);

        TreePath path = new TreePath(nodePath);
        tree.expandPath(path);
    }

    private FileNode addFileWithChildren(File file, String comment, DefaultMutableTreeNode parent, boolean expand,
            boolean isCloseable,
            int depth) {
        FileNode thisFileNode = addNode(file, comment, parent, isCloseable);
        showChildren(thisFileNode, expand, depth);
        return thisFileNode;
    }

    public FileNode addNode(File file, String comment, DefaultMutableTreeNode parent, boolean isCloseable) {
        FileNode thisFileNode = newFileNode(file, comment, isCloseable);
        parent.add(thisFileNode);
        return thisFileNode;
    }

    /**
     * Add the files that are contained within the directory of this node. Thanks to Hovercraft Full Of Eels.
     */
    private void showChildren(final FileNode node, boolean expand, int depthLevel) {
        tree.setEnabled(false);
        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = node.getFile();
                if (file.isDirectory()) {

                    File[] files = fileSystemView.getFiles(file, true);
                    if (node.isLeaf()) {
                        for (File child : files) {
                            publish(child);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {

                for (File child : chunks) {
                    FileNode newFileNode = newFileNode(child, "");
                    if (child.isDirectory() && depthLevel > 1) {
                        addFileWithChildren(child, "", node, false, true, depthLevel - 1);
                    } else {
                        treeModel.insertNodeInto(newFileNode, node, 0);
                    }
                }
            }

            @Override
            protected void done() {
                tree.setEnabled(true);
                if (expand) {
                    TreeNode[] pathToRoot = treeModel.getPathToRoot(node);

                    TreePath nodePath = new TreePath(pathToRoot);
                    tree.expandPath(nodePath);
                }
            }

        };
        worker.execute();

    }

    /**
     * Update the children of a node
     * 
     * @param depth
     */
    private synchronized void updateChildren(final FileNode node, int depth) {
        tree.setEnabled(false);
        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            private final List<FileNode> childrenList = new ArrayList<>();

            @Override
            public Void doInBackground() {

                // if (node.isLeaf()) {
                // return null;
                // }

                for (int i = 0; i < node.getChildCount(); i++) {
                    childrenList.add((FileNode) node.getChildAt(i));
                }

                boolean[] exists = new boolean[childrenList.size()];
                File file = node.getFile();
                if (file.isDirectory()) {

                    File[] files = fileSystemView.getFiles(file, true);
                    for (File child : files) {

                        for (int i = 0; i < childrenList.size(); i++) {

                            File nodeFile = childrenList.get(i).getFile();
                            if (nodeFile.equals(child)) {
                                exists[i] = true;
                                break;
                            }
                        }

                        publish(child);
                    }

                }
                for (int i = 0; i < exists.length; i++) {
                    if (!exists[i]) {
                        FileNode aNode = childrenList.get(i);

                        if (treeModel.getPathToRoot(aNode) != null) { // if is in tree
                            treeModel.removeNodeFromParent(aNode);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {

                for (File child : chunks) {
                    FileNode childNode = getNode(child);
                    if (childNode == null) {
                        // if (depth > 1) {
                        // addFileWithChildren(child, "", node, false, depth - 1);
                        // } else {
                        // treeModel.insertNodeInto(newFileNode(child, ""), node, node.getChildCount());
                        treeModel.insertNodeInto(newFileNode(child, ""), node, 0);
                        // System.out.println("Adding: " + child);
                        // }
                    }
                    // else {
                    // updateChildren(childNode, depth - 1);
                    // }
                }
            }

            /**
             * If contains file will remove the node from the list
             * 
             * @param file
             * @return
             */
            private FileNode getNode(File file) {

                for (FileNode child : childrenList) {

                    File nodeFile = child.getFile();
                    if (nodeFile.equals(file)) {
                        return child;
                    }
                }
                return null;
            }

            @Override
            protected void done() {

                tree.setEnabled(true);
            }

        };
        worker.execute();

    }

    private void released(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
            if (selPath != null) {
                if (!tree.isPathSelected(selPath)) {
                    if (!e.isControlDown() && !e.isShiftDown()) {
                        tree.setSelectionPath(selPath);
                    }
                }

            }
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
        return;

    }

    private void clicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {

            return;
        }

        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selPath == null) {
            return;
        }
        if (e.getButton() == MouseEvent.BUTTON1) {

            switch (e.getClickCount()) {
            case 1: // This case is to control the situation in which the popup steals focus and clicking in the
                // explorer it does nothing!
                if (e.isControlDown()) {
                    tree.addSelectionPath(selPath);
                } else if (e.isShiftDown()) {

                } else {
                    tree.setSelectionPath(selPath);
                }
                break;
            case 2:
                if (selPath.getLastPathComponent() instanceof FileNode) {
                    FileNode lastPathComponent = (FileNode) selPath
                            .getLastPathComponent();
                    File userObject = lastPathComponent.getFile();
                    if (!userObject.isDirectory()) {
                        parent.getTabsContainer().open(userObject);
                    }
                }
                break;
            default:
                break;
            }
        }
    }

    public EditorPanel getEditorPanel() {
        return parent;
    }

    public void open(ActionEvent e) {
        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths == null) {
            JOptionPane.showMessageDialog(parent, "Please first select a file to open.");
            return;
        }
        for (TreePath treePath : selectionPaths) {
            File file = getFile(treePath);
            if (file != null) {
                if (file.isDirectory()) {
                    tree.expandPath(treePath);
                } else {
                    parent.getTabsContainer().open(file);
                }
            }
        }
    }

    public void openMainLara(ActionEvent e) {
        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths == null) {
            JOptionPane.showMessageDialog(parent, "Please first select a file to open.");
            return;
        }
        if (selectionPaths.length > 1) {
            JOptionPane.showMessageDialog(parent, "Only one file can be opened.");
            return;
        }
        TreePath treePath = selectionPaths[0];
        FileNode node = (FileNode) treePath.getLastPathComponent();
        parent.getTabsContainer().loadMainAspectFile(node.getFile());

    }

    public void newProject(ActionEvent e) {
        JFileChooser fc = Factory.newFileChooser("Open Project", JFileChooser.DIRECTORIES_ONLY, "Open",
                Collections.emptyList(), lastDirectory);

        JPanel allPanel = new JPanel();
        allPanel.setPreferredSize(new Dimension(200, 300));
        allPanel.add(new JLabel("Properties", SwingConstants.CENTER), BorderLayout.NORTH);

        GridLayout layout = new GridLayout(2, 2);
        JPanel contentPanel = new JPanel(layout);
        contentPanel.add(new JLabel("Description: ", SwingConstants.TRAILING));
        JTextField descText = new JTextField();
        descText.setPreferredSize(new Dimension(100, 20));
        contentPanel.add(descText);

        contentPanel.add(new JLabel("Expand: ", SwingConstants.TRAILING));
        JCheckBox expand = new JCheckBox();
        expand.setSelected(true);
        contentPanel.add(expand);

        allPanel.add(contentPanel, BorderLayout.CENTER);
        fc.setAccessory(allPanel);

        int returnVal = fc.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String comment = descText.getText().trim();
            if (comment.isEmpty()) {
                comment = file.getAbsolutePath();
            }
            addProject(file, comment, expand.isSelected(), true);
            lastDirectory = file;
        }
    }

    public void removeProject(ActionEvent e) {

        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths == null) {
            return;
        }
        for (TreePath treePath : selectionPaths) {
            Object pathComponent2 = treePath.getPathComponent(1);
            if (pathComponent2 instanceof WorkDirNode) {

                JOptionPane.showMessageDialog(parent, "The project is protected and cannot be closed.");
                continue;
            }
            FileNode pathComponent = (FileNode) pathComponent2;
            if (pathComponent.isCloseable()) {

                treeModel.removeNodeFromParent(pathComponent);
            } else {

                JOptionPane.showMessageDialog(parent, "The project is protected and cannot be closed.");
            }
            // }
        }

    }

    public void expand(ActionEvent e) {
        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths == null) {
            return;
        }
        for (TreePath treePath : selectionPaths) {
            tree.expandPath(treePath);
        }
    }

    public void collapse(ActionEvent e) {
        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths == null) {
            return;
        }
        for (TreePath treePath : selectionPaths) {
            tree.collapsePath(treePath);
        }
    }

    private static File getFile(TreePath path) {
        Object lastPathComponent = path.getLastPathComponent();
        if (lastPathComponent == null) {
            return null;
        }
        FileNode node = (FileNode) lastPathComponent;
        return node.getFile();
    }

    public FileSystemView getFileSystemView() {
        return fileSystemView;
    }

    // public void setTargetWorkspaces(FileList inFile) {
    //
    // }

    // public void setOutputDir(File outputDir) {
    // root.insert(outputNode, 2);
    // reloadInserted(2);
    // TreeNode childAt = root.getChildAt(2);
    // System.out.println("Replaced with " + outputDir + " result: " + childAt);
    // }

    public void setOutputDir(File source) { // this method will only be called when source is guaranteed existent
        // if (root.isLeaf()) {
        // addProject(source, "Aspects", true, false);
        // } else {
        FileNode sourceNode = newFileNode(source, "Output", false);

        showChildren(sourceNode, true, 2);
        FileNode old = (FileNode) root.getChildAt(2);
        if (old.getFile().equals(source)) {
            return;
        }
        Enumeration<TreePath> expandedDescendants = tree.getExpandedDescendants(new TreePath(root));
        treeModel.removeNodeFromParent(old);
        treeModel.insertNodeInto(sourceNode, root, 2);
        treeModel.nodeStructureChanged(root);
        TreePath newPath = new TreePath(treeModel.getPathToRoot(sourceNode));
        tree.expandPath(newPath);

        if (expandedDescendants != null) {
            while (expandedDescendants.hasMoreElements()) {
                TreePath path = expandedDescendants.nextElement();

                tree.expandPath(path);
            }
        }

        // }

    }

    public void setWorkspaces(FileList sources) { // this method will only be called when source is guaranteed existent
        // if (root.isLeaf()) {
        // addProject(source, "Aspects", true, false);
        // } else {
        // List<FileNode> sourceNodes = SpecsFactory.newArrayList();

        for (int i = 0; i < workspaceNode.getChildCount(); i++) {
            MutableTreeNode node = (MutableTreeNode) workspaceNode.getChildAt(i);
            treeModel.removeNodeFromParent(node);
        }
        int i = 0;
        for (File file : sources.getFiles()) {
            FileNode sourceNode = newFileNode(file, "", false);
            showChildren(sourceNode, true, 1);
            // sourceNodes.add(sourceNode);
            // System.out.println(file);
            // treeModel.removeNodeFromParent(old);
            treeModel.insertNodeInto(sourceNode, workspaceNode, i++);

        }
        // Enumeration<TreePath> expandedDescendants = tree.getExpandedDescendants(new TreePath(root));
        // treeModel.nodeStructureChanged(workspaceNode);
        // TreePath newPath = new TreePath(treeModel.getPathToRoot(sourceNode));
        // tree.expandPath(newPath);

        // if (expandedDescendants != null) {
        // while (expandedDescendants.hasMoreElements()) {
        // TreePath path = expandedDescendants.nextElement();
        //
        // tree.expandPath(path);
        // }
        // }

        // FileNode old = (FileNode) root.getChildAt(2);
        // if (old.getFile().equals(source)) {
        // return;
        // }

        // }

    }

}
