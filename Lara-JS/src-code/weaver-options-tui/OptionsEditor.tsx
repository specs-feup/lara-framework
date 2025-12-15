import React from "react";
import { render } from "ink";
import type WeaverConfiguration from "../WeaverConfiguration.js";
import { ConfigService } from "./ConfigService.js";
import { App } from "./App.js";
import type { OptionValueState, ValidationResult, LoadedConfig, OptionMeta } from "./types.js";

/**
 * Launch the TUI options editor
 *
 * This is the main entry point called by WeaverLauncher when the --edit flag is used.
 * It initializes the weaver, loads the configuration file, and renders the Ink TUI.
 *
 * @param configPath - Path to the XML config file to edit
 * @param weaverConfig - The weaver configuration (for Java setup)
 * @returns Exit code (0 for success, non-zero for errors)
 *
 * @example
 * ```typescript
 * import { launchOptionsEditor } from "./weaver-options-tui/OptionsEditor.js";
 *
 * const exitCode = await launchOptionsEditor("./config.xml", weaverConfig);
 * process.exit(exitCode);
 * ```
 */
export async function launchOptionsEditor(
  configPath: string,
  weaverConfig: WeaverConfiguration
): Promise<number> {
  try {
    // Initialize the config service (sets up JVM, loads weaver metadata)
    console.log("Initializing weaver...");
    const configService = new ConfigService(weaverConfig);
    await configService.initialize();

    // Load the config file (or create empty state if file doesn't exist)
    console.log(`Loading config from: ${configPath}`);
    const loadedConfig = await configService.loadConfigFile(configPath);

    if (!loadedConfig.fileExisted) {
      console.log("File does not exist, starting with defaults.");
    }

    // Create handlers for the TUI
    const handleSave = async (
      values: Map<string, OptionValueState>
    ): Promise<void> => {
      await configService.saveConfigFile(configPath, values);
    };

    const handleValidate = async (
      keyId: string,
      value: string
    ): Promise<ValidationResult> => {
      return await configService.validateAndDecode(keyId, value);
    };

    // Render the TUI and wait for it to exit
    return new Promise<number>((resolve) => {
      const { waitUntilExit } = render(
        <App
          loadedConfig={loadedConfig}
          onSave={handleSave}
          onValidate={handleValidate}
        />,
        {
          exitOnCtrlC: false, // We handle exit ourselves
        }
      );

      // App will call process.exit or we wait for unmount
      waitUntilExit().then(() => {
        resolve(0);
      });
    });
  } catch (error) {
    // Handle initialization errors
    console.error("Failed to initialize options editor:");
    if (error instanceof Error) {
      console.error(error.message);
      if (process.env.DEBUG) {
        console.error(error.stack);
      }
    } else {
      console.error(String(error));
    }
    return 1;
  }
}

// Re-export main components and types for external use
export { App } from "./App.js";
export type { LoadedConfig, OptionMeta, OptionValueState };
