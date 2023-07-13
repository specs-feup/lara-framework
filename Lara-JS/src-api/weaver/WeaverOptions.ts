import WeaverDataStore from "./util/WeaverDataStore.js";
import JavaTypes from "../lara/util/JavaTypes.js";
import Weaver from "./Weaver.js";

/**
 * Contains configuration-related methods of the weaver.
 * @class
 */
export default class WeaverOptions {
  /**
   * @return DataStore with the data of the current weaver
   */
  static getData() {
    return new WeaverDataStore(
      JavaTypes.getJavaLaraI().getThreadLocalData()
    ) as WeaverDataStore;
  }

  /**
   * @return {String} a string with the command-line flags for the current options
   */
  static toCli() {
    return JavaTypes.getJavaLaraCli()
      .getWeaverOptions(Weaver.getWeaverEngine())
      .toCli(WeaverOptions.getData().getData()) as string;
  }

  /**
   * @return java.util.Set<String> with the languages supported by the current weaver
   */
  static getSupportedLanguages() {
    return Weaver.getWeaverEngine().getLanguages();
  }
}
