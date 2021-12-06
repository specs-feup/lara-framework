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

package pt.up.fe.specs.lara.doc.data;

import java.util.Collection;
/**
 * Represents a LARA bundle, which can have several mutually-exclusive LARA packages.
 */
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Lara Bundle, a set of mutually exclusive Lara packages.
 * 
 * @author JoaoBispo
 *
 */
public class LaraDocBundle extends LaraDocNode implements Comparable<LaraDocBundle> {

    private final String bundleName;
    private final Map<String, LaraDocPackage> bundlePackages;

    public LaraDocBundle(String bundleName) {
        this.bundleName = bundleName;
        this.bundlePackages = new HashMap<>();
    }

    @Override
    public String getId() {
        return getBundleName();
    }

    /*
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
    
        builder.append("Bundle '" + bundleName + "' -> " + bundlePackages);
    
        return builder.toString();
    }
    */

    @Override
    public String toContentString() {
        return "Bundle '" + bundleName + "'";
    }

    public LaraDocPackage getOrCreate(String packageName) {
        LaraDocPackage laraPackage = bundlePackages.get(packageName);
        if (laraPackage == null) {
            laraPackage = new LaraDocPackage(packageName);
            bundlePackages.put(packageName, laraPackage);
            addChild(laraPackage);
        }

        return laraPackage;
    }

    public Collection<LaraDocPackage> getPackages() {
        return getChildren(LaraDocPackage.class);
        // return bundlePackages.values();
    }

    public String getBundleName() {
        return bundleName;
    }

    @Override
    public int compareTo(LaraDocBundle o) {
        return this.getBundleName().compareTo(o.getBundleName());
    }

}
