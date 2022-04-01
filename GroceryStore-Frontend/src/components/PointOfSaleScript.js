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
      clickedSpItem:
        {
          item:
            {
              name: "",
            },
        },
      clickedSpItemQuantity: 1,
      // item input
      addItemName: "",
      addItemQty: 1,
      // pos pay
      paySuccessMessage: "",
      // item lookup
      itemList: [],
      itemFields: [
        {
          key: "name",
          label: "Item Name",
        },
        {
          key: "price",
          label: "Unit Price",
        },
      ],
      itemSearchQuery: "",
    };
  },
  computed: {
    filteredItemList() {
      return this.itemList.filter(item => {
        return item["name"].toLowerCase().includes(this.itemSearchQuery.trim().toLowerCase());
      });
    },
  },
  methods: {
    clear: function() {
      this.posError = "";
      this.cart = "";
      this.subtotal = 0;
      this.clickedSpItem = {
        item:
          {
            name: "",
          },
      };
      this.clickedSpItemQuantity = 1;
      this.addItemName = "";
      this.addItemQty = 1;
      this.itemSearchQuery = "";
    },
    startNewOrder: async function() {
      this.clear();
      this.isLoading = true;
      await AXIOS.post("/purchase/pos/cart", {}, {})
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
        });
      await AXIOS.get("/item/allInStock", {}).then(response => {
        this.itemList = response.data;
      }).catch(e => {
        console.log(e);
      });
      this.isLoading = false;
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
          this.clear();
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
          this.computeCartCosts();
          // clear fields
          this.addItemName = "";
          this.addItemQty = 1;
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
        let message = "Successfully completed order #" + response.data["id"];
        console.log(message);
        this.paySuccessMessage = message;
        this.$bvModal.show("pos-pay-success");
        this.clear();
        this.inProgress = false;
      }).catch(e => {
        let errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.posError = errorMsg;
      });
      this.isLoading = false;
    },
    editCartDialog: function(item) {
      this.clickedSpItem = item;
      this.clickedSpItemQuantity = item["purchaseQuantity"];
      this.$bvModal.show("edit-cart-item");
    },
    editCartSave: async function() {
      this.posError = "";
      this.isLoading = true;
      await AXIOS.post("/purchase/setItem/".concat(this.cart["id"]), {}, {
        params: {
          itemName: this.clickedSpItem["item"]["name"],
          quantity: this.clickedSpItemQuantity,
        },
      }).then(response => {
        this.cart = response.data;
        this.computeCartCosts();
      }).catch(e => {
        let errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.posError = errorMsg;
      });
      this.$bvModal.hide("edit-cart-item");
      console.log(
        "New subtotal is $" + Vue.filter("formatCurrency")(this.subtotal)
      );
      this.isLoading = false;
    },
    editCartRemove: async function() {
      this.posError = "";
      this.isLoading = true;
      await AXIOS.post("/purchase/setItem/".concat(this.cart["id"]), {}, {
        params: {
          itemName: this.clickedSpItem["item"]["name"],
          quantity: -1,
        },
      }).then(response => {
        this.cart = response.data;
        this.computeCartCosts();
      }).catch(e => {
        let errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.posError = errorMsg;
      });
      this.$bvModal.hide("edit-cart-item");
      console.log(
        "New subtotal is $" + Vue.filter("formatCurrency")(this.subtotal)
      );
      this.isLoading = false;
    },
    itemLookup: function() {
      this.posError = "";
      this.$bvModal.show("item-lookup");
    },
    itemLookupClicked: function(item) {
      this.addItemName = item["name"];
      this.$bvModal.hide("item-lookup");
    },
    computeCartCosts: function() {
      // compute individual total costs of each items in the cart
      this.cart["specificItems"].forEach(function (spItem) {
        let cost = spItem["purchaseQuantity"] * spItem["purchasePrice"];
        spItem["cost"] = cost;
      });
      // compute subtotal
      let newTotal = 0;
      for (const spItem of this.cart["specificItems"]) {
        newTotal += spItem["cost"];
      }
      this.subtotal = newTotal;
    },
  },
};
