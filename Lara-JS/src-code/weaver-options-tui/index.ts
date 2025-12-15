/**
 * Weaver Options TUI Module
 *
 * This module provides a terminal-based user interface for editing weaver configuration files.
 * It is launched by WeaverLauncher when the --edit flag is used.
 *
 * @module weaver-options-tui
 *
 * @example
 * ```typescript
 * import { launchOptionsEditor } from "./weaver-options-tui/index.js";
 *
 * const exitCode = await launchOptionsEditor("./config.xml", weaverConfig);
 * process.exit(exitCode);
 * ```
 */

export { launchOptionsEditor } from "./OptionsEditor.js";
export { ConfigService } from "./ConfigService.js";
export * from "./types.js";
