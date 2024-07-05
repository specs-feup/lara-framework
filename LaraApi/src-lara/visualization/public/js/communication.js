const getWebSocket = () => {
    const url = `ẁs://${window.location.host}`;
    return new WebSocket(url);
};
(() => {
    const ws = getWebSocket();
    ws.addEventListener('message', (message) => {
        console.log(`[client]: Received message => ${message.data}`);
    });
    const continueButton = document.querySelector('#continue-button');
    if (!continueButton)
        return;
    continueButton.addEventListener('click', () => {
        continueButton.disabled = true;
        ws.send(JSON.stringify({ message: 'continue' }));
    });
    ws.addEventListener('message', (message) => {
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
export {};
//# sourceMappingURL=communication.js.map