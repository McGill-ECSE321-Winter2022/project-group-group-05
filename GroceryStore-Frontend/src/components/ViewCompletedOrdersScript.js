import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";
export default {
  name: "ViewHistory",
  data() {
    return {
      purchases: AXIOS.get("/purchase/allCompleted",
        {},
        {}
      )
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
  },
};