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

package pt.up.fe.specs.lara.doc;

public class JsDocHelper {
    public static void main(String[] args) {
        // nodeJsExample();
    }

    /**
     * Taken from here: https://eclipsesource.com/blogs/tutorials/getting-started-with-j2v8/
     */
    // public static void firstExemple() {
    // V8 runtime = V8.createV8Runtime();
    // int result = runtime.executeIntegerScript(""
    // + "require('jsdoc')\n"
    // + "var hello = 'hello, ';\n"
    // + "var world = 'world!';\n"
    // + "hello.concat(world).length;\n");
    // System.out.println(result);
    // runtime.release();
    // }

    /**
     * Taken from here: https://eclipsesource.com/blogs/2016/07/20/running-node-js-on-the-jvm/
     */
    // public static void nodeJsExample() {
    // final NodeJS nodeJS = NodeJS.createNodeJS();
    // File nodeScript = ResourceProvider.newInstance("lara/doc/JsDoc.js")
    // .write(SpecsIo.getWorkingDir());
    //
    // nodeJS.exec(nodeScript);
    //
    // while (nodeJS.isRunning()) {
    // nodeJS.handleMessage();
    // }
    // nodeJS.release();
    // SpecsIo.delete(nodeScript);
    // }
}
