import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import WebSocket, { WebSocketServer } from 'ws';
import { AddressInfo } from 'net';

import { LaraJoinPoint } from '../LaraJoinPoint.js';
import JoinPoints from '../weaver/JoinPoints.js';
import GenericAstConverter, { FilesCode } from './GenericAstConverter.js';
import ToolJoinPoint from './public/js/ToolJoinPoint.js';

/**
 * @brief Abstract class for a the LARA visualization tool.
 * @details To use this class in a compiler, this class must be extended and
 * the getAstConverter method must be implemented, returning the compiler
 * specialization of the GenericAstConverter class.
 */
export default abstract class GenericVisualizationTool {
  #hostname: string | undefined;
  #port: number | undefined;
  #wss: WebSocketServer | undefined;
  #serverClosed: boolean = false;
  #toolAst: ToolJoinPoint | undefined;
  #prettyHtmlCode: FilesCode | undefined;

  /**
   * @brief True whether the visualization tool is launched, and false otherwise
   */
  public get isLaunched(): boolean {
    return this.#wss !== undefined && this.#serverClosed === false;
  }

  /**
   * @brief Hostname to which the visualization tool is listening to.
   */
  public get hostname(): string | undefined {
    return this.#hostname;
  }

  /**
   * @brief Port to which the visualization tool is listening to.
   */
  public get port(): number | undefined {
    return this.#port;
  }

  /**
   * @brief URL to which the visualization tool is listening to.
   */
  public get url(): string | undefined {
    return this.#hostname && this.#port ? `http://${this.#hostname}:${this.#port}` : undefined;
  }

  /**
   * @brief Updates the stored tool AST and code with the info retrieved from the astConverter.
   * 
   * @param astRoot The root of the wanted AST
   */
  private updateAstAndCode(astRoot: LaraJoinPoint): void {
    const astConverter = this.getAstConverter();
    astConverter.updateAst();
    
    this.#toolAst = astConverter.getToolAst(astRoot);
    this.#prettyHtmlCode = astConverter.getPrettyHtmlCode(astRoot);
  }

  /**
   * @brief WebSocket server error handler.
   * 
   * @param error The error that occurred
   */
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

  /**
   * @brief Launches the visualization tool.
   * 
   * @param hostname The hostname to listen to
   * @param port The port to listen to
   */
  private async launch(hostname: string, port: number): Promise<void> {
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

  /**
   * @brief Sends a message to a specific client
   * 
   * @param ws Client WebSocket
   * @param data Data to be sent
   */
  private sendToClient(ws: WebSocket, data: any): void {
    ws.send(JSON.stringify(data));
  }

  /**
   * @brief Sends a message to all the clients
   * 
   * @param data Message data
   */
  private sendToAllClients(data: any): void {
    this.#wss!.clients.forEach(ws => this.sendToClient(ws, data));
  }

  /**
   * @brief Updates the client with the current tool AST and code.
   * 
   * @param ws Client WebSocket
   */
  private updateClient(ws: WebSocket): void {
    this.sendToClient(ws, {
      message: 'update',
      ast: this.#toolAst!.toJson(),
      code: this.#prettyHtmlCode!,
    });
  }

  /**
   * @brief Updates all the clients with the current tool AST and code.
   */
  private updateAllClients(): void {
    this.#wss!.clients.forEach(ws => this.updateClient(ws));
  }

  /**
   * @brief Waits for the tool to be ready to receive the AST and code.
   */
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

  /**
   * @brief Visualizes the given AST.
   * @details This function launches the visualization tool, if it is not
   * already launched, and updates the web interface with the AST and code,
   * otherwise. This can involve the recompilation of the code.
   * 
   * @param astRoot Root of the AST to be visualized
   * @param port The port to listen to
   * @param hostname The hostname to listen to
   */
  public async visualize(astRoot: LaraJoinPoint = JoinPoints.root(), port: number = 3000, hostname: string = '127.0.0.1'): Promise<void> {
    this.updateAstAndCode(astRoot!);

    if (!this.isLaunched) {
      await this.launch(hostname, port);
    } else {
      this.updateAllClients();
    }

    console.log(`\nVisualization tool is running at ${this.url}\n`);
    await this.waitForTool();
  }

  /**
   * @brief Returns the compiler AST converter.
   */
  protected abstract getAstConverter(): GenericAstConverter;
};