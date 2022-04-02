<template>
  <div id="app">
    <!--    <img src="./assets/logo.png" />-->
    <div id="app-inner">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: "app",
};
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  /* Needs to be tweaked */
  margin-top: 0;
}
</style>
