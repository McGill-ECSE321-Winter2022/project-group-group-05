<!--Visibility: owner-->
<!--Assign/Remove schedules to employees-->
<template>
  <b-container fluid>
    <b-row align-v="stretch">
      <b-col>
        <h1 class="header_style">Manage Schedules</h1>
        <b-alert show variant="danger" v-if="!ownerLoggedIn"
          ><h4 class="alert-heading">Error:</h4>
          You must be logged in as an owner to access this page.
        </b-alert>
        <b-alert
          :show="dismissCountDown"
          dismissible
          variant="warning"
          @dismissed="dismissCountDown = 0"
          @dismiss-count-down="countDownChanged"
        >
          Warning: {{ errorMessage }}
        </b-alert>
        <b-container fluid v-if="ownerLoggedIn"
          ><b-row align-v="stretch"
            ><b-col cols="9">
              <b-table
                :items="items"
                :fields="fields"
                striped
                outlined
                head-variant="light"
                sticky-header="85vh"
              >
                <template #cell(modify_schedule)="row">
                  <b-button size="sm" @click="row.toggleDetails" class="mr-2">
                    {{ row.detailsShowing ? "Hide" : "Show" }} Assigned Shifts
                  </b-button>
                </template>
                <template #row-details="row">
                  <div>
                    <b-button-group>
                      <b-button
                        class="button_style"
                        variant="outline-dark"
                        @click="stepBackWeek(row.index)"
                        >&lsaquo;</b-button
                      >
                      <b-button
                        class="button_style"
                        variant="outline-dark"
                        v-bind:disabled="isWeekPresent[row.index]"
                        @click="returnToLatestWeek(row.index)"
                        >Return to this Week</b-button
                      >
                      <b-button
                        class="button_style"
                        variant="outline-dark"
                        @click="stepForwardWeek(row.index)"
                        >&rsaquo;</b-button
                      >
                    </b-button-group>
                    <b-button
                      class="button_style"
                      variant="outline-success"
                      v-bind:disabled="isWeekPresent[row.index] || busy"
                      @click="assignCurrentSchedule(row.index)"
                      >Auto-fill Current Schedule</b-button
                    >
                    <b-button
                      class="button_style"
                      variant="outline-danger"
                      v-bind:disabled="busy"
                      @click="clearWeekSchedule(row.index)"
                      >Clear Week Schedule</b-button
                    >
                  </div>
                  <b-overlay :show="busy"
                    ><b-container class="schedule_style" fluid>
                      <b-form-row class="schedule_row_style">
                        <b-col
                          class="schedule_column_style"
                          v-for="weekday in schedulesOfWeek[row.index]"
                          v-bind:key="weekday.dayOfWeek.format()"
                        >
                          <draggable
                            style="min-height: 300px"
                            draggable=".draggable"
                            v-model="weekday.shiftListener"
                            ghost-class="gone-card"
                            :group="{
                              name: 'weekdays.shiftListener',
                              put: 'shifts',
                            }"
                            @change="
                              addScheduleAssignment(
                                weekday.dayOfWeek,
                                row.index
                              )
                            "
                          >
                            <div class="font_size_large">
                              {{ weekday.dayOfWeek.format("dddd") }}
                            </div>
                            <div class="font_size_med">
                              {{ weekday.dayOfWeek.format("MMM. Do, YYYY") }}
                            </div>
                            <div
                              v-for="schedule in weekday.scheduledShifts"
                              :key="schedule.id"
                            >
                              <b-button-group
                                size="sm"
                                class="scheduled_shift_style"
                              >
                                <div
                                  class="button_label_style bg-light"
                                  variant="light"
                                >
                                  {{ schedule.shift.name }}
                                </div>
                                <b-button
                                  class="button_remove_style"
                                  variant="light"
                                  @click="
                                    deleteScheduleAssignment(
                                      schedule,
                                      row.index
                                    )
                                  "
                                  >&#10005;</b-button
                                >
                              </b-button-group>
                            </div>
                          </draggable>
                        </b-col>
                      </b-form-row>
                    </b-container></b-overlay
                  >
                </template>
              </b-table>
            </b-col>
            <b-col cols="3">
              <div class="shift_header_style bg-light">
                <h2>Available Shifts</h2>
                <b-button style="margin-bottom: 10px">New Shift</b-button>
              </div>
              <div class="shift_container_style">
                <div
                  class="shift_card_style"
                  v-for="shift in shifts"
                  :key="shift.name"
                >
                  <draggable
                    :list="shifts"
                    ghost-class="ghost-card"
                    drag-class="drag-card"
                    :sort="false"
                    :group="{ name: 'shifts', pull: 'clone', put: false }"
                    @start="selectShift(shift.name)"
                  >
                    <b-card
                      class="my-2 text-center shift_card_style"
                      v-bind:header="shift.name"
                    >
                      <b-card-text class="shift_style"
                        >Start Time:
                        {{ shift.startTime | formatTime }}</b-card-text
                      >
                      <b-card-text class="shift_style"
                        >End Time: {{ shift.endTime | formatTime }}</b-card-text
                      >
                    </b-card>
                  </draggable>
                </div>
              </div>
            </b-col>
          </b-row>
        </b-container>
      </b-col>
    </b-row>
  </b-container>
</template>
<script src="./ManageSchedulesScript.js"></script>
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
.shift_header_style {
  padding-top: 5px;
  border-radius: 3px;

  min-width: 200px;
  outline-style: solid;
  outline-color: #cccccc;
  outline-width: 1px;
}
/* Styling for Button Group */
.button_style {
  padding-top: 0px;
  padding-bottom: 0px;
  margin-bottom: 5px;
}
/* Styling for Schedule Calendar */
.schedule_row_style {
  flex-wrap: nowrap;
  white-space: nowrap;
  overflow: auto;
  height: 100%;
  display: flex;
  margin: auto;
}
.schedule_style {
  max-width: 100%;
}
.schedule_column_style {
  height: 100%;
  min-height: 300px;
  min-width: 150px;
  margin-left: 2px;
  margin-right: 2px;
  padding-top: 10px;
  background-color: #ddffbc;
  border: 1px solid #91c788;
  flex-wrap: wrap;
}
.scheduled_shift_style {
  width: 90%;
  margin-top: 4px;
}
.button_label_style {
  font-size: 75%;
  padding-top: 6px;
  width: 85%;
  border-radius: 3px 0px 0px 3px;
  text-align: center;
  outline-style: solid;
  outline-color: #cccccc;
  outline-width: 1px;
}
.button_remove_style {
  font-size: 85%;
  padding: 3px 0px 3px 0px;
  width: 20%;
  outline-style: solid;
  outline-color: #cccccc;
  outline-width: 1px;
}
.shift_container_style {
  padding-top: 5px;
  padding-bottom: 5px;
  border-radius: 3px;
  outline-style: solid;
  outline-color: #cccccc;
  outline-width: 1px;
  height: 74vh;
  min-width: 200px;
  overflow: auto;
  text-align: center;
}
.shift_card_style {
  padding: 0px;
  width: 90%;
  margin: auto;
}
.shift_style {
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.font_size_large {
  font-size: 18px;
}
.font_size_med {
  font-size: 15px;
}
.ghost-card {
  opacity: 0.5;
  background: #f7fafc;
  border: 1px solid #4299e1;
}
.gone-card {
  opacity: 1;
  max-width: 150px;
  max-height: 50px;
  margin: auto;
  padding: 0px;
}
.gone-card .card-text {
  visibility: hidden;
}
.drag-card {
  opacity: 1;
  background: #f7fafc;
}
</style>
