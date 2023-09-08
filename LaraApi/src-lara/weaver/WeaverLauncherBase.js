import WeaverOptions from "./WeaverOptions.js";
/**
 * Object for launching weaver instances.
 *
 * @param language - The language the weaver to launch should use
 */
export default class WeaverLauncherBase {
    language;
    constructor(language) {
        // Check if language is supported
        const supportedLanguages = WeaverOptions.getSupportedLanguages();
        if (!supportedLanguages.contains(language)) {
            throw ("WeaverLauncherBase: language '" +
                language +
                "' not supported. Supported languages: " +
                supportedLanguages.toString());
        }
        this.language = language;
    }
}
//# sourceMappingURL=WeaverLauncherBase.js.map