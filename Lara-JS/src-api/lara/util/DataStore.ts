import Io from "../Io.js";
import { info } from "../core/LaraCore.js";
import JavaTypes, { JavaClasses } from "./JavaTypes.js";

/**
 * Interface with Java class DataStore, used for storing arbitrary information.
 */

export default class DataStore {
  javaDataStoreInstance!: JavaClasses.DataStore;
  definition: any;
  checkKeys: boolean;
  allowedKeys: any;
  keyAliases: Record<string, string> = {};

  constructor(
    dataStore?: JavaClasses.DataStore | string | DataStore,
    definition?: any
  ) {
    if (dataStore === undefined) {
      this.javaDataStoreInstance = new this.DataStoreClass(
        "DataStore from Lara"
      );
    } else if (typeof dataStore === "string") {
      this.javaDataStoreInstance = new this.DataStoreClass(dataStore);
    } else if (dataStore instanceof DataStore) {
      this.javaDataStoreInstance = dataStore.getData();
    } else if (
      JavaTypes.instanceOf(
        dataStore,
        "org.suikasoft.jOptions.Interfaces.DataStore"
      )
    ) {
      this.javaDataStoreInstance = dataStore;
    }

    this.definition = definition;

    // If no definition as argument, try to get one from data
    if (this.definition === undefined) {
      if (this.javaDataStoreInstance.getStoreDefinitionTry().isPresent()) {
        this.definition = this.javaDataStoreInstance
          .getStoreDefinitionTry()
          .get();
      }
    }

    // Enable key checking if there is a definition
    this.checkKeys = this.definition !== undefined;

    // Build set with allowed keys

    this.allowedKeys = undefined;
    if (this.checkKeys) {
      this.allowedKeys = new Set();
      for (const dataKey of this.definition.getKeys()) {
        this.allowedKeys.add(dataKey.getName());
      }
    }
  }

  /**
   * @returns the value corresponding to the given key, or undefined if no value or null value is found
   */
  get(key: string) {
    const processedKey = this.processKey(key, "get");

    const value = this.javaDataStoreInstance.get(processedKey);

    if (value === null) {
      return undefined;
    }

    return value;
  }

  /**
   * Store the given value with the given key.
   */
  put(key: string, value: any) {
    const processedKey = this.processKey(key, "put");
    this.javaDataStoreInstance.setRaw(processedKey, value);
  }

  disableKeyChecking() {
    this.checkKeys = false;
  }

  /**
   * @returns java.lang.Class representing the type of the value of the given key
   */
  getType(key: string) {
    if (!this.checkKeys) {
      info(
        "DataStore has no StoreDefinition, retuning undefined",
        "DataStore.getType"
      );
      return undefined;
    }

    this.checkKey(key, "getType");
    return this.definition.getKey(key).getValueClass();
  }

  getKeys() {
    return this.allowedKeys.values();
  }

  getData() {
    return this.javaDataStoreInstance;
  }

  addAlias(key: string, alias: string) {
    // Check if alias was already defined
    const previousKey = this.keyAliases[alias];
    if (previousKey !== undefined) {
      info(
        "Alias '" +
          alias +
          "' is already defined and points to key '" +
          previousKey +
          "', overwriting definition and pointing to key '" +
          key +
          "'",
        "DataStore.addAlias"
      );
    }

    // Check if key is valid before adding alias
    this.checkKey(key, "addAlias");

    // Add alias
    this.keyAliases[alias] = key;
  }

  /**
   * Wraps a Java DataStore around a Lara DataStore.
   */
  protected dataStoreWrapper(javaDataStore: JavaClasses.DataStore) {
    return new DataStore(javaDataStore, this.definition);
  }

  /**
   * @returns the Java class of DataStore
   */
  private get DataStoreClass() {
    return JavaTypes.DataStore;
  }

  /**
   * @returns the Java class with utility methods for DataStore
   */
  private get UtilityClass() {
    return JavaTypes.JOptionsUtils;
  }

  /**
   * Check if there is an alias for the key.
   * If no alias, return original key; otherwise, return correct key.
   *
   * @param key -
   * @param functionName -
   * @returns The key to be used
   */
  private processKey(key: string, functionName?: string) {
    return this.keyAliases[key] ?? key;
  }

  private checkKey(key: string, functionName = "_checkKey") {
    if (!this.checkKeys) {
      return;
    }

    if (this.allowedKeys.has(key)) {
      return;
    }

    let message =
      "DataStore." +
      functionName +
      " : Key '" +
      key +
      "' is not allowed, available keys:\n";
    message += " - '" + this.allowedKeys.values().join("'\n - '") + "'";

    throw message;
  }

  save(fileOrBaseFolder: any, optionalFile?: any) {
    this.UtilityClass.saveDataStore(
      Io.getPath(fileOrBaseFolder, optionalFile),
      this.javaDataStoreInstance
    );
  }

  load(fileOrBaseFolder: any, optionalFile?: any) {
    if (this.definition === undefined) {
      throw "DataStore.load: current DataStore does not have keys definition, cannot load from file";
    }

    const javaDataStore = this.UtilityClass.loadDataStore(
      Io.getPath(fileOrBaseFolder, optionalFile),
      this.definition
    );

    return this.dataStoreWrapper(javaDataStore);
  }

  copy() {
    // Save this data store to a temporary file, a load it again. Return loaded object

    const tempFilename = "_datastore_copy_temp.datastore";
    this.save(tempFilename);
    return this.load(tempFilename);
  }

  /**
   * Checks if the given key has an associated value in the DataStore.
   *
   * @param key - The name of the key to check
   * @returns true if the data store has a value for the given key, false otherwise
   */
  hasValue(key: string): boolean {
    return this.javaDataStoreInstance.hasValueRaw(key);
  }

  /**
   *
   * @returns The folder of the configuration file, if one was used, or undefined otherwise.
   *
   */
  getConfigurationFolder(): JavaClasses.File | undefined {
    const currentFolder = this.javaDataStoreInstance.get(
      "joptions_current_folder_path"
    );

    if (currentFolder.isEmpty()) {
      return undefined;
    }

    return Io.getPath(currentFolder.get());
  }

  /**
   *
   * @returns The configuration file, if one was used, or undefined otherwise.
   *
   */
  getConfigurationFile(): JavaClasses.File | undefined {
    const configFile = this.javaDataStoreInstance.get("app_config");

    if (configFile === undefined) {
      return undefined;
    }

    return configFile;
  }

  /**
   * The folder that is considered the working folder of the current context.
   *
   * 1) If a configuration file was used, returns the path of the configuration file;
   * 2) Otherwise, returns the folder from where the weaver was launched.
   *
   * @returns The folder where the code represented by the AST will be written at the end of execution.
   */
  getContextFolder(): JavaClasses.File {
    return this.getConfigurationFolder() ?? Io.getWorkingFolder();
  }
}
