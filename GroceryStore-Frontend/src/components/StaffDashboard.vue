<!--Visibility: owner, employee-->
<!--Buttons for: ManageCategories, ManageCustomers, ManageEmployee, -->
<!--ManageItems, ManageOpeningHours, ManagePaidOrders, ManageSchedules, -->
<!--ManageStaffProfile, ViewCompletedOrders, ViewSchedules, -->
<!--Action Button: Purge All POS Carts (call purgePOSCarts service)-->

<!--Visibility of buttons should match visibility of corresponding component-->
<template>
  <div class="center">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="staffdashboard-inner">

        <h1>{{ msg }}</h1>

        <router-link to="/ManageCategories">
          <b-button block variant="success btn" v-if="isOwner"
            >Manage Categories</b-button
          >
        </router-link>

        <router-link to="/ManageCustomers">
          <b-button block variant="success btn" v-if="isEmployee || isOwner"
            >Manage Customers</b-button
          >
        </router-link>

        <router-link to="/ManageEmployee">
          <b-button block variant="success btn" v-if="isOwner"
            >Manage Employee</b-button
          >
        </router-link>

        <router-link to="/ManageItems">
          <b-button block variant="success btn" v-if="isEmployee || isOwner"
            >Manage Items</b-button
          >
        </router-link>

        <router-link to="/ManageOpeningHours">
          <b-button block variant="success btn" v-if="isOwner"
            >Manage Opening Hours</b-button
          >
        </router-link>

        <router-link to="/ManagePaidOrders">
          <b-button block variant="success btn" v-if="isEmployee || isOwner"
            >Manage Paid Orders</b-button
          >
        </router-link>

        <router-link to="/ManageSchedules">
          <b-button block variant="success btn" v-if="isOwner"
            >Manage Schedules</b-button
          >
        </router-link>

        <router-link to="/ManageStaffProfile">
          <b-button block variant="success btn" v-if="isEmployee || isOwner"
            >Manage Staff Profile</b-button
          >
        </router-link>

        <router-link to="/ViewCompletedOrders">
          <b-button block variant="success btn" v-if="isEmployee || isOwner"
            >View Completed Orders</b-button
          >
        </router-link>

        <router-link to="/ViewSchedules">
          <b-button block variant="success btn" v-if="isEmployee"
            >View Schedules</b-button
          >
        </router-link>

        <router-link to="/PointOfSale">
          <b-button 
          block variant="danger btn" 
          v-if="isOwner"
          v-on:click="purgePOSCarts()"
            >Purge All POS Carts</b-button
          >
        </router-link>
      </div>

      <template #overlay>
        <div class="text-center">
          <b-spinner></b-spinner>
          <p class="h2">Waiting for database...</p>
        </div>
      </template>
    </b-overlay>
  </div>
</template>

<script>
import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";

export default {
  name: "StaffDashboard",
  data() {
    return {
      LOGIN_STATE,
      isEmployee:
        LOGIN_STATE.state.userType === "Employee",
      isOwner: 
        LOGIN_STATE.state.userType === "Owner",
        isLoading: false,
    };
  },

  methods: {
    purgePOSCarts() {
      this.isLoading = true;
      AXIOS.delete("/purchase/pos/delete")
    }
  }
};
</script>

<style scoped>
.center {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  border: 10px solid #ffffff;
  padding: 10px;
  margin: auto;
  width: 60%;
}
#staffdashboard-inner {
  padding: 80px;
}

.btn {
    margin: 15px auto;
}

</style>
