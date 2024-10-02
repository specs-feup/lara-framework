/**
 * @file communication.ts
 * @brief Functions for communication with the server.
 */
import { importAst, initCodeContainer } from "./ast-import.js";
import { getContinueButton } from "./components.js";
import { addFile, clearFiles, selectFile } from "./files.js";
import { addHighlighingEventListeners } from "./visualization.js";
/**
 * @brief WebSocket message handler for the 'update' message.
 * @details When executed, this function updates the code container and the AST
 * with the new data.
 *
 * @param data Message data
 */
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
/**
 * @brief Creates a WebSocket connection to the server, with the message event
 * listener.
 *
 * @returns WebSocket object
 */
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