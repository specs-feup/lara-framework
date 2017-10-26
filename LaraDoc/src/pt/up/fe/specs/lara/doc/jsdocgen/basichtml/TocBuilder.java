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

package pt.up.fe.specs.lara.doc.jsdocgen.basichtml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;

public class TocBuilder {

    private final StringBuilder tocHtml;
    private boolean isClosed;
    private final Set<String> currentTitles;

    public TocBuilder(String title) {
        super();
        this.tocHtml = new StringBuilder();
        this.currentTitles = new HashSet<>();
        isClosed = false;
        initToc(title);
    }

    private void initToc(String title) {
        tocHtml.append("<div id='toc_container'>");
        if (title != null && !title.isEmpty()) {
            tocHtml.append("<p class='toc_title'>" + title + "</p>");
        }
        tocHtml.append("<ul class='toc_list'>");
    }

    private void closeToc() {
        tocHtml.append("</ul>");
        tocHtml.append("</div>");
    }

    public String getHtml() {
        if (!isClosed) {
            closeToc();
            isClosed = true;
        }
        return tocHtml.toString();
    }

    public void addLevelOne(String type, String id, String className) {
        if (!currentTitles.contains(type)) {
            currentTitles.add(type);
            tocHtml.append("<p class='toc_class'>" + type + ":" + "</p>");
        }

        tocHtml.append("<li class='toc_class_element'><a href='#" + id + "'>" + className + "</a></li>");
    }

    public void addSubList(List<String> ids, List<String> names, String title) {
        Preconditions.checkArgument(ids.size() == names.size(), "Expected sizes to be the same");

        int numElements = ids.size();

        // If no elements, do nothing
        if (numElements == 0) {
            return;
        }

        tocHtml.append("<ul class='toc_sublist'>");
        if (title != null && !title.isEmpty()) {
            tocHtml.append(title);
        }
        for (int i = 0; i < numElements; i++) {
            tocHtml.append("<li><em><a href='#" + ids.get(i) + "'>" + names.get(i) + "</a></em></li>");
        }
        tocHtml.append("</ul>");
    }
}
