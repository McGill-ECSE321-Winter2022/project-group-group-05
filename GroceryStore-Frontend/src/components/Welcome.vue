<!--Landing page of the application-->
<template>
  <div id="welcome-outer">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="welcome-inner">
        <!--Temporary skeleton to provide functionalities but not looks-->
        <h1>{{ msg }}</h1>

        <div id="holiday-marquee" v-if="nextHolidayDate">
          <marquee-text :duration="8" :repeat="10" :paused="marqueePause">
            <div
              @mouseenter="marqueePause = !marqueePause"
              @mouseleave="marqueePause = false"
            >
              The store will be <b>closed</b> on <b>{{ nextHolidayDate | formatDate }}</b> for
              <b>{{ nextholidayName }}</b
              >.
              &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
              &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
              &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
            </div>
          </marquee-text>
        </div>
        <br />

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
          <b-button pill variant="info" v-if="isStaff"
            >My staff profile</b-button
          >
        </router-link>
        <router-link to="/ViewHistory">
          <b-button pill variant="info" v-if="isCustomer"
            >My purchase history</b-button
          >
        </router-link>
        <router-link to="/ManageCart">
          <b-button pill variant="info" v-if="isCustomer">My cart</b-button>
        </router-link>

        <hr />
        <b-button
          id="populate-database-button"
          variant="warning"
          v-on:click="genData()"
          >Populate database</b-button
        >
        <b-tooltip target="populate-database-button" triggers="hover">
          <b>Development option:</b> populate database with some data
        </b-tooltip>

        <b-button v-b-modal.opening-hours>See our opening hours</b-button>
        <b-modal id="opening-hours" title="Opening Hours" ok-only scrollable>
          <div id="op-hours-outer">
            <b-table borderless hover :items="openingHours">
              <template #cell(startTime)="data">
                {{ data.value | formatTime }}
              </template>
              <template #cell(endTime)="data">
                {{ data.value | formatTime }}
              </template>
            </b-table>
          </div>
        </b-modal>
      </div>

      <template #overlay>
        <div class="text-center">
          <b-spinner></b-spinner>
          <p class="h2">{{ loadingMsg }}</p>
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
#holiday-marquee {
  background: lightblue;
}
</style>
