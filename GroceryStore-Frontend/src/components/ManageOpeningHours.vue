<!--Visibility: owner-->
<!--Edit opening hours-->
<template>
  <b-container fluid>
    <b-row class="vh-100" align-v="stretch">
      <b-col md="auto"><staff-dashboard></staff-dashboard></b-col>
      <b-col>
        <h1 class="header_style">Manage Opening Hours</h1>
        <b-alert show variant="danger" v-if="!isOwnerLoggedIn"
          ><h4 class="alert-heading">Error:</h4>
          You must be logged in as an Owner to access this page.
        </b-alert>
        <b-alert
          :show="dismissCountDown"
          dismissible
          variant="danger"
          @dismissed="dismissCountDown = 0"
          @dismiss-count-down="countDownChanged"
        >
          Error: {{ errorMessage }}
        </b-alert>
        <div v-if="isOwnerLoggedIn">
          <!-- Table of Weekdays -->
          <b-table
            class="hours_table_style"
            hover
            fixed
            :items="items"
            :fields="fields"
            @row-clicked="editOpeningHours"
          >
            <template #cell(startTime)="row">
              {{ row.item.closed ? "Closed" : row.item.startTime | timeFormat }}
            </template>
            <template #cell(endTime)="row">
              {{ row.item.closed ? "Closed" : row.item.endTime | timeFormat }}
            </template>
          </b-table>
          <!-- Popup editing menu -->
          <b-modal
            id="editOpeningHours"
            ref="modal"
            :title="selectedWeekDay"
            @hidden="resetWeekdayForm"
            @ok="handleOk"
          >
            <template>
              <form ref="form" @submit.stop.prevent="updateOpeningHours">
                <b-form-group
                  label="Opening Time"
                  label-for="opening-time-input"
                  invalid-feedback="Opening Time must come before closing time"
                  :state="isTimeValid"
                  ><b-form-input
                    id="opening-time-input"
                    v-model="selectedStartTime"
                    :state="isTimeValid"
                    :type="'time'"
                    required
                  ></b-form-input>
                </b-form-group>
                <b-form-group
                  label="Closing Time"
                  label-for="closing-time-input"
                  invalid-feedback="Opening Time must come before closing time"
                  :state="isTimeValid"
                  ><b-form-input
                    id="closing-time-input"
                    v-model="selectedEndTime"
                    :state="isTimeValid"
                    :type="'time'"
                    required
                  ></b-form-input>
                </b-form-group>
              </form>
            </template>
            <template #modal-footer="{ ok, cancel }">
              <!-- Emulate built in modal footer ok and cancel button actions -->
              <b-button @click="cancel()"> Cancel </b-button>
              <b-button variant="danger" @click="closeOnDay()">
                Close Store</b-button
              >
              <b-button variant="primary" @click="ok()"> Save </b-button>
            </template>
          </b-modal>
        </div>
      </b-col>
    </b-row>
  </b-container>
</template>
<script src="./ManageOpeningHoursScript.js"></script>
<style scoped>
/* Styling for Page Title */
.header_style {
  padding-top: 15px;
  border-color: #91c788;
  border-style: solid;
  border-width: 0px 0px 6px 0px;
  text-align: center;
  margin-bottom: 30px;
}
.hours_table_style {
  width: 95%;
  margin: auto;
}
</style>
