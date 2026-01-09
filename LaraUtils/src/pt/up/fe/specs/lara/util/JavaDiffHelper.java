/**
 * Copyright 2020 SPeCS.
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

import java.util.List;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;

import pt.up.fe.specs.util.utilities.StringLines;

public class JavaDiffHelper {

    public static String getDiff(String original, String revised) {
        // build simple lists of the lines of the two testfiles
        List<String> originalLines = StringLines.getLines(original);
        List<String> revisedLines = StringLines.getLines(revised);

        // compute the patch: this is the diffutils part
        var patch = DiffUtils.diff(originalLines, revisedLines);

        // simple output the computed patch to console
        StringBuilder builder = new StringBuilder();
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            builder.append(delta.toString()).append("\n");
        }

        return builder.toString();
    }
}
