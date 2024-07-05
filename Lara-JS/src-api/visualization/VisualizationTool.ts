import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import WebSocket, { WebSocketServer, MessageEvent } from 'ws';
import Query from '../weaver/Query.js';
import { AddressInfo } from 'net';

export default class VisualizationTool {
  private static host: string | undefined;
  private static port: number | undefined;
  private static wss: WebSocketServer | undefined;

  public static isLaunched(): boolean {
    return this.port !== undefined;
  }

  public static getHost(): string | undefined {
    return this.host;
  }

  public static getPort(): number | undefined {
    return this.port;
  }

  public static async launch(host: string = '127.0.0.1', port?: number): Promise<void> {
    if (this.isLaunched()) {
      console.warn('[server]: Visualization tool is already running at http://${this.host}:${this.port}');
      return;
    }

    const app = express();
    const server = http.createServer(app);
    this.wss = new WebSocketServer({ server: server });

    const filename = fileURLToPath(import.meta.url);
    const dirname = path.dirname(filename);

    app.use(express.static(path.join(dirname, 'public')));

    this.wss.on('error', error => {
      switch ((error as any).code) {
        case 'EADDRINUSE':
          console.error(`[server]: Port ${port} is already in use`);
          break;

        case 'EACCES':
          console.error(`[server]: Permission denied to use port ${port}`);
          break;
        
        default:
          console.error(`[server]: Unknown error occurred: ${error.message}`);
          break;
      };

      server.close();
    });

    this.wss.on('connection', (ws: WebSocket) => {
      console.log('[server]: Client connected');

      ws.on('message', (message: string) => {
        console.log(`[server]: Received message => ${message}`);
      });

      ws.addEventListener('close', () => {
        console.log('[server]: Client disconnected');
      });
    });

    return new Promise(res => {
      server.listen(port ?? 0, host, () => {
        const addressInfo = server.address() as AddressInfo;
        this.host = addressInfo.address;
        this.port = addressInfo.port;

        console.log(`\nVisualization tool is running at http://${this.host}:${this.port}\n`);
        // child.exec(`xdg-open http://${this.host}:${this.port}`);
        // TODO: See if opening automatically is a good idea
        res();
      });
    });
  }

  private static sendToClient(ws: WebSocket, data: any): void {
    ws.send(JSON.stringify(data));
  }

  private static sendToAllClients(data: any): void {
    this.wss!.clients.forEach(ws => this.sendToClient(ws, data));
  }

  public static async waitForTool(): Promise<void> {
    if (!this.isLaunched()) {
      console.warn('Visualization tool is not running');  // TODO: Convert to error
      return;
    }

    return new Promise(res => {
      let placeClientOnWait: (ws: WebSocket) => void;

      const waitOnMessage = (message: string) => {
        const data = JSON.parse(message);
        if (data.message === 'continue') {
          this.wss!.clients.forEach(ws => {
            this.wss!.off('connection', placeClientOnWait);
            ws.off('message', waitOnMessage);
          });

          this.sendToAllClients({ message: 'continue' });
          res();
        }
      }

      placeClientOnWait = (ws: WebSocket) => {
        ws.on('message', waitOnMessage);
        this.sendToClient(ws, { message: 'wait' });
      }

      this.wss!.clients.forEach(placeClientOnWait);
      this.wss!.on('connection', placeClientOnWait);
    });  // TODO: Effectively wait for web page to respond
  }
}