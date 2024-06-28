const express = require('express');
const http = require('http');
const path = require('path');
const WebSocket = require('ws');

const port = 3000;
const domain = 'localhost';

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server: server });

app.use(express.static(path.join(__dirname, 'public')));

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