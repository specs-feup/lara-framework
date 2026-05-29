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
 * specific language governing permissions and limitations under the License.
 */
/**
 * 
 */
package org.lara.interpreter.weaver.defaultweaver.joinpoints;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AWorkspace;

import pt.up.fe.specs.util.SpecsIo;

/**
 * @author Tiago Carvalho
 * 
 */
public class DWorkspace<Self extends DWorkspace<Self>> extends AWorkspace<Self> {

    private final Map<File, DWFolder<?>> folders;

    public DWorkspace(DefaultWeaver weaver) {
        super(null, weaver);
        folders = new HashMap<>();
    }

    @Override
    public Object getNode() {
        return super.getNode();
    }

    public void addFolder(File dir) {
        File canonicalFile = SpecsIo.getCanonicalFile(dir.getAbsoluteFile());
        if (!folders.containsKey(canonicalFile)) {
            folders.put(canonicalFile, new DWFolder<>(canonicalFile, getWeaverEngine()));
        }
    }

    public boolean containsCanonical(File dir) {
        File canonicalFile = SpecsIo.getCanonicalFile(dir.getAbsoluteFile());
        return folders.containsKey(canonicalFile);
    }

    public Collection<DWFolder<?>> getFiles() {
        return folders.values();
    }

    @Override
    public void reportImpl() {
        System.out.println("Action report");
    }
}
