/**
 * Copyright 2013 SuikaSoft.
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

package pt.up.fe.specs.lara.langspec;

import pt.up.fe.specs.util.providers.ResourceProvider;

/**
 * @author Joao Bispo
 *
 */
public enum SchemaResource implements ResourceProvider {
    ACTION_SCHEMA("actionModel.xsd"),
    ATTRIBUTE_SCHEMA("artifacts.xsd"),
    JOIN_POINT_SCHEMA("joinPointModel.xsd");

    private final String resource;

    private static final String BASE_PACKAGE = "schemas/";

    /**
     * @param resource
     */
    private SchemaResource(String resource) {
        this.resource = BASE_PACKAGE + resource;
    }

    @Override
    public String getResource() {
        return resource;
    }

}
