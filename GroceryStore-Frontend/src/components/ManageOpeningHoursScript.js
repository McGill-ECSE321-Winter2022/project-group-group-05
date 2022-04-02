import StaffDashboard from "./StaffDashboard";
import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import moment from "moment";

/**
 * Asynchronously fetches OpeningHours instances from the database for the requested
 * weekday. If the instance does not exist, this function will create it.
 *
 * @param {String} dayOfWeek The weekday (i.e. Sunday, Monday, etc.)
 * @returns the fetched data for the requested weekday
 */
async function fetchWeekday(dayOfWeek) {
  return AXIOS.get("openingH/".concat(dayOfWeek))
    .then(response => {
      return response.data;
    })
    .catch(error => {
      AXIOS.post(
        "openingH/".concat(dayOfWeek),
        {},
        {
          params: {
            startH: "09:00",
            endH: "17:00",
          },
        }
      )
        .then(response => {
          return response.data;
        })
        .catch(error => {
          // This error should never occur. POST Requests will only throw an error
          // when the instance we are creating already exists. This is never the case
          // here because this code will only run if the instance does not exist.
          console.log(error.response.data.message);
        });
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
};
