/**
 * Copyright 2015 SPeCS Research Group.
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

package org.lara.language.specification.graph;

public class JPNodeInfo {

    // Just some changes, let's see if this changes the hash of the file and there
    // is not a collision again
    // Some kind of collision is happening when pulling from master (f6427c4) to
    // pass-system-2 (9370d65)
    // Git tries to merge this file with file NodeTransformRule

    private final String label;

    public JPNodeInfo(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
