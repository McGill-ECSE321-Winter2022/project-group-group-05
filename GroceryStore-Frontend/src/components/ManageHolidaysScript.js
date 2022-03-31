import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";
import StaffDashboard from "./StaffDashboard";

export default {
  name: "ManageHolidays",
  components: { StaffDashboard },
  data() {
    return {
      LOGIN_STATE,
      isOwner: LOGIN_STATE.state.userType === "Owner",
      isLoading: false,
      // browse holidays
      holidayList: [],
      clickedHoliday: "",
      holidaySearchQuery: "",
      // create holidays
      createForm: {
        name: "",
        date: "",
        error: "",
      },
      // edit holidays
      editForm: {
        name: "",
        date: "",
        error: "",
      },
      holidayError: "",
    };
  },
  computed: {
    filteredHolidayList() {
      return this.holidayList.filter(holiday => {
        return holiday["name"]
          .toLowerCase()
          .includes(this.holidaySearchQuery.trim().toLowerCase());
      });
    },
  },
  created: async function () {
    this.isLoading = true;
    await this.fetchHolidays();
    this.isLoading = false;
  },
  methods: {
    fetchHolidays: function () {
      console.log("Fetching list of holidays");
      return AXIOS.get("/holiday/getAll", {})
        .then(response => {
          this.holidayList = response;
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.holidayError = errorMsg;
        });
    },
    editHolidayDialog: function () {},
  },
};
