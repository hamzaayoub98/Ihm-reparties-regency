const express = require('express')
const bodyParser = require('body-parser')
const cors = require('cors')
const http = require("http");
const WebSocket = require("ws");
const baseActions = [{
    title: "Appuyez sur le bouton 1",
    id: 1,
}, {
    title: "Appuyez sur le bouton 2",
    id: 2,
},
{
    title: "Remettre de l`antimatière",
    id: 3,
},
{
    title: "Augmenter les rétro-propulseurs à 80%",
    id: 'slider',
    value: 80,
}]
const nextActions = [{
    title: "Remettre la gravité",
    id: 4,
}, {
    title: "Appuyez sur le bouton 1",
    id: 5,
}, {
    title: "Activez l'hyper vitesse",
    id: 6,
}]

const finishGame = {
    isFinished: true,
}

const app = express()
app.use(bodyParser.urlencoded({ extended: false }))
app.use(cors())
app.use(bodyParser.json())

const PORT_REST = 3000
const PORT_WS = 4000

const server = http.createServer(app);
const websocketServer = new WebSocket.Server({ server });
let sliderValue = 0;
let counter = 0;
let actionStack = [1,2,3]
var sockets = [];
websocketServer.on('sliderValue',function (socket){
   console.log("")
});
websocketServer.on('connection', function (socket) {
    socket.on('message', msg => {
        const rawMsg = `${msg}`;
        const trimmed = rawMsg.split(',');
        console.log(`Message: ${msg}`);
        if(trimmed[0] === 'sliderValue'){
            sliderValue = msg[1];
            processAction({action:'slider'})
            socket.send("slider value is updated");
        }
        else{
            socket.send("Let's go !")
        }
    });
})

function updateDataGame() {
    counter +=1
    if (counter%5==0 && nextActions.length > 0) {
        let tmpAction = nextActions.sort((a, b) => 0.5 - Math.random()).pop()
        baseActions.push(tmpAction)
        actionStack.push(tmpAction.id)
    }
    if (baseActions.length + nextActions.length ==0) {
        finishGame.isFinished = true;
        console.log("🚀 ~ file: index.js ~ line 79 ~ updateDataGame ~ isFinished", finishGame.isFinished)
        
    }
  }


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

app.get('/action-list',function (request,response){
    // console.log("envoie des actions au capitaine")
    response.status(200).json(baseActions);
})

app.post('/start/game', function(request, response){
    this.gameStarted = request.body.started;
    console.log("🚀 ~ file: index.js ~ line 51 ~ app.post ~ this.gameStarted", this.gameStarted)
    setInterval(updateDataGame, 1000)
    response.status(200).send("data game status received")
});

app.post('/finish', function(request, response){
    this.gameStarted = request.body.isFinished;
    console.log("🚀 test finish game", this.gameStarted)
    setInterval(updateDataGame, 10000)
    response.status(200).json(finishGame);
});    

app.post('/action', function(request, response){
    processAction(request.body.action)
    console.log("baseActions", baseActions)
    response.status(200).send("data received")
});

app.get('/actionvr',function (request,response) {
    action = parseInt(request.query.id)
    processAction(action)
    console.log("baseActions VR", baseActions)
    response.status(200).send(actionStack.includes(action));
})




function processAction(action){
    if(action.action === 'slider'){
        baseActions.forEach(action =>{
            if(action.id === 'slider'){
                if(sliderValue >= 80){
                    baseActions.splice(baseActions.indexOf(action),1)
                }
            }
        })
    }
    if(actionStack.includes(action)){
        actionStack.splice(actionStack.indexOf(action),1);
        baseActions.forEach(actions =>{
            if(actions.id === action){
                baseActions.splice(baseActions.indexOf(actions),1);
            }
        })
    }
}
