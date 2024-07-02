import express from 'express';
import http from 'http';
import path from 'path';
import { fileURLToPath } from 'url';
import { WebSocketServer } from 'ws';

const port = 3000;
const domain = 'localhost';

const app = express();
const server = http.createServer(app);
const wss = new WebSocketServer({ server: server });

const filename = fileURLToPath(import.meta.url);
const dirname = path.dirname(filename);

app.use(express.static(path.join(dirname, 'public')));

server.listen(port, () => {
  console.log(`[server]: Server is running at http://${domain}:${port}`);
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