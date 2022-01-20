<template>
  <div id="app" :style="{'background-image':'url(' + require('./assets/background.png') + ')',backgroundPosition: 'center center',
    backgroundSize: 'cover'}">
    <Home></Home>

  </div>
</template>

<script>

import Home from "./components/Home";

export default {
  name: 'App',
  components: {
    Home,
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
