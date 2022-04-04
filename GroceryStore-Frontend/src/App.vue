<template>
  <div id="app">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null">
        <b-navbar
          toggleable="lg"
          type="dark"
          variant="dark"
          class="light-box-shadow mb-3"
          :aria-busy="isLoading"
        >
          <b-navbar-brand to="/" v-b-tooltip title="Back to home page">
            <b-icon
              icon="cart4"
              aria-hidden="true"
              font-scale="1.75"
              class="rounded-circle bg-light p-1 mr-2"
              variant="danger"
            ></b-icon>
            321Grocery<sub><i>&nbsp; by Group 05</i></sub></b-navbar-brand
          >

          <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

          <b-collapse id="nav-collapse" is-nav>
            <b-navbar-nav>
              <b-nav-item v-on:click="clickedOpeningH"
                >Our Opening Hours</b-nav-item
              >
              <b-nav-item v-on:click="clickedHoliday">Our Holidays</b-nav-item>
              <b-nav-item
                to="/CreateCustomerForm"
                v-if="!LOGIN_STATE.state.isLoggedIn"
                >Create Account</b-nav-item
              >
            </b-navbar-nav>

            <b-navbar-nav class="ml-auto">
              <b-nav-item to="/LoginForm" v-if="!LOGIN_STATE.state.isLoggedIn"
                >Sign In</b-nav-item
              >
              <b-nav-item-dropdown v-else :text="greetingMsg" right>
                <b-dropdown-item v-on:click="clickedProfile"
                  >My Profile</b-dropdown-item
                >
                <b-dropdown-item v-on:click="clickedLogout"
                  >Sign Out</b-dropdown-item
                >
              </b-nav-item-dropdown>
              <b-nav-item to="/ViewHistory" :disabled="!isCustomer"
                >My Purchase History</b-nav-item
              >
              <b-nav-item to="/ManageCart" :disabled="!isCustomer"
                >My Cart</b-nav-item
              >
            </b-navbar-nav>
          </b-collapse>
        </b-navbar>
      </div>
    </b-overlay>
    <!-- Show store hours -->
    <b-modal id="opening-h" title="Opening Hours" ok-only scrollable>
      <div id="op-hours-outer">
        <b-table striped :items="openingHours">
          <template #cell(startTime)="data">
            {{ data.value | formatTime }}
          </template>
          <template #cell(endTime)="data">
            {{ data.value | formatTime }}
          </template>
        </b-table>
      </div>
    </b-modal>

    <!-- Show store holidays -->
    <b-modal id="list-of-holidays" title="Holidays" ok-only scrollable>
      <div id="list-of-holidays-outer">
        <b-table striped :items="holidays">
          <template #cell(date)="data">
            {{ data.value | formatDate }}
          </template>
        </b-table>
      </div>
    </b-modal>

    <div id="app-inner">
      <router-view></router-view>
    </div>
  </div>
</template>

<script src="./AppScript.js"></script>

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
.light-box-shadow {
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.1), 0 3px 6px 0 rgba(0, 0, 0, 0.09);
}
</style>
