const express = require('express');
const http = require('http');
const WebSocket = require('ws');

const port = 3000;
const domain = 'localhost';

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server: server });

wss.on('connection', (ws) => {
  console.log('[server]: Client connected');

  ws.on('message', (message) => {
    console.log(`[server]: Received message => ${message}`);
  });

  ws.send('Hello! Message from server!');

  ws.on('close', () => {
    console.log('[server]: Client disconnected');
  });
});

app.use(express.static(__dirname));

app.listen(port, () => {
  console.log(`[server]: Server is running at http://${domain}:${port}`);
});