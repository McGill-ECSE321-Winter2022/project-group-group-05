import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";
export default {
  name: "ViewPaidOrder",
  data() {
    return {
      purchases: AXIOS.get("/purchase/allPaid", {}, {})
        .then(response => {
          this.errorPurchase = "";
          this.purchases = response.data;
        })
        .catch(e => {
          var errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.errorPurchase = errorMsg;
        }),
      totalPrice: 0,
      isOwner: LOGIN_STATE.state.userType === "Owner",
      isEmployee: LOGIN_STATE.state.userType === "Employee",
    };
  },
  methods: {
    orderType(purchase) {
      if (purchase.delivery) {
        return "delivery";
      } else {
        return "pick up";
      }
    },
    addToTotal(price) {
      this.totalPrice += price;
    },
    clearSum() {
      this.totalPrice = 0;
    },
    prepare: function (id) {
      AXIOS.post("/purchase/prepare/".concat(id), {}, {})
        .then(response => {
          this.errorPurchase = "";
        })
        .catch(e => {
          var errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.errorPurchase = errorMsg;
        });
      window.location.reload();
    },
  },
};
