import { continueButtonOnClick, getWebSocket, webSocketOnMessage } from "./communication.js";

(() => {
  const astContainer = document.querySelector<HTMLElement>('#ast-container');
  const codeContainer = document.querySelector<HTMLElement>('#code-container code');
  const continueButton = document.querySelector<HTMLButtonElement>('#continue-button');

  if (!astContainer || !codeContainer || !continueButton) {
    console.error('Required elements not found');
    return;
  }

  let ws: WebSocket;
  const setupWebSocket = () => {
    ws = getWebSocket();
    ws.addEventListener('message', event => webSocketOnMessage(event, continueButton, astContainer, codeContainer));
    ws.addEventListener('close', () => setupWebSocket());
  };
  setupWebSocket();

  continueButton.addEventListener('click', () => continueButtonOnClick(continueButton, ws));
})();