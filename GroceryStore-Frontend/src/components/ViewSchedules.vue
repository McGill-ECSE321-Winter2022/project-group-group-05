<!--Visibility: employee-->
<!--View the employee's schedules-->
<template>
  <b-container fluid>
    <b-row class="vh-100" align-v="stretch">
      <b-col md="auto"><staff-dashboard></staff-dashboard></b-col>
      <b-col>
        <h1 class="header_style">Work Schedule</h1>
        <b-alert show variant="danger" v-if="!isEmployeeLoggedIn"
          ><h4 class="alert-heading">Error:</h4>
          You must be logged in as an Employee to access this page.
        </b-alert>
        <div v-if="isEmployeeLoggedIn">
          <h2>
            Week of
            {{
              scheduledShiftsByWeek[
                currentWeek
              ].schedulesOfWeek[0].dayOfWeek.format("MMMM Do, YYYY")
            }}
          </h2>
          <div>
            <!-- Schedule Navbar -->
            <b-button-group>
              <b-button
                class="button_style"
                variant="primary"
                v-bind:disabled="currentWeek === 0"
                @click="selectFirstWeek()"
                >&laquo;</b-button
              >
              <b-button
                class="button_style"
                variant="primary"
                v-bind:disabled="currentWeek === 0"
                @click="stepBackWeek()"
                >&lsaquo;</b-button
              >
              <b-button
                class="button_style"
                variant="primary"
                v-bind:disabled="currentWeek === latestWeek"
                @click="returnToLatestWeek()"
                >Return to Latest Assigned Shifts</b-button
              >
              <b-button
                class="button_style"
                variant="primary"
                v-bind:disabled="
                  currentWeek === scheduledShiftsByWeek.length - 1
                "
                @click="stepForwardWeek()"
                >&rsaquo;</b-button
              >
              <b-button
                class="button_style"
                variant="primary"
                v-bind:disabled="
                  currentWeek === scheduledShiftsByWeek.length - 1
                "
                @click="selectLastWeek()"
                >&raquo;</b-button
              >
            </b-button-group>
          </div>
          <b-alert
            class="no_schedule_style"
            show
            dismissible
            v-if="
              !scheduledShiftsByWeek[currentWeek].hasSchedules && !errorMessage
            "
            >No scheduled shifts!</b-alert
          >
          <!-- Schedule Component -->
          <b-aspect class="schedule_style" aspect="2.25:1">
            <b-alert show variant="danger" v-if="errorMessage"
              ><h4 class="alert-heading">Error:</h4>
              {{ errorMessage }}
            </b-alert>
            <b-container
              style="height: 100%"
              fluid
              overflow-auto
              v-if="!errorMessage"
            >
              <b-form-row class="schedule_row_style">
                <b-col
                  class="schedule_column_style"
                  v-for="weekday in scheduledShiftsByWeek[currentWeek]
                    .schedulesOfWeek"
                  :key="weekday.dayOfWeek.toString()"
                >
                  <h4>
                    {{ weekday.dayOfWeek.format("dddd") }}
                  </h4>
                  <div>
                    {{ weekday.dayOfWeek.format("MMM. Do, YYYY") }}
                  </div>
                  <!-- Schedule Assignment Cards -->
                  <b-card
                    class="my-2 text-center shift_style"
                    no-body
                    v-bind:header="schedule.shift.name"
                    v-for="schedule in weekday.scheduledShifts"
                    :key="schedule.shift.name"
                  >
                    <b-card-text style="padding-top: 15px"
                      >Start Time:
                      {{ schedule.shift.startTime | formatTime }}</b-card-text
                    >
                    <b-card-text style="padding-bottom: 15px"
                      >End Time:
                      {{ schedule.shift.endTime | formatTime }}</b-card-text
                    >
                  </b-card>
                </b-col>
              </b-form-row>
            </b-container>
          </b-aspect>
        </div>
      </b-col>
    </b-row>
  </b-container>
</template>
<script src="./ViewSchedulesScript.js"></script>
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
/* Styling for Button Group */
.button_style {
  padding-top: 3px;
  padding-bottom: 3px;
}
/* Styling for Schedule Calendar */
.schedule_style {
  padding-bottom: 10px;
  margin-top: 10px;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: auto;
  max-width: 93%;
}
.schedule_row_style {
  flex-wrap: nowrap;
  white-space: nowrap;
  overflow: auto;
  height: 100%;
}
.schedule_column_style {
  height: 99%;
  min-width: 160px;
  margin: 2px;
  padding-top: 10px;
  border-radius: 3px;
  outline-style: solid;
  outline-color: #cccccc;
  outline-width: 1px;
}
.no_schedule_style {
  width: 79%;
  margin-top: 10px;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: auto;
  padding: 10px 0px 10px 0px;
}
/* Styling for Shift Cards */
.shift_style .card-header {
  white-space: normal;
}
.shift_style .card-text {
  margin: 0px;
  font-size: 16px;
}
</style>
