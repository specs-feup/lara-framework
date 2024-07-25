import { importAst, initCodeContainer } from "./ast-import.js";
import { getContinueButton } from "./components.js";
import { addFile, clearFiles, selectFile } from "./files.js";
import { addHighlighingEventListeners } from "./visualization.js";
const onUpdate = (data) => {
    const buttonDisabled = getContinueButton().disabled;
    getContinueButton().disabled = true;
    initCodeContainer();
    clearFiles();
    for (const [filename, filecode] of Object.entries(data.code))
        addFile(filename, filecode);
    importAst(data.ast);
    addHighlighingEventListeners(data.ast);
    selectFile(Object.keys(data.code)[0]);
    getContinueButton().disabled = buttonDisabled;
};
const webSocketOnMessage = (message) => {
    const continueButton = getContinueButton();
    const data = parseMessage(message);
    switch (data.message) {
        case 'update':
            onUpdate(data);
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