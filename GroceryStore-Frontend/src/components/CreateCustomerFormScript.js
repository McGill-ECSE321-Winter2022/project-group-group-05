import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";

export default {
  name: "CreateCustomerForm",
  data() {
    return {
      LOGIN_STATE,
      form: {
        username: "",
        password: "",
        email: "",
        address: "",
        isLocal: false,
      },
      formError: "",
      isLoading: false,
    };
  },
  methods: {
    /**
     * Called when the customer creation form is submitted
     */
    onsubmit(event) {
      event.preventDefault();
      let userType = "Customer";
      let username = this.form.username;
      let password = this.form.password;
      let email = this.form.email;
      let address = this.form.address;
      let isLocal = this.form.isLocal;
      this.formError = "";
      this.isLoading = true;
      AXIOS.post(
        "/customer/".concat(username),
        {},
        {
          params: {
            password: password,
            email: email,
            address: address,
            isLocal: isLocal,
          },
        }
      )
        .then(() => {
          LOGIN_STATE.commit("login", { userType, username });
          this.isLoading = false;
          // return to welcome page
          this.$router.push("/");
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.formError = errorMsg;
          this.isLoading = false;
        });
    },
  },
};
