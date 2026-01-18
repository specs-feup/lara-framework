package org.lara.interpreter.joptions.config.interpreter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.joptions.keys.FileList;
import org.suikasoft.jOptions.Interfaces.DataStore;

class LaraiKeysTest {

    @Test
    @DisplayName("STORE_DEFINITION contains all expected keys and JAR_PATHS defaults to empty FileList")
    void storeDefinitionContents() {
        var def = LaraiKeys.STORE_DEFINITION;
        var names = def.getKeys().stream().map(k -> k.getName()).collect(Collectors.toSet());

        // Expected key names as defined in LaraiKeys
        Set<String> expected = Set.of(
                LaraiKeys.LARA_FILE.getName(),
                LaraiKeys.ASPECT_ARGS.getName(),
                LaraiKeys.WORKSPACE_FOLDER.getName(),
                LaraiKeys.OUTPUT_FOLDER.getName(),
                LaraiKeys.LOG_FILE.getName(),
                LaraiKeys.DEBUG_MODE.getName(),
                LaraiKeys.JAR_PATHS.getName(),
                LaraiKeys.SHOW_HELP.getName());

        assertThat(names).containsAll(expected);

        // Create a data store and verify defaults
        DataStore ds = DataStore.newInstance(def, true);
        FileList jars = ds.get(LaraiKeys.JAR_PATHS);
        assertThat(jars).isNotNull();
        assertThat(jars.isEmpty()).isTrue();
    }
}
