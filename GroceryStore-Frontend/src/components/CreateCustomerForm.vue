<!--Visibility: notLoggedIn-->
<!--Customer creates their own account-->
<template>
  <div id="customer-form-outer" v-if="!LOGIN_STATE.state.isLoggedIn">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="customer-form-inner">
        <p class="h2 text-center mb-5">Create a New Customer Account</p>
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
            >
            </b-form-input>
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
          <b-form-group
            id="email-input-group"
            label="Your email"
            label-for="email-input"
            label-size="lg"
          >
            <b-form-input
              type="email"
              id="email-input"
              v-model="form.email"
              placeholder="Enter email"
              required
            ></b-form-input>
          </b-form-group>
          <b-form-group
            id="address-input-group"
            label="Your address"
            label-for="address-input"
            label-size="lg"
          >
            <b-form-input
              id="address-input"
              v-model="form.address"
              placeholder="Enter address"
              required
            >
            </b-form-input>
          </b-form-group>
          <b-form-checkbox v-model="form.isLocal"
            >This is a local address</b-form-checkbox
          >
          <div class="text-center" v-show="formError">
            <p style="color: red">{{ formError }}</p>
          </div>
          <div class="text-center mt-5">
            <b-button
              type="submit"
              variant="primary"
              v-bind:disabled="isLoading"
              class="mr-5"
              >Create account</b-button
            >
            <b-button
              type="reset"
              variant="danger"
              v-bind:disabled="isLoading"
              class="ml-5"
              >Reset form</b-button
            >
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

<script src="./CreateCustomerFormScript.js"></script>

<style scoped>
#customer-form-outer {
  width: 650px;
  margin-top: 50px;
  margin-left: auto;
  margin-right: auto;
  text-align: left;
}
#customer-form-inner {
  padding: 50px;
}
</style>
