import { importAst, initCodeContainer } from "./ast-import.js";
import { addFile, selectFile } from "./files.js";

const getWebSocket = (): WebSocket => {
  const url = `ẁs://${window.location.host}`;
  return new WebSocket(url);
};

const sendData = (ws: WebSocket, data: any): void => {
  ws.send(JSON.stringify(data));
};

const parseMessage = (message: MessageEvent): any => {
  return JSON.parse(message.data);
};

const webSocketOnMessage = (message: MessageEvent, continueButton: HTMLButtonElement,
  astContainer: HTMLElement, codeContainer: HTMLElement, fileTabs: HTMLElement): void => {

  const data = parseMessage(message);

  switch (data.message) {
    case 'update':
      const { code, ast } = data;
      const buttonDisabled = continueButton.disabled;

      continueButton.disabled = true;

      initCodeContainer(codeContainer);

      for (const [filename, filecode] of Object.entries(code))
        addFile(filename, filecode as string);
      
      importAst(ast, astContainer, codeContainer);
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

const continueButtonOnClick = (continueButton: HTMLButtonElement, ws: WebSocket): void => {
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
