<template>
  <div id="app" :style="{'background-image':'url(' + require('./assets/background.png') + ')',backgroundPosition: 'center center',
    backgroundSize: 'cover'}">
      <div v-if="gameStarted">
        <Home></Home>
      </div>
      <div v-else>
        <h1>Bienvenue à bord !</h1>
        <button v-on:click="startGame()">Nouvelle Partie</button>
    </div>
  </div>
</template>

<script>
import Home from "@/components/Home";
import  Axios from 'axios';

export default {
  name: 'App',
  components: {
    Home,
  },
  data(){
    return {
        gameStarted : false,
    }
  },
  mounted() {
    Axios.get("http://localhost:3000/game/status")
    .then(response =>(this.gameStarted = response.data.started,
    console.log(response)));
  },
  methods: {
    startGame:function () {
        Axios.post('http://localhost:3000/start/game', {
            started: true,
        })
            .then(res => {
              console.log(res)
              this.gameStarted = true;
            })
            .catch(error => {
                console.log(error);
    });
    },
  }
}
</script>

<style>
#app {
  display: flex;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #000000;
  width: 100%;
  height: 100%;
  margin: none;
  
  
}
html,body{
width: 100%;
height: 100%;
}

</style>
