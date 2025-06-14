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

package org.lara.interpreter.profile;

import java.io.Writer;

public interface ReportWriter {

    public void write(Writer writer);

    /**
     * start a new object. has to closed with endObject()
     */
    public ReportWriter beginObject();

    /**
     * start a new object. has to closed with endArray()
     */
    public ReportWriter beginArray();

    /**
     * ends the previous object created
     */
    public ReportWriter endObject();

    /**
     * ends the previous array created
     */
    public ReportWriter endArray();

    /**
     * This method serializes the specified object into an equivalent report
     * representation
     * 
     */
    public ReportWriter report(String name, Object obj);

    /**
     * Add a field in the report with the given name and a long value
     * 
     */
    public ReportWriter report(String name, long value);

    /**
     * Add a field in the report with the given name and a double value
     */
    public ReportWriter report(String name, double value);

    /**
     * Add a field in the report with the given name and a boolean value
     */
    public ReportWriter report(String name, boolean value);

    /**
     * Add a field in the report with the given name and a Number value. This method
     * does not allow NaN or Infinity
     */
    public ReportWriter report(String name, Number number);

    /**
     * Add a field in the report with the given name and a string value
     */
    public ReportWriter report(String name, String value);

}
