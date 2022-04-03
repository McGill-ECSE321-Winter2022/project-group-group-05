import StaffDashboard from "./StaffDashboard";
import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";

export default {
  name: "ViewHistory",
  components: {
    StaffDashboard,
  },
  data() {
    return {
      purchases: [],
      isLoading: false,
      isOwner: LOGIN_STATE.state.userType === "Owner",
      isEmployee: LOGIN_STATE.state.userType === "Employee",
    };
  },
  created: function () {
    this.isLoading = true;
    AXIOS.get("/purchase/allCompleted", {}, {})
      .then(response => {
        this.errorPurchase = "";
        this.purchases = response.data;
      })
      .catch(e => {
        var errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.errorPurchase = errorMsg;
      })
      .then(response => {
        this.purchases.forEach(function (purchase) {
          var total = 0;
          purchase.specificItems.forEach(function (specificItem) {
            total += specificItem.purchaseQuantity * specificItem.purchasePrice;
          });
          purchase.total = total;
        });
      })
      .finally(() => {
        this.isLoading = false;
      });
  },
  methods: {
    orderType(purchase) {
      if (purchase.delivery) {
        return "delivery";
      } else {
        return "pick up";
      }
    },
    deliveryFee(purchase) {
      if (purchase.delivery) {
        return " + delivery fee $10";
      } else {
        return "";
      }
    },
  },
};
