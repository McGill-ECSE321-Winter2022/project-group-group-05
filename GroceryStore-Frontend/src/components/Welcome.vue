<!--Landing page of the application-->
<template>
  <div id="welcome-outer">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="welcome-inner">
        <!--Temporary skeleton to provide functionalities but not looks-->
        <h1>{{ msg }}</h1>
        <router-link to="/LoginForm">
          <b-button variant="success" v-if="!LOGIN_STATE.state.isLoggedIn"
          >Sign in</b-button
          >
        </router-link>
        <b-button
          variant="outline-info"
          v-if="LOGIN_STATE.state.isLoggedIn"
          @click="logout()"
        >
          <b-icon icon="power" aria-hidden="true"></b-icon> Sign out</b-button
        >
        <router-link to="/CreateCustomerForm">
          <b-button variant="primary" v-if="!LOGIN_STATE.state.isLoggedIn"
          >Create account</b-button
          >
        </router-link>
        <hr />
        <router-link to="/ManageProfile">
          <b-button pill variant="info" v-if="isCustomer">My profile</b-button>
        </router-link>
        <router-link to="/ManageStaffProfile">
          <b-button pill variant="info" v-if="isStaff">My staff profile</b-button>
        </router-link>
        <router-link to="/ViewHistory">
          <b-button pill variant="info" v-if="isCustomer"
          >My purchase history</b-button
          >
        </router-link>
        <router-link to="/ManageCart">
          <b-button pill variant="info" v-if="isCustomer">My cart</b-button>
        </router-link>
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

<script src="./WelcomeScript.js"></script>

<style scoped>
#welcome-inner {
  padding: 100px;
}
</style>
