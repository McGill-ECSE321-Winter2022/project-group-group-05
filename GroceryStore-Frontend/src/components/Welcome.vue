<!--Landing page of the application-->
<template>
  <div>
    <h1>{{ msg }}</h1>
  </div>
</template>

<script>
export default {
  name: "Welcome",
  data() {
    return {
      msg: "Welcome to the Grocery Store",
    };
  },
};
</script>

<style scoped></style>
