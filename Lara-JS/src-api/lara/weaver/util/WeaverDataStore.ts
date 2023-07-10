import JavaTypes from "../../util/JavaTypes.js";
import Weaver from "../../../../src-code/Weaver.js";
import DataStore from "../../util/DataStore.js";
import Io from "../../Io.js";

/**
 * DataStore used in LaraI weavers.
 * @constructor
 */
export default class WeaverDataStore extends DataStore{

	constructor(data: any, definition: any){
		data = data !== undefined ? data : "LaraI Options";
		definition = definition !== undefined ? definition 
		: JavaTypes.getLaraiKeys().STORE_DEFINITION;
		super(data, definition);
	}


	/*** PRIVATE OVERRIDABLE FUNCTIONS ***/

	/**
 	* Wraps a Java DataStore around a Lara DataStore.
 	*/
	private dataStoreWrapper(javaDataStore: any) {
		return new WeaverDataStore(javaDataStore, this.definition);
	}


	/*** NEW WEAVER_DATA_STORE FUNCTIONS ***/


	/**
 	* @return a number between 0 and 3, representing the verbose level (0 - less information, 3 - more information)
 	*/
	getVerboseLevel() {
		return this.get("verbose").ordinal();
	}

	/**
	 * @param verboseLevel a number between 0 and 3, representing the verbose level (0 - less information, 3 - more information)
	 */
	setVerboseLevel(verboseLevel: number){
		
		if(!(verboseLevel >= 0 && verboseLevel <= 3)){
			throw "WeaverDataStore.setVerboseLevel: expected a number  between 0 and 3";
		}	

		this.put("verbose", JavaTypes.getVerboseLevel().values()[verboseLevel]);
	}

	/**
 	* @return a java.io.File representing the current output folder of the weaver
 	*/
	getOutputFolder() {
		return this.get("output");
	}

	/**
	 * @param outputFolder a java.io.File or a String, representing the current output folder of the weaver
	 * 
	 */
	setOutputFolder(outputFolder: any) {
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
	setSourceFolders(sourceFolders: any) {
		
		const fileList = JavaTypes.getFileList().newInstance(sourceFolders);  // Change to JavaTypes	
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
	setIncludeFolders(includeFolders: any) {
		
		const fileList = JavaTypes.getFileList().newInstance(includeFolders);	
		this.put("include", fileList);	
	}

	setTools(toolsPath: any) {
		
		var toolsFile = JavaTypes.getOptionalFile().newInstance(toolsPath);	
		this.put("tools", toolsFile);	
	}

	setLogFile(logPath: any) {
	
		var logFile = JavaTypes.getOptionalFile().newInstance(logPath);	
		this.put("log", logFile);	
	}

	getLogFile() {
		
		const optionalFile = this.get("log");
		return optionalFile !== undefined ? optionalFile.getFile() : undefined;
	
	}
}

		
