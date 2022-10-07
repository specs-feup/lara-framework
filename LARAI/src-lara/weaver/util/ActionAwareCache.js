laraImport("weaver.Weaver");

class ActionAwareCache {

	#data;
	
	static #JS_GEAR_TYPE = "org.lara.interpreter.utils.JsGear";
	
	constructor(data) {
		this.#data = data;
		
		// Create JsGear
		// TODO: JsGear should be wrapped by a JS class
		const jsGearClass = Java.type(ActionAwareCache.#JS_GEAR_TYPE);
		const jsGear = new jsGearClass();
		// Define onAction
		jsGear.setJsOnAction(d => this.#clearData(d));
//		const success = jsGear.setJsOnAction(10);		
//		if(!success) {
//			throw new Error("Could not set function on action");
//		}
		
		// Register object 
		const engine = Weaver.getWeaverEngine();
		engine.getEventTrigger().registerReceiver(jsGear);
	}
	
	get data() {
		return this.#data;
	}
	
	set data(data) {
		this.#data = data;
	}
	
	#clearData(actionEvent) {
		this.#data = undefined;
	}

}