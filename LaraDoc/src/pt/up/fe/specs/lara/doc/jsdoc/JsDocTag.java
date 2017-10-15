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

    // private static final String PROPERTY_TAG_NAME = "tagName";
    // private static final String PROPERTY_CONTENT = "content";

    private final Map<String, String> properties;

    public JsDocTag(String tagName) {
        this.properties = new HashMap<>();
        setValue(JsDocTagProperty.TAG_NAME, tagName);
        // this.properties.put(PROPERTY_TAG_NAME, tagName);
    }

    // public static JsDocTag(String tagName, JsDocTagProperty property, String value) {
    // JsDocTag tag = new JsDocTag(tagName);
    // tag.setValue(property, value);
    // }

    public Collection<String> getKeys() {
        return properties.keySet();
    }

    public String getValue(JsDocTagProperty property) {
        return getValue(property, null);
    }

    public String getValue(JsDocTagProperty property, String defaultValue) {
        return genericGet(property.getPropertyName(), defaultValue);
    }

    public String getTagName() {
        return getValue(JsDocTagProperty.TAG_NAME);
        // return properties.get(PROPERTY_TAG_NAME);
    }

    public JsDocTag setContent(String content) {
        setValue(JsDocTagProperty.CONTENT, content);
        return this;
    }

    public JsDocTag addContent(String value) {
        // String content = properties.getOrDefault(PROPERTY_CONTENT, "");
        String content = getValue(JsDocTagProperty.CONTENT, "");

        String newContent = content.isEmpty() ? value : content + " " + value;

        setValue(JsDocTagProperty.CONTENT, newContent);
        // properties.put(PROPERTY_CONTENT, newContent);

        return this;
    }

    // public void setType(String value) {
    //
    // }

    public JsDocTag setValue(JsDocTagProperty property, String value) {
        setValue(property.getPropertyName(), value);
        return this;
    }

    public JsDocTag setValue(String key, String value) {
        String previousValue = properties.put(key, value);
        if (previousValue != null) {
            SpecsLogs.msgInfo(
                    "Replacing previous value of property '" + key + "' in JsDoc tag '" + getTagName()
                            + "'. Current value '" + value + "', previous value '" + previousValue + "'");
        }

        return this;
    }

    private String genericGet(String key, String defaultValue) {
        String value = properties.get(key);

        return value != null ? value : defaultValue;
    }

    @Override
    public String toString() {
        return properties.toString();
    }

}
