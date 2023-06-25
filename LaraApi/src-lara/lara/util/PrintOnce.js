//import { print } from "../core/output.js";
import StringSet from "./StringSet.js";
/**
 * @class
 */
class PrintOnce {
    static messagesSet = new StringSet();
    static message(message) {
        if (message === undefined) {
            return;
        }
        if (this.messagesSet.has(message)) {
            return;
        }
        this.messagesSet.add(message);
        // @ts-ignore
        println(message);
    }
}
//export default new PrintOnce();
export default PrintOnce;
//# sourceMappingURL=PrintOnce.js.map