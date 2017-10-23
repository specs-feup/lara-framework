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

package pt.up.fe.specs.lara.doc.aspectir;

import java.util.ArrayList;
import java.util.List;

import org.lara.interpreter.aspectir.Aspect;
import org.lara.interpreter.aspectir.Aspects;
import org.lara.interpreter.aspectir.Statement;

import pt.up.fe.specs.lara.doc.comments.LaraCommentsParser;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;

/**
 * Incrementally builds an AspectIrDoc.
 * 
 * @author joaobispo
 *
 */
public class AspectIrDocBuilder {

    private final LaraCommentsParser commentParser;
    private final AspectIrParser aspectIrParser;
    private final List<AspectIrElement> aspectIrElements;

    public AspectIrDocBuilder() {
        this.commentParser = new LaraCommentsParser();
        this.aspectIrParser = new AspectIrParser();

        aspectIrElements = new ArrayList<>();
    }

    public List<AspectIrElement> getAspectIrElements() {
        return aspectIrElements;
    }

    public void parse(Aspects aspects) {
        // For each element, create LaraDocComment

        for (Aspect aspect : aspects.aspects.values()) {
            LaraDocComment laraComment = commentParser.parse(aspect.comment);

            AspectIrElement aspectIrElement = aspectIrParser.parse(aspect, laraComment);
            aspectIrElements.add(aspectIrElement);
        }

        for (Statement declaration : aspects.declarations) {
            // System.out.println("Declaration class:" +
            // declaration.getClass().getSimpleName());
            // if (declaration.name.equals("expr")) {
            // System.out.println("Declaration Name:" + declaration.name);
            // }
            // System.out.println("Declaration Name:" + declaration.name);

            LaraDocComment laraComment = commentParser.parse(declaration.comment);

            AspectIrElement aspectIrElement = aspectIrParser.parse(declaration, laraComment);
            aspectIrElements.add(aspectIrElement);
            // System.out.println("Declaration Comment:" + declaration.comment);
            // System.out.println("Declaration Desc:" + declaration.desc);

            // Extract info from the comment

            // Extract info from the declaration node
            // e.g., if function, extract param names?
        }

    }

    public AspectIrDoc build() {
        return AspectIrDoc.newInstance(aspectIrElements);
    }
}
