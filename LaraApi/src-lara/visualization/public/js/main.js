import { continueButtonOnClick, getWebSocket, webSocketOnMessage } from "./communication.js";
import { addDividerEventListeners } from "./visualization.js";
(() => {
    const astContainer = document.querySelector('#ast-container');
    const codeContainer = document.querySelector('#code-container');
    const continueButton = document.querySelector('#continue-button');
    const resizer = document.querySelector('#resizer');
    const fileTabs = document.querySelector('#file-tabs');
    if (!astContainer || !codeContainer || !continueButton || !resizer || !fileTabs) {
        console.error('Required elements not found');
        return;
    }
    addDividerEventListeners(resizer, astContainer, codeContainer, continueButton);
    let ws;
    const setupWebSocket = () => {
        ws = getWebSocket();
        ws.addEventListener('message', event => webSocketOnMessage(event, continueButton, astContainer, codeContainer, fileTabs));
        ws.addEventListener('close', () => setupWebSocket());
    };
    setupWebSocket();
    continueButton.addEventListener('click', () => continueButtonOnClick(continueButton, ws));
})();
//# sourceMappingURL=main.js.map