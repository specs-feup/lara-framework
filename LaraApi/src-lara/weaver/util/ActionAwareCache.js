import JavaTypes from "../../lara/util/JavaTypes.js";
import Weaver from "../Weaver.js";
export default class ActionAwareCache {
    _data = undefined;
    constructor(data) {
        this._data = data;
        // Create JsGear
        // TODO: JsGear should be wrapped by a JS class
        const jsGear = new JavaTypes.JsGear();
        // Define onAction
        jsGear.setJsOnAction(this.clearData.bind(this));
        // Register object
        const engine = Weaver.getWeaverEngine();
        engine.getEventTrigger().registerReceiver(jsGear);
    }
    get data() {
        return this._data;
    }
    set data(data) {
        this._data = data;
    }
    clearData(actionEvent) {
        this._data = undefined;
    }
}
//# sourceMappingURL=ActionAwareCache.js.map