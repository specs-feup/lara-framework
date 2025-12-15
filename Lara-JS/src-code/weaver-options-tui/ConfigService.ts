/**
 * ConfigService - Bridges TypeScript to Java for the Weaver Options TUI.
 *
 * This module provides the configuration service that interacts with Java's
 * jOptions framework to load/save weaver configurations and retrieve option
 * metadata from the store definition.
 */

import fs from "fs";
import path from "path";
import java from "java";
import type WeaverConfiguration from "../WeaverConfiguration.js";
import type {
  OptionMeta,
  SectionMeta,
  OptionValueState,
  ValidationResult,
  LoadedConfig,
  OptionType,
} from "./types.js";

// Configure java async options (matches Weaver.ts pattern)
java.asyncOptions = {
  asyncSuffix: "Async",
  syncSuffix: "",
  promiseSuffix: "P",
};

/**
 * Service for managing weaver configuration through Java interop.
 *
 * Provides methods to:
 * - Initialize the JVM and load weaver metadata
 * - Load and save configuration files
 * - Validate and decode option values
 */
export class ConfigService {
  private config: WeaverConfiguration;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private javaWeaver: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private storeDefinition: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private xmlPersistence: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private dataStore: any;
  private initialized: boolean = false;

  // Java class references (cached after initialization)
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private JavaFile: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private JavaDataStore: any;

  /**
   * Creates a new ConfigService instance.
   *
   * @param config - The weaver configuration specifying JARs and weaver class
   */
  constructor(config: WeaverConfiguration) {
    this.config = config;
  }

  /**
   * Sets up the Java environment by adding JARs to the classpath.
   *
   * @param sourceDir - Directory containing JAR files
   */
  private async setupJavaEnvironment(sourceDir: string): Promise<void> {
    const files = fs.readdirSync(sourceDir, { recursive: true });

    for (const file of files) {
      if (typeof file === "string") {
        if (file.endsWith(".jar")) {
          java.classpath.push(path.join(sourceDir, file));
        }
      } else {
        throw new Error(
          `Returned a Buffer instead of a string for path: ${file.toString()}.`
        );
      }
    }

    // Add JVM options to open modules for reflection (needed by XStream/jOptions)
    // These must be added before ensureJvm() is called
    const moduleOptions = [
      "--add-opens=java.base/java.util=ALL-UNNAMED",
      "--add-opens=java.base/java.lang=ALL-UNNAMED",
      "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
      "--add-opens=java.base/java.text=ALL-UNNAMED",
      "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED",
    ];

    for (const option of moduleOptions) {
      if (!java.options.includes(option)) {
        java.options.push(option);
      }
    }

    await java.ensureJvm();
  }

  /**
   * Initializes the service by setting up the JVM and loading weaver metadata.
   *
   * This must be called before any other methods.
   *
   * @throws Error if initialization fails
   */
  async initialize(): Promise<void> {
    if (this.initialized) {
      return;
    }

    // Setup JVM with JARs from the weaver's jar path
    await this.setupJavaEnvironment(this.config.jarPath);

    // Run standard initialization for proper print formatting
    const JavaSpecsSystem = java.import("pt.up.fe.specs.util.SpecsSystem");
    JavaSpecsSystem.programStandardInit();

    // Import required Java classes
    this.JavaFile = java.import("java.io.File");
    this.JavaDataStore = java.import(
      "org.suikasoft.jOptions.Interfaces.DataStore"
    );
    const JavaLaraI = java.import("larai.LaraI");
    const JavaXmlPersistence = java.import(
      "org.suikasoft.jOptions.persistence.XmlPersistence"
    );

    // Import and instantiate the weaver class
    const JavaWeaverClass = java.import(this.config.javaWeaverQualifiedName);
    this.javaWeaver = new JavaWeaverClass();
    this.javaWeaver.setWeaver();

    // Get the store definition from LaraI (includes both LaraI and weaver-specific keys)
    this.storeDefinition = JavaLaraI.getStoreDefinition(this.javaWeaver);

    // Create XmlPersistence for load/save operations
    this.xmlPersistence = new JavaXmlPersistence(this.storeDefinition);

    // Create a default DataStore from the store definition
    this.dataStore = await this.JavaDataStore.newInstanceP(this.storeDefinition);

    this.initialized = true;
  }

  /**
   * Ensures the service is initialized before performing operations.
   *
   * @throws Error if the service has not been initialized
   */
  private ensureInitialized(): void {
    if (!this.initialized) {
      throw new Error(
        "ConfigService not initialized. Call initialize() first."
      );
    }
  }

  /**
   * Loads metadata for all options from the Java store definition.
   *
   * @returns Object containing options map and sections array
   */
  async loadMetadata(): Promise<{
    options: Map<string, OptionMeta>;
    sections: SectionMeta[];
  }> {
    this.ensureInitialized();

    const options = new Map<string, OptionMeta>();
    const sections: SectionMeta[] = [];

    // Get sections from store definition
    const javaSections = this.storeDefinition.getSections();
    const sectionsSize: number = javaSections.size();

    for (let i = 0; i < sectionsSize; i++) {
      const section = javaSections.get(i);
      const sectionNameOpt = section.getName();
      const sectionName: string = sectionNameOpt.isPresent()
        ? sectionNameOpt.get().toString()
        : `Section ${i + 1}`;

      const keyIds: string[] = [];

      // Get keys from section
      const keys = section.getKeys();
      const keysSize: number = keys.size();

      for (let j = 0; j < keysSize; j++) {
        const key = keys.get(j);
        const keyId: string = key.getName();
        const label: string = key.getLabel() || keyId;
        const valueClass = key.getValueClass();
        const javaTypeName: string = valueClass.getName();

        // Get default value as string if available
        let defaultValue: string | undefined;
        const defaultOpt = key.getDefault();
        if (defaultOpt.isPresent()) {
          const defVal = defaultOpt.get();
          if (defVal !== null && defVal !== undefined) {
            // Try to encode using the key's decoder if available
            const decoderOpt = key.getDecoder();
            if (decoderOpt.isPresent()) {
              try {
                defaultValue = decoderOpt.get().encode(defVal);
              } catch {
                defaultValue = String(defVal);
              }
            } else {
              defaultValue = String(defVal);
            }
          }
        }

        // Map Java type to OptionType
        const type = this.mapJavaTypeToOptionType(javaTypeName, key);

        // Get allowed values for enum types
        const allowedValues = this.getEnumValues(key, valueClass);

        const optionMeta: OptionMeta = {
          keyId,
          label,
          section: sectionName,
          type,
          defaultValue,
          javaTypeName,
          ...(allowedValues && { allowedValues }),
        };

        options.set(keyId, optionMeta);
        keyIds.push(keyId);
      }

      sections.push({
        name: sectionName,
        keyIds,
      });
    }

    return { options, sections };
  }

  /**
   * Loads a configuration file and returns the loaded state.
   *
   * @param filePath - Path to the configuration file
   * @returns LoadedConfig with file info, initial state, and metadata
   */
  async loadConfigFile(filePath: string): Promise<LoadedConfig> {
    this.ensureInitialized();

    const file = new this.JavaFile(filePath);
    const fileExisted: boolean = file.exists();

    // Load metadata first
    const metadata = await this.loadMetadata();

    // Initialize values as all unset
    const initialState = new Map<string, OptionValueState>();
    for (const keyId of metadata.options.keys()) {
      initialState.set(keyId, { kind: "unset" });
    }

    // If file exists, load values from it
    if (fileExisted) {
      try {
        const loadedDataStore = this.xmlPersistence.loadData(file);

        // Get keys with values from the loaded datastore
        const keysWithValues = loadedDataStore.getKeysWithValues();
        const iterator = keysWithValues.iterator();

        while (iterator.hasNext()) {
          const keyName: string = iterator.next().toString();

          // Only process keys that are in our metadata
          if (metadata.options.has(keyName)) {
            const value = loadedDataStore.get(keyName);
            if (value !== null && value !== undefined) {
              // Try to encode the value as a string
              let stringValue: string;
              try {
                const keyDef = this.storeDefinition.getKey(keyName);
                const decoderOpt = keyDef.getDecoder();
                if (decoderOpt.isPresent()) {
                  stringValue = decoderOpt.get().encode(value);
                } else {
                  stringValue = String(value);
                }
              } catch {
                stringValue = String(value);
              }

              initialState.set(keyName, { kind: "set", value: stringValue });
            }
          }
        }
      } catch (error) {
        // Log error but continue with empty state
        console.error(`Failed to load config file: ${error}`);
      }
    }

    return { filePath, fileExisted, initialState, metadata };
  }

  /**
   * Saves the current configuration to a file.
   *
   * Only options in the "set" state are written to the file.
   *
   * @param filePath - Path to save the configuration file
   * @param values - Map of option values to save
   */
  async saveConfigFile(
    filePath: string,
    values: Map<string, OptionValueState>
  ): Promise<void> {
    this.ensureInitialized();

    // Create a new DataStore from the store definition
    const dataStoreToSave = await this.JavaDataStore.newInstanceP(
      this.storeDefinition
    );

    // Only add keys that are in "set" state
    for (const [keyId, state] of values) {
      if (state.kind === "set") {
        try {
          // Get the DataKey from store definition
          const key = this.storeDefinition.getKey(keyId);

          // Decode the string value using the key's decoder
          const decoderOpt = key.getDecoder();
          if (decoderOpt.isPresent()) {
            const decodedValue = decoderOpt.get().decode(state.value);
            dataStoreToSave.setRaw(key, decodedValue);
          } else {
            // No decoder, try to set as raw string (may fail for non-string types)
            console.warn(
              `No decoder for key '${keyId}', attempting raw string set`
            );
            dataStoreToSave.setRaw(key, state.value);
          }
        } catch (error) {
          console.error(`Failed to set value for key '${keyId}': ${error}`);
        }
      }
    }

    // Save the datastore to file
    const file = new this.JavaFile(filePath);
    this.xmlPersistence.saveData(file, dataStoreToSave, false);
  }

  /**
   * Validates and decodes a raw string value for a given key.
   *
   * @param keyId - The key identifier
   * @param rawValue - The raw string value to validate
   * @returns ValidationResult indicating success or failure
   */
  async validateAndDecode(
    keyId: string,
    rawValue: string
  ): Promise<ValidationResult> {
    this.ensureInitialized();

    try {
      // Get the DataKey from store definition
      const key = this.storeDefinition.getKey(keyId);

      // Get its decoder
      const decoderOpt = key.getDecoder();
      if (decoderOpt.isEmpty()) {
        // No decoder available, assume valid
        return { valid: true, decodedValue: rawValue };
      }

      // Try to decode the value
      const decoder = decoderOpt.get();
      const decodedValue = decoder.decode(rawValue);

      // Re-encode to get the canonical string representation
      const encodedValue: string = decoder.encode(decodedValue);

      return { valid: true, decodedValue: encodedValue };
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : String(error);
      return { valid: false, error: `Invalid value: ${errorMessage}` };
    }
  }

  /**
   * Maps a Java type name to an OptionType.
   *
   * @param javaTypeName - The fully qualified Java class name
   * @param key - The DataKey instance (for additional type checking)
   * @returns The corresponding OptionType
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private mapJavaTypeToOptionType(javaTypeName: string, key: any): OptionType {
    // Boolean types
    if (
      javaTypeName === "java.lang.Boolean" ||
      javaTypeName === "boolean"
    ) {
      return "boolean";
    }

    // String types
    if (javaTypeName === "java.lang.String") {
      return "string";
    }

    // Integer types
    if (
      javaTypeName === "java.lang.Integer" ||
      javaTypeName === "int" ||
      javaTypeName === "java.lang.Long" ||
      javaTypeName === "long"
    ) {
      return "integer";
    }

    // Double/Float types
    if (
      javaTypeName === "java.lang.Double" ||
      javaTypeName === "double" ||
      javaTypeName === "java.lang.Float" ||
      javaTypeName === "float"
    ) {
      return "double";
    }

    // File type - check if it's a folder or file
    if (javaTypeName === "java.io.File") {
      // Check for extra data that might indicate folder
      try {
        const extraDataOpt = key.getExtraData();
        if (extraDataOpt.isPresent()) {
          const extraData = extraDataOpt.get();
          // Check if isFolder method exists and returns true
          if (typeof extraData.isFolder === "function" && extraData.isFolder()) {
            return "folder";
          }
        }
      } catch {
        // Ignore errors checking extra data
      }
      return "file";
    }

    // FileList type
    if (
      javaTypeName === "org.lara.interpreter.joptions.keys.FileList" ||
      javaTypeName.endsWith("FileList")
    ) {
      return "fileList";
    }

    // StringList type
    if (
      javaTypeName === "java.util.List" ||
      javaTypeName.includes("StringList") ||
      javaTypeName.endsWith("List<String>")
    ) {
      // Check if it's specifically a string list via extra data or decoder
      return "stringList";
    }

    // Check if it's an enum
    try {
      const valueClass = key.getValueClass();
      if (valueClass.isEnum()) {
        return "enum";
      }
    } catch {
      // Ignore errors checking enum
    }

    return "unknown";
  }

  /**
   * Gets the allowed values for an enum type.
   *
   * @param key - The DataKey instance
   * @param valueClass - The Java Class object for the value type
   * @returns Array of enum value strings, or undefined if not an enum
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private getEnumValues(key: any, valueClass: any): string[] | undefined {
    try {
      // Check if the value class is an enum
      if (!valueClass.isEnum()) {
        return undefined;
      }

      // Get all enum constants
      const enumConstants = valueClass.getEnumConstants();
      if (!enumConstants) {
        return undefined;
      }

      const values: string[] = [];
      for (let i = 0; i < enumConstants.length; i++) {
        const enumValue = enumConstants[i];
        values.push(enumValue.toString());
      }

      return values.length > 0 ? values : undefined;
    } catch {
      return undefined;
    }
  }

  /**
   * Gets a specific DataKey by its ID.
   *
   * @param keyId - The key identifier
   * @returns The DataKey object or null if not found
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  getDataKey(keyId: string): any {
    this.ensureInitialized();
    try {
      return this.storeDefinition.getKey(keyId);
    } catch {
      return null;
    }
  }

  /**
   * Gets the store definition.
   *
   * @returns The Java StoreDefinition object
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  getStoreDefinition(): any {
    this.ensureInitialized();
    return this.storeDefinition;
  }

  /**
   * Checks if the service has been initialized.
   *
   * @returns true if initialized, false otherwise
   */
  isInitialized(): boolean {
    return this.initialized;
  }
}

export default ConfigService;
