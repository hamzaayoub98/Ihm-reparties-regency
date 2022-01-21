const express = require('express')
const bodyParser = require('body-parser')
const cors = require('cors')
const http = require("http");
const WebSocket = require("ws");
const baseActions = [{
    title:"Appuyez sur le bouton 1",
    id:1,
},{
    title:"Appuyez sur le bouton 2",
    id:2,
},
    {
        title :"Remettre de l`antimatière",
        id: 3,
    }]
const app = express()
app.use(bodyParser.urlencoded({ extended: false }))
app.use(cors())
app.use(bodyParser.json())

const PORT_REST = 3000
const PORT_WS = 4000

const server = http.createServer(app);
const websocketServer = new WebSocket.Server({ server });

let actionStack = []
var sockets = [];

websocketServer.on('connection', function (socket) {
    socket.on('message', msg => {
        console.log(`Message: ${msg}`);
        socket.send("Let's go !")
    });
})
app.listen(PORT_REST, () => {
    console.log('Serveur REST à l\'écoute ! port', PORT_REST)
})
server.listen(PORT_WS, () => {
    console.log("Serveur WS à l\'écoute ! port", PORT_WS);
});

let gameStarted = false;

app.get('/', function(req, res) {
    res.status(200).json({"helloworld": "Hello World !"});
});

app.post('/start', (req, res) => {
    res.status(200).json('ok');
    sockets.forEach((s) => s.send("Décollage !"));
});

app.get('/game/status', (req, res) => {
    res.status(200).json({"started": gameStarted});
});

app.post('/start/game', function(request, response){
    this.gameStarted = request.body.started;
    console.log("🚀 ~ file: index.js ~ line 51 ~ app.post ~ this.gameStarted", this.gameStarted)
    response.status(200).send("data game statis received")
});

app.post('/button', function(request, response){
    processAction(request.body.action)
    console.log(baseActions)
    response.status(200).send("data received")
});


function processAction(action){
    if(actionStack.includes(action)){
        actionStack.splice(actionStack.indexOf(action),1);
        baseActions.forEach(actions =>{
            if(actions.id === action){
                baseActions.splice(baseActions.indexOf(actions),1);
            }
        })
    }

}
