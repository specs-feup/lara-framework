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

package org.lara.interpreter.joptions.panels.editor.utils;

import java.util.prefs.Preferences;

import org.lara.interpreter.joptions.panels.editor.EditorPanel;

public class SettingsManager {

    private static final String CONSOLE_SPLIT_FACTOR_PREFIX = "split_factor_";
    private static final String EXPLORER_SPLIT_FACTOR_PREFIX = "explorer_factor_";
    private static final String ASK_SAVE_PREFIX = "ask_save_";
    private static final String SHOW_CONSOLE_PREFIX = "show_console_";
    private static final String SHOW_LANG_SPEC_PREFIX = "show_lang_spec_";

    public Preferences prefs;
    private String keySufix;

    public SettingsManager(EditorPanel panel, String keySufix) {
        this.keySufix = keySufix;
        this.prefs = Preferences.userRoot();
    }

    /**
     * Remove preferences from system
     */
    protected void removePreferences() {

        prefs.remove(getConsoleSplitFactorSetting());
        prefs.remove(getShowConsoleSetting());
        prefs.remove(getShowLangSpecSetting());
        prefs.remove(getAskSaveSetting());
    }

    //////////////////////////////////////////////////
    /////////////// Load/Save Preferences ////////////
    //////////////////////////////////////////////////

    public void saveConsoleSplitFactor(double value) {
        prefs.putDouble(getConsoleSplitFactorSetting(), value);
    }

    public double loadConsoleSplitFactor(double defaultVale) {
        return prefs.getDouble(getConsoleSplitFactorSetting(), defaultVale);
    }

    public void saveExplorerSplitFactor(double value) {
        prefs.putDouble(getExplorerSplitFactorSetting(), value);
    }

    public double loadExplorerSplitFactor(double defaultVale) {
        return prefs.getDouble(getExplorerSplitFactorSetting(), defaultVale);
    }

    public void saveShowConsole(boolean value) {
        prefs.putBoolean(getShowConsoleSetting(), value);
    }

    public boolean loadShowConsole(boolean defaultVale) {
        return prefs.getBoolean(getShowConsoleSetting(), defaultVale);
    }

    public void saveShowLangSpec(boolean value) {
        prefs.putBoolean(getShowLangSpecSetting(), value);
    }

    public boolean loadShowLangSpec(boolean defaultVale) {
        return prefs.getBoolean(getShowLangSpecSetting(), defaultVale);
    }

    public void saveAskSave(int value) {
        prefs.putInt(getAskSaveSetting(), value);
    }

    public int loadAskSave(int defaultVale) {
        return prefs.getInt(getAskSaveSetting(), defaultVale);
    }

    /////////////////////////////////////////////////
    /////////////// Get Preferences Keys ////////////
    /////////////////////////////////////////////////
    private String getAskSaveSetting() {
        return ASK_SAVE_PREFIX + getKeySufix();
    }

    private String getConsoleSplitFactorSetting() {
        return CONSOLE_SPLIT_FACTOR_PREFIX + getKeySufix();
    }

    private String getExplorerSplitFactorSetting() {
        return EXPLORER_SPLIT_FACTOR_PREFIX + getKeySufix();
    }

    private String getShowConsoleSetting() {
        return SHOW_CONSOLE_PREFIX + getKeySufix();
    }

    private String getShowLangSpecSetting() {
        return SHOW_LANG_SPEC_PREFIX + getKeySufix();
    }

    private String getKeySufix() {
        return keySufix;
    }

}
