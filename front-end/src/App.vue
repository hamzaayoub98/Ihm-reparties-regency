<template>
  <div id="app">
    <Home id="home"></Home>
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
  margin-top: 60px;
}
</style>
