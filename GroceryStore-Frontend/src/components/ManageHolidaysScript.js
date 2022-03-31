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
      holidayListFields: [
        {
          key: "name",
          sortable: true,
        },
        {
          key: "date",
          sortable: true,
        },
      ],
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
    await AXIOS.get("/holiday/getAll", {})
      .then(response => {
        this.holidayList = response.data;
      })
      .catch(e => {
        let errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.holidayError = errorMsg;
      });
    this.isLoading = false;
  },
  methods: {
    editHolidayDialog: function () {},
    createHolidayDialog: function () {},
  },
};
