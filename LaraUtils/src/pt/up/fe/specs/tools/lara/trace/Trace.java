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

package pt.up.fe.specs.tools.lara.trace;

public class Trace {
    private static final String SEPARATOR = "@";
    private String identifier;
    private String location;

    private Trace(String identifier, String location) {
        this.setIdentifier(identifier);
        this.setLocation(location);
    }

    public static Trace newInstance(String identifier, String location) {
        return new Trace(identifier, location);
    }

    @Override
    public String toString() {
        return getIdentifier() + (location == null ? "" : (SEPARATOR + getLocation()));
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
