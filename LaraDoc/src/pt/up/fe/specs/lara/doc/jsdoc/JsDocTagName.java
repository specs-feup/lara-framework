package pt.up.fe.specs.lara.doc.jsdoc;

public enum JsDocTagName {

    /**
     * Properties NAME_PATH
     */
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
     * Properties NAME, TYPE_NAME, OPTIONAL, DEFAULT_VALUE
     */
    PARAM("param"),
    /**
     * Properties NAME, TYPE_NAME, OPTIONAL, DEFAULT_VALUE
     */
    OUTPUT("output"),
    /**
     * Indicates that this class extends another class.
     * <p>
     * Properties NAME_PATH
     */
    AUGMENTS("augments"),
    DEPRECATED("deprecated"),
    /**
     * The import this element belongs to.
     * <p>
     * Properties NAME_PATH
     */
    IMPORT("import");

    private final String name;

    private JsDocTagName(String name) {
        this.name = name;
    }

    public String getTagName() {
        return name;
    }
}
