/**
 * Copyright 2022 SPeCS.
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

package pt.up.fe.specs.lara.util;

import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.parse.Parser;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSwing;

public class GraphHelper {

    public static void showDot(String dotGraph) {
        showDot(dotGraph, "Graph");
    }

    public static void showDot(String dotGraph, String title) {

        try {
            var g = new Parser().read(dotGraph);

            // Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("C:/Temp/clava_tests/dot.png"));
            var image = Graphviz.fromGraph(g).render(Format.PNG).toImage();
            var picIcon = new ImageIcon(image);
            JLabel picLabel = new JLabel(picIcon);

            JPanel panel = new JPanel();
            panel.add(picLabel);
            SpecsSwing.showPanel(panel, title);
        } catch (IOException e) {
            SpecsLogs.info("Could not show graph: " + e.getMessage());
        }
    }
}
