package pt.up.fe.specs.lara.doc.jsdocgen;

import java.io.File;
import java.util.Optional;
import java.util.function.Predicate;

import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.lara.doc.jsdocgen.basichtml.LaraModuleBuilder;
import pt.up.fe.specs.util.SpecsIo;

public class BasicHtmlGenerator implements JsDocGenerator {

    private final Predicate<String> nameFilter;

    public BasicHtmlGenerator() {
        this(null);
    }

    public BasicHtmlGenerator(Predicate<String> nameFilter) {
        this.nameFilter = nameFilter;
    }

    @Override
    public Predicate<String> getNameFilter() {
        if (nameFilter == null) {
            return JsDocGenerator.super.getNameFilter();
        }

        return nameFilter;
    }

    @Override
    public Optional<File> generate(LaraDocModule module, File outputFolder) {
        LaraModuleBuilder moduleBuilder = new LaraModuleBuilder(module, getNameFilter());

        String moduleHtml = moduleBuilder.getHtml();

        if (moduleHtml.isEmpty()) {
            return Optional.empty();
        }

        File moduleHtmlFile = new File(outputFolder, "module.html");
        SpecsIo.write(moduleHtmlFile, moduleHtml);

        return Optional.of(moduleHtmlFile);
    }

}
