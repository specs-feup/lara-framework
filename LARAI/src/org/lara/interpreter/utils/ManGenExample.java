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

package org.lara.interpreter.utils;

import java.io.File;

public class ManGenExample {

    public static void main(String[] args) {

        ManGen mg = new ManGen("clava")
                .setVersion("1.0")
                .setShortDesc("a C/C++ LARA-based weaver")
                .setLongDesc(new File("/home/pedro/Desktop/clava_long_desc.txt"))
                .addSynopsis(ManGen.arg("aspect") + ManGen.optArg("options") + ManGen.optFlag("-c", "config"))
                .addSynopsis(ManGen.flag("-c", "config") + ManGen.optFlag("-g"));

        System.out.println(mg.generate());
    }
}
