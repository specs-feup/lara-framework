package org.lara.interpreter.weaver.fixtures;

import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

import java.io.File;
import java.util.Optional;

public final class TestDataStores {

    private TestDataStores() {}

    public static DataStore empty() {
        StoreDefinition def = new StoreDefinitionBuilder("Test DataStore").build();
        return DataStore.newInstance(def, true);
    }

    public static DataStore withWorkingFolder(File folder, boolean useRelativePaths) {
        StoreDefinition def = new StoreDefinitionBuilder("Test DataStore")
                .addKeys(JOptionKeys.CURRENT_FOLDER_PATH, JOptionKeys.USE_RELATIVE_PATHS)
                .build();
        DataStore ds = DataStore.newInstance(def, true);
        ds.set(JOptionKeys.CURRENT_FOLDER_PATH, Optional.of(folder.getAbsolutePath()));
        ds.set(JOptionKeys.USE_RELATIVE_PATHS, useRelativePaths);
        return ds;
    }
}
