/**
 * Copyright (c) 2021 Okaeri, Dawid Sawicki, bensku, Luís M. Sousa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

declare global {
    /**
     * Loads (parses and executes) the specified JavaScript source code
     *
     * Source can be of type:
     * - a String: the path of the source file or a URL to execute.
     * - java.lang.URL: the URL is queried for the source code to execute if the js.load-from-url option is set to true.
     * - java.io.File: the file is read for the source code to execute.
     * - a JavaScript object: the object is queried for a name and a script property, which represent the source name and code, respectively.
     * - all other types: the source is converted to a String.
     * @param script
     */
    function load(script: string | any): any;

    /**
     See more: https://github.com/oracle/graaljs/blob/master/docs/user/JavaScriptCompatibility.md
     */
    class Polyglot {
        // exports the JavaScript value under the name key (a string) to the polyglot bindings:
        static export(key: string, value: any): any;
        // imports the value identified by key (a string) from the polyglot bindings and returns it:
        static import(key: string): any;
        // parses and evaluates the sourceCode with the interpreter identified by languageId
        static eval(languageId: string, sourceCode: string | any): any;
        // parses the file sourceFileName with the interpreter identified by languageId
        static evalFile(languageId: string, sourceFileName: string | any): any;
    }

    /**
     * GraalJS and GraalVM information.
     */
    class Graal {
        /**
         * GraalJS version.
         */
        static readonly versionJS: string;

        /**
         * GraalVM version, if this is executing on GraalVM.
         */
        static readonly versionGraalVM: string | null;

        /**
         * Whether this is executed on GraalVM. If false, performance is likely
         * to be bad.
         */
        static isGraalRuntime(): boolean;
    }

    /**
     * GraalJS Java interoperability tools.
     */
    class Java {
        /**
         * Gets a Java class or throws if it cannot be found.
         * @param name Fully qualified name.
         * @returns Java class.
         */
        static type(name: string): any;

        /**
         * Greates a shallow copy of given Java array or list.
         * This is usually unnecessary, because you can use the Java arrays and lists
         * directly as well.
         * @param obj Java array or list.
         * @returns JavaScript array.
         */
        static from<T>(obj: T[]): T[];
        //static from<T>(obj: T[] | List<T>): T[];

        /**
         * Converts a JavaScript value to Java type. This is usually handled
         * automatically.
         * @param value JavaScript value.
         * @param javaType Java type to convert it to.
         * @returns Java object.
         */
        static to(value: any, javaType: any): any;

        /**
         * Checks if a value is Java object or native JavaScript value.
         * @param value Value to check.
         * @returns If it is a Java object.
         */
        static isJavaObject(value: any): boolean;

        /**
         * Checks if a value is Java class instance (type).
         * @param value Value to check.
         * @returns If it is a Java class instance.
         */
        static isType(value: any): boolean;

        /**
         * Gets type name of given Java class instance.
         * @param value Value to get type name for.
         * @returns Type name of given class instance, or undefined if a value that
         * is not a class instance was given.
         */
        static typeName(value: any): string | undefined;
    }

    /**
     * Contains all top-level packages (e.g. 'java', 'net', 'org').
     */
    const Packages: JavaPackage;

    /**
     * Contains sub-packages and Java classes.
     */
    interface JavaPackage {
        [name: string]: any;
        //[name: string]: Package | any;
    }
}

export {};
