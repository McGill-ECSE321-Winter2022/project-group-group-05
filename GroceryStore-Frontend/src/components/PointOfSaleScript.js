import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import Vue from "vue";

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
      subtotal: 0,
    };
  },
  methods: {
    startNewOrder() {
      this.posError = "";
      this.isLoading = true;
      AXIOS.post("/purchase/pos/cart", {}, {})
        .then(response => {
          this.cart = response.data;
          console.log("Starting POS Order #" + this.cart["id"]);
          this.inProgress = true;
          this.subtotal = 0;
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
      this.isLoading = true;
      AXIOS.delete("/purchase/pos/delete/".concat(this.cart["id"]), {})
        .then(() => {
          console.log("Deleted Order #" + this.cart["id"]);
        })
        .catch(e => {
          console.log(e);
          // no need to display the message when cancelling
        })
        .finally(() => {
          this.cart = "";
          this.subtotal = 0;
          this.inProgress = false;
          this.isLoading = false;
        });
    },
    addItem(itemName, quantity) {
      this.posError = "";
      this.isLoading = true;
      AXIOS.post(
        "/purchase/addItem/".concat(this.cart["id"]),
        {},
        {
          params: {
            itemName: itemName,
            quantity: quantity,
          },
        }
      )
        .then(response => {
          this.cart = response.data;
          let newTotal = 0;
          for (const spItem of response.data["specificItems"]) {
            newTotal += spItem["purchaseQuantity"] * spItem["purchasePrice"];
          }
          this.subtotal = newTotal;
          // TODO: what's the best way to update the displayed items in cart?
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.posError = errorMsg;
        })
        .finally(() => {
          this.isLoading = false;
          console.log(
            "New subtotal is $" + Vue.filter("formatCurrency")(this.subtotal)
          );
        });
    },
  },
};
