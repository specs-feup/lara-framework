import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import { WebSocketServer } from 'ws';
import { wrapJoinPoint } from '../LaraJoinPoint.js';
import JoinPoints from '../weaver/JoinPoints.js';
export default class VisualizationTool {
    static hostname;
    static port;
    static wss;
    static serverClosed = false;
    static isLaunched() {
        return this.wss !== undefined && this.serverClosed === false;
    }
    static getHostname() {
        return this.hostname;
    }
    static getPort() {
        return this.port;
    }
    static onWssError(error) {
        switch (error.code) {
            case 'EADDRINUSE':
                console.error(`[server]: Port ${this.port} is already in use`);
                break;
            case 'EACCES':
                console.error(`[server]: Permission denied to use port ${this.port}`);
                break;
            default:
                console.error(`[server]: Unknown error occurred: ${error.message}`);
                break;
        }
        ;
        this.wss.close();
    }
    static async launch(hostname = '127.0.0.1', port) {
        if (this.isLaunched()) {
            console.warn(`Visualization tool is already running at http://${this.hostname}:${this.port}`);
            return;
        }
        const app = express();
        const server = http.createServer(app);
        this.wss = new WebSocketServer({ server: server });
        const filename = fileURLToPath(import.meta.url);
        const dirname = path.dirname(filename);
        app.use(express.static(path.join(dirname, 'public')));
        this.wss.on('connection', (ws) => {
            console.log('[server]: Client connected');
            ws.on('message', (message) => {
                console.log(`[server]: Received message => ${message}`);
            });
            ws.addEventListener('close', () => {
                console.log('[server]: Client disconnected');
            });
        }); // TODO: Remove this
        this.wss.on('connection', ws => this.updateClient(ws));
        this.wss.on('close', () => {
            this.serverClosed = true;
        });
        this.wss.on('error', error => this.onWssError(error));
        return new Promise(res => {
            server.listen(port ?? 0, hostname, () => {
                const addressInfo = server.address();
                this.hostname = addressInfo.address;
                this.port = addressInfo.port;
                this.serverClosed = false;
                console.log(`\nVisualization tool is running at http://${this.hostname}:${this.port}\n`);
                // child.exec(`xdg-open http://${this.host}:${this.port}`);
                // TODO: See if opening automatically is a good idea
                res();
            });
        });
    }
    static sendToClient(ws, data) {
        ws.send(JSON.stringify(data));
    }
    static sendToAllClients(data) {
        this.wss.clients.forEach(ws => this.sendToClient(ws, data));
    }
    static verifyToolIsRunning() {
        if (!this.isLaunched()) {
            throw Error('Visualization tool is not running');
        }
    }
    static async waitForTool() {
        this.verifyToolIsRunning();
        return new Promise(res => {
            let placeClientOnWait;
            const waitOnMessage = (message) => {
                const data = JSON.parse(message);
                if (data.message === 'continue') {
                    this.wss.clients.forEach(ws => {
                        this.wss.off('connection', placeClientOnWait);
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
            this.wss.clients.forEach(placeClientOnWait);
            this.wss.on('connection', placeClientOnWait);
        });
    }
    static toToolJpJson(jp) {
        return {
            id: wrapJoinPoint(jp._javaObject.getAstId()),
            type: wrapJoinPoint(jp._javaObject.getAstName()),
            code: wrapJoinPoint(jp._javaObject.getCode()),
            children: jp.children
                .slice()
                .sort((a, b) => wrapJoinPoint(a._javaObject.getLocation()).localeCompare(wrapJoinPoint(b._javaObject.getLocation()), 'en', { numeric: true }))
                .map(child => this.toToolJpJson(child))
        };
    }
    static updateClient(ws) {
        this.sendToClient(ws, { message: 'update', ast: this.toToolJpJson(JoinPoints.root()) });
    }
    static update() {
        this.verifyToolIsRunning();
        this.wss.clients.forEach(ws => this.updateClient(ws));
    }
}
//# sourceMappingURL=VisualizationTool.js.map