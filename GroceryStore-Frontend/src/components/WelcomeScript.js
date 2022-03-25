import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";

export default {
  name: "Welcome",
  data() {
    return {
      msg: "Welcome to the Grocery Store",
      LOGIN_STATE,
      isStaff:
        LOGIN_STATE.state.userType === "Employee" ||
        LOGIN_STATE.state.userType === "Owner",
      isCustomer: LOGIN_STATE.state.userType === "Customer",
      isLoading: false,
      openingHours: [],
    };
  },
  created: function () {
    // upon creation, fetch opening hours
    this.isLoading = true;
    AXIOS.get("/openingH/getAll", {})
      .then(response => {
        this.openingHours = response.data;
      })
      .catch(e => {
        console.log(e);
      })
      .finally(() => {
        // upon creation, verify if stored logged in user is still in the system
        let userType = LOGIN_STATE.state.userType;
        let username = LOGIN_STATE.state.username;
        if (LOGIN_STATE.state.isLoggedIn) {
          if (userType === "Owner") {
            AXIOS.get("/owner/".concat(username), {})
              .then(response => {})
              .catch(e => {
                this.logout();
              });
          } else if (userType === "Employee") {
            AXIOS.get("/employee/".concat(username), {})
              .then(response => {})
              .catch(e => {
                this.logout();
              });
          } else if (userType === "Customer") {
            AXIOS.get("/customer/".concat(username), {})
              .then(response => {})
              .catch(e => {
                this.logout();
              });
          } else {
            this.logout();
          }
        }
        this.isLoading = false;
      });
  },
  methods: {
    logout: function () {
      LOGIN_STATE.commit("logout");
      window.location.reload();
    },
  },
};
