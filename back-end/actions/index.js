let counter = 0;
let actionStack = [1,2,"antimatiere","asteroidsVue",7,8]
let showButton = false;
let slider1Value = 0
let slider2Value = 0
let seqRelancerCourant = []
let courantRestart = false

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
},{
    title:"Abaisser le levier",
    id:'lever'
}
]
const nextActions = [{
    title: "Activez l'hyper vitesse",
    id: 6,
},
{
    title: "Détruire les asteroids",
    id: 8,
},
{
    title: "Appuyez sur le bouton rouge",
    id: 2,
}]

const finishGame = {
    isFinished: false,
}

const antimatiereValue = {
    value: 0,
}

function arrayEquals(a, b) {
    return Array.isArray(a) &&
      Array.isArray(b) &&
      a.length === b.length &&
      a.every((val, index) => val === b[index]);
  }

  function processActionSeq(seq){
    courantRestart = arrayEquals(seq.splice(-3), [1,2,3])
    return courantRestart
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

  function processAction(action){
    console.log("test", seqRelancerCourant)

    if (action.id === 1) slider1Value = action.value
    if (action.id === 2) slider2Value = action.value
    console.log("test3", slider1Value, slider2Value)
    if (slider1Value == 100 && slider2Value == 100) {
        seqRelancerCourant.push(1);
    }

    if(action.action === 'slider'){
        baseActions.forEach(baseAction =>{
            if(baseAction.id === 'slider'){
                if(action.value >= baseAction.value - 10 && action.value <= baseAction.value + 10){
                    baseActions.splice(baseActions.indexOf(baseAction),1)
                }
            }
            if(baseAction.id === 6){
                if(action.value === 100){
                    baseActions.splice(baseActions.indexOf(baseAction),1)
                }
            }
        })
    }
    else if (action.action === 'lever'){
            baseActions.forEach(baseAction =>{
                if(baseAction.id === 'lever'){
                    showButton = true
                    baseActions.splice(baseAction.indexOf(baseAction),1)
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

        // Actions du mécano
        if (action.action === "activerLevier"){
            seqRelancerCourant.push(2);
            processActionSeq(seqRelancerCourant)
        }
        if (action.action === "desactiverLevier"){
            seqRelancerCourant.forEach(e => {
                seqRelancerCourant.splice(seqRelancerCourant.indexOf(e),1)
            })
            processActionSeq(seqRelancerCourant)
        }
        if (action === "activerCourant"){ // Action du capitaine
            seqRelancerCourant.push(3)
            console.log("HHIHIHI : " + seqRelancerCourant)
            processActionSeq(seqRelancerCourant)
        }
}

function getBaseActions(){return baseActions}
function getActionStack(){return actionStack}
function getFinishGame(){return finishGame}
function getAntimatiereValue(){return antimatiereValue}
function getShowButton(){return showButton}
function setShowButton(newVal){showButton = newVal}
function getCourantStatus(){return courantRestart}
function getCourantSequence(){return seqRelancerCourant}

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
}
