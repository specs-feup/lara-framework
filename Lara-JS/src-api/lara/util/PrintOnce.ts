//import { println } from "../core/output.js";
import StringSet from "./StringSet.js";


/**
 * @class
 */
export default class PrintOnce {
    static messagesSet = new StringSet();

    static  message(message: string) {
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
