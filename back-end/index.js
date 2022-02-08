const express = require('express')
const bodyParser = require('body-parser')
const cors = require('cors')
const http = require("http");
const WebSocket = require("ws");
const api = require('./api');
const actions = require('./actions')

const app = express()
app.use(bodyParser.urlencoded({ extended: false }))
app.use(cors())
app.use(bodyParser.json())
app.use('', api);
app.use(express.static('public'));

const PORT_REST = 3000
const PORT_WS = 4000

const server = http.createServer(app);
const websocketServer = new WebSocket.Server({ server });

let frontWS;
let mobileWS;
websocketServer.on('connection', function (socket, req) {
    if (req.url.includes("0")) {
        console.log('Front is connected');
        frontWS = socket;
        socket.on('message', onMessageFromFront);

    } else {
        console.log("Mobile is connected");
        mobileWS = socket;
        socket.on('message', onMessageFromMobile);

    }
})

function onMessageFromFront(msg) {
    console.log("Message received from front:", msg.toString())
    if (mobileWS) mobileWS.send(msg.toString())

    const rawMsg = `${msg}`;
    let trimmed = rawMsg.split(',');
    if(trimmed[0] === 'sliderValue'){
        let sliderValueTmp = parseInt(trimmed[1]);
        actions.processAction({action:'slider', value: sliderValueTmp})
    }
}

function onMessageFromMobile(msg) {
    console.log("Message received from mobile:", msg.toString())
    if (frontWS) frontWS.send(msg.toString())
}



app.listen(PORT_REST, () => {
    console.log('Serveur REST à l\'écoute ! port', PORT_REST)
})
server.listen(PORT_WS, () => {
    console.log("Serveur WS à l\'écoute ! port", PORT_WS);
});



module.exports = app;