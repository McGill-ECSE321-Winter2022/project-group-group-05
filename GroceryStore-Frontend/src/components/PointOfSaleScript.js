import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";

export default {
  name: "PointOfSale",
  data() {
    return {
      LOGIN_STATE,
      isKiosk: LOGIN_STATE.state.username === "kiosk",
      isLoading: false,
      inProgress: false,
      posError: "",
      cart: "",
    };
  },
  methods: {
    startNewOrder() {
      this.posError = "";
      this.isLoading = true;
      AXIOS.post("/purchase/pos/cart", {}, {})
        .then(response => {
          this.cart = response.data;
          console.log("Starting POS Order #" + this.cart.id);
          this.inProgress = true;
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.posError = errorMsg;
        })
        .finally(() => {
          this.isLoading = false;
        });
    },
    cancelNewOrder() {
      this.posError = "";
      this.inProgress = false;
    },
  },
};
