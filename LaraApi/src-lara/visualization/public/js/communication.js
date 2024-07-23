import { importAst, initCodeContainer } from "./ast-import.js";
import { addFile, selectFile } from "./files.js";
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
const webSocketOnMessage = (message, continueButton, astContainer, codeContainer, fileTabs) => {
    const data = parseMessage(message);
    switch (data.message) {
        case 'update':
            const { code, ast } = data;
            const buttonDisabled = continueButton.disabled;
            continueButton.disabled = true;
            initCodeContainer(codeContainer);
            for (const [filename, filecode] of Object.entries(code))
                addFile(filename, filecode);
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
const continueButtonOnClick = (continueButton, ws) => {
    continueButton.disabled = true;
    sendData(ws, { message: 'continue' });
};
export { getWebSocket, sendData, parseMessage, webSocketOnMessage, continueButtonOnClick, };
//# sourceMappingURL=communication.js.map