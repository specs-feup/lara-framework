/**
 * Copyright 2019 SPeCS.
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

package pt.up.fe.specs.lara;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.interf.WeaverEngine;

import larai.LaraI;
import pt.up.fe.specs.lara.doc.LaraDocLauncher;
import pt.up.fe.specs.lara.unit.LaraUnitLauncher;
import pt.up.fe.specs.util.SpecsLogs;

/**
 * Utility methods what weavers can use to bootstrap execution.
 * 
 * @author JoaoBispo
 *
 */
public class WeaverLauncher {

    private final WeaverEngine engine;

    public WeaverLauncher(WeaverEngine engine) {
        this.engine = engine;
    }

    /**
     * Launch Lara Interpreter with the given engine and the input arguments. If no arguments are given a GUI is
     * launched.
     *
     * <p>
     * Has support for LaraDoc and LaraUnit.
     *
     * @param args
     * @param engine
     */
    public boolean launch(String[] args) {

        // If unit testing flag is present, run unit tester
        Optional<Boolean> unitTesterResult = runUnitTester(args);
        if (unitTesterResult.isPresent()) {
            return unitTesterResult.get();
        }

        // If doc generator flag is present, run doc generator
        Optional<Boolean> docGeneratorResult = runDocGenerator(args);
        if (docGeneratorResult.isPresent()) {
            return docGeneratorResult.get();
        }

        return LaraI.exec(args, engine);
    }

    private Optional<Boolean> runUnitTester(String[] args) {
        // Look for flag
        String unitTestingFlag = "-" + LaraiKeys.getUnitTestFlag();

        int flagIndex = IntStream.range(0, args.length)
                .filter(index -> unitTestingFlag.equals(args[index]))
                .findFirst()
                .orElse(-1);

        if (flagIndex == -1) {
            return Optional.empty();
        }

        List<String> laraUnitArgs = new ArrayList<>();
        // laraUnitArgs.add("lara-unit-weaver=" + CxxWeaver.class.getName());
        laraUnitArgs.add("--weaver");
        laraUnitArgs.add(engine.getClass().getName());

        // laraUnitArgs.add("lara-unit-weaver=" + CxxWeaver.class.getName());
        for (int i = flagIndex + 1; i < args.length; i++) {
            laraUnitArgs.add(args[i]);
        }

        SpecsLogs.debug("Launching lara-unit with flags '" + laraUnitArgs + "'");

        int unitResults = LaraUnitLauncher.execute(laraUnitArgs.toArray(new String[0]));

        return Optional.of(unitResults == 0);
    }

    private Optional<Boolean> runDocGenerator(String[] args) {
        // Look for flag
        String docGeneratorFlag = "-" + LaraiKeys.getDocGeneratorFlag();

        int flagIndex = IntStream.range(0, args.length)
                .filter(index -> docGeneratorFlag.equals(args[index]))
                .findFirst()
                .orElse(-1);

        if (flagIndex == -1) {
            return Optional.empty();
        }

        List<String> laraDocArgs = new ArrayList<>();
        laraDocArgs.add("--weaver");
        laraDocArgs.add(engine.getClass().getName());

        for (int i = flagIndex + 1; i < args.length; i++) {
            laraDocArgs.add(args[i]);
        }

        SpecsLogs.debug("Launching lara-doc with flags '" + laraDocArgs + "'");

        int docResults = LaraDocLauncher.execute(laraDocArgs.toArray(new String[0]));

        return Optional.of(docResults != -1);
    }

}
