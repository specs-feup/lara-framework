package applet;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.Permission;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.dojo.jsl.parser.ast.LARAEcmaScript;
import org.dojo.jsl.parser.ast.SimpleNode;

import larac.exceptions.LARACompilerException;
import larac.utils.FileUtils;
import larac.utils.FileUtils.Commands;

/**
 * 
 * @deprecated Bispo: It seems is not been used, nor there are plans to use it
 *
 */
@Deprecated
class errorReport extends Frame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    int factor = 50;

    public errorReport(String error) {
        final GridBagLayout gbl = new GridBagLayout();
        final GridBagConstraints gbcon = new GridBagConstraints();
        final int width = GUI.window_Width * 2 / 3;
        int height = error.split("\n").length * this.factor;
        int lines = height / this.factor;
        if (height > GUI.window_Height) {
            lines = lines * GUI.window_Height / (height * 1 / 2);
            height = GUI.window_Height;
        }
        setSize(width, height);
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final Dimension screenSize = tk.getScreenSize();
        final int screenHeight = screenSize.height;
        final int screenWidth = screenSize.width;
        // setSize(screenWidth / 2, screenHeight / 2);
        setLocation(screenWidth / 2 - width / 2, screenHeight / 2 - height / 2);
        final Label l = new Label("AspectIR");
        final TextArea ta = new TextArea(error, lines, (int) Math.round(GUI.areaWidth * 2.5 / 4),
                TextArea.SCROLLBARS_VERTICAL_ONLY);

        // ta.setSize(width,height);
        gbcon.anchor = GridBagConstraints.NORTH;
        gbcon.gridx = 0;
        gbcon.gridy = 0;
        gbl.setConstraints(l, gbcon);
        add(l);
        gbcon.gridy = 1;
        gbl.setConstraints(ta, gbcon);
        add(ta);
        setLayout(gbl);
        addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                setVisible(false);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }
        });
    }
}

class AspectIRPopup extends Frame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    int factor = 50;

    public AspectIRPopup(String aspectIR) {
        final GridBagLayout gbl = new GridBagLayout();
        final GridBagConstraints gbcon = new GridBagConstraints();
        final int width = GUI.window_Width * 2 / 3;
        int height = aspectIR.split("\n").length * this.factor;
        int lines = height / this.factor;
        if (height > GUI.window_Height) {
            lines = lines * GUI.window_Height / (height * 1 / 2);
            height = GUI.window_Height;
        }
        setSize(width, height);
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final Dimension screenSize = tk.getScreenSize();
        final int screenHeight = screenSize.height;
        final int screenWidth = screenSize.width;
        // setSize(screenWidth / 2, screenHeight / 2);
        setLocation(screenWidth / 2 - width / 2, screenHeight / 2 - height / 2);
        final Label l = new Label("AspectIR");
        final TextArea ta = new TextArea(aspectIR, lines, (int) Math.round(GUI.areaWidth * 2.5 / 4),
                TextArea.SCROLLBARS_VERTICAL_ONLY);
        // ta.setSize(width,height);
        gbcon.anchor = GridBagConstraints.NORTH;
        gbcon.gridx = 0;
        gbcon.gridy = 0;
        gbl.setConstraints(l, gbcon);
        add(l);
        gbcon.gridy = 1;
        gbl.setConstraints(ta, gbcon);
        add(ta);
        setLayout(gbl);
        addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }
        });
    }
}

class RunLara {

    private final String code;
    private final boolean showAspectIR;
    private final boolean interpret;
    private final boolean showTree;

    private static class ExitTrappedException extends SecurityException {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
    }

    public RunLara(String code, boolean showAspectIR, boolean showIntepreter, boolean showTree, GUI app) {
        this.code = code;
        this.showAspectIR = showAspectIR;
        this.interpret = showIntepreter;
        this.showTree = showTree;
    }

    public void run() {

        try {

            System.out.println("EcmaScript Parser:  Reading from console");
            final InputStream is = new ByteArrayInputStream(this.code.getBytes("UTF-8"));
            final LARAEcmaScript parser = new LARAEcmaScript(is);
            final SimpleNode n = parser.Start();

            System.out.println("LARAEcmaScript parser:  EcmaScript program parsed successfully.");
            if (this.interpret) {

            }
            if (this.showAspectIR) {
                final AspectIRPopup popup = new AspectIRPopup(n.dumpToString(""));
                popup.setVisible(true);
            }
            if (this.showTree) {

            }

            // } catch (final ExitTrappedException e) {
        } catch (final Exception e) {
            throw new LARACompilerException("during GUI execution", e);
        }
    }
}

class LowPanel extends Panel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Button save, saveAs, open, clear;
    GUI app;
    JFileChooser fc = new JFileChooser() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void approveSelection() {
            final File f = getSelectedFile();
            if (f.exists() && getDialogType() == JFileChooser.SAVE_DIALOG) {
                final int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (result) {
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                case JOptionPane.NO_OPTION:
                    return;
                case JOptionPane.CANCEL_OPTION:
                    cancelSelection();
                    return;
                default:
                    break;
                }
            }
            super.approveSelection();
        }
    };
    String filePathSave = "";

    public LowPanel(GUI parent) {
        this.app = parent;
        final GridBagLayout gbl = new GridBagLayout();
        final GridBagConstraints gbcon = new GridBagConstraints();

        initBottomBoxs(gbl, gbcon);
        setLayout(gbl);
    }

    private void initBottomBoxs(GridBagLayout gbl, GridBagConstraints gbcon) {
        this.open = new Button("Open");
        this.open.setActionCommand(Commands.OPEN.toString());
        this.save = new Button("Save");
        this.save.setActionCommand(Commands.SAVE.toString());
        this.saveAs = new Button("SaveAs");
        this.saveAs.setActionCommand(Commands.SAVEAS.toString());
        this.clear = new Button("Clear");
        this.clear.setActionCommand(Commands.CLEAR.toString());
        gbcon.anchor = GridBagConstraints.WEST;
        gbcon.gridx = 0;
        gbcon.gridy = 0;
        gbl.setConstraints(this.open, gbcon);
        add(this.open);
        gbcon.gridx = 1;
        gbl.setConstraints(this.save, gbcon);
        add(this.save);
        gbcon.gridx = 2;
        gbl.setConstraints(this.saveAs, gbcon);
        add(this.saveAs);
        gbcon.gridx = 3;
        gbl.setConstraints(this.clear, gbcon);
        add(this.clear);
    }

    @Override
    public boolean action(Event evt, Object arg) {
        // new StringBuffer(50);
        if (evt.target instanceof Button) {
            final Button bt = (Button) evt.target;
            final String command = bt.getActionCommand();
            this.fc.setCurrentDirectory(new File("."));
            switch (Commands.valueOf(command)) {
            case SAVE:
                if (!this.filePathSave.isEmpty()) {
                    FileUtils.toFile(this.filePathSave, "", this.app.console.code.getText(), new File("."));
                    break;
                }
                //$FALL-THROUGH$
            case SAVEAS:
                this.filePathSave = FileUtils.processSaveFileChooser(this.app, this.fc, FileUtils.aspectFilter, "Save",
                        JFileChooser.FILES_ONLY);
                if (!this.filePathSave.isEmpty()) {
                    this.filePathSave += this.filePathSave.endsWith(".lara") ? "" : ".lara";
                    FileUtils.toFile(this.filePathSave, "", this.app.console.code.getText(), new File("."));
                }
                break;
            case CLEAR:
                this.filePathSave = "";
                this.app.console.code.setText("");
                break;
            case OPEN:
                final File f = FileUtils.processOpenFileChooser(this.app, this.fc, FileUtils.aspectFilter, "Open",
                        JFileChooser.FILES_ONLY);
                this.filePathSave = f.getPath();
                this.app.console.code.setText(FileUtils.fromFile(f));
                break;
            case ASPECT:
                break;
            case GENERATE:
                break;
            case LANGUAGE:
                break;
            case LOAD:
                break;
            case OUTPUT:
                break;
            case RESOURCE:
                break;
            case WORKSPACE:
                break;
            case XML:
                break;
            default:
                break;
            }
            return true;
        }
        return false;
    }
}

class Console extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    TextArea code;

    Button generate;
    TextArea output;
    GUI app;
    PrintStream SystemOut = System.out;

    public Console(GUI parent) {

        this.app = parent;
        final GridBagLayout gbl = new GridBagLayout();
        final GridBagConstraints gbcon = new GridBagConstraints();

        initTxtBoxs(gbl, gbcon);
        final OutputStream outStream = new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                Console.this.output.append("" + (char) b);
            }
        };
        try (PrintStream printStream = new PrintStream(outStream);) {
            System.setOut(printStream);
        }
        setLayout(gbl);

    }

    private void initTxtBoxs(GridBagLayout gbl, GridBagConstraints gbcon) {
        this.code = new TextArea("aspectdef example\n\nend", GUI.areaHeight, (int) Math.round(GUI.areaWidth * 2.5 / 4),
                TextArea.SCROLLBARS_VERTICAL_ONLY);

        this.generate = new Button("Generate");
        this.output = new TextArea("", GUI.areaHeight, (int) Math.round(GUI.areaWidth * 1.5 / 4),
                TextArea.SCROLLBARS_VERTICAL_ONLY);
        this.output.setEditable(false);
        gbcon.gridx = 0;
        gbcon.gridy = 0;
        gbl.setConstraints(this.code, gbcon);
        add(this.code);
        gbcon.gridx = 1;
        gbl.setConstraints(this.generate, gbcon);
        add(this.generate);
        gbcon.gridx = 2;
        gbl.setConstraints(this.output, gbcon);
        add(this.output);
    }

    @Override
    public boolean action(Event evt, Object arg) {

        if (evt.target instanceof Button) {
            this.output.setText("");
            runProgram();
            return true;
        }
        return false;
    }

    public void runProgram() {

        final TopPanel tp = this.app.topPanel;
        runLara(this.app, this.code.getText(), tp.languageTxt.getText(), tp.resourcesTxt.getText(), tp.xmlTxt.getText(),
                tp.outputTxt.getText(), tp.workingTxt.getText(), tp.showAspectIR.getState(), tp.interpreter.getState(),
                tp.showTreeDefinition.getState());
        final String out = this.output.getText();
        if (out.contains("at line ")) {

            final int pos = out.indexOf("at line ") + "at line ".length();
            final int line = Integer.parseInt(out.substring(pos, out.indexOf(",", pos)));
            final int colPos = out.indexOf(" column ", pos) + " column ".length();
            final int column = Integer.parseInt(out.substring(colPos, out.indexOf(".", colPos)));
            System.out.println("select: " + line);
            this.code.select(line, line + 10);
            System.getProperty("line.separator");

            final String[] lines = this.code.getText().split("\n");
            System.out.println("totalLines: " + lines.length);
            if (line - 1 < lines.length) {
                int posInit = 0;
                for (int i = 0; i < line - 1; i++) {
                    posInit += (lines[i] + "\n").length();
                }

                final int finalSel = posInit + (lines[line - 1] + "\n").length();
                this.code.select(posInit + column, finalSel);
                this.code.requestFocus();
            }
        }

    }

    private static void runLara(GUI app, String code, String language, String resource, String xmlDir, String outputDir,
            String workDir, boolean showAspectIR, boolean interpret, boolean showTree) {
        final RunLara runner = new RunLara(code, showAspectIR, interpret, showTree, app);
        runner.run();
    }
}

class TopPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    HashMap<String, String> examples;
    Checkbox showAspectIR;
    Checkbox interpreter;
    Checkbox showTreeDefinition;
    Label aspectLabel, languageLabel, resourcesLabel, xmlLabel, outputLabel, workingLabel;
    Button aspectDial, languageDial, resourcesDial, xmlDial, outputDial, workingDial;
    Choice loader;
    Button load;
    TextField aspectTxt, languageTxt, resourcesTxt, xmlTxt, outputTxt, workingTxt;
    GUI app;

    public TopPanel(GUI parent) {
        this.app = parent;
        final GridBagLayout gbl = new GridBagLayout();
        final GridBagConstraints gbcon = new GridBagConstraints();

        initLabels(gbl, gbcon);
        initDialogs(gbl, gbcon);
        initTxtBoxs(gbl, gbcon);
        initExamplesChoice(gbl, gbcon);
        setLayout(gbl);
    }

    private void initExamplesChoice(GridBagLayout gbl, GridBagConstraints gbcon) {
        this.load = new Button("Load");
        this.showAspectIR = new Checkbox("Show Aspect-IR", true);
        this.interpreter = new Checkbox("Interpret (not working)", false);
        this.showTreeDefinition = new Checkbox("Tree of Definitions", false);
        this.load.setActionCommand(Commands.LOAD.toString());
        this.loader = new Choice();
        this.examples = new HashMap<>();
        if (FileUtils.getExamplesFromDir("examples", this.examples)) {
            for (final String example : this.examples.keySet()) {
                this.loader.addItem(example);
            }
        }
        gbcon.gridx = 1;
        gbcon.gridy = 3;
        gbcon.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(this.loader, gbcon);
        add(this.loader);

        gbcon.gridx = 0;
        gbcon.gridy = 3;
        gbcon.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(this.load, gbcon);
        add(this.load);
        gbcon.anchor = GridBagConstraints.WEST;
        gbcon.gridx = 4;
        gbcon.gridy = 2;
        gbl.setConstraints(this.showAspectIR, gbcon);
        add(this.showAspectIR);
        gbcon.anchor = GridBagConstraints.WEST;
        gbcon.gridy = 3;
        gbcon.gridx = 4;
        gbl.setConstraints(this.interpreter, gbcon);
        add(this.interpreter);
        gbcon.gridy = 4;
        gbl.setConstraints(this.showTreeDefinition, gbcon);
        add(this.showTreeDefinition);
    }

    private void initTxtBoxs(GridBagLayout gbl, GridBagConstraints gbcon) {

        this.aspectTxt = new TextField(20);
        this.languageTxt = new TextField(20);
        this.languageTxt.setText("C");
        this.resourcesTxt = new TextField(20);
        this.xmlTxt = new TextField(20);
        this.xmlTxt.setText("." + File.separator);
        this.outputTxt = new TextField(20);
        this.outputTxt.setText("." + File.separator);
        this.workingTxt = new TextField(20);
        this.workingTxt.setText("." + File.separator);

        gbcon.gridx = 1;
        gbcon.gridy = 0;

        // gbcon.gridx = 4;
        gbl.setConstraints(this.languageTxt, gbcon);
        add(this.languageTxt);

        gbcon.gridx = 1;
        gbcon.gridy = 1;
        gbl.setConstraints(this.resourcesTxt, gbcon);
        add(this.resourcesTxt);

        gbcon.gridx = 4;
        gbcon.gridy = 0;
        gbl.setConstraints(this.xmlTxt, gbcon);
        add(this.xmlTxt);

        gbcon.gridx = 1;
        gbcon.gridy = 2;
        gbl.setConstraints(this.outputTxt, gbcon);
        add(this.outputTxt);

        gbcon.gridx = 4;
        gbcon.gridy = 1;
        gbl.setConstraints(this.workingTxt, gbcon);
        add(this.workingTxt);
    }

    private void initDialogs(GridBagLayout gbl, GridBagConstraints gbcon) {

        this.aspectDial = new Button("..");
        this.aspectDial.setActionCommand(Commands.ASPECT.toString());
        this.languageDial = new Button("..");
        this.languageDial.setActionCommand(Commands.LANGUAGE.toString());
        this.resourcesDial = new Button("..");
        this.resourcesDial.setActionCommand(Commands.RESOURCE.toString());
        this.xmlDial = new Button("..");
        this.xmlDial.setActionCommand(Commands.XML.toString());
        this.outputDial = new Button("..");
        this.outputDial.setActionCommand(Commands.OUTPUT.toString());
        this.workingDial = new Button("..");
        this.workingDial.setActionCommand(Commands.WORKSPACE.toString());

        // gbcon.gridx = 2; gbcon.gridy = 0;
        // gbl.setConstraints(aspectDial, gbcon);
        // add(aspectDial);
        gbcon.gridx = 2;
        gbcon.gridy = 1;
        gbl.setConstraints(this.resourcesDial, gbcon);
        add(this.resourcesDial);
        gbcon.gridx = 5;
        gbcon.gridy = 0;
        gbl.setConstraints(this.xmlDial, gbcon);
        add(this.xmlDial);
        gbcon.gridx = 2;
        gbcon.gridy = 2;
        gbl.setConstraints(this.outputDial, gbcon);
        add(this.outputDial);
        gbcon.gridx = 5;
        gbcon.gridy = 1;
        gbl.setConstraints(this.workingDial, gbcon);
        add(this.workingDial);
    }

    private void initLabels(GridBagLayout gbl, GridBagConstraints gbcon) {
        this.aspectLabel = new Label("Aspect File:");
        this.languageLabel = new Label("Language:");
        this.resourcesLabel = new Label("Resources:");
        this.xmlLabel = new Label("XML dir:");
        this.outputLabel = new Label("Output dir:");
        this.workingLabel = new Label("Working dir:");
        gbcon.gridx = 0;
        gbcon.gridy = 0;
        // gbcon.gridwidth = 2;
        gbcon.anchor = GridBagConstraints.EAST;
        // gbcon.weighty = 0.1;
        // gbl.setConstraints(aspectLabel, gbcon);
        // add(aspectLabel);

        // gbcon.gridx = 3;
        gbl.setConstraints(this.languageLabel, gbcon);
        add(this.languageLabel);

        gbcon.gridx = 0;
        gbcon.gridy = 0;
        gbl.setConstraints(this.resourcesLabel, gbcon);
        add(this.resourcesLabel);

        gbcon.gridx = 3;
        gbl.setConstraints(this.xmlLabel, gbcon);
        add(this.xmlLabel);

        gbcon.gridx = 0;
        gbcon.gridy = 1;
        gbl.setConstraints(this.outputLabel, gbcon);
        add(this.outputLabel);

        gbcon.gridx = 3;
        gbl.setConstraints(this.workingLabel, gbcon);
        add(this.workingLabel);
    }

    JFileChooser fc = new JFileChooser("File selection");

    @Override
    public boolean action(Event evt, Object arg) {
        // new StringBuffer(50);

        if (evt.target instanceof Button) {
            final Button bt = (Button) evt.target;
            final String command = bt.getActionCommand();
            this.fc.setCurrentDirectory(new File("."));
            switch (Commands.valueOf(command)) {
            case ASPECT:
                FileUtils.processJFileChooser(this.app, this.fc, FileUtils.aspectFilter, this.aspectTxt,
                        JFileChooser.FILES_ONLY);
                break;
            case RESOURCE:
                FileUtils.processJFileChooser(this.app, this.fc, FileUtils.txtFilter, this.resourcesTxt,
                        JFileChooser.FILES_ONLY);
                break;
            case XML:
                FileUtils.processJFileChooser(this.app, this.fc, FileUtils.dirXMLFilter, this.xmlTxt,
                        JFileChooser.DIRECTORIES_ONLY);
                break;
            case OUTPUT:
                FileUtils.processJFileChooser(this.app, this.fc, FileUtils.dirFilter, this.outputTxt,
                        JFileChooser.DIRECTORIES_ONLY);
                break;
            case WORKSPACE:
                FileUtils.processJFileChooser(this.app, this.fc, FileUtils.dirFilter, this.workingTxt,
                        JFileChooser.DIRECTORIES_ONLY);
                break;
            case LOAD:
                final String key = this.loader.getSelectedItem();
                final String example = this.examples.get(key);
                this.app.console.code.setText(example);
                break;
            case CLEAR:
                break;
            case GENERATE:
                break;
            case LANGUAGE:
                break;
            case OPEN:
                break;
            case SAVE:
                break;
            case SAVEAS:
                break;
            default:
                break;
            }
            return true;
        }
        return false;
    }
}

public class GUI extends Applet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    static final Color BGCOLOR = new Color(192, 192, 192);
    static final int window_Width = 1000;
    static final int window_Height = 650;
    static final int areaHeight = (int) (GUI.window_Height / 10 * 0.40);
    static final int areaWidth = (int) (GUI.window_Width / 10 * 1.2);

    Label title = new Label("Meta-Aspect Language");
    TopPanel topPanel;
    Console console;
    LowPanel lowPanel;

    @Override
    public void init() {

        setBackground(GUI.BGCOLOR);

        this.topPanel = new TopPanel(this);
        this.console = new Console(this);
        this.lowPanel = new LowPanel(this);
        final GridBagLayout gbl = new GridBagLayout();

        final GridBagConstraints gbcon = new GridBagConstraints();
        setLayout(gbl);
        gbcon.gridx = 0;
        gbcon.gridy = 0;
        // gbcon.gridwidth = 2;
        gbcon.fill = GridBagConstraints.BOTH;

        gbcon.weighty = 0.2;
        gbcon.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(this.topPanel, gbcon);
        add(this.topPanel);
        this.topPanel.validate();
        gbcon.gridy = 1;
        gbcon.weighty = 0.1;
        gbcon.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(this.console, gbcon);
        add(this.console);
        this.console.validate();
        gbcon.gridy = 2;
        gbcon.weighty = 0.1;
        gbcon.gridx = 0;
        gbcon.anchor = GridBagConstraints.WEST;
        gbcon.insets = new Insets(0, 0, 0, GUI.window_Width / 2);
        gbl.setConstraints(this.lowPanel, gbcon);
        add(this.lowPanel);
        this.lowPanel.validate();

        setSize(GUI.window_Width, GUI.window_Height);
        setMinimumSize(new Dimension(GUI.window_Width, GUI.window_Height));
        addComponentListener(new ComponentListener() {

            @Override
            public void componentShown(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void componentResized(ComponentEvent e) {
                // determine the size of the new JFrame
                final GUI gui = (GUI) e.getComponent();
                final Dimension oldDim = gui.getSize();

                /*
                 * resize the JScrollPane to be 20 pixels less in width and 70
                 * pixels less in height that new size
                 */
                final int width = (int) oldDim.getWidth();
                final int height = (int) oldDim.getHeight();

                // new Dimension(width, height);

                gui.setSize(new Dimension(width, height));

            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // TODO Auto-generated method stub

            }
        });

    }
}
