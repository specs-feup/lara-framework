import { importAst } from "./ast-import.js";
const getWebSocket = () => {
    const url = `ẁs://${window.location.host}`;
    return new WebSocket(url);
};
const sendData = (ws, data) => {
    ws.send(JSON.stringify(data));
};
const parseMessage = (message) => {
    return JSON.parse(message.data);
};
const webSocketOnMessage = (message, continueButton, astContainer, codeContainer) => {
    const data = parseMessage(message);
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
};
const continueButtonOnClick = (continueButton, ws) => {
    continueButton.disabled = true;
    sendData(ws, { message: 'continue' });
};
export { getWebSocket, sendData, parseMessage, webSocketOnMessage, continueButtonOnClick, };
//# sourceMappingURL=communication.js.map