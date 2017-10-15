package pt.up.fe.specs.lara.doc.jsdocgen;

import java.io.File;
import java.util.Optional;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrDoc;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.util.SpecsIo;

public class BasicHtmlGenerator implements JsDocGenerator {

	@Override
	public Optional<File> generate(LaraDocModule module, File outputFolder) {
		StringBuilder htmlCode = new StringBuilder();

		if(!module.getDocumentation().isPresent()) {
			return Optional.empty();
		}

		AspectIrDoc doc = module.getDocumentation().get();
		
		htmlCode.append("<em>Hello!</em> Elements -> " + doc);
		
		File moduleHtml = new File(outputFolder, "module.html");
		SpecsIo.write(moduleHtml, htmlCode.toString());
		
		return Optional.of(moduleHtml);
	}

}
