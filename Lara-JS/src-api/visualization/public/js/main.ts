import { continueButtonOnClick, getWebSocket, webSocketOnMessage } from "./communication.js";
import { addDividerEventListeners } from "./visualization.js";

(() => {
  const astContainer = document.querySelector<HTMLElement>('#ast-container');
  const codeContainer = document.querySelector<HTMLElement>('#code-container');
  const continueButton = document.querySelector<HTMLButtonElement>('#continue-button');
  const resizer = document.querySelector<HTMLElement>('#resizer');

  if (!astContainer || !codeContainer || !continueButton || !resizer) {
    console.error('Required elements not found');
    return;
  }

  addDividerEventListeners(resizer, astContainer, codeContainer, continueButton);

  let ws: WebSocket;
  const setupWebSocket = () => {
    ws = getWebSocket();
    ws.addEventListener('message', event => webSocketOnMessage(event, continueButton, astContainer, codeContainer));
    ws.addEventListener('close', () => setupWebSocket());
  };
  setupWebSocket();

  continueButton.addEventListener('click', () => continueButtonOnClick(continueButton, ws));
})();
