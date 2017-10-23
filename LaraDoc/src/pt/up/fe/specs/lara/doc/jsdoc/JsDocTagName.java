package pt.up.fe.specs.lara.doc.jsdoc;

public enum JsDocTagName {

    ASPECT("aspect"),
    /**
     * Properties NAME_PATH
     */
    ALIAS("alias"),
    /**
     * Properties NAME
     */
    CLASS("class"),
    RETURNS("constructor"),
    /**
     * Properties NAME, TYPE_NAME
     */
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
