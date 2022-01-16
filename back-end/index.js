const express = require('express')
const app = express()
var bodyParser = require('body-parser')
app.use(bodyParser.urlencoded({ extended: false }))
var cors = require('cors')
app.use(cors())
app.use(bodyParser.json())
let actionStack = []

function oui(){

}

app.listen(3000, () => {
    console.log('Serveur à l\'écoute')
  })

app.get('/', function(req, res) {
    res.status(200).send('{helloworld: "Hello World !"}');
});

app.post('/button', function(request, response){
    actionStack.push(request.body.action)
    console.log(actionStack)
    response.status(200).send("data received")
});
