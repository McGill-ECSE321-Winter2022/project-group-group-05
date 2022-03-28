import StaffDashboard from "./StaffDashboard";
import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import moment from "moment";
import draggable from "vuedraggable";
import Vue from "vue";

function generateSchedulesOfWeek(date, employeeSchedules) {
  var weekSchedule = [];
  for (var i = 0; i < 7; i++) {
    const weekDaySchedule = {
      dayOfWeek: moment(date).weekday(i),
      shiftListener: [],
      scheduledShifts: [],
    };
    employeeSchedules.forEach(function (schedule) {
      var scheduleMoment = moment(schedule.date);
      if (scheduleMoment.isSame(weekDaySchedule.dayOfWeek, "day")) {
        weekDaySchedule.scheduledShifts.push(schedule);
      }
    });
    weekSchedule.push(weekDaySchedule);
  }
  return weekSchedule;
}

export default {
  name: "ManageSchedules",
  components: {
    StaffDashboard,
    draggable,
  },
  created: async function () {
    this.ownerLoggedIn = true; // LOGIN_STATE.state.isLoggedIn && LOGIN_STATE.state.userType === "owner";
    this.selectedShift = "";
    this.errorMessage = "";
    await AXIOS.get("/employee/getAll")
      .then(response => {
        this.items = response.data;
        var weekMarkers = [];
        for (var i = 0; i < response.data.length; i++) {
          weekMarkers.push(moment());
        }
        this.weekMarkers = weekMarkers;
      })
      .catch(error => {
        // This should never happen in normal execution
        console.log(error.response.data.message);
      });
    await AXIOS.get("/shift/getAll")
      .then(response => {
        this.shifts = response.data;
      })
      .catch(error => {
        // This should never happen in normal execution
        console.log(error.response.data.message);
      });
  },
  data() {
    return {
      fields: ["username", "email", "modify_schedule"],
      items: [],
      weekMarkers: [],
      shifts: [],
      ownerLoggedIn: false,
      busy: false,
      selectedShift: "",
      dismissSecs: 5,
      dismissCountDown: 0,
      errorMessage: "Test",
    };
  },
  computed: {
    schedulesOfWeek: function () {
      var allSchedulesOfWeek = [];
      for (var i = 0; i < this.items.length; i++) {
        allSchedulesOfWeek.push(
          generateSchedulesOfWeek(
            this.weekMarkers[i],
            this.items[i].employeeSchedules
          )
        );
      }
      return allSchedulesOfWeek;
    },
    isWeekPresent: function () {
      var allIsWeekPresent = [];
      for (var i = 0; i < this.weekMarkers.length; i++) {
        allIsWeekPresent.push(this.weekMarkers[i].isSame(moment(), "week"));
      }
      return allIsWeekPresent;
    },
  },
  methods: {
    selectShift: function (shiftName) {
      this.selectedShift = shiftName;
    },
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown;
    },
    stepForwardWeek: function (rowIndex) {
      var nextWeek = this.weekMarkers[rowIndex];
      nextWeek.add(1, "week");
      console.log(nextWeek.format());
      Vue.set(this.weekMarkers, rowIndex, nextWeek);
    },
    stepBackWeek: function (rowIndex) {
      var lastWeek = this.weekMarkers[rowIndex];
      lastWeek.subtract(1, "week");
      console.log(lastWeek.format());
      Vue.set(this.weekMarkers, rowIndex, lastWeek);
    },
    returnToLatestWeek: function (rowIndex) {
      Vue.set(this.weekMarkers, rowIndex, moment());
    },
    async deleteScheduleAssignment(schedule, rowIndex) {
      await AXIOS.patch(
        "/employee/"
          .concat(this.items[rowIndex].username)
          .concat("/removeSchedule"),
        {},
        {
          params: {
            date: schedule.date,
            shift: schedule.shift.name,
          },
        }
      )
        .then(response => {
          var updatedEmployee = Object.assign(response.data, {
            _showDetails: true,
          });
          Vue.set(this.items, rowIndex, updatedEmployee);
        })
        .catch(error => {
          // TODO: not sure what to do here. If it reaches an error, either:
          // A) the shift is no longer valid, or B) the employee is no longer valid
          // Figure out behavior in these cases
          console.log(error.response);
        });
    },
    async addScheduleAssignment(date, rowIndex) {
      await AXIOS.patch(
        "/employee/"
          .concat(this.items[rowIndex].username)
          .concat("/addSchedule"),
        {},
        {
          params: {
            date: date.format("YYYY-MM-DD"),
            shift: this.selectedShift,
          },
        }
      )
        .then(response => {
          var updatedEmployee = Object.assign(response.data, {
            _showDetails: true,
          });
          Vue.set(this.items, rowIndex, updatedEmployee);
        })
        .catch(error => {
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
        });
    },
    async clearWeekSchedule(rowIndex) {
      this.busy = true;
      var schedulesToBeCleared = [];
      this.schedulesOfWeek[rowIndex].forEach(weekday => {
        weekday.scheduledShifts.forEach(schedule =>
          schedulesToBeCleared.push(schedule)
        );
      });
      for (const schedule of schedulesToBeCleared) {
        await AXIOS.patch(
          "/employee/"
            .concat(this.items[rowIndex].username)
            .concat("/removeSchedule"),
          {},
          {
            params: {
              date: schedule.date,
              shift: schedule.shift.name,
            },
          }
        )
          .then(response => {
            var updatedEmployee = Object.assign(response.data, {
              _showDetails: true,
            });
            Vue.set(this.items, rowIndex, updatedEmployee);
          })
          .catch(error => {
            // TODO: not sure what to do here. If it reaches an error, either:
            // A) the shift is no longer valid, or B) the employee is no longer valid
            // Figure out behavior in these cases
            console.log(error.response);
          });
      }
      this.busy = false;
    },
    async assignCurrentSchedule(rowIndex) {
      this.clearWeekSchedule(rowIndex);
      this.busy = true;
      var schedulesToBeAdded = [];
      var weekOffset = this.weekMarkers[rowIndex].week() - moment().week()
      for (const schedule of this.items[rowIndex].employeeSchedules) {
        if (moment(schedule.date).isSame(moment(), "week")) {
          schedulesToBeAdded.push(schedule);
        }
      }
      for (const schedule of schedulesToBeAdded) {
        await AXIOS.patch(
          "/employee/"
            .concat(this.items[rowIndex].username)
            .concat("/addSchedule"),
          {},
          {
            params: {
              date: moment(schedule.date).add(weekOffset, "week").format("YYYY-MM-DD"),
              shift: schedule.shift.name,
            },
          }
        )
          .then(response => {
            var updatedEmployee = Object.assign(response.data, {
              _showDetails: true,
            });
            Vue.set(this.items, rowIndex, updatedEmployee);
          })
          .catch(error => {
            // TODO: not sure what to do here. If it reaches an error, either:
            // A) the shift is no longer valid, or B) the employee is no longer valid
            // Figure out behavior in these cases
            console.log(error.response);
          });
      }
      this.busy = false;
    },
  },
};
