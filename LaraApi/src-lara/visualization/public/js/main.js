import { continueButtonOnClick, getWebSocket } from "./communication.js";
import { getContinueButton } from "./components.js";
import { addDividerEventListeners } from "./visualization.js";
const setupEventListeners = (ws) => {
    const continueButton = getContinueButton();
    continueButton.addEventListener('click', () => continueButtonOnClick(ws));
    addDividerEventListeners();
};
(() => {
    let ws;
    const setupWebSocket = () => {
        ws = getWebSocket();
        ws.addEventListener('close', () => setTimeout(setupWebSocket));
    };
    setupWebSocket();
    setupEventListeners(ws);
})();
//# sourceMappingURL=main.js.map