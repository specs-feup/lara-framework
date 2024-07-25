import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import { WebSocketServer } from 'ws';
import JoinPoints from '../weaver/JoinPoints.js';
export default class GenericVisualizationTool {
    #hostname;
    #port;
    #wss;
    #serverClosed = false;
    #toolAst;
    #prettyHtmlCode;
    get isLaunched() {
        return this.#wss !== undefined && this.#serverClosed === false;
    }
    get hostname() {
        return this.#hostname;
    }
    get port() {
        return this.#port;
    }
    get url() {
        return this.#hostname && this.#port ? `http://${this.#hostname}:${this.#port}` : undefined;
    }
    updateAstAndCode(astRoot) {
        const astConverter = this.getAstConverter();
        astConverter.updateAst();
        this.#toolAst = astConverter.getToolAst(astRoot);
        this.#prettyHtmlCode = astConverter.getPrettyHtmlCode(astRoot);
    }
    onWssError(error) {
        switch (error.code) {
            case 'EADDRINUSE':
                console.error(`[server]: Port ${this.#port} is already in use`);
                break;
            case 'EACCES':
                console.error(`[server]: Permission denied to use port ${this.#port}`);
                break;
            default:
                console.error(`[server]: Unknown error occurred: ${error.message}`);
                break;
        }
        ;
        this.#wss.close();
    }
    async launch({ hostname, port }) {
        const app = express();
        const server = http.createServer(app);
        this.#wss = new WebSocketServer({ server: server });
        const filename = fileURLToPath(import.meta.url);
        const dirname = path.dirname(filename);
        app.use(express.static(path.join(dirname, 'public')));
        this.#wss.on('connection', ws => this.updateClient(ws));
        this.#wss.on('close', () => { this.#serverClosed = true; });
        this.#wss.on('error', error => this.onWssError(error));
        return new Promise(res => {
            server.listen(port, hostname, () => {
                const addressInfo = server.address();
                this.#hostname = addressInfo.address;
                this.#port = addressInfo.port;
                this.#serverClosed = false;
                res();
            });
        });
    }
    sendToClient(ws, data) {
        ws.send(JSON.stringify(data));
    }
    sendToAllClients(data) {
        this.#wss.clients.forEach(ws => this.sendToClient(ws, data));
    }
    updateClient(ws) {
        this.sendToClient(ws, {
            message: 'update',
            ast: this.#toolAst.toJson(),
            code: this.#prettyHtmlCode,
        });
    }
    async waitForTool() {
        return new Promise(res => {
            let placeClientOnWait;
            const waitOnMessage = (message) => {
                const data = JSON.parse(message);
                if (data.message === 'continue') {
                    this.#wss.clients.forEach(ws => {
                        this.#wss.off('connection', placeClientOnWait);
                        ws.off('message', waitOnMessage);
                    });
                    this.sendToAllClients({ message: 'continue' });
                    res();
                }
            };
            placeClientOnWait = (ws) => {
                ws.on('message', waitOnMessage);
                this.sendToClient(ws, { message: 'wait' });
            };
            this.#wss.clients.forEach(placeClientOnWait);
            this.#wss.on('connection', placeClientOnWait);
        });
    }
    async visualize(astRoot = JoinPoints.root(), port = 3000, hostname = '127.0.0.1') {
        this.updateAstAndCode(astRoot);
        if (!this.isLaunched) {
            await this.launch({ astRoot, hostname, port });
        }
        else {
            this.#wss.clients.forEach(ws => this.updateClient(ws));
        }
        console.log(`\nVisualization tool is running at ${this.url}\n`);
        await this.waitForTool();
    }
}
//# sourceMappingURL=GenericVisualizationTool.js.map