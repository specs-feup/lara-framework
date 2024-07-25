import { importAst, initCodeContainer } from "./ast-import.js";
import { getContinueButton } from "./components.js";
import { addFile, clearFiles, selectFile } from "./files.js";
import { addEventListenersToAstNodes } from "./visualization.js";
const webSocketOnMessage = (message) => {
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
                addFile(filename, filecode);
            importAst(ast);
            addEventListenersToAstNodes(ast);
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
const getWebSocket = () => {
    const url = '/';
    const ws = new WebSocket(url);
    ws.addEventListener('message', webSocketOnMessage);
    return ws;
};
const sendData = (ws, data) => {
    ws.send(JSON.stringify(data));
};
const parseMessage = (message) => {
    return JSON.parse(message.data);
};
const continueButtonOnClick = (ws) => {
    const continueButton = getContinueButton();
    continueButton.disabled = true;
    sendData(ws, { message: 'continue' });
};
export { getWebSocket, sendData, parseMessage, webSocketOnMessage, continueButtonOnClick, };
//# sourceMappingURL=communication.js.map