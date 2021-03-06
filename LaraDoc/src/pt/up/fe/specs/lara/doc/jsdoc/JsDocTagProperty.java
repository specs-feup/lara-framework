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

package pt.up.fe.specs.lara.doc.jsdoc;

public enum JsDocTagProperty {

    /**
     * Name of the tag
     */
    TAG_NAME("tagName"),
    /**
     * Text associated with the tag, any tag might have content
     */
    CONTENT("content"),
    NAME("name"),
    TYPE_NAME("type"),
    NAME_PATH("namePath"),
    OPTIONAL("optional"),
    DEFAULT_VALUE("default");

    private final String property;

    private JsDocTagProperty(String property) {
        this.property = property;
    }

    public String getPropertyName() {
        return property;
    }
}
