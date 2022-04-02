<template>
  <div id="app">
    <router-link to="/">
          <b-button variant="outline-primary" v-if="$route.path != '/'"
            >Back to the welcome page &#128722;</b-button
          >
        </router-link>
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
