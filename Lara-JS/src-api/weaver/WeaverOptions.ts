import JavaTypes from "../lara/util/JavaTypes.js";
import Weaver from "./Weaver.js";
import WeaverDataStore from "./util/WeaverDataStore.js";

/**
 * Contains configuration-related methods of the weaver.
 */
export default class WeaverOptions {
  /**
   * @returns DataStore with the data of the current weaver
   */
  static getData() {
    const weaverEngine = Weaver.getWeaverEngine();
    return new WeaverDataStore(weaverEngine.getData().get(), weaverEngine.getStoreDefinition());
  }

  /**
   * @returns a string with the command-line flags for the current options
   */
  static toCli(): string {
    return JavaTypes.LaraCli.getWeaverOptions(Weaver.getWeaverEngine()).toCli(
      WeaverOptions.getData().getData()
    );
  }

  /**
   * @returns {java.util.Set<String>} with the languages supported by the current weaver
   */
  static getSupportedLanguages() {
    return Weaver.getWeaverEngine().getLanguages();
  }
}
