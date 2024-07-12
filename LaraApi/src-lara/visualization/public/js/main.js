import { continueButtonOnClick, getWebSocket, webSocketOnMessage } from "./communication.js";
(() => {
    const astContainer = document.querySelector('#ast-container');
    const codeContainer = document.querySelector('#code-container');
    const continueButton = document.querySelector('#continue-button');
    if (!astContainer || !codeContainer || !continueButton) {
        console.error('Required elements not found');
        return;
    }
    let ws;
    const setupWebSocket = () => {
        ws = getWebSocket();
        ws.addEventListener('message', event => webSocketOnMessage(event, continueButton, astContainer, codeContainer));
        ws.addEventListener('close', () => setupWebSocket());
    };
    setupWebSocket();
    continueButton.addEventListener('click', () => continueButtonOnClick(continueButton, ws));
})();
//# sourceMappingURL=main.js.map