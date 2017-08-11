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
package org.lara.interpreter.weaver.defaultweaver.joinpoints;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AFile;
import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AFunction;
import org.lara.interpreter.weaver.defaultweaver.report.Report;

public class DWFile extends AFile {

    private final Report report;
    private final File file;

    public DWFile(File f) {
        file = f;
        // setName(file.getName());
        // setAbsolutePath(file.getAbsolutePath());
        report = new Report();
    }

    @Override
    public List<? extends AFunction> selectFunction() {
        final List<DWFunction> functions = new ArrayList<>();
        // report.extract(getAbsolutePath());
        final Iterator<String> itr = report.AttributeValues.iterator();
        while (itr.hasNext()) {
            final String element = itr.next();
            final DWFunction function = new DWFunction(element);
            functions.add(function);
        }
        return functions;
    }

    @Override
    public String getNameImpl() {
        return file.getName();
    }

    @Override
    public String getAbsolutePathImpl() {
        // TODO Auto-generated method stub
        return file.getAbsolutePath();
    }

    @Override
    public Object getNode() {
        return getName();
    }
    // @Override
    // public boolean same(JoinPoint iJoinPoint) {
    // if (!(iJoinPoint instanceof DWFile))
    // return false;
    // return this.getName().equals(((DWFile) iJoinPoint).getName());
    // }
}
