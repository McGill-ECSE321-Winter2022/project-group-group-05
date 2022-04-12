import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";
import StaffDashboard from "./StaffDashboard";

export default {
  name: "ManageHolidays",
  components: { StaffDashboard },
  data() {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
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
      minDate: today,
      holidayError: "",
    };
  },
  computed: {
    /**
     * Apply string search filter to the list of Holiday
     * @returns {*[]}
     */
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
    editHolidayDialog: function (item) {
      this.resetForms();
      this.editForm.date = item["date"];
      this.editForm.name = item["name"];
      this.$bvModal.show("edit-holiday-dialog");
    },
    createHolidayDialog: function () {
      this.resetForms();
      this.$bvModal.show("create-holiday-dialog");
    },
    submitEdit: function (event) {
      event.preventDefault();
      this.editForm.error = "";
      if (this.editForm.date === "") {
        this.editForm.error = "Please choose a date";
      } else {
        this.isLoading = true;
        AXIOS.patch(
          "/holiday/".concat(this.editForm.name),
          {},
          {
            params: {
              date: this.editForm.date,
            },
          }
        )
          .then(() => {
            this.$bvModal.hide("edit-holiday-dialog");
            this.resetForms();
            AXIOS.get("/holiday/getAll", {})
              .then(response => {
                this.holidayList = response.data;
              })
              .catch(e => {
                let errorMsg = e.response.data.message;
                console.log(errorMsg);
                this.holidayError = errorMsg;
              });
          })
          .catch(e => {
            let errorMsg = e.response.data.message;
            console.log(errorMsg);
            this.editForm.error = errorMsg;
          })
          .finally(() => {
            this.isLoading = false;
          });
      }
    },
    deleteHoliday: function () {
      this.editForm.error = "";
      this.isLoading = true;
      AXIOS.delete("/holiday/".concat(this.editForm.name))
        .then(() => {
          this.$bvModal.hide("edit-holiday-dialog");
          this.resetForms();
          AXIOS.get("/holiday/getAll", {})
            .then(response => {
              this.holidayList = response.data;
            })
            .catch(e => {
              let errorMsg = e.response.data.message;
              console.log(errorMsg);
              this.holidayError = errorMsg;
            });
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.editForm.error = errorMsg;
        })
        .finally(() => {
          this.isLoading = false;
        });
    },
    submitCreate: function (event) {
      event.preventDefault();
      this.createForm.error = "";
      if (this.createForm.date === "") {
        this.createForm.error = "Please choose a date";
      } else {
        this.isLoading = true;
        AXIOS.post(
          "/holiday/".concat(this.createForm.name),
          {},
          {
            params: {
              date: this.createForm.date,
            },
          }
        )
          .then(() => {
            this.$bvModal.hide("create-holiday-dialog");
            this.resetForms();
            AXIOS.get("/holiday/getAll", {})
              .then(response => {
                this.holidayList = response.data;
              })
              .catch(e => {
                let errorMsg = e.response.data.message;
                console.log(errorMsg);
                this.holidayError = errorMsg;
              });
          })
          .catch(e => {
            let errorMsg = e.response.data.message;
            console.log(errorMsg);
            this.createForm.error = errorMsg;
          })
          .finally(() => {
            this.isLoading = false;
          });
      }
    },
    resetForms: function () {
      this.createForm.date = "";
      this.createForm.name = "";
      this.createForm.error = "";
      this.editForm.date = "";
      this.editForm.name = "";
      this.editForm.error = "";
    },
  },
};
