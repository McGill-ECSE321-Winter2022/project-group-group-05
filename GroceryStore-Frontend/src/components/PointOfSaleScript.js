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
      // cart display
      cart: "",
      spItemFields: [
        {
          key: "item",
          label: "Item Name",
        },
        {
          key: "purchasePrice",
          label: "Unit Price",
        },
        {
          key: "purchaseQuantity",
          label: "Quantity",
        },
        {
          key: "cost",
          label: "Total Cost of Item",
        },
      ],
      subtotal: 0,
      // item input
      addItemName: "",
      addItemQty: 1,
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
    addItem: function(){
      let itemName = this.addItemName;
      let quantity = this.addItemQty;
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
          this.cart["specificItems"].forEach(function (spItem) {
            let cost = spItem["purchaseQuantity"] * spItem["purchasePrice"];
            spItem["cost"] = cost;
          });
          let newTotal = 0;
          for (const spItem of this.cart["specificItems"]) {
            newTotal += spItem["cost"];
          }
          this.subtotal = newTotal;
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
    payNow: async function() {
      this.posError = "";
      this.isLoading = true;
      await AXIOS.post("/purchase/pos/pay/".concat(this.cart["id"]), {}, {}).then(response => {
        console.log("Successfully completed order #" + response["id"]);
      }).catch(e => {
        let errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.posError = errorMsg;
      });
      this.isLoading = false;
    },
  },
};
