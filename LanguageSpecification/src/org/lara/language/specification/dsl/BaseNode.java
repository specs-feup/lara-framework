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

package org.lara.language.specification.dsl;

import java.util.Optional;

public abstract class BaseNode {

    private String tooltip = null;
    private boolean isDefault = false;

    public Optional<String> getToolTip() {
        return Optional.ofNullable(tooltip);
    }

    public void setToolTip(String comment) {
        tooltip = comment;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * If true, this is a default node that is already implemented and should not be
     * generated.
     * 
     * @return
     */
    public boolean isDefault() {
        return isDefault;
    }
}
