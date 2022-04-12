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
      clickedSpItem: {
        item: {
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
    /**
     * Apply string search filter to the list of items
     * @returns {*[]}
     */
    filteredItemList() {
      return this.itemList.filter(item => {
        return item["name"]
          .toLowerCase()
          .includes(this.itemSearchQuery.trim().toLowerCase());
      });
    },
  },
  methods: {
    clear: function () {
      this.posError = "";
      this.cart = "";
      this.subtotal = 0;
      this.clickedSpItem = {
        item: {
          name: "",
        },
      };
      this.clickedSpItemQuantity = 1;
      this.addItemName = "";
      this.addItemQty = 1;
      this.itemSearchQuery = "";
    },
    /**
     * Called when the start new order button is clicked
     * @returns {Promise<void>}
     */
    startNewOrder: async function () {
      this.clear();
      this.isLoading = true;
      // create new cart purchase
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
      // update list with all items that are currently in stock
      await AXIOS.get("/item/allInStock", {})
        .then(response => {
          this.itemList = response.data;
        })
        .catch(e => {
          console.log(e);
        });
      this.isLoading = false;
    },
    /**
     * Called when the cancel order button is clicked
     */
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
    /**
     * Add the selected item with the selected quantity to the cart
     */
    addItem: function () {
      this.posError = "";
      this.isLoading = true;
      AXIOS.post(
        "/purchase/addItem/".concat(this.cart["id"]),
        {},
        {
          params: {
            itemName: this.addItemName,
            quantity: this.addItemQty,
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
    /**
     * Called when the checkout button is clicked
     * @returns {Promise<void>}
     */
    payNow: async function () {
      this.posError = "";
      this.isLoading = true;
      await AXIOS.post("/purchase/pos/pay/".concat(this.cart["id"]), {}, {})
        .then(response => {
          // display success popup after payment
          let message = "Successfully completed order #" + response.data["id"];
          console.log(message);
          this.paySuccessMessage = message;
          this.$bvModal.show("pos-pay-success");
          this.clear();
          this.inProgress = false;
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.posError = errorMsg;
        });
      this.isLoading = false;
    },
    /**
     * Called when an item in the cart is clicked, in order to edit the quantity
     * @param item
     */
    editCartDialog: function (item) {
      this.clickedSpItem = item;
      this.clickedSpItemQuantity = item["purchaseQuantity"];
      this.$bvModal.show("edit-cart-item");
    },
    /**
     * Called to save the modification made in the editCartDialog popup window
     * @returns {Promise<void>}
     */
    editCartSave: async function () {
      this.posError = "";
      this.isLoading = true;
      await AXIOS.post(
        "/purchase/setItem/".concat(this.cart["id"]),
        {},
        {
          params: {
            itemName: this.clickedSpItem["item"]["name"],
            quantity: this.clickedSpItemQuantity,
          },
        }
      )
        .then(response => {
          this.cart = response.data;
          this.computeCartCosts();
        })
        .catch(e => {
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
    /**
     * Called if the remove item button is clicked
     * @returns {Promise<void>}
     */
    editCartRemove: async function () {
      this.posError = "";
      this.isLoading = true;
      await AXIOS.post(
        "/purchase/setItem/".concat(this.cart["id"]),
        {},
        {
          params: {
            itemName: this.clickedSpItem["item"]["name"],
            quantity: -1,
          },
        }
      )
        .then(response => {
          this.cart = response.data;
          this.computeCartCosts();
        })
        .catch(e => {
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
    /**
     * Show the item lookup popup window
     */
    itemLookup: function () {
      this.posError = "";
      this.$bvModal.show("item-lookup");
    },
    /**
     * Hide the item lookup popup window when an item is chosen
     * @param item
     */
    itemLookupClicked: function (item) {
      this.addItemName = item["name"];
      this.$bvModal.hide("item-lookup");
    },
    /**
     * Recompute the costs of the items in the cart
     */
    computeCartCosts: function () {
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
