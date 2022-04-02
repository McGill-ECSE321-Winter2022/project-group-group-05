import StaffDashboard from "./StaffDashboard";
import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import moment from "moment";

/**
 * Asynchronously fetches OpeningHours instances from the database for the requested
 * weekday. If the instance does not exist, this function will substitute a dummy object
 * for use with the frontend.
 *
 * @param {String} dayOfWeek The weekday (i.e. Sunday, Monday, etc.)
 * @returns the fetched data for the requested weekday
 */
async function fetchWeekday(dayOfWeek) {
  return AXIOS.get("openingH/".concat(dayOfWeek))
    .then(response => {
      return Object.assign(response.data, { closed: false });
    })
    .catch(error => {
      // This will occur if the store is not open on the requested day
      const dummyWeekday = {
        daysOfWeek: dayOfWeek,
        startTime: "09:00:00",
        endTime: "17:00:00",
        closed: true,
      };
      return dummyWeekday;
    });
}

export default {
  name: "ManageOpeningHours",
  components: {
    StaffDashboard,
  },
  created: async function () {
    this.isOwnerLoggedIn =
      LOGIN_STATE.state.isLoggedIn && LOGIN_STATE.state.userType === "Owner";
    /**
     * There should always be once instance of OpeningHours in the database for every day of the week.
     *
     * We fetch each instance and load them into the items list ordered by day of the week. If a day
     * does not exist in the database, we will create it.
     */
    this.items.push(await fetchWeekday("Sunday"));
    this.items.push(await fetchWeekday("Monday"));
    this.items.push(await fetchWeekday("Tuesday"));
    this.items.push(await fetchWeekday("Wednesday"));
    this.items.push(await fetchWeekday("Thursday"));
    this.items.push(await fetchWeekday("Friday"));
    this.items.push(await fetchWeekday("Saturday"));
  },
  data() {
    return {
      isOwnerLoggedIn: false,
      items: [],
      fields: [
        {
          key: "daysOfWeek",
          label: "Day of the Week",
          thStyle: { width: "60%" },
        },
        { key: "startTime", label: "Opens at", thStyle: { width: "20%" } },
        { key: "endTime", label: "Closes at", thStyle: { width: "20%" } },
      ],

      // Used for error message
      dismissSecs: 5,
      dismissCountDown: 0,
      errorMessage: "",

      // form data
      selectedWeekDayIndex: 0,
      selectedWeekDay: "",
      selectedStartTime: "",
      selectedEndTime: "",
      editAttempt: false,
    };
  },
  computed: {
    isTimeValid: function () {
      if (
        this.selectedStartTime !==
          this.items[this.selectedWeekDayIndex].startTime ||
        this.selectedEndTime !== this.items[this.selectedWeekDayIndex].endTime
      ) {
        this.editAttempt = true;
      }
      return this.editAttempt
        ? moment(this.selectedEndTime, "HH:mm").isAfter(
            moment(this.selectedStartTime, "HH:mm")
          )
        : null;
    },
  },
  methods: {
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown;
    },
    resetWeekdayForm() {
      this.selectedWeekDayIndex = 0;
      this.selectedWeekDay = "";
      this.selectedStartTime = "";
      this.selectedEndTime = "";
      this.editAttempt = false;
    },
    handleOk(okEvent) {
      // Prevent the default function of the "ok" button in b-modal and replace with custom function
      okEvent.preventDefault();
      this.updateOpeningHours();
    },
    editOpeningHours(dayOfWeek, rowIndex) {
      // set the selection variables to match the attributes of our selection
      this.selectedWeekDayIndex = rowIndex;
      this.selectedWeekDay = dayOfWeek.daysOfWeek;
      this.selectedStartTime = dayOfWeek.startTime;
      this.selectedEndTime = dayOfWeek.endTime;
      this.editAttempt = false;
      this.$bvModal.show("editOpeningHours");
    },
    async updateOpeningHours() {
      this.editAttempt = true;
      await AXIOS.patch(
        "/openingH/".concat(this.selectedWeekDay),
        {},
        {
          params: {
            startH: moment(this.selectedStartTime, "HH:mm:ss").format("HH:mm"),
            endH: moment(this.selectedEndTime, "HH:mm:ss").format("HH:mm"),
          },
        }
      )
        .then(response => {
          // update frontend
          this.items[this.selectedWeekDayIndex].startTime =
            response.data.startTime;
          this.items[this.selectedWeekDayIndex].endTime = response.data.endTime;
          this.items[this.selectedWeekDayIndex].closed = false;
        })
        .catch(error => {
          // an error here means the weekday instance did not exist
          // (i.e. the store was previously closed on that day)
          // Therefore, create a weekday instance and update the backend/frontend accordingly
          AXIOS.post(
            "/openingH/".concat(this.selectedWeekDay),
            {},
            {
              params: {
                startH: moment(this.selectedStartTime, "HH:mm:ss").format(
                  "HH:mm"
                ),
                endH: moment(this.selectedEndTime, "HH:mm:ss").format("HH:mm"),
              },
            }
          )
            .then(response => {
              // update frontend
              this.items[this.selectedWeekDayIndex].startTime =
                response.data.startTime;
              this.items[this.selectedWeekDayIndex].endTime =
                response.data.endTime;
              this.items[this.selectedWeekDayIndex].closed = false;
            })
            .catch(error => {
              // This error should not occur in regular operation. The post request only throws an
              // error if it's trying to create an already existing weekday. This code is only reachable
              // if the weekday does not exist.
              console.log(error.response.data.message);
              this.dismissCountDown = this.dismissSecs;
              this.errorMessage = error.response.data.message;
            });
        });
      this.$nextTick(() => {
        this.$bvModal.hide("editOpeningHours");
      });
    },
    async closeOnDay() {
      await AXIOS.delete("/openingH/".concat(this.selectedWeekDay))
        .then(response => {
          // set frontend values to 9-to-5 as a default setting
          this.items[this.selectedWeekDayIndex].startTime = "09:00:00";
          this.items[this.selectedWeekDayIndex].endTime = "17:00:00";
          this.items[this.selectedWeekDayIndex].closed = true;
        })
        .catch(error => {
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
        });
      this.$nextTick(() => {
        this.$bvModal.hide("editOpeningHours");
      });
    },
  },
  filters: {
    // custom formatTime filter to handle the "Closed" case that arises
    // as a consequence of the ternary operator in the template
    timeFormat: function (timeString) {
      if (timeString === "Closed") {
        return "Closed";
      }
      return moment(timeString, "HH:mm:ss").format("hh:mm A");
    },
  },
};
