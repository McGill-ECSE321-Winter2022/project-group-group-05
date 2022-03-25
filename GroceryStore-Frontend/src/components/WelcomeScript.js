import { LOGIN_STATE } from "../common/StateScript";

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
    };
  },
  methods: {
    logout: function () {
      LOGIN_STATE.commit("logout");
      window.location.reload();
    },
  },
};
