import lara.Platforms;

aspectdef PlatformsTest

	println("Is Windows? " + Platforms.isWindows());
	Platforms.setLinux();
	println("Is Windows? " + Platforms.isWindows());
	println("Is Linux? " + Platforms.isLinux());
end
