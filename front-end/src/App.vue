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
      <div class="row">
        <div class="col">
          <button type="button" class="btn btn-primary" v-on:click="startGame()">
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
    </div>
  </div>
</template>

<script>
import Home from "@/components/Home";
import Axios from "axios";

export default {
  name: "App",
  components: {
    Home,
  },
  data() {
    return {
      gameStarted: false,
    };
  },
  mounted() {
    Axios.get("http://localhost:3000/game/status").then(
      (response) => ((this.gameStarted = response.data.started), console.log(response))
    );
  },
  methods: {
    startGame: function () {
      Axios.post("http://localhost:3000/start/game", {
        started: true,
      })
        .then((res) => {
          console.log(res);
          this.gameStarted = true;
        })
        .catch((error) => {
          console.log(error);
        });
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
  background-image: url("./assets/background.png");

  /* Full height */
  height: 100%;

  /* Center and scale the image nicely */
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}
</style>
