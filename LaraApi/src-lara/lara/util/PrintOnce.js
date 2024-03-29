import { println } from "../../core/output.js";
import StringSet from "./StringSet.js";
/**
 * @class
 */
export default class PrintOnce {
    static messagesSet = new StringSet();
    static message(message) {
        if (message === undefined) {
            return;
        }
        if (this.messagesSet.has(message)) {
            return;
        }
        this.messagesSet.add(message);
        println(message);
    }
}
//# sourceMappingURL=PrintOnce.js.map