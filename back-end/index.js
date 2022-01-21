const express = require('express')
const app = express()
var bodyParser = require('body-parser')
app.use(bodyParser.urlencoded({ extended: false }))
var cors = require('cors')
app.use(cors())
app.use(bodyParser.json())
let actionStack = [1,2,3]

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

app.listen(3000, () => {
    console.log('Serveur à l\'écoute')
  })

app.get('/', function(req, res) {
    res.status(200).send('Bienvenue à bord !');
});

app.post('/button', function(request, response){
    processAction(request.body.action)
    console.log(actionStack)
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
