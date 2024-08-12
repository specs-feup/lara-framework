import JavaTypes from "./util/JavaTypes.js";
/**
 * Information about the current platform (e.g., if it is Linux, Windows or Mac)
 *
 */
export default class Platforms {
    static customPlatform = undefined;
    static LINUX = "linux";
    static UNIX = "unix";
    static WINDOWS = "windows";
    static MAC = "mac";
    static isWindows() {
        return (Platforms.testCustomPlatform(Platforms.WINDOWS) ??
            JavaTypes.SpecsPlatforms.isWindows());
    }
    static isLinux() {
        return (Platforms.testCustomPlatform(Platforms.LINUX) ??
            JavaTypes.SpecsPlatforms.isLinux());
    }
    static isUnix() {
        return (Platforms.testCustomPlatform(Platforms.UNIX) ??
            JavaTypes.SpecsPlatforms.isUnix());
    }
    static isMac() {
        return (Platforms.testCustomPlatform(Platforms.MAC) ??
            JavaTypes.SpecsPlatforms.isMac());
    }
    static getPlatformName() {
        return JavaTypes.SpecsPlatforms.getPlatformName();
    }
    static setLinux() {
        Platforms.customPlatform = Platforms.LINUX;
    }
    static setWindows() {
        Platforms.customPlatform = Platforms.WINDOWS;
    }
    static setMac() {
        Platforms.customPlatform = Platforms.MAC;
    }
    static testCustomPlatform(platform) {
        if (Platforms.customPlatform === undefined) {
            return undefined;
        }
        return Platforms.customPlatform === platform;
    }
    /**
     * If platform has been previously set to a custom one, resets setting. Otherwise, does nothing.
     */
    static setCurrent() {
        Platforms.customPlatform = undefined;
    }
}
//# sourceMappingURL=Platforms.js.map