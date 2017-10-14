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

import java.util.List;

import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;

public class LaraDocComment {

    private final String text;
    private final List<JsDocTag> tags;

    public LaraDocComment(String text, List<JsDocTag> tags) {
        this.text = text;
        this.tags = tags;
    }

    public List<JsDocTag> getTags() {
        return tags;
    }

    public String getText() {
        return text;
    }
}
