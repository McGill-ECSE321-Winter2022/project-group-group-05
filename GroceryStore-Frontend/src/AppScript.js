import { AXIOS } from "./common/AxiosScript";
import { LOGIN_STATE } from "./common/StateScript";

export default {
  name: "app",
  data() {
    return {
      LOGIN_STATE,
      isLoading: false,
      // navbar data
      openingHours: [],
      holidays: [],
    };
  },
  computed: {
    greetingMsg() {
      let username = LOGIN_STATE.state.username;
      return "Welcome, " + username;
    },
    isCustomer() {
      return (
        LOGIN_STATE.state.userType === "Customer" &&
        LOGIN_STATE.state.username !== "kiosk"
      );
    },
  },
  methods: {
    clickedOpeningH: async function () {
      this.isLoading = true;
      await this.fetchOpeningH();
      this.$bvModal.show("opening-h");
      this.isLoading = false;
    },
    clickedHoliday: async function () {
      this.isLoading = true;
      await this.fetchHoliday();
      this.$bvModal.show("list-of-holidays");
      this.isLoading = false;
    },
    clickedProfile: function () {
      if (LOGIN_STATE.state.userType === "Customer") {
        this.$router.push("/ManageProfile");
      } else if (
        LOGIN_STATE.state.userType === "Employee" ||
        LOGIN_STATE.state.userType === "Owner"
      ) {
        this.$router.push("/ManageStaffProfile");
      }
    },
    clickedLogout: function () {
      LOGIN_STATE.commit("logout");
      this.$router.push("/");
    },
    fetchHoliday() {
      return AXIOS.get("holiday/getAll", {})
        .then(response => {
          this.holidays = response.data;
        })
        .catch(e => {
          console.log(e);
        });
    },
    fetchOpeningH() {
      return AXIOS.get("/openingH/getAll", {})
        .then(response => {
          this.openingHours = response.data.sort((a, b) => {
            return (
              daysOfWeekSorter[a["daysOfWeek"].toLowerCase()] -
              daysOfWeekSorter[b["daysOfWeek"].toLowerCase()]
            );
          });
        })
        .catch(e => {
          console.log(e);
        });
    },
  },
};

const daysOfWeekSorter = {
  monday: 1,
  tuesday: 2,
  wednesday: 3,
  thursday: 4,
  friday: 5,
  saturday: 6,
  sunday: 7,
};
