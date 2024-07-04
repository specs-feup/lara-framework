import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import WebSocket, { WebSocketServer } from 'ws';
export default class VisualizationTool {
    host;
    port;
    constructor(host = '127.0.0.1', port) {
        this.host = host;
        this.port = port;
    }
    getHost() {
        return this.host;
    }
    getPort() {
        return this.port;
    }
    async launch() {
        const app = express();
        const server = http.createServer(app);
        const wss = new WebSocketServer({ server: server });
        const filename = fileURLToPath(import.meta.url);
        const dirname = path.dirname(filename);
        app.use(express.static(path.join(dirname, 'public')));
        wss.on('error', error => {
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
            server.close();
        });
        wss.on('connection', (ws) => {
            console.log('[server]: Client connected');
            ws.on('message', (message) => {
                console.log(`[server]: Received message => ${message}`);
            });
            ws.on('close', () => {
                console.log('[server]: Client disconnected');
            });
        });
        return new Promise(res => {
            server.listen(this.port, this.host, () => {
                const addressInfo = server.address();
                this.port = addressInfo.port;
                console.log(`[server]: Server is running at http://${this.host}:${this.port}`);
                res();
            });
        });
    }
    createWebSocketClient() {
        return new WebSocket(`ws://${this.host}:${this.port}`);
    }
}
(() => {
    const tool = new VisualizationTool('127.0.0.1', 3000);
    tool.launch();
})();
//# sourceMappingURL=VisualizationTool.js.map