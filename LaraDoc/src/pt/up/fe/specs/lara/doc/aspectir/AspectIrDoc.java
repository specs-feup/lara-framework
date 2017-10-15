package pt.up.fe.specs.lara.doc.aspectir;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pt.up.fe.specs.lara.doc.aspectir.elements.VarDeclElement;
import pt.up.fe.specs.util.SpecsCollections;

public class AspectIrDoc {

    private final Map<String, VarDeclElement> varDeclarations;

    public AspectIrDoc(Map<String, VarDeclElement> vardeclarations) {

        this.varDeclarations = vardeclarations;
    }

    public static AspectIrDoc newInstance(List<AspectIrElement> aspectIrElements) {
        // TODO: Organize elements (e.g., separate into classes / functions, put together elements that belong to each
        // other, etc.)

        // List<VarDeclElement> varDecls = ;
        Map<String, VarDeclElement> vardeclarations = new LinkedHashMap<>();

        SpecsCollections.remove(aspectIrElements, VarDeclElement.class::isInstance).stream()
                .map(VarDeclElement.class::cast)
                .forEach(varDecl -> vardeclarations.put(varDecl.getVarDeclName(), varDecl));
        // .collect(Collectors.toMap(VarDeclElement::getVarDeclName, varDecl -> varDecl));

        return new AspectIrDoc(vardeclarations);
    }

    public Collection<VarDeclElement> getVarDecls() {
        return varDeclarations.values();
    }

    @Override
    public String toString() {
        return varDeclarations.toString();
    }

}
