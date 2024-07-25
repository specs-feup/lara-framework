import { continueButtonOnClick, getWebSocket } from "./communication.js";
import { getContinueButton } from "./components.js";
import { addDividerEventListeners } from "./visualization.js";

(() => {
  let ws: WebSocket;
  const setupWebSocket = () => {
    ws = getWebSocket();
    ws.addEventListener('close', () => setupWebSocket());
  };
  setupWebSocket();

  const continueButton = getContinueButton();
  continueButton.addEventListener('click', () => continueButtonOnClick(ws));

  addDividerEventListeners();
})();
