package pt.up.fe.specs.lara.doc.jsdocgen;

import java.io.File;
import java.util.Optional;

import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.lara.doc.jsdocgen.basichtml.LaraModuleBuilder;
import pt.up.fe.specs.util.SpecsIo;

public class BasicHtmlGenerator implements JsDocGenerator {

    public BasicHtmlGenerator() {
        super();
    }

    @Override
    public Optional<File> generate(LaraDocModule module, File outputFolder) {
        LaraModuleBuilder moduleBuilder = new LaraModuleBuilder(module);

        String moduleHtml = moduleBuilder.getHtml();

        if (moduleHtml.isEmpty()) {
            return Optional.empty();
        }

        File moduleHtmlFile = new File(outputFolder, "module.html");
        SpecsIo.write(moduleHtmlFile, moduleHtml);

        return Optional.of(moduleHtmlFile);
    }

}
