package pt.up.fe.specs.lara.doc.jsdocgen;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrDoc;
import pt.up.fe.specs.lara.doc.aspectir.elements.VarDeclElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.utilities.StringLines;

public class BasicHtmlGenerator implements JsDocGenerator {

    @Override
    public Optional<File> generate(LaraDocModule module, File outputFolder) {
        StringBuilder htmlCode = new StringBuilder();

        if (!module.getDocumentation().isPresent()) {
            return Optional.empty();
        }

        AspectIrDoc doc = module.getDocumentation().get();

        for (VarDeclElement varDecl : doc.getVarDecls()) {
            htmlCode.append("<h1>" + varDecl.getVarDeclName() + "</h1>");
            LaraDocComment comment = varDecl.getComment();

            if (!comment.getText().isEmpty()) {

                String text = StringLines.getLines(comment.getText()).stream().collect(Collectors.joining("<br>"));
                htmlCode.append("<p>" + text + "</p>");
            }

        }

        // htmlCode.append("<em>Hello!</em> Elements -> " + doc);

        File moduleHtml = new File(outputFolder, "module.html");
        SpecsIo.write(moduleHtml, htmlCode.toString());

        return Optional.of(moduleHtml);
    }

}
