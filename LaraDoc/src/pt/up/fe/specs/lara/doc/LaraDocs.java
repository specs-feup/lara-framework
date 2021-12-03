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

package pt.up.fe.specs.lara.doc;

import java.io.File;

import larac.LaraC;
import larai.larabundle.BundleType;
import larai.larabundle.LaraBundleProperty;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.properties.SpecsProperties;

public class LaraDocs {

    public static String getImportPath(File laraFile, File baseFolder) {
        String relativePath = SpecsIo.getRelativePath(laraFile, baseFolder);

        var extension = SpecsIo.getExtension(relativePath);

        // Relative paths are always normalized
        // Preconditions.checkArgument(relativePath.endsWith(".lara"), "Expected file to end in '.lara': " + laraFile);
        SpecsCheck.checkArgument(LaraC.isSupportedExtension(extension),
                () -> "Expected file to have extensions " + LaraC.getSupportedExtensions() + ": " + laraFile);

        // +1 because of . before extension
        int endIndex = extension.length() + 1;

        return relativePath.replace('/', '.').substring(0, relativePath.length() - endIndex);
    }

    public static String getBundleName(SpecsProperties laraBundle) {
        BundleType bundleType = BundleType.getHelper().fromValue(laraBundle.get(LaraBundleProperty.BUNDLE_TYPE));

        switch (bundleType) {
        case WEAVER:
            return "-- Weaver --";
        case CUSTOM:
            return laraBundle.get(LaraBundleProperty.BUNDLE_TAG);
        default:
            throw new RuntimeException("Case not defined:" + bundleType);
        }
    }
}
