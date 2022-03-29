import StaffDashboard from "./StaffDashboard";
import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import moment from "moment";
import draggable from "vuedraggable";
import Vue from "vue";

/**
 * Creates a javascript object with the date and all schedule instances that correspond to that date. This
 * is used to organize the schedule assignments by weekday.
 *
 * @param {moment} date - Momemt-wrapped date
 * @param {EmployeeSchedule[]} employeeSchedules - the list of employeeSchedules to choose from when populating the schedule assignments
 * @returns A list of each day of the week the parameter date is in, and their corresponding shift assignments
 */
function generateSchedulesOfWeek(date, employeeSchedules) {
  var weekSchedule = [];
  for (var i = 0; i < 7; i++) {
    const weekDaySchedule = {
      dayOfWeek: moment(date).weekday(i),
      shiftListener: [],
      scheduledShifts: [],
    };
    // populate weekday object with the corresponding dates
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
    this.ownerLoggedIn =
      LOGIN_STATE.state.isLoggedIn && LOGIN_STATE.state.userType === "owner";
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
        // This should never happen in normal execution, since this request doesn't generally return errors
        console.log(error.response.data.message);
      });
    await AXIOS.get("/shift/getAll")
      .then(response => {
        this.shifts = response.data;
      })
      .catch(error => {
        // This should never happen in normal execution, since this request doesn't generally return errors
        console.log(error.response.data.message);
      });
  },
  data() {
    return {
      fields: ["username", "email", "modify_schedule"],
      items: [], // List of Employees
      weekMarkers: [], // List of Moment Objects marking the week to display for that Employee
      shifts: [], // List of Shifts
      ownerLoggedIn: false,
      busy: false, // Used to disable actions while requests are being handled
      selectedShift: "", // Holds the name of the selected Shift when adding shifts

      // Used for error message
      dismissSecs: 5,
      dismissCountDown: 0,
      errorMessage: "",

      // Used in the creation of a new shift
      create_shiftName: "",
      create_shiftStartTime: "",
      create_shiftEndTime: "",
    };
  },
  computed: {
    // Computed Data
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
    // Computed Data for determining the validity of the inputs to the Create Shift form.
    isShiftNameValid: function () {
      return this.create_shiftEndTime === "" &&
        this.create_shiftStartTime === "" &&
        this.create_shiftName === ""
        ? null
        : this.create_shiftName !== "";
    },
    isTimeValid: function () {
      return this.create_shiftEndTime === "" &&
        this.create_shiftStartTime === "" &&
        this.create_shiftName === ""
        ? null
        : moment(this.create_shiftEndTime, "HH:mm").isAfter(
            moment(this.create_shiftStartTime, "HH:mm")
          );
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
          // An error should not occur for this action, since it should only be performed on existing EmployeeSchedules
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
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
          // If the user tries to: add a schedule that conflicts with an already assigned schedule, or add a schedule to a
          // deleted employee account, display the error message on screen.
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
        });
    },
    async clearWeekSchedule(rowIndex) {
      this.busy = true;
      // Gather all instances of EmployeeSchedules that need to be deleted
      var schedulesToBeCleared = [];
      this.schedulesOfWeek[rowIndex].forEach(weekday => {
        weekday.scheduledShifts.forEach(schedule =>
          schedulesToBeCleared.push(schedule)
        );
      });
      // Send Remove Request for each schedule
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
            // This should not occur in standard operation, since it is performed on previously fetched assignments.
            console.log(error.response.data.message);
            this.dismissCountDown = this.dismissSecs;
            this.errorMessage = error.response.data.message;
          });
      }
      this.busy = false;
    },
    async assignCurrentSchedule(rowIndex) {
      // Clear the week's current schedule to prevent conflicts
      this.clearWeekSchedule(rowIndex);
      this.busy = true;
      // Gather all the Schedules to be assigned from the present week
      var schedulesToBeAdded = [];
      var weekOffset = this.weekMarkers[rowIndex].week() - moment().week();
      for (const schedule of this.items[rowIndex].employeeSchedules) {
        if (moment(schedule.date).isSame(moment(), "week")) {
          schedulesToBeAdded.push(schedule);
        }
      }
      // Send Add Requests to add each schedule to the employee
      for (const schedule of schedulesToBeAdded) {
        await AXIOS.patch(
          "/employee/"
            .concat(this.items[rowIndex].username)
            .concat("/addSchedule"),
          {},
          {
            params: {
              date: moment(schedule.date)
                .add(weekOffset, "week")
                .format("YYYY-MM-DD"),
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
            // This should not occur in standard operation, since it is performed on previously fetched assignments.
            console.log(error.response.data.message);
            this.dismissCountDown = this.dismissSecs;
            this.errorMessage = error.response.data.message;
          });
      }
      this.busy = false;
    },
    resetShiftForm() {
      this.create_shiftName = "";
      this.create_shiftStartTime = "";
      this.create_shiftEndTime = "";
    },
    handleOk(okEvent) {
      // Prevent the default function of the "ok" button in b-modal and replace with custom function
      okEvent.preventDefault();
      this.createNewShift();
    },
    async createNewShift() {
      if (this.isShiftNameValid && this.isTimeValid) {
        await AXIOS.post(
          "/shift/".concat(this.create_shiftName),
          {},
          {
            params: {
              startTime: this.create_shiftStartTime,
              endTime: this.create_shiftEndTime,
            },
          }
        )
          .then(response => {
            this.shifts.push(response.data);
          })
          .catch(error => {
            console.log(error.response.data.message);
            this.dismissCountDown = this.dismissSecs;
            this.errorMessage = error.response.data.message;
          });
        // Hide the Create new Shift form after the request completes
        this.$nextTick(() => {
          this.$bvModal.hide("createShift");
        });
        return;
      }
    },
    async deleteShift(shiftName) {
      await AXIOS.delete("/shift/".concat(shiftName))
        .then(() => {
          for (var i = 0; i < this.shifts.length; i++) {
            if (this.shifts[i].name === shiftName) {
              this.shifts.splice(i, 1);
              break;
            }
          }
        })
        .catch(error => {
          // This should not occur in standard operation, since it is performed on previously fetched shifts.
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
        });
      // Refresh the page once a shift is deleted to update all employee schedules that might have had that shift assigned
      await AXIOS.get("/employee/getAll")
        .then(response => {
          // The items list and the response data should be the same length; deleting a shift should
          // not affect the number of employees in the system, nor should it change the order of the Employees
          // Throw an error if the above is not the case.
          if (response.data.length !== this.items.length) {
            throw error(
              "Warning: Fetched Data does not match the data in the table!"
            );
          }
          for (var i = 0; i < this.items.length; i++) {
            if (this.items[i].username !== response.data[i].username) {
              throw error(
                "Warning: Fetched Data does not match the data in the table!"
              );
            }
            this.items[i].employeeSchedules =
              response.data[i].employeeSchedules;
          }
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
    },
  },
};
