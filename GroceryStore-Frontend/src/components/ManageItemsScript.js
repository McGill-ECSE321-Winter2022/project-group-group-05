import StaffDashboard from "./StaffDashboard";
import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import Vue from "vue";

export default {
  name: "ManageItems",
  components: {
    StaffDashboard,
  },
  created: async function () {
    this.isOwnerLoggedIn =
      LOGIN_STATE.state.userType === "Owner" && LOGIN_STATE.state.isLoggedIn;
    this.isEmployeeLoggedIn =
      LOGIN_STATE.state.userType === "Employee" && LOGIN_STATE.state.isLoggedIn;
    await AXIOS.get("/item/getAll")
      .then(response => {
        this.fetchedItems = response.data;
      })
      .catch(error => {
        // This error should never occur, as the get request doesn't traditionally return an error
        console.log(error.response.data.message);
        this.errorMessage = error.response.data.message;
      });
  },
  data() {
    return {
      isOwnerLoggedIn: false,
      isEmployeeLoggedIn: false,

      // used for item table
      searchQuery: "",
      perPage: 10,
      currentPage: 1,
      fetchedItems: [],
      fields: [
        { key: "name", sortable: true },
        { key: "price", sortable: true },
        { key: "inventory", sortable: true },
        "canDeliver",
        "canPickUp",
        "discontinued",
      ],

      // used for error handling
      errorMessage: "",
      dismissCountDown: 0,
      dismissSecs: 5,

      // used in the creation of an item
      itemName: "",
      itemImage: "",
      itemPrice: 0,
      itemInventory: 0,
      canBePickedUp: true,
      canBeDelivered: true,
      // Indicates whether the user tried to make an item
      // If true, the form will evaluate the validity of the inputs
      creationAttempt: false,

      // holds the info for the item to be modified
      itemToBeModified: [],
    };
  },
  computed: {
    items: function () {
      return this.fetchedItems.filter(item =>
        item.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    },
    isItemNameValid: function () {
      if (
        this.itemName !== "" &&
        this.itemPrice > 0 &&
        this.itemInventory > 0
      ) {
        this.creationAttempt = true;
      }
      return this.creationAttempt ? this.itemName !== "" : null;
    },
    isItemPriceValid: function () {
      if (
        this.itemName !== "" &&
        this.itemPrice > 0 &&
        this.itemInventory > 0
      ) {
        this.creationAttempt = true;
      }
      return this.creationAttempt ? this.itemPrice > 0 : null;
    },
    isItemInventoryValid: function () {
      if (
        this.itemName !== "" &&
        this.itemPrice > 0 &&
        this.itemInventory > 0
      ) {
        this.creationAttempt = true;
      }
      return this.creationAttempt ? this.itemInventory > 0 : null;
    },
    isItemModifyPriceValid: function () {
      // replace "true" with "null" to avoid showing green outline
      return this.itemToBeModified.price > 0 ? null : false;
    },
    isItemModifyInventoryValid: function () {
      // replace "true" with "null" to avoid showing green outline
      return this.itemToBeModified.inventory > 0 ? null : false;
    },
  },
  methods: {
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown;
    },
    resetItemForm() {
      this.itemName = "";
      this.itemImage = "";
      this.itemPrice = 0;
      this.itemInventory = 0;
      this.canBePickedUp = true;
      this.canBeDelivered = true;
      this.creationAttempt = false;
    },
    handleOk(okEvent) {
      // Prevent the default function of the "ok" button in b-modal and replace with custom function
      okEvent.preventDefault();
      this.addNewItem();
    },
    async addNewItem() {
      this.creationAttempt = true;
      if (
        this.isItemNameValid &&
        this.isItemPriceValid &&
        this.isItemInventoryValid
      ) {
        await AXIOS.post(
          "/item/".concat(this.itemName),
          {},
          {
            params: {
              image: this.itemImage,
              price: this.itemPrice,
              inventory: this.itemInventory,
              canDeliver: this.canBeDelivered,
              canPickUp: this.canBePickedUp,
            },
          }
        )
          .then(response => {
            this.fetchedItems.push(response.data);
          })
          .catch(error => {
            console.log(error.response.data.message);
            this.dismissCountDown = this.dismissSecs;
            this.errorMessage = error.response.data.message;
          });
        // Hide the Create new Shift form after the request completes
        this.$nextTick(() => {
          this.$bvModal.hide("createItem");
        });
      }
    },
    modify(item) {
      // only allow modification if the owner is logged in
      if (this.isOwnerLoggedIn) {
        this.itemToBeModified = Object.assign({}, item);
        this.$bvModal.show("editItem");
      }
    },
    resetEditItemForm() {
      this.itemToBeModified = [];
    },
    async deleteItem() {
      await AXIOS.delete("/item/".concat(this.itemToBeModified.name))
        .then(() => {
          // update the frontend
          for (var i = 0; i < this.fetchedItems.length; i++) {
            if (this.fetchedItems[i].name === this.itemToBeModified.name) {
              this.fetchedItems.splice(i, 1);
              break;
            }
          }
        })
        .catch(error => {
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
        });
      this.$nextTick(() => {
        this.$bvModal.hide("editItem");
      });
    },
    async discontinue() {
      await AXIOS.patch(
        "/item/"
          .concat(this.itemToBeModified.name)
          .concat("/setIsDiscontinued"),
        {},
        {
          params: {
            isDiscontinued: !this.itemToBeModified.discontinued,
          },
        }
      )
        .then(response => {
          // update the frontend
          for (var i = 0; i < this.fetchedItems.length; i++) {
            if (this.fetchedItems[i].name === response.data.name) {
              this.fetchedItems[i].discontinued = response.data.discontinued;
              this.itemToBeModified.discontinued = response.data.discontinued;
              break;
            }
          }
        })
        .catch(error => {
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
        });
    },
    async saveItemChanges() {
      // if item parameters are invalid, do not save changes
      // Note: we check that the boolean values are equal to false for readability
      // Javascript's boolean evaluation for null values is not immediately obvious
      if (
        this.isItemModifyInventoryValid === false ||
        this.isItemModifyPriceValid === false
      ) {
        return;
      }
      // modify price
      await AXIOS.patch(
        "/item/".concat(this.itemToBeModified.name).concat("/setPrice"),
        {},
        {
          params: {
            price: this.itemToBeModified.price,
          },
        }
      ).catch(error => {
        console.log(error.response.data.message);
        this.dismissCountDown = this.dismissSecs;
        this.errorMessage = error.response.data.message;
      });
      // modify image
      await AXIOS.patch(
        "/item/".concat(this.itemToBeModified.name).concat("/setImage"),
        {},
        {
          params: {
            image: this.itemToBeModified.image,
          },
        }
      ).catch(error => {
        console.log(error.response.data.message);
        this.dismissCountDown = this.dismissSecs;
        this.errorMessage = error.response.data.message;
      });
      // modify inventory
      await AXIOS.patch(
        "/item/".concat(this.itemToBeModified.name).concat("/setInventory"),
        {},
        {
          params: {
            inventory: this.itemToBeModified.inventory,
          },
        }
      ).catch(error => {
        console.log(error.response.data.message);
        this.dismissCountDown = this.dismissSecs;
        this.errorMessage = error.response.data.message;
      });
      // modify deliverable
      await AXIOS.patch(
        "/item/".concat(this.itemToBeModified.name).concat("/setCanDeliver"),
        {},
        {
          params: {
            canDeliver: this.itemToBeModified.canDeliver,
          },
        }
      ).catch(error => {
        console.log(error.response.data.message);
        this.dismissCountDown = this.dismissSecs;
        this.errorMessage = error.response.data.message;
      });
      // modify pickup-able
      await AXIOS.patch(
        "/item/".concat(this.itemToBeModified.name).concat("/setCanPickUp"),
        {},
        {
          params: {
            canPickUp: this.itemToBeModified.canPickUp,
          },
        }
      ).catch(error => {
        console.log(error.response.data.message);
        this.dismissCountDown = this.dismissSecs;
        this.errorMessage = error.response.data.message;
      });
      await AXIOS.patch(
        "/item/"
          .concat(this.itemToBeModified.name)
          .concat("/setIsDiscontinued"),
        {},
        {
          params: {
            isDiscontinued: this.itemToBeModified.discontinued,
          },
        }
      )
        .then(response => {
          // only update the frontend after all modifications have been made
          for (var i = 0; i < this.fetchedItems.length; i++) {
            if (this.fetchedItems[i].name === response.data.name) {
              Vue.set(this.fetchedItems, i, response.data);
              break;
            }
          }
        })
        .catch(error => {
          console.log(error.response.data.message);
          this.dismissCountDown = this.dismissSecs;
          this.errorMessage = error.response.data.message;
        });
      this.$nextTick(() => {
        this.$bvModal.hide("editItem");
      });
    },
  },
};
