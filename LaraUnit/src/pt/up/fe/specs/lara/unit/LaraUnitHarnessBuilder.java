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

package pt.up.fe.specs.lara.unit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class LaraUnitHarnessBuilder {

    private final File baseFolder;
    private final LaraArgs globalArguments;

    private Collection<File> filesToDelete;

    public LaraUnitHarnessBuilder(File baseFolder, LaraArgs globalArguments) {
        this.baseFolder = baseFolder;
        this.globalArguments = globalArguments;

        this.filesToDelete = new ArrayList<>();
    }

    // public Pair<File, String[]> buildTestAndArguments(File testFile) {
    // return null;
    // }

    // public File buildTest(File testFile) {
    // // TODO Auto-generated method stub
    // return null;
    // }

    /*
    public String[] buildArguments(File testFile) {
        // Create test harness
        File testHarnessFile = new File(SpecsIo.getTempFolder(), UUID.randomUUID().toString());
        filesToDelete.add(testHarnessFile);
    
        // First argument is the harness test
    
        // Add global arguments
    
        // Add test arguments
    
        // Add base folder as LARA include
    
        // Return args
    
        // LaraArgs testArguments = new LaraArgs();
        //
        // // Add lara file to test as first argument
        //
        // // Check if there is a custom args file
        // String customArgsFilename = SpecsIo.removeExtension(testFile) + LaraArgs.getArgsExtension();
        // File customArgsFile = new File(testFile.getParentFile(), customArgsFilename);
        //
        // if (customArgsFile.isFile()) {
        // testArguments = testArguments.copy();
        // globalArguments.addArgs(customArgsFile);
        // }
    
        // TODO Auto-generated method stub
        return null;
    }
    */

    public Iterable<LaraUnitHarness> buildTests(File testFile) {
        // Get all methods in test file to iterate over

        return () -> new LaraUnitHarnessIterator();
    }

    private class LaraUnitHarnessIterator implements Iterator<LaraUnitHarness> {

        @Override
        public boolean hasNext() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public LaraUnitHarness next() {

            return new LaraUnitHarness(null);
        }

    }

    // @Override
    // public void close() throws IOException {
    // // Delete temporary files
    // for (File temporaryFile : filesToDelete) {
    // SpecsIo.delete(temporaryFile);
    // }
    // }

}
