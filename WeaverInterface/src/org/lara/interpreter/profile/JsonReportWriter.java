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

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.lara.interpreter.exception.ReportException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

/**
 * Json approach for the {@link ReportWriter}.
 * 
 * @author tiago
 *
 */
public class JsonReportWriter implements ReportWriter, Closeable {
    private Gson gson;
    private JsonWriter jsonWriter;
    private StringWriter writer;

    public JsonReportWriter() {
        writer = new StringWriter();
        gson = new GsonBuilder().create();
        try {
            jsonWriter = gson.newJsonWriter(writer);
        } catch (IOException e) {
            throw new ReportException("Could not create report file", e);
        }
    }

    @Override
    public JsonReportWriter beginObject() {
        try {
            jsonWriter.beginObject();
        } catch (IOException e) {
            throw new ReportException("Could not start object", e);
        }
        return this;
    }

    @Override
    public JsonReportWriter beginArray() {
        try {
            jsonWriter.beginArray();
        } catch (IOException e) {
            throw new ReportException("Could not start array", e);
        }
        return this;
    }

    @Override
    public JsonReportWriter endObject() {
        try {
            jsonWriter.endObject();
        } catch (IOException e) {
            throw new ReportException("Could not end object", e);
        }
        return this;
    }

    @Override
    public JsonReportWriter endArray() {
        try {
            jsonWriter.endArray();
        } catch (IOException e) {
            throw new ReportException("Could not end array", e);
        }
        return this;
    }

    @Override
    public JsonReportWriter report(String name, Object obj) {
        try {
            jsonWriter.name(name);
            String toJson = gson.toJson(obj);
            jsonWriter.jsonValue(toJson);
        } catch (IOException e) {
            throw new ReportException("Could not write the report", name, e);
        }
        return this;
    }

    @Override
    public JsonReportWriter report(String name, long value) {
        try {
            jsonWriter.name(name);
            jsonWriter.value(value);
        } catch (IOException e) {
            throw new ReportException("Could not write the report", name, e);
        }
        return this;
    }

    @Override
    public JsonReportWriter report(String name, double value) {
        try {
            jsonWriter.name(name);
            jsonWriter.value(value);
        } catch (IOException e) {
            throw new ReportException("Could not write the report", name, e);
        }
        return this;
    }

    @Override
    public JsonReportWriter report(String name, boolean value) {
        try {
            jsonWriter.name(name);
            jsonWriter.value(value);
        } catch (IOException e) {
            throw new ReportException("Could not write the report", name, e);
        }
        return this;
    }

    @Override
    public JsonReportWriter report(String name, Number number) {
        try {
            jsonWriter.name(name);
            jsonWriter.value(number);
        } catch (IOException e) {
            throw new ReportException("Could not write the report", name, e);
        }
        return this;
    }

    @Override
    public JsonReportWriter report(String name, String value) {
        try {
            jsonWriter.name(name);
            jsonWriter.value(value);
        } catch (IOException e) {
            throw new ReportException("Could not write the report", name, e);
        }
        return this;
    }

    @Override
    public void close() throws IOException {

        jsonWriter.close();

    }

    @Override
    public void write(Writer writer) {
        try {
            String prettyJSON = prettyJSON();
            writer.write(prettyJSON);
        } catch (IOException e) {
            throw new ReportException("Could not write the report", e);
        }
    }

    private String prettyJSON() {
        try {
            return writer.toString();
        } catch (Exception e) {
            throw new ReportException("Could not write the report", e);
        }
    }

    @Override
    public String toString() {
        return prettyJSON();
    }

}
