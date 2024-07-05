import { continueButtonOnClick, getWebSocket, webSocketOnMessage } from "./communication.js";
(() => {
    const ws = getWebSocket();
    const astContainer = document.querySelector('#ast-container');
    const codeContainer = document.querySelector('#code-container');
    const continueButton = document.querySelector('#continue-button');
    if (!astContainer || !codeContainer || !continueButton) {
        console.error('Required elements not found');
        return;
    }
    ws.addEventListener('message', event => webSocketOnMessage(event, continueButton, astContainer, codeContainer));
    continueButton.addEventListener('click', () => continueButtonOnClick(continueButton, ws));
})();
//# sourceMappingURL=main.js.map