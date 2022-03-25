import StaffDashboard from "./StaffDashboard";
import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import moment from "moment";

function generateWeekObject(date) {
  var weekSchedule = [];
  for (let i = 1; i <= 7; i++) {
    const weekDaySchedule = {
      dayOfWeek: moment(date).isoWeekday(i),
      scheduledShifts: []
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
  created: function () {
    AXIOS.get(
      "/employee/".concat(LOGIN_STATE.state.username).concat("/getSchedules")
    ).then(response => {
      // Because the StaffDashboard page is only accessible once the user logs in as an employee,
      // we can safely assume that the username used here corresponds to an Employee username.

      /**
       * The incoming array is sorted from the earliest schedule assignment to the latest.
       * To take advantage of javascript's pop() function, we first reverse the array so we
       * pop the first element rather than the last.
       */
      var fetchedEmployeeSchedules = response.data.reverse();
      if (fetchedEmployeeSchedules.length > 0) {
        this.scheduledShiftsByWeek = [];
        this.currentWeek = 0;
        this.latestWeek = 0;
        var week = moment().isAfter(fetchedEmployeeSchedules.at(-1).date, "week")
        ? moment(schedule.date)
        : moment();
        while (
          fetchedEmployeeSchedules.length > 0 &&
          !moment().isAfter(week, "week")
        ) {
          var weekSchedule = generateWeekObject(week);
          var weekHasSchedules = false;
          for (let i = 0; i < weekSchedule.length; i++) {
            while (fetchedEmployeeSchedules.length > 0) {
              schedule = fetchedEmployeeSchedules.pop();
              var scheduleMoment = moment(schedule.date);
              if (scheduleMoment.isSame(week[i].dayOfWeek, "day")) {
                weekHasSchedules = true;
                week[i].scheduledShifts.push(schedule);
              } else {
                break;
              }
            }
          }
          if (!moment().isSameOrAfter(week[0].dayOfWeek, "week")) {
            currentWeek++;
            latestWeek++;
          }
          const scheduledShiftsForWeek = {
            hasSchedules: weekHasSchedules,
            schedulesOfWeek: week,
          };
          this.scheduledShiftsByWeek.push(scheduledShiftsForWeek);
          week.add(1, "week")
        }
      } else {
        this.scheduledShiftsByWeek = [
          {
            hasSchedules: false,
            schedulesOfWeek: generateWeekObject(new Date()),
          },
        ];
        this.currentWeek = 0;
        this.latestWeek = 0;
      }
    });
  },
  data() {
    return {
      scheduledShiftsByWeek: [
        {
          hasSchedules: false,
          schedulesOfWeek: generateWeekObject(new Date()),
        },
      ],
      currentWeek: 0,
      latestWeek: 0
    };
  },
  methods: {
    selectFirstWeek: function () {
      this.currentWeek = 0;
    },
    selectLastWeek: function () {
      this.currentWeek = scheduledShiftsByWeek.length - 1;
    },
    stepForwardWeek: function () {
      this.currentWeek = Math.min(this.currentWeek + 1, scheduledShiftsByWeek.length - 1);
    },
    stepBackWeek: function () {
      this.currentWeek = Math.max(this.currentWeek - 1, 0);
    },
    returnToLatestWeek: function () {
      this.currentWeek = this.latestWeek;
    },
  },
};
