package pt.up.fe.specs.lara.doc.aspectir.elements;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;

public class AspectElement extends AAspectIrElement {

    public AspectElement(LaraDocComment laraDocComment) {
        super(laraDocComment);
    }

    public String getAspectName() {
        // System.out.println("LAST TAG ASPECT:" + getComment().getLastTag(JsDocTagName.ASPECT));
        // System.out.println("LAST TAG ASPECT NAME_PATH:"
        // + getComment().getLastTag(JsDocTagName.ASPECT).getValue(JsDocTagProperty.NAME_PATH));
        String aspectName = getComment().getLastTag(JsDocTagName.ASPECT).getValue(JsDocTagProperty.NAME_PATH);
        Preconditions.checkNotNull(aspectName, "AspectElements should always have a name");
        return aspectName;
    }

    @Override
    public String getName() {
        return getAspectName();
    }
    //
    // public List<String> getParameters() {
    // return getComment().getTags(JsDocTagName.PARAM).stream()
    // .map(jsdoctag -> jsdoctag.getValue(JsDocTagProperty.NAME))
    // .collect(Collectors.toList());
    //
    // }
}
