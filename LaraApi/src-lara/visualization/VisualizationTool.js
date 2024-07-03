import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import { WebSocketServer } from 'ws';
export default class VisualizationTool {
    host;
    port;
    constructor(host, port) {
        this.host = host;
        this.port = port;
    }
    getDomain() {
        return this.host;
    }
    getPort() {
        return this.port;
    }
    launch() {
        const app = express();
        const server = http.createServer(app);
        const wss = new WebSocketServer({ server: server });
        const filename = fileURLToPath(import.meta.url);
        const dirname = path.dirname(filename);
        app.use(express.static(path.join(dirname, 'public')));
        server.listen(this.port, () => {
            console.log(`[server]: Server is running at http://${this.host}:${this.port}`);
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
    }
}
new VisualizationTool('localhost', 3000).launch();
//# sourceMappingURL=VisualizationTool.js.map