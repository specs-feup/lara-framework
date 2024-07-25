import { importAst, initCodeContainer } from "./ast-import.js";
import { getContinueButton } from "./components.js";
import { addFile, clearFiles, selectFile } from "./files.js";
import { addHighlighingEventListeners } from "./visualization.js";

const webSocketOnMessage = (message: MessageEvent): void => {
  const continueButton = getContinueButton();
  const data = parseMessage(message);

  switch (data.message) {
    case 'update':
      const { code, ast } = data;
      const buttonDisabled = continueButton.disabled;

      continueButton.disabled = true;

      initCodeContainer();

      clearFiles();
      for (const [filename, filecode] of Object.entries(code))
        addFile(filename, filecode as string);
      
      importAst(ast);
      addHighlighingEventListeners(ast);

      selectFile(Object.keys(code)[0]);

      continueButton.disabled = buttonDisabled;

      break;
      
    case 'wait':
      continueButton.disabled = false;
      break;

    case 'continue':
      continueButton.disabled = true;
      break;
  }
};

const getWebSocket = (): WebSocket => {
  const url = '/';
  const ws = new WebSocket(url);
  ws.addEventListener('message', webSocketOnMessage);
  return ws;
};

const sendData = (ws: WebSocket, data: any): void => {
  ws.send(JSON.stringify(data));
};

const parseMessage = (message: MessageEvent): any => {
  return JSON.parse(message.data);
};

const continueButtonOnClick = (ws: WebSocket): void => {
  const continueButton = getContinueButton();
  continueButton.disabled = true;
  sendData(ws, { message: 'continue' });
};

export {
  getWebSocket,
  sendData,
  parseMessage,
  webSocketOnMessage,
  continueButtonOnClick,
}
