import JavaTypes from "./util/JavaTypes.js";

/**
 * Information about the current platform (e.g., if it is Linux, Windows or Mac)
 *
 */
export default class Platforms {
  private static customPlatform: string | undefined = undefined;
  private static LINUX = "linux";
  private static UNIX = "unix";
  private static WINDOWS = "windows";
  private static MAC = "mac";

  static isWindows(): boolean {
    return (
      Platforms.testCustomPlatform(Platforms.WINDOWS) ??
      (JavaTypes.SpecsPlatforms.isWindows() as boolean)
    );
  }

  static isLinux(): boolean {
    return (
      Platforms.testCustomPlatform(Platforms.LINUX) ??
      (JavaTypes.SpecsPlatforms.isLinux() as boolean)
    );
  }

  static isUnix(): boolean {
    return (
      Platforms.testCustomPlatform(Platforms.UNIX) ??
      (JavaTypes.SpecsPlatforms.isUnix() as boolean)
    );
  }

  static isMac(): boolean {
    return (
      Platforms.testCustomPlatform(Platforms.MAC) ??
      (JavaTypes.SpecsPlatforms.isMac() as boolean)
    );
  }

  static getPlatformName(): string {
    return JavaTypes.SpecsPlatforms.getPlatformName() as string;
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

  private static testCustomPlatform(platform?: string) {
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