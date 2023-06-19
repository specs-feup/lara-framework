import { println } from "../core/output.js";
import StringSet from "./StringSet.js";
/**
 * @class
 */
class PrintOnce {
    messagesSet = new StringSet();
    message(message) {
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
export default new PrintOnce();
//# sourceMappingURL=PrintOnce.js.map