package org.lara.interpreter.joptions.config;

import org.junit.jupiter.api.Test;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

import java.io.File;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LaraiKeys} - STORE_DEFINITION contains all keys; expectations for defaults 
 * (e.g., JAR_PATHS empty FileList).
 */
class LaraiKeysTest {

    @Test
    void testLaraFileKey() {
        DataKey<File> laraFile = LaraiKeys.LARA_FILE;
        
        assertThat(laraFile).isNotNull();
        assertThat(laraFile.getName()).isEqualTo("aspect");
        assertThat(laraFile.getLabel()).isEqualTo("Aspect");
    }

    @Test
    void testAspectArgsKey() {
        DataKey<String> aspectArgs = LaraiKeys.ASPECT_ARGS;
        
        assertThat(aspectArgs).isNotNull();
        assertThat(aspectArgs.getName()).isEqualTo("argv");
        assertThat(aspectArgs.getLabel()).isEqualTo("Aspect Arguments");
        
        // Verify custom getter is set (should trim whitespace)
        assertThat(aspectArgs.getCustomGetter()).isNotNull();
    }

    @Test
    void testWorkspaceFolderKey() {
        DataKey<FileList> workspaceFolder = LaraiKeys.WORKSPACE_FOLDER;
        
        assertThat(workspaceFolder).isNotNull();
        assertThat(workspaceFolder.getName()).isEqualTo("workspace");
        assertThat(workspaceFolder.getLabel()).isEqualTo("Sources");
    }

    @Test
    void testOutputFolderKey() {
        DataKey<File> outputFolder = LaraiKeys.OUTPUT_FOLDER;
        
        assertThat(outputFolder).isNotNull();
        assertThat(outputFolder.getName()).isEqualTo("output");
        assertThat(outputFolder.getLabel()).isEqualTo("Output Folder");
        
        // Default should be current directory
        File defaultValue = outputFolder.getDefault().get();
        assertThat(defaultValue).isEqualTo(new File("."));
    }

    @Test
    void testLogFileKey() {
        DataKey<OptionalFile> logFile = LaraiKeys.LOG_FILE;
        
        assertThat(logFile).isNotNull();
        assertThat(logFile.getName()).isEqualTo("log");
        assertThat(logFile.getLabel()).isEqualTo("Use Log File");
    }

    @Test
    void testDebugModeKey() {
        DataKey<Boolean> debugMode = LaraiKeys.DEBUG_MODE;
        
        assertThat(debugMode).isNotNull();
        assertThat(debugMode.getName()).isEqualTo("debug");
        assertThat(debugMode.getLabel()).isEqualTo("Debug Mode");
    }

    @Test
    void testJarPathsKey() {
        DataKey<FileList> jarPaths = LaraiKeys.JAR_PATHS;
        
        assertThat(jarPaths).isNotNull();
        assertThat(jarPaths.getName()).isEqualTo("jarPaths");
        assertThat(jarPaths.getLabel()).isEqualTo("Paths to JARs");
        
        // Default should be empty FileList
        FileList defaultValue = jarPaths.getDefault().get();
        assertThat(defaultValue.isEmpty()).isTrue();
    }

    @Test
    void testShowHelpKey() {
        DataKey<Boolean> showHelp = LaraiKeys.SHOW_HELP;
        
        assertThat(showHelp).isNotNull();
        assertThat(showHelp.getName()).isEqualTo("help");
        assertThat(showHelp.getLabel()).isEqualTo("Show Help");
    }

    @Test
    void testStoreDefinitionContainsAllKeys() {
        StoreDefinition storeDefinition = LaraiKeys.STORE_DEFINITION;
        
        assertThat(storeDefinition).isNotNull();
        assertThat(storeDefinition.getName()).isEqualTo("LaraI Options");
        
        // Get all keys in the store definition
        Set<DataKey<?>> keys = storeDefinition.getKeys();
        
        // Verify all expected keys are present
        assertThat(keys).contains(
                LaraiKeys.LARA_FILE,
                LaraiKeys.ASPECT_ARGS,
                LaraiKeys.WORKSPACE_FOLDER,
                LaraiKeys.OUTPUT_FOLDER,
                LaraiKeys.LOG_FILE,
                LaraiKeys.DEBUG_MODE,
                LaraiKeys.JAR_PATHS,
                LaraiKeys.SHOW_HELP
        );
        
        // Verify we have exactly the expected number of keys
        assertThat(keys).hasSize(8);
    }

    @Test
    void testKeyDefaults() {
        // Test specific default values that are documented in the plan
        
        // OUTPUT_FOLDER default should be current directory
        File outputDefault = LaraiKeys.OUTPUT_FOLDER.getDefault().get();
        assertThat(outputDefault).isEqualTo(new File("."));
        
        // JAR_PATHS default should be empty FileList
        FileList jarPathsDefault = LaraiKeys.JAR_PATHS.getDefault().get();
        assertThat(jarPathsDefault.isEmpty()).isTrue();
        
        // LOG_FILE default should be unused OptionalFile
        OptionalFile logFileDefault = LaraiKeys.LOG_FILE.getDefault().get();
        assertThat(logFileDefault.isUsed()).isFalse();
    }

    @Test
    void testKeyTypes() {
        // Verify the types of all keys are as expected
        assertThat(LaraiKeys.LARA_FILE.getKeyClass()).isEqualTo(File.class);
        assertThat(LaraiKeys.ASPECT_ARGS.getKeyClass()).isEqualTo(String.class);
        assertThat(LaraiKeys.WORKSPACE_FOLDER.getKeyClass()).isEqualTo(FileList.class);
        assertThat(LaraiKeys.OUTPUT_FOLDER.getKeyClass()).isEqualTo(File.class);
        assertThat(LaraiKeys.LOG_FILE.getKeyClass()).isEqualTo(OptionalFile.class);
        assertThat(LaraiKeys.DEBUG_MODE.getKeyClass()).isEqualTo(Boolean.class);
        assertThat(LaraiKeys.JAR_PATHS.getKeyClass()).isEqualTo(FileList.class);
        assertThat(LaraiKeys.SHOW_HELP.getKeyClass()).isEqualTo(Boolean.class);
    }

    @Test
    void testStoreDefinitionName() {
        StoreDefinition storeDefinition = LaraiKeys.STORE_DEFINITION;
        assertThat(storeDefinition.getName()).isEqualTo("LaraI Options");
    }

    @Test
    void testAllKeysHaveLabels() {
        // Verify all keys have non-empty labels
        assertThat(LaraiKeys.LARA_FILE.getLabel()).isNotEmpty();
        assertThat(LaraiKeys.ASPECT_ARGS.getLabel()).isNotEmpty();
        assertThat(LaraiKeys.WORKSPACE_FOLDER.getLabel()).isNotEmpty();
        assertThat(LaraiKeys.OUTPUT_FOLDER.getLabel()).isNotEmpty();
        assertThat(LaraiKeys.LOG_FILE.getLabel()).isNotEmpty();
        assertThat(LaraiKeys.DEBUG_MODE.getLabel()).isNotEmpty();
        assertThat(LaraiKeys.JAR_PATHS.getLabel()).isNotEmpty();
        assertThat(LaraiKeys.SHOW_HELP.getLabel()).isNotEmpty();
    }

    @Test
    void testAllKeysHaveNames() {
        // Verify all keys have non-empty names
        assertThat(LaraiKeys.LARA_FILE.getName()).isNotEmpty();
        assertThat(LaraiKeys.ASPECT_ARGS.getName()).isNotEmpty();
        assertThat(LaraiKeys.WORKSPACE_FOLDER.getName()).isNotEmpty();
        assertThat(LaraiKeys.OUTPUT_FOLDER.getName()).isNotEmpty();
        assertThat(LaraiKeys.LOG_FILE.getName()).isNotEmpty();
        assertThat(LaraiKeys.DEBUG_MODE.getName()).isNotEmpty();
        assertThat(LaraiKeys.JAR_PATHS.getName()).isNotEmpty();
        assertThat(LaraiKeys.SHOW_HELP.getName()).isNotEmpty();
    }
}