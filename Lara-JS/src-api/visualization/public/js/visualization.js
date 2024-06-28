const socket = new WebSocket('ws://localhost:3000');

socket.onmessage = (event) => {
    console.log(`[client]: Received message => ${event.data}`);
}