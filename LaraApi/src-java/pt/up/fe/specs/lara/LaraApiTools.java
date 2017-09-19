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

package pt.up.fe.specs.lara;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.suikasoft.SymjaPlus.SymjaPlusUtils;

import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.parsing.arguments.ArgumentsParser;
import pt.up.fe.specs.util.system.ProcessOutputAsString;

public class LaraApiTools {

    public static ProcessOutputAsString runCommand(String command, String workingDir, boolean printToConsole,
            Long timeoutNanos) {

        ArgumentsParser argsParser = ArgumentsParser.newCommandLine();
        List<String> commandList = argsParser.parse(command);

        // System.out.println("CURRENT FOLDER:" + SpecsIo.getWorkingDir().getAbsolutePath());
        try {
            return SpecsSystem.runProcess(commandList, new File(workingDir), true, printToConsole, timeoutNanos);
        } catch (Exception e) {
            SpecsLogs.msgInfo("Problems while running command '" + command + "':" + e.getMessage());
            return new ProcessOutputAsString(-1, "", "");
        }

    }

    /**
     * 'replace' is a reserved keyword in LARA, this method allows to use the method 'String.replace'
     * 
     * @param string
     * @param oldSequence
     * @param newSequence
     * @return
     */
    public static String replacer(String string, String oldSequence, String newSequence) {
        return string.replaceAll(oldSequence, newSequence);
    }

    public static String simplifyExpression(String expression, Map<Object, Object> constants) {

        // Convert object map to strings
        Map<String, String> stringConstants = new HashMap<>();
        if (constants != null) {
            for (Entry<Object, Object> entry : constants.entrySet()) {
                stringConstants.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }

        return SymjaPlusUtils.simplify(expression, stringConstants);
    }
}

// cmd("sh dsfklsd fskld fsdkl fsjkl ") > "log.txt";