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

package pt.up.fe.specs.lara.util;

import pt.up.fe.specs.util.utilities.Replacer;

public class ReplacerHelper {

    private final Replacer replacer;

    public ReplacerHelper(String contents) {
        replacer = new Replacer(contents);
    }

    public String replaceAll(String target, String replacement) {
        replacer.replace(target, replacement);
        return replacer.toString();
    }

    public String getString() {
        return replacer.toString();
    }

}
