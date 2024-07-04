import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import WebSocket, { WebSocketServer } from 'ws';
import Query from '../weaver/Query.js';
import { AddressInfo } from 'net';

export default class VisualizationTool {
  private static host: string | undefined;
  private static port: number | undefined;

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
    const wss = new WebSocketServer({ server: server });

    const filename = fileURLToPath(import.meta.url);
    const dirname = path.dirname(filename);

    app.use(express.static(path.join(dirname, 'public')));

    wss.on('error', error => {
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

  public static async waitForTool(): Promise<void> {
    if (!this.isLaunched()) {
      console.warn('Visualization tool is not running');  // TODO: Convert to error
      return;
    }
    await new Promise(() => {});  // TODO: Effectively wait for web page to respond
  }
}