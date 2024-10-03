import lara.units.TimeUnit;

aspectdef TimeUnitTest

	println("10ms in us: " + TimeUnit.micro().convert(10, "ms"));
	println("1 day in hours: " + TimeUnit.hour().convert(1, "days"));

end
