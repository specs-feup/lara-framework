import { continueButtonOnClick, getWebSocket } from "./communication.js";
import { getContinueButton } from "./components.js";
import { addResizerEventListeners } from "./visualization.js";
const setupEventListeners = (ws) => {
    const continueButton = getContinueButton();
    continueButton.addEventListener('click', () => continueButtonOnClick(ws));
    addResizerEventListeners();
};
(() => {
    let ws;
    const setupWebSocket = () => {
        ws = getWebSocket();
        ws.addEventListener('close', () => setTimeout(setupWebSocket, 1000));
    };
    setupWebSocket();
    setupEventListeners(ws);
})();
//# sourceMappingURL=main.js.map