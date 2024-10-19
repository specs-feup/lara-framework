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

package pt.up.fe.specs.lara.commonlang;

import pt.up.fe.specs.util.providers.ResourceProvider;

/**
 * @author Joao Bispo
 *
 */
public enum CommonLangResource implements ResourceProvider {
    JOINPOINTS("joinPointModel.xml"),
    ARTIFACTS("artifacts.xml"),
    ACTIONS("actionModel.xml");

    private final String resource;

    private static final String basePackage = "pt/up/fe/specs/lara/commonlang/specs/";

    /**
     * @param resource
     */
    private CommonLangResource(String resource) {
        this.resource = CommonLangResource.basePackage + resource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.up.fe.specs.util.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getResource() {
        return resource;
    }

}
