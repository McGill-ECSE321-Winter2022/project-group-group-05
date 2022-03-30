import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import Vue from "vue";

export default {
  name: "ManageCart",
  created: async function () {
    // only fetch cart if the logged in user is a customer
    this.isCustomerLoggedIn =
      LOGIN_STATE.state.isLoggedIn &&
      LOGIN_STATE.state.userType === "Customer" &&
      LOGIN_STATE.state.username !== "kiosk";
    if (this.isCustomerLoggedIn) {
      // fetch customer to check if address is local
      await AXIOS.get(
        "/customer/".concat(LOGIN_STATE.state.username))
        .then(response => {
          this.isLocal = response.data.isLocal;
        })
        .catch(error => {
          // An error will be thrown if the customer logged in does not exist
          // Generally this error should not occur
          console.log(error.response.data.message);
          this.errorMessage = error.response.data.message;
        });
      await AXIOS.post(
        "/purchase/cart",
        {},
        { params: { username: LOGIN_STATE.state.username } }
      )
        .then(response => {
          this.items = response.data.specificItems;
          this.cartId = response.data.id;
          console.log(response.data);
          this.isDelivery = response.data.delivery;
        })
        .catch(error => {
          // An error will be thrown if the customer logged in does not exist
          // Generally this error should not occur
          console.log(error.response.data.message);
          this.errorMessage = error.response.data.message;
        });
    } else {
      this.errorMessage =
        "You must be logged in as a Customer to view this page";
    }
  },
  data() {
    return {
      isCustomerLoggedIn: false,

      fields: [
        {
          key: "name",
          label: "Item",
          thStyle: { width: "30%" },
          tdClass: "align-middle",
        },
        {
          key: "quantity",
          label: "Purchase Quantity",
          thStyle: { width: "20%" },
          tdClass: "align-middle",
        },
        {
          key: "price",
          label: "Item Price",
          thStyle: { width: "20%" },
          tdClass: "align-middle",
        },
        {
          key: "availability",
          label: "Available For",
          thStyle: { width: "20%" },
          tdClass: "align-middle",
        },
        {
          key: "remove",
          label: "Remove Item",
          thStyle: { width: "10%" },
          tdClass: "align-middle",
        },
      ],
      items: [],
      cartId: 0,

      isDelivery: false,
      isLocal: false,

      // error checking
      errorMessage: "",
      dismissCountDown: 0,
      dismissSecs: 5,
    };
  },
  computed: {
    totalCost: function () {
      var total = 0;
      this.items.forEach(item => {
        total += item.purchaseQuantity * item.purchasePrice;
      });
      // If the customer is not local to the town and requests delivery, 
      // add a flat 10 dollar shipping fee.
      if (!this.isLocal && this.isDelivery) {
        total += 10;
      }
      return (Math.round(total * 100) / 100).toFixed(2);
    },
  },
  methods: {
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown;
    },
    async updateItemQuantity(rowIndex) {
      await AXIOS.post(
        "/purchase/setItem/".concat(this.cartId),
        {},
        {
          params: {
            itemName: this.items[rowIndex].item.name,
            quantity: this.items[rowIndex].purchaseQuantity,
          },
        }
      ).catch(error => {
        // Throws an error if:
        // Cart is not in cart state
        // New purchaseQuantity is too large
        // This should never throw an error in normal operation
        console.log(error.response.data.message);
        this.dismissCountDown = this.dismissSecs;
        this.errorMessage = error.response.data.message;
      });
    },
    async removeItem(rowIndex) {
      await AXIOS.post(
        "/purchase/setItem/".concat(this.cartId),
        {},
        {
          params: {
            itemName: this.items[rowIndex].item.name,
            quantity: 0,
          },
        }
      )
        .then(response => {
          this.items = response.data.specificItems;
        })
        .catch(error => {
          // Throws an error if:
          // Cart is not in cart state
          // This should never throw an error in normal operation
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
        });
    },
    async orderPurchase() {
      // evaluate if the order type is valid (i.e. are all items in cart available to be delivered/picked up)
      var orderValid = true;
      if (this.isDelivery) {
        this.items.forEach(item => {
          if (!item.item.canDeliver) {
            this.dismissCountDown = this.dismissSecs;
            this.errorMessage =
              "One or more of the items in your cart can not be delivered!";
            orderValid = false;
          }
        });
      } else {
        this.items.forEach(item => {
          if (!item.item.canPickUp) {
            this.dismissCountDown = this.dismissSecs;
            this.errorMessage =
              "One or more of the items in your cart is not available for pick up!";
            orderValid = false;
          }
        });
      }
      // stop execution if order is not valid
      if (!orderValid) {
        return;
      }
      // set order type
      await AXIOS.post(
        "/purchase/setIsDelivery/".concat(this.cartId),
        {},
        {
          params: {
            isDelivery: this.isDelivery,
          },
        }
      ).catch(error => {
        // Throws an error if:
        // POS purchase
        // Cart is not in cart state
        // One of the items cannot be delivered/picked up
        // This should never throw an error in normal operation
        console.log(error.response.data.message);
        this.dismissCountDown = this.dismissSecs;
        this.errorMessage = error.response.data.message;
      });
      // pay for purchase
      await AXIOS.post("/purchase/pay/".concat(this.cartId))
        .then(this.$bvModal.show("order-complete"))
        .catch(error => {
          // Throws an error if:
          // Cart is not in cart state
          // Cart is empty
          // One of the items is out of stock
          // One of the items cannot be delivered/picked up
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
        });
    },
  },
};
