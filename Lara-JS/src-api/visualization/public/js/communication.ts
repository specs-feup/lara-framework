const getWebSocket = (): WebSocket => {
  const url = `ẁs://${window.location.host}`;
  return new WebSocket(url);
};

(() => {
  const ws = getWebSocket();
  const continueButton = document.querySelector<HTMLButtonElement>('#continue-button');
  if (!continueButton)
    return;

  continueButton.addEventListener('click', () => {
    continueButton.disabled = true;
    ws.send(JSON.stringify({ message: 'continue' }));
  });

  ws.addEventListener('message', (message: MessageEvent) => {
    const data = JSON.parse(message.data);

    switch (data.message) {
      case 'continue':
        continueButton.disabled = true;
        break;
      
      case 'wait':
        continueButton.disabled = false;
        break;
    }
  });
})();
