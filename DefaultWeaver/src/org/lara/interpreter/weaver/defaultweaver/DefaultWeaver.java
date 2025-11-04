/*
 * Copyright 2013 SPeCS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.lara.interpreter.weaver.defaultweaver;

import static org.lara.interpreter.weaver.defaultweaver.specification.DefaultWeaverResource.ACTIONS;
import static org.lara.interpreter.weaver.defaultweaver.specification.DefaultWeaverResource.ARTIFACTS;
import static org.lara.interpreter.weaver.defaultweaver.specification.DefaultWeaverResource.JOINPOINTS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lara.interpreter.weaver.defaultweaver.abstracts.weaver.ADefaultWeaver;
import org.lara.interpreter.weaver.defaultweaver.gears.TestGear;
import org.lara.interpreter.weaver.defaultweaver.joinpoints.DWorkspace;
import org.lara.interpreter.weaver.defaultweaver.options.DefaultWeaverOption;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.lara.langspec.LangSpecsXmlParser;

/**
 * Abstract Weaver Implementation for DefaultWeaver. The implementation of the abstract methods is mandatory!
 *
 * @author Lara C.
 */
public class DefaultWeaver extends ADefaultWeaver {
    // Fields
    DWorkspace root;
    private final TestGear testGear;
    private boolean property;
    private DataStore args;

    // Constructors
    public DefaultWeaver() {
        testGear = new TestGear();
    }

    /**
     * Set a file/folder in the weaver if it is valid file/folder type for the weaver.
     *
     * @param source    the file with the source code
     * @param outputDir output directory for the generated file(s)
     * @return true if the file type is valid
     */
    @Override
    public boolean begin(List<File> sources, File output, DataStore args) {

        this.args = args;
        root = new DWorkspace();
        for (File source : sources) {
            if (source.isDirectory()) {
                root.addFolder(source);
            }
        }

        return !root.getFiles().isEmpty();
    }

    /**
     * Closes the weaver and specifies the output directory location if the weaver generates new file(s)
     *
     * @return if close was successful
     */
    @Override
    public boolean close() {
        return true;
    }

    @Override
    public JoinPoint getRootJp() {
        return root;
    }

    @Override
    public List<AGear> getGears() {
        final List<AGear> gears = new ArrayList<>();
        gears.add(testGear);
        return gears;
    }

    /**
     * @return the property
     */
    public boolean isProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(boolean property) {
        this.property = property;
    }

    @Override
    public List<WeaverOption> getOptions() {

        List<WeaverOption> options = new ArrayList<>();
        options.add(DefaultWeaverOption.BOOL);
        options.add(DefaultWeaverOption.STRING);
        options.add(DefaultWeaverOption.FILE);
        return options;
    }

    /**
     * Creates the default language specification
     *
     * @return
     */
    public static LanguageSpecification createDefaultLanguageSpecification() {
        // TODO: Why validate is false?
        return LangSpecsXmlParser.parse(JOINPOINTS, ARTIFACTS, ACTIONS, false);
    }

    @Override
    protected LanguageSpecification buildLangSpecs() {
        return DefaultWeaver.createDefaultLanguageSpecification();
    }

    public void ensureThatContains(File appFolder) {
        root.addFolder(appFolder);
    }

    @Override
    public String getName() {
        return "LaraI";
    }

    public static DefaultWeaver getDefaultWeaver() {
        return (DefaultWeaver) getThreadLocalWeaver();
    }

    public DataStore getArgs() {
        return args;
    }
}
