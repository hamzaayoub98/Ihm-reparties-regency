var express = require('express');
var router = express.Router();
const actions = require('../actions');

let gameStarted = false;
let antimatiereValue = 0;
let antimatiereVRValue = 0;
let noMoreAntimatiere = false;
let hyperVitesseActivated = false;
let placerMissile = false;
let readyMissile = false;
let launchedMissile = false;

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
});

router.get('/game/finish', (req, res) => {
    res.status(200).json({"isFinished": actions.getFinishGame().isFinished});
});

router.post('/start/game', function(request, response){
    this.gameStarted = request.body.started;
    console.log("🚀 ~ file: index.js ~ line 51 ~ router.post ~ this.gameStarted", this.gameStarted)
    setInterval(actions.updateDataGame, 1000)
    response.status(200).send("data game status received")
});

router.post('/finish', function(request, response){
    this.gameFinished = request.body.isFinished;
    console.log("🚀 test finish game", this.gameStarted)
    setInterval(actions.updateDataGame, 10000)
    response.status(200).json(actions.getFinishGame());
});

router.get('/show-button',function(request,response){
    actions.setShowButton(true);
    actions.setCourantStatus();
    response.status(200).json("new val is  : " + actions.getShowButton())
})

router.post('/action', function(request, response){
    actions.processAction(request.body.action)
    // console.log("baseActions", actions.getBaseActions())
    response.status(200).json()
});

router.get('/actionvr', async function (request,response) {
    let tmp = request.query.id

    actions.processAction({
        action: tmp,
    })
    // console.log("baseActions VR", actions.getBaseActions())
    response.status(200).send(actions.getActionStack().includes(tmp));
})

/* ########################### COURANT ###########################
*/
router.get('/courant/status', (req, res) => {
    res.status(200).json({"restart": actions.getCourantStatus()});
});

router.post('/courant/status', (req, res) => {
    res.status(200).json('ok');
    actions.setCourantStatus();
});

router.get('/courant/seq', (req, res) => {
    res.status(200).json({"sequence": actions.getCourantSequence()});
});

router.get('/slider1/value', (req, res) => {
    res.status(200).json(actions.getSliderValue2());
});

router.get('/slider2/value', (req, res) => {
    res.status(200).json(actions.getSliderValue1());
});
router.get('/activateMater',(req, res) => {
    actions.processAction('antimater')
    res.status(200).json("antimater is now active")
})

router.get('/antimatiere/unlocked',(req, res) => {
    res.status(200).json({"unlocked": actions.getActivateAntiMatiere()})
})

/* ########################### ANTIMATIERE ###########################
*/
router.post('/addAntimatiere', function(request, response){
    this.antimatiereValue = request.body.value;
    console.log("🚀 test antimatiere value : ", this.antimatiereValue)
    response.status(200).json(actions.getAntimatiereValue());
});

router.get('/getVRAntimatiere', function(request, response){
    console.log("getVRAntimatiere ", actions.getAntimatiereVRValue())
    response.status(200).json({"antimatiereValueVR": actions.getAntimatiereVRValue()});
});

router.get('/addVRAntimatiere', function(request, response){
    actions.incrementAntimatiereVRValue();
    console.log("Vr adding 1 antimatter, antimatiereVRValue = ", actions.getAntimatiereVRValue())
    response.status(200).json("value added");
});

router.get('/antimatiere',function (request,response){
    console.log("Getting antimatiere value : " + this.antimatiereValue);
    response.status(200).json({"antimatiereValue": this.antimatiereValue});
});

router.get('/no-more-antimatiere', function (request,response){
    this.noMoreAntimatiere = true;
    actions.processAction('antimatiere')
    // console.log("baseActions", actions.getBaseActions())
    response.status(200).json({"noMoreAntimatiere": this.noMoreAntimatiere})
});

router.get('/is-there-no-more-antimatiere', function (request,response){
    // console.log("baseActions", actions.getBaseActions())
    response.status(200).json({"value": this.noMoreAntimatiere})
});

router.get('/antimatiere/value', function (request,response){
    // console.log("baseActions", actions.getBaseActions())
    response.status(200).json({"value": this.antimatiereValue})
});


/* ########################### HYPERVITESSE ###########################
*/
router.get('/hypervitesse', function (request,response){
    // console.log("baseActions", actions.getBaseActions())
    if(actions.getSliderValue3() == 100){
        this.hyperVitesseActivated = true
    } else {
        this.hyperVitesseActivated = false
    }
    response.status(200).json({"hyperVitesseStatus": this.hyperVitesseActivated})
});


router.post('/hypervitesse/activated', function(request, response){
    actions.getFinishGame().isFinished= true
    response.status(200).json(actions.getFinishGame());
});

/* ########################### MISSILE ###########################
*/
router.get('/placerMissile', function (request,response){
    this.placerMissile = true
    response.status(200).json({"placerMissile": this.placerMissile})
});

router.get('/missile/placed', function (request,response){
    response.status(200).json({"missilePlaced": this.placerMissile})
});

// router.get('/readyMissile', function (request,response){
//     this.readyMissile = true
//     response.status(200)
// });

router.get('/missile/ready', function (request,response){
    response.status(200).json({"missileReady": actions.getReadyMissile()})
});

router.get('/launchMissile', function (request,response){
    this.launchedMissile = true
    launchedMissile = true
    response.status(200).json("missile inbound !")
});

router.get('/missile/launched', function (request,response){
    response.status(200).json({"missileLaunched": launchedMissile})
});




module.exports = router;
