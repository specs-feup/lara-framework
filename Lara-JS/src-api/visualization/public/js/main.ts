import { continueButtonOnClick, getWebSocket, webSocketOnMessage } from "./communication.js";

(() => {
  const ws = getWebSocket();

  const astContainer = document.querySelector<HTMLElement>('#ast-container');
  const codeContainer = document.querySelector<HTMLElement>('#code-container');
  const continueButton = document.querySelector<HTMLButtonElement>('#continue-button');

  if (!astContainer || !codeContainer || !continueButton) {
    console.error('Required elements not found');
    return;
  }

  ws.addEventListener('message', event => webSocketOnMessage(event, continueButton, astContainer, codeContainer));
  continueButton.addEventListener('click', () => continueButtonOnClick(continueButton, ws));
})();