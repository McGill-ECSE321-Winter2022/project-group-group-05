<!--Visibility: notLoggedIn-->
<!--Unified login form for everyone-->
<template>
  <div class="login-form" v-if="!LOGIN_STATE.state.isLoggedIn">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="login-form-inner">
        <p class="h2 text-center mb-5">Sign In to Your Account</p>
        <b-form @submit="onsubmit">
          <b-form-group
            id="username-input-group"
            label="Your username"
            label-for="username-input"
            label-size="lg"
          >
            <b-form-input
              id="username-input"
              v-model="form.username"
              placeholder="Enter username"
              required
            ></b-form-input>
          </b-form-group>
          <b-form-group
            id="password-input-group"
            label="Your password"
            label-for="password-input"
            label-size="lg"
          >
            <b-form-input
              type="password"
              id="password-input"
              v-model="form.password"
              placeholder="Enter password"
              required
            ></b-form-input>
          </b-form-group>
          <div class="text-center" v-show="formError">
            <p style="color: red">{{ formError }}</p>
          </div>
          <div class="text-center mt-5">
            <b-button
              type="submit"
              variant="primary"
              v-bind:disabled="isLoading"
              >Sign in
            </b-button>
          </div>
        </b-form>
      </div>

      <template #overlay>
        <div class="text-center h2">
          <b-icon icon="stopwatch" font-scale="3" animation="cylon"></b-icon>
          <p id="wait-message">Please wait...</p>
        </div>
      </template>
    </b-overlay>
  </div>
</template>

<script src="./LoginFormScript.js"></script>

<style scoped>
.login-form {
  border: 1px solid black;
  width: 600px;
  margin-top: 100px;
  margin-left: auto;
  margin-right: auto;
  text-align: left;
}
#login-form-inner {
  padding: 100px;
}
</style>
