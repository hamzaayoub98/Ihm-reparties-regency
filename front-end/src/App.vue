<template>

  <div id="app">
    <div v-if="gameStarted" class="bg">
      <Home></Home>
    </div>
    <div v-else class="starter">
      <div class="row py-4">
        <div class="col align-self-center">
          <h1 class="text-primary d-flex justify-content-center">Embarquement immmédiat !</h1>
        </div>
      </div>
      <div class="row my-4">
        <div class="col">
          <button type="button" class="btn btn-lg btn-primary" v-on:click="startGame()">
            Nouvelle Partie
          </button>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <img
            src="https://raw.githubusercontent.com/kabirbatra03/space-animation/c825fc0dff92bec170b370f6eb425cf1dd34c5ce/assets/spacecraft.svg"
            height="400"
            width="800"
            class="rocket fixed-bottom"
            id="rocket"
          />
        </div>
      </div>
      <div v-if="this.displayError && !this.captainConnected" class="row my-4">
        <div class="col text-danger"><h3>Le mobile du capitaine n'est pas connecté</h3></div>
      </div>
      <div v-if="this.displayError && !this.mecanoConnected" class="row my-4">
        <div class="col text-danger"><h3>Le mobile du mécanicien n'est pas connecté</h3></div>
      </div>
    </div>
  </div>
</template>

<script>
import Home from "@/components/Home";
import Axios from "axios";
import { URL_REST } from './main.js'


export default {
  name: "App",
  components: {
    Home
  },
  data() {
    return {
      gameStarted: false,
      captainConnected: false,
      mecanoConnected: false,
      displayError: false
    };
  },
  methods: {
    startGame: function () {
      if (this.displayError){ //TODO : disable for the moment
        this.displayError = true
        this.captainConnected = false
        this.mecanoConnected = false
      } else {
        Axios.post("http://" + URL_REST + "/start/game", {
          started: true,
        })
          .then((res) => {
            console.log(res);
            this.gameStarted = true;
          })
          .catch((error) => {
            console.log(error);
          });
      }
    },
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #000000;
  width: 100%;
  height: 100%;
  margin: none;
  position: relative;
    background-color: #0e1118;
    background-image: radial-gradient(
    ellipse at bottom,
    #1b2735 30%,
    #090a0f 100%
  );
}
html,
body {
  width: 100%;
  height: 100%;
}
.bg {
  /* The image used */
  background-image: url("./assets/background1.png");

  /* Full height */
  height: 100%;

  /* Center and scale the image nicely */
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}
</style>
