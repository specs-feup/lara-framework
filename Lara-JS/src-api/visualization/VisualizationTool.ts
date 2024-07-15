import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import WebSocket, { WebSocketServer, MessageEvent } from 'ws';
import { AddressInfo } from 'net';

import { LaraJoinPoint, wrapJoinPoint } from '../LaraJoinPoint.js';
import JoinPoints from '../weaver/JoinPoints.js';


export default class VisualizationTool {
  private static hostname: string | undefined;
  private static port: number | undefined;
  private static wss: WebSocketServer | undefined;
  private static serverClosed: boolean = false;

  public static isLaunched(): boolean {
    return this.wss !== undefined && this.serverClosed === false;
  }

  public static getHostname(): string | undefined {
    return this.hostname;
  }

  public static getPort(): number | undefined {
    return this.port;
  }

  private static onWssError(error: NodeJS.ErrnoException): void {
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
    };

    this.wss!.close();
  }

  public static async launch(hostname: string = '127.0.0.1', port?: number): Promise<void> {
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

    this.wss.on('connection', (ws: WebSocket) => {
      console.log('[server]: Client connected');

      ws.on('message', (message: string) => {
        console.log(`[server]: Received message => ${message}`);
      });

      ws.addEventListener('close', () => {
        console.log('[server]: Client disconnected');
      });
    });  // TODO: Remove this
      
    this.wss.on('connection', ws => this.updateClient(ws));

    this.wss.on('close', () => {
      this.serverClosed = true;
    });

    this.wss.on('error', error => this.onWssError(error));

    return new Promise(res => {
      server.listen(port ?? 0, hostname, () => {
        const addressInfo = server.address() as AddressInfo;
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

  private static sendToClient(ws: WebSocket, data: any): void {
    ws.send(JSON.stringify(data));
  }

  private static sendToAllClients(data: any): void {
    this.wss!.clients.forEach(ws => this.sendToClient(ws, data));
  }

  private static verifyToolIsRunning(): void {
    if (!this.isLaunched()) {
      throw Error('Visualization tool is not running');
    }
  }

  public static async waitForTool(): Promise<void> {
    this.verifyToolIsRunning();

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
    });
  }

  private static toToolJpJson(jp: LaraJoinPoint): any {
    return {
      id: wrapJoinPoint(jp._javaObject.getAstId()),
      type: wrapJoinPoint(jp._javaObject.getAstName()),
      code: wrapJoinPoint(jp._javaObject.getCode()),
      children: jp.children
        .slice()
        .sort((a, b) => wrapJoinPoint(a._javaObject.getLocation()).localeCompare(wrapJoinPoint(b._javaObject.getLocation()), 'en', { numeric: true }))  // TODO: Perform sorting on frontend
        .map(child => this.toToolJpJson(child))
    };
  }

  private static updateClient(ws: WebSocket): void {
    this.sendToClient(ws, { message: 'update', ast: this.toToolJpJson(JoinPoints.root()) });
  }

  public static update(): void {
    this.verifyToolIsRunning();
    this.wss!.clients.forEach(ws => this.updateClient(ws));
  }
}