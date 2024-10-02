import JoinPoints from "../weaver/JoinPoints.js";
export default class VisualizationToolUpdate {
    static updateVisualizationTool() {
        const socket = new WebSocket("ws://127.0.0.1:3000");
        console.log(JSON.stringify(JoinPoints.root().dump));
    }
}
;
//# sourceMappingURL=VisualizationToolUpdate.js.map