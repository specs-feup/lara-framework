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

package pt.up.fe.specs.lara.doc.comments;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;

public class LaraDocComment {

    private final String text;
    private final List<JsDocTag> tags;
    private final Set<String> currentTags;

    public LaraDocComment(String text, List<JsDocTag> tags) {
        this.text = text;
        this.tags = tags;
        this.currentTags = new HashSet<>();
        tags.stream().map(JsDocTag::getTagName).forEach(currentTags::add);
    }

    @Override
    public String toString() {
        return "LaraDocComment text:'" + text + "'; tags: " + tags;
    }

    public List<JsDocTag> getTags() {
        return tags;
    }

    public String getText() {
        return text;
    }

    public LaraDocComment addTag(JsDocTag tag) {
        tags.add(tag);
        this.currentTags.add(tag.getTagName());
        return this;
    }

    public LaraDocComment addTagIfMissing(JsDocTag tag) {
        if (currentTags.contains(tag.getTagName())) {
            return this;
        }

        return addTag(tag);
    }
}
