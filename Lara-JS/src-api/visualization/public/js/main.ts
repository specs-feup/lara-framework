import { continueButtonOnClick, getWebSocket } from "./communication.js";
import { getContinueButton } from "./components.js";
import { addDividerEventListeners } from "./visualization.js";

const setupEventListeners = (ws: WebSocket): void => {
  const continueButton = getContinueButton();
  continueButton.addEventListener('click', () => continueButtonOnClick(ws));

  addDividerEventListeners();
}

(() => {
  let ws: WebSocket;
  const setupWebSocket = () => {
    ws = getWebSocket();
    ws.addEventListener('close', () => setTimeout(setupWebSocket, 1000));
  };
  setupWebSocket();

  setupEventListeners(ws!);
})();
