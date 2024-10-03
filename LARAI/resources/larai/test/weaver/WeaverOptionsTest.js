import weaver.WeaverOptions;
import lara.Io;

aspectdef WeaverData

	var data = WeaverOptions.getData();

	println("Original verbose level: " + data.getVerboseLevel());
	data.setVerboseLevel(3);
	println("New verbose level: " + data.getVerboseLevel());

	println("Original output folder: " + data.getOutputFolder());
	data.setOutputFolder("subfolder");
	println("New output folder: " + data.getOutputFolder());
	
	println("Supported languages: " + WeaverOptions.getSupportedLanguages());
	
	// Cannot test this in a platform independent way
	//println("CLI: " + WeaverOptions.toCli());
	
	//println("Weaver keys: " + WeaverOptions.getKeys());	

end
