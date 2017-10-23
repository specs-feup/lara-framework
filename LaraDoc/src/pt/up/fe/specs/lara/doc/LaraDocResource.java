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

package pt.up.fe.specs.lara.doc;

import pt.up.fe.specs.util.providers.ResourceProvider;

/**
 * @author Joao Bispo
 *
 */
public enum LaraDocResource implements ResourceProvider {

    // Doc
    INDEX_HTML("index.html"),
    JQUERY("jquery-3.2.1.min.js"),
    JS_DOC_JS("JsDoc.js"),
    STYLES_CSS("styles.css");

    private final String resource;

    private static final String BASE_PACKAGE = "lara/doc/";

    /**
     * @param resource
     */
    private LaraDocResource(String resource) {
        this.resource = BASE_PACKAGE + resource;
    }

    /* (non-Javadoc)
     * @see org.suikasoft.SharedLibrary.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getResource() {
        return resource;
    }

}
