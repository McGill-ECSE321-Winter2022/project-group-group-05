import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";
import StaffDashboard from "./StaffDashboard";
export default {
  name: "ViewPaidOrder",
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
    AXIOS.get("/purchase/allPaid", {}, {}
    )
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
    prepare: function (id) {
      this.isLoading = true;
      AXIOS.post("/purchase/prepare/".concat(id), {}, {})
        .then(response => {
          this.errorPurchase = "";
        })
        .catch(e => {
          var errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.errorPurchase = errorMsg;
        })
        .finally(() => {
          window.location.reload();
        });
    },
  },
};
