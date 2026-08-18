import path from "path";
import { fileURLToPath } from "url";

export default interface WeaverConfiguration {
  /**
   * Name of the weaver.
   */
  weaverName: string;

  /**
   * Name of the weaver to be displayed in the UI.
   */
  weaverPrettyName: string;

  /**
   * Path to the JS weaver file. Used to override the default Lara-JS weaving behavior.
   */
  weaverFileName?: string;

  /**
   * Path to the jar files that contain the weaver.
   * This path will be recursively searched for .jar files.
   * All jars found will be added to the classpath.
   */
  jarPath: string;

  /**
   * Qualified name of the Java class that implements the weaver.
   */
  javaWeaverQualifiedName: string;

  /**
   * List of files that should be imported for side effects.
   * This is useful for registering the joinpoint mappers for the weaver.
   * Files must be importable using the ES6 import syntax.
   *
   * @example ["@specs-feup/clava/api/Joinpoints.js"]
   */
  importForSideEffects?: string[];
}

/**
 * Default configuration for packages that require a Lara weaver during setup.
 */
export const weaverConfig: WeaverConfiguration = {
  weaverName: "DefaultWeaver",
  weaverPrettyName: "Default Weaver",
  weaverFileName: "@specs-feup/lara/code/Weaver.js",
  jarPath: path.join(
    path.dirname(path.dirname(path.dirname(fileURLToPath(import.meta.url)))),
    "./DefaultWeaver/build/install/DefaultWeaver"
  ),
  javaWeaverQualifiedName:
    "org.lara.interpreter.weaver.defaultweaver.DefaultWeaver",
};
