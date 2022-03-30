import StaffDashboard from "./StaffDashboard";
import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import moment from "moment";

/**
 * Creates a javascript object with the date and all schedule instances that correspond to that date. This
 * is used to organize the schedule assignments by weekday.
 *
 * @param {moment} date - Momemt-wrapped date
 * @returns A list of each day of the week the parameter date is in, and their corresponding shift assignments
 */
function generateWeekObject(date) {
  var weekSchedule = [];
  for (var i = 0; i < 7; i++) {
    const weekDaySchedule = {
      dayOfWeek: moment(date).weekday(i),
      scheduledShifts: [],
    };
    weekSchedule.push(weekDaySchedule);
  }
  return weekSchedule;
}

export default {
  name: "ViewSchedules",
  components: {
    StaffDashboard,
  },
  created: async function () {
    this.isEmployeeLoggedIn =
      LOGIN_STATE.state.isLoggedIn && LOGIN_STATE.state.userType === "Employee";
    await AXIOS.get(
      "/employee/".concat(LOGIN_STATE.state.username).concat("/getSchedules")
    )
      .then(response => {
        if (response.data.length > 0) {
          this.scheduledShiftsByWeek = [];
          this.currentWeek = 0;
          this.latestWeek = 0;
          this.errorMessage = "";
          var firstSchedule = response.data[0];
          var week = moment().isAfter(firstSchedule.date, "week")
            ? moment(firstSchedule.date)
            : moment();
          var responseNumber = 0;
          /**
           * We generate a list of the schedule assignments organized by week. var {week} stores the date
           * of the first week in this list. We want to make sure we include today's date in the list, so
           * we continue to generate weeks until we reach the end of the {response.data} and today is encapsulated
           * in the list.
           *
           * Note: all operations are done based on locale using moment.js
           */
          while (
            responseNumber < response.data.length ||
            moment().isSameOrAfter(week, "week")
          ) {
            var weekSchedule = generateWeekObject(week);
            var weekHasSchedules = false;
            // populate the week list with all corresponding schedule assignments
            for (var i = 0; i < weekSchedule.length; i++) {
              while (responseNumber < response.data.length) {
                var schedule = response.data[responseNumber];
                var scheduleMoment = moment(schedule.date);
                if (scheduleMoment.isSame(weekSchedule[i].dayOfWeek, "day")) {
                  weekHasSchedules = true;
                  weekSchedule[i].scheduledShifts.push(schedule);
                  responseNumber++;
                } else {
                  // because the list is sorted, we can break and move to the next weekday once a schedule assignment
                  // doesn't match the current weekday.
                  break;
                }
              }
            }
            // Track the location of "today's" week in the list. Initially, we will display the week including today's date
            if (moment().isAfter(week, "week")) {
              this.currentWeek++;
              this.latestWeek++;
            }
            // add data from this week to the list, and move to the next week
            const scheduledShiftsForWeek = {
              hasSchedules: weekHasSchedules,
              schedulesOfWeek: weekSchedule,
            };
            this.scheduledShiftsByWeek.push(scheduledShiftsForWeek);
            week.add(1, "week");
          }
        } else {
          // clear and reset schedule assignment list if the employee hasn't been assigned to any
          this.scheduledShiftsByWeek = [
            {
              hasSchedules: false,
              schedulesOfWeek: generateWeekObject(new Date()),
            },
          ];
          this.currentWeek = 0;
          this.latestWeek = 0;
          this.errorMessage = "";
        }
      })
      .catch(error => {
        // This error should never be relevant. The request will fail only if the logged in user is not an employee,
        // and the page is disabled if that is the case.
        console.log(error.response.data.message);
        this.errorMessage = error.response.data.message;
      });
  },
  data() {
    return {
      isEmployeeLoggedIn: false,
      /**
       * List of EmployeeSchedules organized by week
       *    Each element in scheduledShiftsByWeek has two fields:
       *      -> hasSchedules - indicates whether there are any schedule assignments in that week
       *      -> schedulesOfWeek - list of schedule assignments in that week organized by weekday
       *            Each element in schedulesOfWeek has two fields:
       *                -> dayOfWeek - moment instance of the date
       *                -> scheduledShifts - list of the EmployeeSchedule instances assigned to this date
       */
      scheduledShiftsByWeek: [
        {
          hasSchedules: false,
          schedulesOfWeek: generateWeekObject(new Date()),
        },
      ],
      // currentWeek is used to track what element in scheduledShiftsByWeek is currently being displayed
      currentWeek: 0,
      // latestWeek tracks the element in scheduledShiftsByWeek that corresponds to today's week.
      // Once set in created(), this value does not change
      latestWeek: 0,
      errorMessage: "",
    };
  },
  methods: {
    selectFirstWeek: function () {
      this.currentWeek = 0;
    },
    selectLastWeek: function () {
      this.currentWeek = this.scheduledShiftsByWeek.length - 1;
    },
    stepForwardWeek: function () {
      this.currentWeek = Math.min(
        this.currentWeek + 1,
        this.scheduledShiftsByWeek.length - 1
      );
    },
    stepBackWeek: function () {
      this.currentWeek = Math.max(this.currentWeek - 1, 0);
    },
    returnToLatestWeek: function () {
      this.currentWeek = this.latestWeek;
    },
  },
};
