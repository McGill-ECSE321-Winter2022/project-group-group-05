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
    };
  },
  created: function () {
    // upon creation, verify if stored logged in user is still in the system
    let userType = LOGIN_STATE.state.userType;
    let username = LOGIN_STATE.state.username;
    if (LOGIN_STATE.state.isLoggedIn) {
      this.isLoading = true;
      if (userType === "Owner") {
        AXIOS.get("/owner/".concat(username), {})
          .then(response => {
            this.isLoading = false;
          })
          .catch(e => {
            this.isLoading = false;
            this.logout();
          });
      } else if (userType === "Employee") {
        AXIOS.get("/employee/".concat(username), {})
          .then(response => {
            this.isLoading = false;
          })
          .catch(e => {
            this.isLoading = false;
            this.logout();
          });
      } else if (userType === "Customer") {
        AXIOS.get("/customer/".concat(username), {})
          .then(response => {
            this.isLoading = false;
          })
          .catch(e => {
            this.isLoading = false;
            this.logout();
          });
      } else {
        this.isLoading = false;
        this.logout();
      }
    }
  },
  methods: {
    logout: function () {
      LOGIN_STATE.commit("logout");
      window.location.reload();
    },
  },
};
