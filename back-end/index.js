const express = require('express')
const app = express()

var cors = require('cors')
app.use(cors())

let actionStack = []

function oui(){

}

app.listen(3000, () => {
    console.log('Serveur à l\'écoute')
  })

app.get('/', function(req, res) {
    res.status(200).send('Bienvenue à bord !');
});

app.post('/button', function(request, response){
    console.log(response);
    response.status(200).send("data received")
});
