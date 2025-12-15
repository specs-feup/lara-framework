/**
 * Type definitions for the Weaver Options TUI.
 *
 * This module defines the core types and interfaces used throughout
 * the TUI for managing weaver configuration options.
 */

/**
 * Supported option types derived from Java DataKey types.
 * These determine how options are displayed and edited in the TUI.
 */
export type OptionType =
  | "boolean"
  | "string"
  | "integer"
  | "double"
  | "file"
  | "folder"
  | "fileList"
  | "stringList"
  | "enum"
  | "unknown";

/**
 * Metadata for a single datakey, retrieved from Java.
 * Contains all information needed to display and edit an option.
 */
export interface OptionMeta {
  /** Unique key identifier */
  keyId: string;
  /** Human-readable label (display name) */
  label: string;
  /** Optional description/help text */
  description?: string;
  /** Section/group name this key belongs to */
  section: string;
  /** The option type determining the UI control */
  type: OptionType;
  /** For enum types, the list of allowed values */
  allowedValues?: string[];
  /** The default value as a string (for display as ghost text) */
  defaultValue?: string;
  /** The Java class name for the type (for advanced type handling) */
  javaTypeName?: string;
}

/**
 * Metadata for a section grouping.
 * Sections organize related options together in the TUI.
 */
export interface SectionMeta {
  /** Section name */
  name: string;
  /** Keys in this section, in order */
  keyIds: string[];
}

/**
 * State for a single key's value.
 * Represents whether an option has been explicitly set or is unset.
 */
export type OptionValueState =
  | { kind: "unset" }
  | { kind: "set"; value: string };

/**
 * Complete state model for the TUI.
 * Contains all metadata and current working values.
 */
export interface OptionsState {
  /** Metadata for all options, keyed by keyId */
  options: Map<string, OptionMeta>;
  /** Sections in order */
  sections: SectionMeta[];
  /** Current values (working state), keyed by keyId */
  values: Map<string, OptionValueState>;
}

/**
 * Represents a loaded configuration.
 * Tracks the file source and initial state for dirty checking.
 */
export interface LoadedConfig {
  /** Path to the config file */
  filePath: string;
  /** Whether the file existed when loaded */
  fileExisted: boolean;
  /** The initial state loaded from file (for dirty checking) */
  initialState: Map<string, OptionValueState>;
  /** The metadata and sections */
  metadata: {
    options: Map<string, OptionMeta>;
    sections: SectionMeta[];
  };
}

/**
 * Result of validating a value.
 * Contains either the decoded value on success or an error message on failure.
 */
export type ValidationResult =
  | { valid: true; decodedValue: string }
  | { valid: false; error: string };

/**
 * Maps OptionType to editor component type.
 * Determines which UI component is used to edit an option.
 */
export type EditorType =
  | "toggle" // boolean
  | "select" // enum
  | "textInput" // string, integer, double, file, folder
  | "multiLine"; // fileList, stringList

/**
 * Gets the appropriate editor type for a given option type.
 *
 * @param optionType - The option type to map
 * @returns The corresponding editor type
 *
 * @example
 * ```typescript
 * getEditorType("boolean");   // "toggle"
 * getEditorType("enum");      // "select"
 * getEditorType("string");    // "textInput"
 * getEditorType("fileList");  // "multiLine"
 * ```
 */
export function getEditorType(optionType: OptionType): EditorType {
  switch (optionType) {
    case "boolean":
      return "toggle";
    case "enum":
      return "select";
    case "fileList":
    case "stringList":
      return "multiLine";
    default:
      return "textInput";
  }
}

/**
 * Compares two option value state maps to determine if there are any differences.
 *
 * @param initial - The initial state map (from when config was loaded)
 * @param current - The current state map (working state)
 * @returns `true` if any key has a different state between the two maps, `false` otherwise
 *
 * @example
 * ```typescript
 * const initial = new Map([["key1", { kind: "unset" }]]);
 * const current = new Map([["key1", { kind: "set", value: "foo" }]]);
 * isDirty(initial, current); // true
 * ```
 */
export function isDirty(
  initial: Map<string, OptionValueState>,
  current: Map<string, OptionValueState>
): boolean {
  // Check if any key in current differs from initial
  for (const [keyId, currentState] of current) {
    const initialState = initial.get(keyId);

    // Key exists in current but not in initial
    if (!initialState) {
      if (currentState.kind === "set") {
        return true;
      }
      continue;
    }

    // Compare states
    if (currentState.kind !== initialState.kind) {
      return true;
    }

    // Both are "set", compare values
    if (
      currentState.kind === "set" &&
      initialState.kind === "set" &&
      currentState.value !== initialState.value
    ) {
      return true;
    }
  }

  // Check if any key in initial is missing from current (and was set)
  for (const [keyId, initialState] of initial) {
    if (!current.has(keyId) && initialState.kind === "set") {
      return true;
    }
  }

  return false;
}
