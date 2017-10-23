package pt.up.fe.specs.lara.doc.aspectir.elements;

import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;

public class AspectElement extends AAspectIrElement {

    public AspectElement(LaraDocComment laraDocComment) {
        super(laraDocComment);
    }

    public String getAspectName() {
        return getComment().getLastTag(JsDocTagName.ASPECT).getValue(JsDocTagProperty.NAME_PATH);
    }
    //
    // public List<String> getParameters() {
    // return getComment().getTags(JsDocTagName.PARAM).stream()
    // .map(jsdoctag -> jsdoctag.getValue(JsDocTagProperty.NAME))
    // .collect(Collectors.toList());
    //
    // }
}
