/**
 * Information about the current platform (e.g., if it is Linux, Windows or Mac)
 *
 * @class
 */
var Platforms = {};

Platforms._customPlatform = undefined;
Platforms._LINUX = "linux";
Platforms._UNIX = "unix";
Platforms._WINDOWS = "windows";
Platforms._MAC = "mac";


Platforms.isWindows = function() {
	return defaultValue(Platforms._testCustomPlatform(Platforms._WINDOWS), SpecsPlatforms.isWindows());
	//return SpecsPlatforms.isWindows();
}

Platforms.isLinux = function() {
	return defaultValue(Platforms._testCustomPlatform(Platforms._LINUX), SpecsPlatforms.isLinux());
	//return SpecsPlatforms.isLinux();
}

Platforms.isUnix = function() {
	return defaultValue(Platforms._testCustomPlatform(Platforms._UNIX), SpecsPlatforms.isUnix());
//	return SpecsPlatforms.isUnix();
}

Platforms.isMac = function() {
	return defaultValue(Platforms._testCustomPlatform(Platforms._MAX), SpecsPlatforms.isMac());
	//return SpecsPlatforms.isMac();
}

Platforms.getPlatformName = function() {
	return SpecsPlatforms.getPlatformName();
}

Platforms.setLinux = function() {
	Platforms._customPlatform = Platforms._LINUX;
}

Platforms.setWindows = function() {
	Platforms._customPlatform = Platforms._WINDOWS;
}

Platforms.setMac = function() {
	Platforms._customPlatform = Platforms._MAC;
}

Platforms._testCustomPlatform = function(platform) {
	if(Platforms._customPlatform === undefined) {
		return undefined;
	}
	
	return Platforms._customPlatform === platform;
}

/**
 * If platform has been previously set to a custom one, resets setting. Otherwise, does nothing.
 */
Platforms.setCurrent = function() {
	Platforms._customPlatform = undefined;
}