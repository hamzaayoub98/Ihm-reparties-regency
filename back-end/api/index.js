var express = require('express');
var router = express.Router();
const actions = require('../actions');

let gameStarted = false;

router.get('/', function(req, res) {
    res.status(200).json({"helloworld": "Hello World !"});
});

router.post('/start', (req, res) => {
    res.status(200).json('ok');
    sockets.forEach((s) => s.send("Décollage !"));
});

router.get('/game/status', (req, res) => {
    res.status(200).json({"started": gameStarted});
});

router.get('/action-list',function (request,response){
    response.status(200).json(actions.getBaseActions());
})

router.post('/start/game', function(request, response){
    this.gameStarted = request.body.started;
    console.log("🚀 ~ file: index.js ~ line 51 ~ router.post ~ this.gameStarted", this.gameStarted)
    setInterval(actions.updateDataGame, 1000)
    response.status(200).send("data game status received")
});

router.post('/finish', function(request, response){
    this.gameStarted = request.body.isFinished;
    console.log("🚀 test finish game", this.gameStarted)
    setInterval(actions.updateDataGame, 10000)
    response.status(200).json(actions.getFinishGame());
});    

router.post('/addAntimatiere', function(request, response){
    this.antimatiereValue = request.body.value;
    console.log("🚀 test finish game", this.antimatiereValue)
    response.status(200).json(actions.getAntimatiereValue());
});

router.post('/action', function(request, response){
    actions.processAction(request.body.action)
    console.log("baseActions", actions.getBaseActions())
    response.status(200).send("data received")
});

router.get('/actionvr',function (request,response) {
    action = parseInt(request.query.id)
    actions.processAction(action)
    console.log("baseActions VR", actions.getBaseActions())
    response.status(200).send(actions.getActionStack().includes(action));
})

module.exports = router;