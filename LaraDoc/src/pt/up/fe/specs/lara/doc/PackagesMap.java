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

package pt.up.fe.specs.lara.doc;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.collections.MultiMap;

public class PackagesMap {

    private final MultiMap<String, File> packageFolders;

    public PackagesMap(MultiMap<String, File> packageFolders) {
        // this.packageFolders = new MultiMap<>();
        this.packageFolders = packageFolders;
    }

    public MultiMap<String, File> getPackageFolders() {
        return packageFolders;
    }

    public static PackagesMap decode(String contents) {
        // Use Gson to decode string
        Type type = new TypeToken<Map<String, String[]>>() {
        }.getType();

        // System.out.println("ESCAPED CONTENTS:" + SpecsStrings.escapeJson(contents, true));
        Map<String, String[]> argsMap = new Gson()
                .fromJson(SpecsStrings.escapeJson(contents, true), type);

        // Map<String, String> myMap = gson.fromJson("{'k1':'apple','k2':'orange'}", type);

        // System.out.println("CONTENTS:" + contents);
        // System.out.println("MAP:" + argsMap);

        MultiMap<String, File> packagesMap = new MultiMap<>();

        for (Entry<String, String[]> entry : argsMap.entrySet()) {
            String packageName = entry.getKey();

            for (String packageFoldername : entry.getValue()) {
                File packageFolder = new File(packageFoldername);
                if (!packageFolder.isDirectory()) {
                    SpecsLogs.msgInfo(
                            "LaraDoc: could not find folder '" + packageFoldername + "' for package '" + packageName
                                    + "', ignoring");
                    continue;
                }

                packagesMap.put(packageName, packageFolder);
            }

        }

        return new PackagesMap(packagesMap);

        // PackagesMap result = new PackagesMap(packagesMap);
        // System.out.println("RESULT:" + result);
        // return result;
    }

    @Override
    public String toString() {
        return packageFolders.toString();
    }

}
