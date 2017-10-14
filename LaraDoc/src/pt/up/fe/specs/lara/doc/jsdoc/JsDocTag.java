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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pt.up.fe.specs.util.SpecsLogs;

public class JsDocTag {

    private static final String PROPERTY_TAG_NAME = "tagName";
    private static final String PROPERTY_CONTENT = "content";

    private final Map<String, String> properties;

    public JsDocTag(String tagName) {
        this.properties = new HashMap<>();
        this.properties.put(PROPERTY_TAG_NAME, tagName);
    }

    public Collection<String> getKeys() {
        return properties.keySet();
    }

    public String getTagName() {
        return properties.get(PROPERTY_TAG_NAME);
    }

    public void setContent(String content) {
        genericSet(PROPERTY_CONTENT, content);
    }

    public void addContent(String value) {
        String content = properties.getOrDefault(PROPERTY_CONTENT, "");

        String newContent = content.isEmpty() ? value : content + " " + value;

        properties.put(PROPERTY_CONTENT, newContent);
    }

    private void genericSet(String key, String value) {
        String previousValue = properties.put(key, value);
        if (previousValue != null) {
            SpecsLogs.msgInfo(
                    "Replacing previous value of property '" + key + "' in JsDoc tag '" + getTagName()
                            + "'. Current value '" + value + "', previous value '" + previousValue + "'");
        }
    }

    @Override
    public String toString() {
        return properties.toString();
    }

}
