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
    /**
     * Show the opening hours popup window
     * @returns {Promise<void>}
     */
    clickedOpeningH: async function () {
      this.isLoading = true;
      await this.fetchOpeningH();
      this.$bvModal.show("opening-h");
      this.isLoading = false;
    },
    /**
     * Show the holidays popup window
     * @returns {Promise<void>}
     */
    clickedHoliday: async function () {
      this.isLoading = true;
      await this.fetchHoliday();
      this.$bvModal.show("list-of-holidays");
      this.isLoading = false;
    },
    /**
     * Redirect to user profile management page
     */
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
    /**
     * Perform logout
     */
    clickedLogout: function () {
      LOGIN_STATE.commit("logout");
      this.$router.push("/");
    },
    /**
     * Fetch all holidays from the database
     * @returns {Promise<AxiosResponse<any>>}
     */
    fetchHoliday() {
      return AXIOS.get("holiday/getAll", {})
        .then(response => {
          this.holidays = response.data;
        })
        .catch(e => {
          console.log(e);
        });
    },
    /**
     * Fetch all opening hours from the database
     * @returns {Promise<AxiosResponse<any>>}
     */
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

/**
 * Used to sort the opening hours by daysOfWeek
 * @type {{sunday: number, saturday: number, tuesday: number, wednesday: number, thursday: number, friday: number, monday: number}}
 */
const daysOfWeekSorter = {
  monday: 1,
  tuesday: 2,
  wednesday: 3,
  thursday: 4,
  friday: 5,
  saturday: 6,
  sunday: 7,
};
