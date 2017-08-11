/**
 * Copyright 2013 SPeCS Research Group.
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

package larai;

import pt.up.fe.specs.util.providers.ResourceProvider;

/**
 * @author Joao Bispo
 *
 */
public enum JsLaraCompatibilityResource implements ResourceProvider {
    COLLECTIONS("Collections.js"),
    DOT("dot.js"),
    DWARV_UTILS("dwarvUtils.js"),
    INPUT("input.js"),
    LARA_OBJECT(
            "LaraObject.js"),
    MERGE("merge.js"),
    NAMED_ARGUMENTS("namedArguments.js"),
    OUTPUT("output.js"),
    SELECT(
            "select.js"),
    TOOL_EXECUTION("toolExecution.js"),
    UTILS("utils.js"),
    XILINX("xilinx.js"),
    ENUM("Enum.js");

    private static final String BASE_PATH = "larai/includes/scripts/";

    private final String resource;

    /**
     * @param resource
     */
    private JsLaraCompatibilityResource(String resource) {
        this.resource = JsLaraCompatibilityResource.BASE_PATH + resource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.up.fe.specs.util.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getResource() {
        // TODO Auto-generated method stub
        return resource;
    }

}
