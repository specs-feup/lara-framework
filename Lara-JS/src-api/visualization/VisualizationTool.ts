import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import WebSocket, { WebSocketServer } from 'ws';
import { AddressInfo } from 'net';

export default class VisualizationTool {
  private host: string;
  private port: number | undefined;

  constructor(host: string = '127.0.0.1', port?: number) {
    this.host = host;
    this.port = port;
  }

  public getHost(): string {
    return this.host;
  }

  public getPort(): number | undefined {
    return this.port;
  }

  public async launch(): Promise<void> {
    const app = express();
    const server = http.createServer(app);
    const wss = new WebSocketServer({ server: server });

    const filename = fileURLToPath(import.meta.url);
    const dirname = path.dirname(filename);

    app.use(express.static(path.join(dirname, 'public')));

    wss.on('error', error => {
      switch ((error as any).code) {
        case 'EADDRINUSE':
          console.error(`[server]: Port ${this.port} is already in use`);
          break;

        case 'EACCES':
          console.error(`[server]: Permission denied to use port ${this.port}`);
          break;
        
        default:
          console.error(`[server]: Unknown error occurred: ${error.message}`);
          break;
      };

      server.close();
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

    return new Promise(res => {
      server.listen(this.port, this.host, () => {
        const addressInfo = server.address() as AddressInfo;
        this.port = addressInfo.port;

        console.log(`\nVisualization tool is running at http://${this.host}:${this.port}\n`);
        // child.exec(`xdg-open http://${this.host}:${this.port}`);
        // TODO: See if opening automatically is a good idea
        res();
      });
    });
  }

  public createWebSocketClient(): WebSocket {
    return new WebSocket(`ws://${this.host}:${this.port}`);
  }
}