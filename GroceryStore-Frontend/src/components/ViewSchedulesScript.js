import StaffDashboard from "./StaffDashboard";
import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import moment from "moment";

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
  created: function () {
    LOGIN_STATE.commit("logout");
    LOGIN_STATE.commit("login", {
      userType: "testEmployee",
      username: "testEmployee",
    });
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
      if (response.data.length > 0) {
        this.scheduledShiftsByWeek = [];
        this.currentWeek = 0;
        this.latestWeek = 0;
        var firstSchedule = response.data[0];
        var week = moment().isAfter(firstSchedule.date, "week")
          ? moment(firstSchedule.date)
          : moment();
        var responseNumber = 0;
        while (
          responseNumber < response.data.length ||
          moment().isSameOrAfter(week, "week")
        ) {
          var weekSchedule = generateWeekObject(week);
          var weekHasSchedules = false;
          for (var i = 0; i < weekSchedule.length; i++) {
            while (responseNumber < response.data.length) {
              var schedule = response.data[responseNumber];
              var scheduleMoment = moment(schedule.date);
              if (scheduleMoment.isSame(weekSchedule[i].dayOfWeek, "day")) {
                weekHasSchedules = true;
                weekSchedule[i].scheduledShifts.push(schedule);
                responseNumber++;
              } else {
                break;
              }
            }
          }
          if (moment().isAfter(week, "week")) {
            this.currentWeek++;
            this.latestWeek++;
          }
          const scheduledShiftsForWeek = {
            hasSchedules: weekHasSchedules,
            schedulesOfWeek: weekSchedule,
          };
          this.scheduledShiftsByWeek.push(scheduledShiftsForWeek);
          week.add(1, "week");
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
      latestWeek: 0,
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
