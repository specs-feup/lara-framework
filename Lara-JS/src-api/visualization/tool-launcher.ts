import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import WebSocket, { WebSocketServer } from 'ws';

const launchVisualizationTool = (domain: string, port: number) => {
  const app = express();
  const server = http.createServer(app);
  const wss = new WebSocketServer({ server: server });

  const filename = fileURLToPath(import.meta.url);
  const dirname = path.dirname(filename);

  app.use(express.static(path.join(dirname, 'public')));

  server.listen(port, () => {
    console.log(`[server]: Server is running at http://${domain}:${port}`);
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
};

launchVisualizationTool('localhost', 3000);