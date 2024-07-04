import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import WebSocket, { WebSocketServer } from 'ws';

export default class VisualizationTool {
  host: string;
  port: number;

  constructor(host: string, port: number | undefined) {
    this.host = host;
    this.port = port ?? 80;
  }

  getDomain(): string {
    return this.host;
  }

  getPort(): number {
    return this.port;
  }

  launch(): void {
    const app = express();
    const server = http.createServer(app);
    const wss = new WebSocketServer({ server: server });

    const filename = fileURLToPath(import.meta.url);
    const dirname = path.dirname(filename);

    app.use(express.static(path.join(dirname, 'public')));

    server.listen(this.port, () => {
      console.log(`[server]: Server is running at http://${this.host}:${this.port}`);
    });

    wss.on('connection', (ws: WebSocket) => {
      console.log('[server]: Client connected');

      ws.on('message', (message: string) => {
        console.log(`[server]: Received message => ${message}`);
      });

      ws.on('close', () => {
        console.log('[server]: Client disconnected');
      });
    });
  }

  createWebSocketClient(): WebSocket {
    return new WebSocket(`ws://${this.host}:${this.port}`);
  }
}

new VisualizationTool('localhost', 3000).launch();