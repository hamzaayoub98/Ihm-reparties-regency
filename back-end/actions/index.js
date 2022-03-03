let counter = 0;
let actionStack = [1,2,"antimatiere","asteroidsVue",7,8]
let showButton = false;
let slider1Value = 0
let slider2Value = 0
let slider3Value = 0
var seqRelancerCourant = []
var courantRestart = false
let activateAntimatière = false;
let readyMissile = false;

const baseActions = [{
    title: "Appuyer sur les boutons rouge et bleu en même temps",
    id:7,
},
{
    title: "Appuyez sur le bouton bleu",
    id: 1,
},
{
    title: "Remettre de l`antimatière",
    id: 'antimatiere',
},
{
    title: "Augmenter les rétro-propulseurs",
    id: 'slider',
    value: 100,
},
// {
//     title:"Abaisser le levier",
//     id:'lever'
// },
    {
        title:"Activer le courant",
        id:'courant'
    },
    {
        title:"Activer la distribution d'antimatière",
        id:'antimater'
    },
    {
        title:"Activer l'hypervitesse",
        id: 6
    }
]
const nextActions = [
{
    title: "Appuyer sur les 4 boutons orange en même temps",
    id:"missileReady",
},
{
    title: "Détruire les asteroids avec un missile",
    id: 8,
},
{
    title: "Appuyez sur le bouton rouge",
    id: 2,
}]

const finishGame = {
    isFinished: false,
}
var antimatiereVRValue = 0;
const antimatiereValue = {
    value: 0,
}

  function processActionSeq(seq){
    console.log("Sequence : " + seq);
    courantRestart = seq.includes(1) && seq.includes(2) && seq.includes(3) && seq.includes(4) || courantRestart
    console.log("Restart : " + courantRestart);
    if (seq.length>5) courantRestart = true
  }

function updateDataGame() {
    counter +=1
    if (counter%10==0 && nextActions.length > 0) {
        let tmpAction = nextActions.sort((a, b) => 0.5 - Math.random()).pop()
        baseActions.push(tmpAction)
        actionStack.push(tmpAction.id)
    }
    if (baseActions.length + nextActions.length ==0) {
        finishGame.isFinished = true;
    }
  }

function processAction(action) {
    if (action.id === 1) {
        slider1Value = action.value
        if (slider1Value > 99) {
            seqRelancerCourant.push(1);
        } else {
            seqRelancerCourant.forEach(e => {
                if(e == 1) seqRelancerCourant.splice(seqRelancerCourant.indexOf(e), 1)
            })
        }
    }
    if (action.id === 2) {
        slider2Value = action.value
        if (slider2Value > 99) {
            seqRelancerCourant.push(2);
        } else {
            seqRelancerCourant.forEach(e => {
                if(e == 2) seqRelancerCourant.splice(seqRelancerCourant.indexOf(e), 1)
            })
        }
    }

    if (action.id === 3) {
        slider3Value = action.value
    }

    if(action === "missileReady") {
        readyMissile = true;
        console.log("readyMissile : " + true)
    }
    

    if (action.action === 'slider') {
        baseActions.forEach(baseAction => {
            if (baseAction.id === 'slider') {
                if (action.value >= baseAction.value - 10 && action.value <= baseAction.value + 10) {
                    baseActions.splice(baseActions.indexOf(baseAction), 1)
                }
            }
            if (baseAction.id === 6) {
                if (action.value === 100) {
                    baseActions.splice(baseActions.indexOf(baseAction), 1)

                }
            }
        })
    }
    else if (action.action === 'lever') {
        baseActions.forEach(baseAction => {
            if (baseAction.id === 'lever') {
                showButton = true
                baseActions.splice(baseActions.indexOf(baseAction), 1)
            }
        })
    }
    else if (action === 'antimater') {
        baseActions.forEach(baseAction => {
            if (baseAction.id === 'antimater') {
                activateAntimatière = true;
                baseActions.splice(baseActions.indexOf(baseAction), 1)
            }
        })
    }
    if (actionStack.includes(action)) {
        actionStack.splice(actionStack.indexOf(action), 1);
        baseActions.forEach(actions => {
            if (actions.id === action) {
                baseActions.splice(baseActions.indexOf(actions), 1);
            }
        })
    }

    // Actions du mécano
    if (action.action === "activerLevier") {
        console.log("action detected :  activerLevier")
        seqRelancerCourant.push(3);
    }
    if (action.action === "desactiverLevier") {
        console.log("action detected :  desactiverLevier")
        seqRelancerCourant.forEach(e => {
            if(e == 3) seqRelancerCourant.splice(seqRelancerCourant.indexOf(e), 1)
        })
    }
    if (action === "activerCourant") { // Action du capitaine
        seqRelancerCourant.push(4)
        console.log("action detected :  activer courant")
        courantRestart = true
        baseActions.forEach(actions => {
            if (actions.id === "courant") {
                baseActions.splice(baseActions.indexOf(actions), 1);
            }
        })
    }
    processActionSeq([...seqRelancerCourant])
}

function getBaseActions(){return baseActions}
function getActionStack(){return actionStack}
function getFinishGame(){return finishGame}
function getAntimatiereValue(){return antimatiereValue}
function incrementAntimatiereVRValue(){antimatiereVRValue= antimatiereVRValue +1;}
function getAntimatiereVRValue(){return antimatiereVRValue}
function getShowButton(){return showButton}
function setShowButton(newVal){showButton = newVal}
function getCourantStatus(){return courantRestart}
function getCourantSequence(){return seqRelancerCourant}
function getSliderValue1(){return slider1Value}
function getSliderValue2(){return slider2Value}
function getSliderValue3(){return slider3Value}
function getActivateAntiMatiere(){return activateAntimatière}
function setCourantStatus(){courantRestart = true}
function getReadyMissile(){return readyMissile}

module.exports = {
    updateDataGame,
    processAction,
    getBaseActions,
    getActionStack,
    getFinishGame,
    getShowButton,
    setShowButton,
    getAntimatiereValue,
    getCourantStatus,
    getCourantSequence,
    getSliderValue1,
    getSliderValue2,
    getSliderValue3,
    getActivateAntiMatiere,
    setCourantStatus,
    incrementAntimatiereVRValue,
    getAntimatiereVRValue,
    getReadyMissile,
    processAction
}
