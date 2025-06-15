/**
 * Copyright 2014 SPeCS Research Group.
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

package org.lara.interpreter.weaver.interf.events.data;

import java.io.File;
import java.util.List;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.suikasoft.jOptions.Interfaces.DataStore;

public class WeaverEvent extends BaseEvent {
    private DataStore args;
    private List<File> sources;
    private String mainAspect;
    private String aspectFile;

    public WeaverEvent(Stage stage, DataStore args, String mainAspect, String aspectFile) {
        super(stage);
        setArgs(args);
        setFolder(args.get(LaraiKeys.WORKSPACE_FOLDER).getFiles());
        setMainAspect(mainAspect);
        setAspectFile(aspectFile);
    }

    /**
     * @return the folder
     */
    public List<File> getSources() {
        return sources;
    }

    /**
     * @param sources
     *                the folder to set
     */
    protected void setFolder(List<File> sources) {
        this.sources = sources;
    }

    /**
     * @return the args
     */
    public DataStore getArgs() {
        return args;
    }

    /**
     * @param args
     *             the args to set
     */
    protected void setArgs(DataStore args) {
        this.args = args;
    }

    public String getMainAspect() {
        return mainAspect;
    }

    public void setMainAspect(String mainAspect) {
        this.mainAspect = mainAspect;
    }

    public String getAspectFile() {
        return aspectFile;
    }

    public void setAspectFile(String aspectFile) {
        this.aspectFile = aspectFile;
    }

    @Override
    public String toString() {
        String ret = super.toString();
        ret += ", args: ";
        // {";
        // if (args.length != 0) {
        // ret += args[0];
        // for (int i = 1; i < args.length; i++) {
        // ret += ", " + args[i];
        // }
        // }
        // ret += "},";
        ret += args.toString();
        ret += ", folder: ";
        ret += sources;
        ret += ", mainAspect: ";
        ret += mainAspect;
        ret += ", aspectFile: ";
        ret += aspectFile;
        return ret;
    }
}
