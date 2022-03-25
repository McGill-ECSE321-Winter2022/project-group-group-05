<!--Visibility: employee-->
<!--View the employee's schedules-->
<template>
  <b-container fluid>
    <b-row class="vh-100" align-v="stretch">
      <b-col>
        <h1 class="header_style">Work Schedule</h1>
        <h2>
          Week of
          {{
            scheduledShiftsByWeek[
              currentWeek
            ].schedulesOfWeek[0].dayOfWeek.format("MMMM Do, YYYY")
          }}
        </h2>
        <div>
          <b-button-group>
            <b-button
              class="font_size_large button_style"
              variant="outline-dark"
              v-bind:disabled="currentWeek === 0"
              @click="selectFirstWeek()"
              >&laquo;</b-button
            >
            <b-button
              class="font_size_large button_style"
              variant="outline-dark"
              v-bind:disabled="currentWeek === 0"
              @click="stepBackWeek()"
              >&lsaquo;</b-button
            >
            <b-button
              class="font_size_medium button_style"
              variant="outline-dark"
              v-bind:disabled="currentWeek === latestWeek"
              @click="returnToLatestWeek()"
              >Return to Latest Assigned Shifts</b-button
            >
            <b-button
              class="font_size_large button_style"
              variant="outline-dark"
              v-bind:disabled="currentWeek === scheduledShiftsByWeek.length - 1"
              @click="stepForwardWeek()"
              >&rsaquo;</b-button
            >
            <b-button
              class="font_size_large button_style"
              variant="outline-dark"
              v-bind:disabled="currentWeek === scheduledShiftsByWeek.length - 1"
              @click="selectLastWeek()"
              >&raquo;</b-button
            >
          </b-button-group>
        </div>
        <b-alert
          class="no_schedule_style font_size_large"
          show
          v-if="!scheduledShiftsByWeek[currentWeek].hasSchedules"
          >No scheduled shifts!</b-alert
        >
        <b-aspect class="schedule_style" aspect="2.25:1">
          <b-container style="height: 100%" fluid overflow-auto>
            <b-form-row class="schedule_row_style">
              <b-col class="schedule_column_style">
                <h4>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[0].dayOfWeek.format("dddd")
                  }}
                </h4>
                <div>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[0].dayOfWeek.format("MMM. Do, YYYY")
                  }}
                </div>
                <b-card
                  class="my-2 text-center"
                  v-bind:header="schedule.shift.name"
                  v-for="schedule in scheduledShiftsByWeek[currentWeek]
                    .schedulesOfWeek[0].scheduledShifts"
                  :key="schedule.shift.name"
                >
                  <b-card-text class="shift_style"
                    >Start Time:
                    {{ schedule.shift.startTime | formatTime }}</b-card-text
                  >
                  <b-card-text class="shift_style"
                    >End Time:
                    {{ schedule.shift.endTime | formatTime }}</b-card-text
                  >
                </b-card>
              </b-col>
              <b-col class="schedule_column_style">
                <h4>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[1].dayOfWeek.format("dddd")
                  }}
                </h4>
                <div>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[1].dayOfWeek.format("MMM. Do, YYYY")
                  }}
                </div>
                <b-card
                  class="my-2 text-center"
                  v-bind:header="schedule.shift.name"
                  v-for="schedule in scheduledShiftsByWeek[currentWeek]
                    .schedulesOfWeek[1].scheduledShifts"
                  :key="schedule.shift.name"
                >
                  <b-card-text class="shift_style"
                    >Start Time:
                    {{ schedule.shift.startTime | formatTime }}</b-card-text
                  >
                  <b-card-text class="shift_style"
                    >End Time:
                    {{ schedule.shift.endTime | formatTime }}</b-card-text
                  >
                </b-card>
              </b-col>
              <b-col class="schedule_column_style">
                <h4>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[2].dayOfWeek.format("dddd")
                  }}
                </h4>
                <div>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[2].dayOfWeek.format("MMM. Do, YYYY")
                  }}
                </div>
                <b-card
                  class="my-2 text-center"
                  v-bind:header="schedule.shift.name"
                  v-for="schedule in scheduledShiftsByWeek[currentWeek]
                    .schedulesOfWeek[2].scheduledShifts"
                  :key="schedule.shift.name"
                >
                  <b-card-text class="shift_style"
                    >Start Time:
                    {{ schedule.shift.startTime | formatTime }}</b-card-text
                  >
                  <b-card-text class="shift_style"
                    >End Time:
                    {{ schedule.shift.endTime | formatTime }}</b-card-text
                  >
                </b-card>
              </b-col>
              <b-col class="schedule_column_style">
                <h4>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[3].dayOfWeek.format("dddd")
                  }}
                </h4>
                <div>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[3].dayOfWeek.format("MMM. Do, YYYY")
                  }}
                </div>
                <b-card
                  class="my-2 text-center"
                  v-bind:header="schedule.shift.name"
                  v-for="schedule in scheduledShiftsByWeek[currentWeek]
                    .schedulesOfWeek[3].scheduledShifts"
                  :key="schedule.shift.name"
                >
                  <b-card-text class="shift_style"
                    >Start Time:
                    {{ schedule.shift.startTime | formatTime }}</b-card-text
                  >
                  <b-card-text class="shift_style"
                    >End Time:
                    {{ schedule.shift.endTime | formatTime }}</b-card-text
                  >
                </b-card>
              </b-col>
              <b-col class="schedule_column_style">
                <h4>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[4].dayOfWeek.format("dddd")
                  }}
                </h4>
                <div>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[4].dayOfWeek.format("MMM. Do, YYYY")
                  }}
                </div>
                <b-card
                  class="my-2 text-center"
                  v-bind:header="schedule.shift.name"
                  v-for="schedule in scheduledShiftsByWeek[currentWeek]
                    .schedulesOfWeek[4].scheduledShifts"
                  :key="schedule.shift.name"
                >
                  <b-card-text class="shift_style"
                    >Start Time:
                    {{ schedule.shift.startTime | formatTime }}</b-card-text
                  >
                  <b-card-text class="shift_style"
                    >End Time:
                    {{ schedule.shift.endTime | formatTime }}</b-card-text
                  >
                </b-card>
              </b-col>
              <b-col class="schedule_column_style">
                <h4>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[5].dayOfWeek.format("dddd")
                  }}
                </h4>
                <div>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[5].dayOfWeek.format("MMM. Do, YYYY")
                  }}
                </div>
                <b-card
                  class="my-2 text-center"
                  v-bind:header="schedule.shift.name"
                  v-for="schedule in scheduledShiftsByWeek[currentWeek]
                    .schedulesOfWeek[5].scheduledShifts"
                  :key="schedule.shift.name"
                >
                  <b-card-text class="shift_style"
                    >Start Time:
                    {{ schedule.shift.startTime | formatTime }}</b-card-text
                  >
                  <b-card-text class="shift_style"
                    >End Time:
                    {{ schedule.shift.endTime | formatTime }}</b-card-text
                  >
                </b-card>
              </b-col>
              <b-col class="schedule_column_style">
                <h4>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[6].dayOfWeek.format("dddd")
                  }}
                </h4>
                <div>
                  {{
                    scheduledShiftsByWeek[
                      currentWeek
                    ].schedulesOfWeek[6].dayOfWeek.format("MMM. Do, YYYY")
                  }}
                </div>
                <b-card
                  class="my-2 text-center"
                  v-bind:header="schedule.shift.name"
                  v-for="schedule in scheduledShiftsByWeek[currentWeek]
                    .schedulesOfWeek[6].scheduledShifts"
                  :key="schedule.shift.name"
                >
                  <b-card-text class="shift_style"
                    >Start Time:
                    {{ schedule.shift.startTime | formatTime }}</b-card-text
                  >
                  <b-card-text class="shift_style"
                    >End Time:
                    {{ schedule.shift.endTime | formatTime }}</b-card-text
                  >
                </b-card>
              </b-col>
            </b-form-row>
          </b-container>
        </b-aspect>
      </b-col>
    </b-row>
  </b-container>
</template>
<script src="./ViewSchedulesScript.js"></script>
<style scoped>
/* Styling for Page Title */
.header_style {
  padding-top: 15px;
  padding-left: 100px;
  border-color: #91c788;
  border-style: solid;
  border-width: 0px 0px 6px 0px;
  text-align: left;
  margin-bottom: 30px;
}
/* Styling for Button Group */
.button_style {
  padding-top: 0px;
  padding-bottom: 0px;
}
/* Styling for Schedule Calendar */
.schedule_style {
  padding-bottom: 10px;
  margin-top: 10px;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: auto;
  max-width: 80%;
}
.schedule_row_style {
  flex-wrap: nowrap;
  white-space: nowrap;
  overflow: auto;
  height: 100%;
}
.schedule_column_style {
  height: 100%;
  min-width: 160px;
  margin-left: 2px;
  margin-right: 2px;
  padding-top: 10px;
  background-color: #ddffbc;
}
.no_schedule_style {
  width: 78%;
  margin-top: 10px;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: auto;
  padding: 2px 0px 2px 0px;
}
/* Styling for Shift Cards */
.shift_style {
  margin: 0px;
  font-size: 11px;
}
</style>
