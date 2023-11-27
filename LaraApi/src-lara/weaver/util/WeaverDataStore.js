import Io from "../../lara/Io.js";
import DataStore from "../../lara/util/DataStore.js";
import JavaTypes from "../../lara/util/JavaTypes.js";
import Weaver from "../Weaver.js";
/**
 * DataStore used in LaraI weavers.
 */
export default class WeaverDataStore extends DataStore {
    constructor(data = "LaraI Options", definition = JavaTypes.LaraiKeys.STORE_DEFINITION) {
        super(data, definition);
    }
    /**
     * Wraps a Java DataStore around a Lara DataStore.
     */
    dataStoreWrapper(javaDataStore) {
        return new WeaverDataStore(javaDataStore, this.definition);
    }
    /**
     * @returns a number between 0 and 3, representing the verbose level (0 - less information, 3 - more information)
     */
    getVerboseLevel() {
        return this.get("verbose").ordinal();
    }
    /**
     * @param verboseLevel - a number between 0 and 3, representing the verbose level (0 - less information, 3 - more information)
     */
    setVerboseLevel(verboseLevel) {
        if (!(verboseLevel >= 0 && verboseLevel <= 3)) {
            throw "WeaverDataStore.setVerboseLevel: expected a number  between 0 and 3";
        }
        this.put("verbose", JavaTypes.VerboseLevel.values()[verboseLevel]);
    }
    /**
     * @returns a java.io.File representing the current output folder of the weaver
     */
    getOutputFolder() {
        return this.get("output");
    }
    /**
     * @param outputFolder - a java.io.File or a String, representing the current output folder of the weaver
     *
     */
    setOutputFolder(outputFolder) {
        const normalizedOutputFolder = Io.getPath(outputFolder);
        this.put("output", normalizedOutputFolder);
    }
    /**
     *
     */
    getSourceFolders() {
        const sourcesFileList = this.get("workspace");
        return Weaver.toJs(sourcesFileList.getFiles());
    }
    /**
     *
     */
    setSourceFolders(sourceFolders) {
        const fileList = JavaTypes.FileList.newInstance(sourceFolders); // Change to JavaTypes
        this.put("workspace", fileList);
    }
    /**
     *
     */
    getIncludeFolders() {
        const includesFileList = this.get("include");
        return Weaver.toJs(includesFileList.getFiles());
    }
    /**
     *
     */
    setIncludeFolders(includeFolders) {
        const fileList = JavaTypes.FileList.newInstance(includeFolders);
        this.put("include", fileList);
    }
    setTools(toolsPath) {
        var toolsFile = JavaTypes.OptionalFile.newInstance(toolsPath);
        this.put("tools", toolsFile);
    }
    setLogFile(logPath) {
        var logFile = JavaTypes.OptionalFile.newInstance(logPath);
        this.put("log", logFile);
    }
    getLogFile() {
        const optionalFile = this.get("log");
        return optionalFile ?? optionalFile.getFile();
    }
}
//# sourceMappingURL=WeaverDataStore.js.map