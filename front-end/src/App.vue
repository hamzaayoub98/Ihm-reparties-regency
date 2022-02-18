<template>

  <div id="app">
    <div v-if="gameStarted" class="bg">
      <Home></Home>
    </div>
    <div v-else class="starter">
      <div class="row py-4">
        <div class="col align-self-center">
          <h1 class="text-danger" v-touch:swipe="swipeHandler">Swipe Me</h1>
          <h1 class="text-danger" v-touch:tap="logger">Tape Me</h1>
          <button class="btn-lg mr-4" id="b2">Btn 2</button>
          <button class="btn-lg" id="b1">Btn 1</button>
          <!-- <h1 class="text-primary d-flex justify-content-center">Embarquement immmédiat !</h1> -->

          <h2 class="text-success">event detected : {{this.eventDetected}}</h2>
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
          <!-- <img
            src="https://raw.githubusercontent.com/kabirbatra03/space-animation/c825fc0dff92bec170b370f6eb425cf1dd34c5ce/assets/spacecraft.svg"
            height="400"
            width="800"
            class="rocket fixed-bottom"
            id="rocket"
          /> -->
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
  mounted() {
          let button1Pressed = false;
          let button2Pressed = false;
          var self = this;
            this.doc = document.getElementById("home")
            this.button1 = document.getElementById("b1")
            this.button2 = document.getElementById("b2")
            this.button1.addEventListener('touchstart',function (event){
                console.log("b1",event);
                button1Pressed = true;
                self.eventDetected = "single tap b1"

                if(button1Pressed && button2Pressed){
                  console.log("multi touch detected")
                  self.eventDetected = "multi tap"
                }
          });
          this.button2.addEventListener('touchstart',function (event){
                console.log("b2",event);
                button2Pressed = true;
                self.eventDetected = "single tap b2"
              console.log(button1Pressed, button2Pressed)
            if(button1Pressed && button2Pressed){
              console.log("multi touch detected")
              self.eventDetected = "multi tap"
            }
          });
          this.button1.addEventListener('touchend',function (event){
            console.log("b1-end",event);
            button1Pressed = false;
          });
          this.button2.addEventListener('touchend',function (event){
            console.log("b2-end",event);
            button2Pressed = false;
          });
        },
  data() {
    return {
      eventDetected: null,
      gameStarted: false,
      captainConnected: false,
      mecanoConnected: false,
      displayError: false
    };
  },
  methods: {
    logger (msg){
      this.eventDetected = "single tap"
      console.log("test", msg)
    },
    swipeHandler (direction) {
        this.eventDetected = "swipe " + direction
        console.log(direction)  // May be left / right / top / bottom
    },
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
