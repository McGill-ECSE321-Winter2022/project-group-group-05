import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";

export default {
  name: "LoginForm",
  data() {
    return {
      LOGIN_STATE,
      form: {
        username: "",
        password: "",
      },
      formError: "",
      isLoading: false,
    };
  },
  methods: {
    onsubmit(event) {
      // disable the default response on submit buttons
      event.preventDefault();
      // find the user in Owner, Employee, then Customer
      let username = this.form.username;
      let password = this.form.password;
      this.formError = "";
      this.isLoading = true;
      AXIOS.get("/owner/".concat(username), {})
        .then(response => {
          if (password === response.data.password) {
            let userType = "Owner";
            LOGIN_STATE.commit("login", { userType, username });
            this.isLoading = false;
            this.$router.push("/ManageStaffProfile");
          } else {
            this.formError = "Wrong password";
            this.isLoading = false;
          }
        })
        .catch(e => {
          AXIOS.get("/employee/".concat(username), {})
            .then(response => {
              if (password === response.data.password) {
                let userType = "Employee";
                LOGIN_STATE.commit("login", { userType, username });
                this.isLoading = false;
                this.$router.push("/ManageStaffProfile");
              } else {
                this.formError = "Wrong password";
                this.isLoading = false;
              }
            })
            .catch(e => {
              AXIOS.get("/customer/".concat(username), {})
                .then(response => {
                  if (password === response.data.password) {
                    let userType = "Customer";
                    LOGIN_STATE.commit("login", { userType, username });
                    this.isLoading = false;
                    if (username === "kiosk") {
                      this.$router.push("/PointOfSale");
                    } else {
                      this.$router.push("/");
                    }
                  } else {
                    this.formError = "Wrong password";
                    this.isLoading = false;
                  }
                })
                .catch(e => {
                  this.formError = "User does not exist";
                  this.isLoading = false;
                });
            });
        });
    },
  },
};
