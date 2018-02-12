/**
 * < * Copyright 2017 SPeCS.
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

package org.lara.interpreter.weaver.utils;

import java.util.Collection;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import pt.up.fe.specs.tools.lara.exception.DefaultLARAException;

/**
 * Represents the JavaScript engine used by LARA.
 * 
 * @author JoaoBispo
 *
 */
public class JsScriptEngine {

    private static final String NEW_ARRAY = "[]"; // Faster
    // private static final String NEW_ARRAY_CODE = "new Array()";

    private final ScriptEngine engine;

    public JsScriptEngine(ScriptEngine engine) {
        this.engine = engine;
    }

    // public Bindings toNativeArray(Object[] values) {
    // return Converter.toNativeArray(engine, values);
    // }

    /**
     * Converts an array of objects to a JavaScript array
     * 
     * @param values
     *            the array of values
     * @return a javascript array containing all the elements in values, with the same indexes
     */
    public Bindings newNativeArray() {
        try {
            return (Bindings) engine.eval(NEW_ARRAY);
        } catch (ScriptException e) {
            throw new DefaultLARAException("Could not create new array ", e);
        }
    }

    public Bindings toNativeArray(Object[] values) {
        Bindings bindings = newNativeArray();
        for (int i = 0; i < values.length; i++) {
            bindings.put("" + i, values[i]);
        }
        return bindings;
    }

    /**
     * Converts a list of objects to a JavaScript array
     * 
     * @param values
     *            the array of values
     * @return a javascript array containing all the elements in values, with the same indexes
     */
    public Bindings toNativeArray(Collection<? extends Object> values) {
        return toNativeArray(values.toArray());
    }

    /**
     * Converts an array of ints to a JavaScript array
     * 
     * @param values
     *            the array of values
     * @return a javascript array containing all the elements in values, with the same indexes
     */
    public Bindings toNativeArray(int[] values) {

        Object[] newObject = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newObject[i] = values[i];
        }
        return toNativeArray(newObject);
    }

    /**
     * Converts an array of floats to a JavaScript array
     * 
     * @param values
     *            the array of values
     * @return a javascript array containing all the elements in values, with the same indexes
     */
    public Bindings toNativeArray(float[] values) {

        Object[] newObject = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newObject[i] = values[i];
        }
        return toNativeArray(newObject);
    }

    /**
     * Converts an array of doubles to a JavaScript array
     * 
     * @param values
     *            the array of values
     * @return a javascript array containing all the elements in values, with the same indexes
     */
    public Bindings toNativeArray(double[] values) {

        Object[] newObject = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newObject[i] = values[i];
        }
        return toNativeArray(newObject);
    }

    /**
     * Converts an array of booleans to a JavaScript array
     * 
     * @param values
     *            the array of values
     * @return a javascript array containing all the elements in values, with the same indexes
     */
    public Bindings toNativeArray(boolean[] values) {

        Object[] newObject = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newObject[i] = values[i];
        }
        return toNativeArray(newObject);
    }

    /**
     * Converts an array of chars to a JavaScript array
     * 
     * @param values
     *            the array of values
     * @return a javascript array containing all the elements in values, with the same indexes
     */
    public Bindings toNativeArray(char[] values) {

        Object[] newObject = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newObject[i] = values[i];
        }
        return toNativeArray(newObject);
    }

    /**
     * Converts an array of bytes to a JavaScript array
     * 
     * @param values
     *            the array of values
     * @return a javascript array containing all the elements in values, with the same indexes
     */
    public Bindings toNativeArray(byte[] values) {

        Object[] newObject = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newObject[i] = values[i];
        }
        return toNativeArray(newObject);
    }

    /**
     * Converts an array of shorts to a JavaScript array
     * 
     * @param values
     *            the array of values
     * @return a javascript array containing all the elements in values, with the same indexes
     */
    public Bindings toNativeArray(short[] values) {

        Object[] newObject = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newObject[i] = values[i];
        }
        return toNativeArray(newObject);
    }

    public Object eval(String script, Bindings n) throws ScriptException {
        return engine.eval(script, n);
    }

    public Bindings createBindings() {
        return engine.createBindings();
    }

    /**
     * Based on this site: http://programmaticallyspeaking.com/nashorns-jsobject-in-context.html
     * 
     * @return
     */
    public Object getUndefined() {
        try {
            ScriptObjectMirror arrayMirror = (ScriptObjectMirror) engine.eval("[undefined]");
            return arrayMirror.getSlot(0);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    // private Object[] getArray(Object val) {
    //
    // if (!val.getClass().isArray()) {
    //
    // throw new IllegalArgumentException("the argument should be an array");
    // }
    //
    // int arrlength = Array.getLength(val);
    // Object[] outputArray = new Object[arrlength];
    // for (int i = 0; i < arrlength; ++i) {
    // outputArray[i] = Array.get(val, i);
    // }
    // return outputArray;
    // }

}
