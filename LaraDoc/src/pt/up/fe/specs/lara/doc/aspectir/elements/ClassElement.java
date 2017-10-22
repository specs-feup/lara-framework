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

package pt.up.fe.specs.lara.doc.aspectir.elements;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.specs.lara.doc.comments.LaraDocComment;

public class ClassElement extends AAspectIrElement {

    private final String className;
    private final List<String> parameters;
    private final List<AssignmentElement> staticElements;
    private final List<AssignmentElement> instanceElements;

    public ClassElement(String className, List<String> parameters, LaraDocComment laraDocComment) {
        super(laraDocComment);

        this.className = className;
        this.parameters = parameters;
        this.staticElements = new ArrayList<>();
        this.instanceElements = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<AssignmentElement> getStaticElements() {
        return staticElements;
    }

    public List<AssignmentElement> getInstanceElements() {
        return instanceElements;
    }

    public void addAssignment(AssignmentElement assignment) {
        switch (assignment.getAssignmentType()) {
        case STATIC:
            staticElements.add(assignment);
            return;
        case INSTANCE:
            instanceElements.add(assignment);
            return;
        default:
            throw new RuntimeException("Assignment type not supported: " + assignment.getAssignmentType());
        }
    }
}
