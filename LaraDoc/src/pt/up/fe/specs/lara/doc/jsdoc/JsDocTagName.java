package pt.up.fe.specs.lara.doc.jsdoc;

public enum JsDocTagName {

    ASPECT("aspect"),
    CLASS("class"),
    RETURNS("constructor"),
    PARAM("param"),
    AUGMENTS("augments"),
    DEPRECATED("deprecated");

    private final String name;

    private JsDocTagName(String name) {
        this.name = name;
    }

    public String getTagName() {
        return name;
    }
}
