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

package pt.up.fe.specs.lara.parser;

import org.junit.Test;

public class EsprimaParserTest {

    @Test
    public void test() {
        var parser = new EsprimaJsParser();
        var script = parser.parseScript("var a = 10; var b = 20 + a;", this.getClass().getName());
        System.out.println("SCRIPT: " + script);
    }

}