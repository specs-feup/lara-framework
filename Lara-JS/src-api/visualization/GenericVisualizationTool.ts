import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import WebSocket, { WebSocketServer } from 'ws';
import { AddressInfo } from 'net';
import child from 'child_process';

import { LaraJoinPoint } from '../LaraJoinPoint.js';
import JoinPoints from '../weaver/JoinPoints.js';
import GenericAstConverter, { FilesCode } from './GenericAstConverter.js';
import ToolJoinPoint from './public/js/ToolJoinPoint.js';

type VisualizationOptions = {
  astRoot: LaraJoinPoint;
  hostname: string;
  port: number;
}

export default abstract class GenericVisualizationTool {
  #hostname: string | undefined;
  #port: number | undefined;
  #wss: WebSocketServer | undefined;
  #serverClosed: boolean = false;
  #toolAst: ToolJoinPoint | undefined;
  #prettyHtmlCode: FilesCode | undefined;

  get isLaunched(): boolean {
    return this.#wss !== undefined && this.#serverClosed === false;
  }

  get hostname(): string | undefined {
    return this.#hostname;
  }

  get port(): number | undefined {
    return this.#port;
  }

  get url(): string | undefined {
    return this.#hostname && this.#port ? `http://${this.#hostname}:${this.#port}` : undefined;
  }

  private updateAstAndCode(astRoot: LaraJoinPoint): void {
    const astConverter = this.getAstConverter();
    astConverter.updateAst();
    
    this.#toolAst = astConverter.getToolAst(astRoot);
    this.#prettyHtmlCode = astConverter.getPrettyHtmlCode(astRoot);
  }

  private onWssError(error: NodeJS.ErrnoException): void {
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
    };

    this.#wss!.close();
  }

  private async launch({ hostname, port }: VisualizationOptions): Promise<void> {
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
        const addressInfo = server.address() as AddressInfo;
        this.#hostname = addressInfo.address;
        this.#port = addressInfo.port;
        this.#serverClosed = false;
        
        res();
      });
    });
  }

  private sendToClient(ws: WebSocket, data: any): void {
    ws.send(JSON.stringify(data));
  }

  private sendToAllClients(data: any): void {
    this.#wss!.clients.forEach(ws => this.sendToClient(ws, data));
  }

  private updateClient(ws: WebSocket): void {
    this.sendToClient(ws, {
      message: 'update',
      ast: this.#toolAst!.toJson(),
      code: this.#prettyHtmlCode!,
    });
  }

  private async waitForTool(): Promise<void> {
    return new Promise(res => {
      let placeClientOnWait: (ws: WebSocket) => void;

      const waitOnMessage = (message: string) => {
        const data = JSON.parse(message);
        if (data.message === 'continue') {
          this.#wss!.clients.forEach(ws => {
            this.#wss!.off('connection', placeClientOnWait);
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

      this.#wss!.clients.forEach(placeClientOnWait);
      this.#wss!.on('connection', placeClientOnWait);
    });
  }

  public async visualize(astRoot: LaraJoinPoint = JoinPoints.root(), port: number = 3000, hostname: string = '127.0.0.1'): Promise<void> {
    this.updateAstAndCode(astRoot!);

    if (!this.isLaunched) {
      await this.launch({astRoot, hostname, port});
    } else {
      this.#wss!.clients.forEach(ws => this.updateClient(ws));
    }

    console.log(`\nVisualization tool is running at ${this.url}\n`);
    await this.waitForTool();
  }

  protected abstract getAstConverter(): GenericAstConverter;
}