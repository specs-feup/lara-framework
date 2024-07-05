import { importAst } from "./ast-import.js";

const getWebSocket = (): WebSocket => {
  const url = `ẁs://${window.location.host}`;
  return new WebSocket(url);
};

(() => {
  const ws = getWebSocket();
  const continueButton = document.querySelector<HTMLButtonElement>('#continue-button');
  const astContainer = document.querySelector<HTMLElement>('#ast code');
	const codeContainer = document.querySelector<HTMLElement>('#code code');

  if (!continueButton || !astContainer || !codeContainer)
    return;

  continueButton.addEventListener('click', () => {
    continueButton.disabled = true;
    ws.send(JSON.stringify({ message: 'continue' }));
  });

  ws.addEventListener('message', (message: MessageEvent) => {
    const data = JSON.parse(message.data);

    switch (data.message) {
      case 'update':
        importAst(data.ast, astContainer, codeContainer);
        break;

      case 'wait':
        continueButton.disabled = false;
        break;

      case 'continue':
        continueButton.disabled = true;
        break;
    }
  });
})();
