/**
 * Copyright 2018 SPeCS.
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

package org.lara.language.specification.joinpointmodel.constructor;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.bind.ValidationEventHandler;

import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.xml.sax.SAXException;

import com.sun.xml.bind.IDResolver;

import pt.up.fe.specs.util.SpecsCollections;

public class JoinPointResolver extends IDResolver {
    // ALLOW MULTIPLE WAYS of using "joinpoint"
    private static final String BASIC_JOINPOINT_ID = "joinpoint";
    private static final String BASIC_JOINPOINT_ID2 = "joinPoint";
    private static final String BASIC_JOINPOINT_ID3 = "JoinPoint";
    private static final String BASIC_JOINPOINT_ID4 = "JOINPOINT";
    Map<String, JoinPointType> type;

    public JoinPointResolver() {
        reset();
    }

    private void reset() {
        type = SpecsCollections.newHashMap();
        type.put(BASIC_JOINPOINT_ID, JoinPointModelConstructor.defaultJoinPointType());
        type.put(BASIC_JOINPOINT_ID2, JoinPointModelConstructor.defaultJoinPointType());
        type.put(BASIC_JOINPOINT_ID3, JoinPointModelConstructor.defaultJoinPointType());
        type.put(BASIC_JOINPOINT_ID4, JoinPointModelConstructor.defaultJoinPointType());
    }

    @Override
    public void bind(String id, Object obj) throws SAXException {
        // System.out.println("BIND: " + id + " to " + obj);
        type.put(id, (JoinPointType) obj);
    }

    @Override
    public Callable<?> resolve(String id, @SuppressWarnings("rawtypes") Class targetType) throws SAXException {
        // System.out.println("RESOLVE: " + id + " to " + targetType);
        return () -> type.get(id);
    }

    @Override
    public void startDocument(ValidationEventHandler eventHandler) throws SAXException {
        // System.out.println("STARTING DOCUMENT");
        reset();
    }
}
