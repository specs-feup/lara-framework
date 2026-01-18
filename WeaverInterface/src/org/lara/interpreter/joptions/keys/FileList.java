/**
 * Copyright 2016 SPeCS.
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

package org.lara.interpreter.joptions.keys;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import pt.up.fe.specs.util.SpecsIo;
import tdrc.utils.StringUtils;

public class FileList implements Iterable<File> {
    private final List<File> fileList;

    private FileList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a StringList with the file names from the files on the list passed as
     * parameter.
     */
    public FileList(Collection<File> stringFile) {
        fileList = new ArrayList<>();
        fileList.addAll(stringFile);
    }

    /**
     * Helper constructor with variadic inputs.
     *
     */
    public static FileList newInstance(File... values) {
        return new FileList(Arrays.asList(values));
    }

    public static FileList newInstance(Collection<File> values) {
        return new FileList(values);
    }

    public static FileList newInstance(String fileListStrs) {
        if (fileListStrs == null) {
            return new FileList();
        }
        List<File> files = decode(fileListStrs);
        return new FileList(files);
    }

    private static List<File> decode(String fileListStrs) {
        String[] file = SpecsIo.splitPaths(fileListStrs);
        List<File> files = new ArrayList<>();
        for (String string : file) {
            files.add(new File(string));
        }
        return files;
    }

    public String encode() {
        return StringUtils.join(fileList, SpecsIo::getCanonicalPath, SpecsIo.getUniversalPathSeparator());
    }

    public static FileList newInstance() {
        return new FileList();
    }

    @Override
    public Iterator<File> iterator() {
        return fileList.iterator();
    }

    @Override
    public String toString() {
        return fileList.stream()
                .map(File::toString)
                .collect(Collectors.joining(SpecsIo.getUniversalPathSeparator()));
    }

    @Override
    public int hashCode() {
        return fileList.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileList other = (FileList) obj;
        return fileList.equals(other.fileList);
    }

    public List<File> getFiles() {
        return fileList;
    }

    public boolean isEmpty() {
        return fileList.isEmpty();
    }
}
