const express = require('express')
const app = express()

app.listen(8080, () => {
    console.log('Serveur à l\'écoute')
  })

app.get('/', function(req, res) {
    res.status(200).send('Bienvenue à bord !');
});
