/*
 * Copyright 2013 SPeCS.
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
package org.lara.interpreter.utils;

import java.util.ArrayList;
import java.util.List;

import org.dojo.jsl.parser.ast.utils.LARACConstantPool;
import org.lara.interpreter.weaver.joinpoint.LaraJoinPoint;

public class SelectUtils {

    // after the join select, create function to get join point chain list
    public static LaraJoinPoint join(LaraJoinPoint a, LaraJoinPoint b) {
	if (a == null || b == null) {
	    return null;
	}

	final List<LaraJoinPoint> aChildren = a.getChildren();
	final List<LaraJoinPoint> bChildren = b.getChildren();

	if (aChildren.size() == 0 || bChildren.size() == 0) {
	    return null;
	}
	String aAlias = aChildren.get(0).getClassAlias();
	String bAlias = bChildren.get(0).getClassAlias();

	if (aAlias.contains(LARACConstantPool.HIDDEN_TAG)) {
	    aAlias = aAlias.substring(0, aAlias.indexOf(LARACConstantPool.HIDDEN_TAG));
	}
	if (bAlias.contains(LARACConstantPool.HIDDEN_TAG)) {
	    bAlias = bAlias.substring(0, bAlias.indexOf(LARACConstantPool.HIDDEN_TAG));
	}

	if (aAlias.equals(bAlias)) {

	    LaraJoinPoint c = null;
	    for (final LaraJoinPoint aChild : aChildren) {

		for (final LaraJoinPoint bChild : bChildren) {

		    if (aChild.getReference().same(bChild.getReference())) {

			final LaraJoinPoint cChild = join(aChild, bChild);

			if (cChild != null) {
			    if (c == null) {

				c = a.cleanClone();
			    }
			    c.addChild(cChild);

			}
			break;
		    }
		}
	    }
	    return c;
	}

	final LaraJoinPoint c = a.clone();
	final List<LaraJoinPoint> cLeaves = c.getLeaves();
	for (final LaraJoinPoint cLeaf : cLeaves) {

	    // cLeaf is no longer a leaf
	    cLeaf.addChildren(bChildren);
	}
	return c;

    }

    public static List<String> getJoinChain(List<String> leftChain, List<String> rightChain) {
	final List<String> joinChain = new ArrayList<>();
	int i = 0;
	do {
	    String left = leftChain.get(i);
	    String right = rightChain.get(i);
	    if (left.contains(LARACConstantPool.HIDDEN_TAG)) {
		left = left.substring(0, left.indexOf(LARACConstantPool.HIDDEN_TAG));
	    }
	    if (right.contains(LARACConstantPool.HIDDEN_TAG)) {
		right = right.substring(0, right.indexOf(LARACConstantPool.HIDDEN_TAG));
	    }
	    if (left.equals(right)) {
		joinChain.add(leftChain.get(i));
	    } else {
		break;
	    }
	    i++;
	} while (i < leftChain.size() && i < rightChain.size());

	for (int j = i; j < leftChain.size(); j++) {
	    joinChain.add(leftChain.get(j));
	}
	for (int j = i; j < rightChain.size(); j++) {
	    joinChain.add(rightChain.get(j));
	}
	return joinChain;
    }
}
