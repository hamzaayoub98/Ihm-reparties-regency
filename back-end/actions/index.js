let counter = 0;
let actionStack = [1,2,"antimatiere","asteroidsVue",7,8]
let showButton = false;

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
    value: 80,
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
}

function getBaseActions(){return baseActions}
function getActionStack(){return actionStack}
function getFinishGame(){return finishGame}
function getAntimatiereValue(){return antimatiereValue}
function getShowButton(){return showButton}
function setShowButton(newVal){showButton = newVal}

module.exports = {
    updateDataGame,
    processAction,
    getBaseActions,
    getActionStack,
    getFinishGame,
    getShowButton,
    setShowButton,
}
