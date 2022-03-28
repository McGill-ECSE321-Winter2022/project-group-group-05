<!--Visibility: customer-->
<!--View list of all purchases (state [cancelled, completed], timeofpurchase, total price, etc.)-->
<!--Cancel button for purchases in paid state-->
<template>
  <div id="viewhistory" v-if="userType === 'Customer'">
    <b-overlay
      id="overlay"
      :show="isLoading"
      :variant="variant"
      :opacity="0.85"
      rounded="sm"
    >
      <h1>Purchase History</h1>
      <br />
      <table
        id="purchaseTable"
        v-for="purchase in purchases"
        :key="purchase.id"
      >
        <div id="header">
          <td id="state">{{ purchase.state }} &nbsp; &nbsp; &nbsp;</td>
          <td id="date">Date: {{ purchase.dateOfPurchase }} &nbsp;</td>
          <td id="orderID">Order# {{ purchase.id }} &nbsp;</td>
          <td id="orderType">Order type: {{ orderType(purchase) }} &nbsp;</td>
          <td v-if="purchase.state == 'Paid'">
            <b-button
              variant="danger"
              id="cancel"
              v-on:click="cancel(purchase.id)"
              >Cancel Order</b-button
            >
          </td>
          <td v-else-if="purchase.state == 'Prepared'">
            <b-button
              variant="success"
              id="confirm"
              v-on:click="confirm(purchase.id)"
              >Confirm Order Received</b-button
            >
          </td>
        </div>
        <table id="itemTable">
          <tr>
            <th>item</th>
            <th>price</th>
            <th>quantity</th>
          </tr>
          <tr
            v-for="specificItem in purchase.specificItems"
            :key="specificItem.id"
          >
            <td>{{ specificItem.item.name }}</td>
            <td>${{ specificItem.purchasePrice | formatCurrency }}</td>
            <td>
              {{ specificItem.purchaseQuantity }}
            </td>
          </tr>
        </table>
        <hr />
        <tr id="totalPrice">
          Total: ${{
            purchase.total | formatCurrency
          }}
        </tr>
        <br />
      </table>
    </b-overlay>
  </div>
</template>

<script>
import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";
export default {
  name: "ViewHistory",
  data() {
    return {
      purchases: [],
      userType: LOGIN_STATE.state.userType,
      isLoading: false,
      variant: "light",
    };
  },
  created: function () {
    this.isLoading = true;
    AXIOS.get(
      "/customer/".concat(LOGIN_STATE.state.username).concat("/getPurchases"),
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
    cancel: function (id) {
      this.isLoading = true;
      AXIOS.post("/purchase/cancel/".concat(id), {}, {})
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
    confirm: function (id) {
      this.isLoading = true;
      AXIOS.post("/purchase/complete/".concat(id), {}, {})
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
</script>

<style scoped>
#overlay {
  position: fixed;
  height: 50%;
}
#cancel {
  position: absolute;
  right: 0px;
}
#confirm {
  position: absolute;
  right: 0px;
}
#header {
  border-radius: 5px;
  position: relative;
  background-color: rgb(231, 234, 240);
  display: flex;
  flex-direction: row;
  height: 40px;
  text-align: bottom;
}
#viewhistory {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 60%;
  transform: translate(-50%, -50%);
  height: 750px;
}
#purchaseTable {
  position: relative;
  width: 100%;
}
#itemTable {
  width: 80%;
  text-align: left；;
}
#state {
  font-size: 150%;
  font-weight: bold;
  color: rgb(59, 64, 85);
}
#date {
  position: absolute;
  left: 140px;
  bottom: 5px;
}
#orderID {
  position: absolute;
  left: 300px;
  bottom: 5px;
}
#orderType {
  position: absolute;
  left: 480px;
  bottom: 5px;
}
#totalPrice {
  position: absolute;
  right: 200px;
  bottom: 10px;
}
</style>
