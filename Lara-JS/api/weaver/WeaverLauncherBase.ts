import WeaverOptions from "./WeaverOptions.js";

/**
 * Object for launching weaver instances.
 * 
 * @param language - The language the weaver to launch should use
 */
export default abstract class WeaverLauncherBase {
  language: string;

  constructor(language: string) {
    // Check if language is supported
    const supportedLanguages = WeaverOptions.getSupportedLanguages();
    if (!supportedLanguages.contains(language)) {
      throw (
        "WeaverLauncherBase: language '" +
        language +
        "' not supported. Supported languages: " +
        supportedLanguages.toString()
      );
    }

    this.language = language;
  }

  /**
   * Launches a Clava weaving session.
   *
   * @param args - The arguments to pass to the weaver, as if it was launched from the command-line
   * @returns True if the weaver execution without problems, false otherwise
   */
  abstract execute(args: string | any[]): boolean;
}
