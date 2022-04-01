<!--Visibility: employee, owner-->
<!--View all completed order-->
<template>
  <div id="view-completed-orders" v-if="isEmployee || isOwner">
    <b-overlay
      id="overlay"
      :show="isLoading"
      :opacity="0.85"
      rounded="sm"
    >
      <div :aria-hidden="isLoading ? 'true' : null" id="manage-inner">
        <b-container fluid>
          <b-row>
            <b-col md="auto">
              <StaffDashboard></StaffDashboard>
            </b-col>

            <b-col>
              <h1 class="header_style">Completed Orders</h1>

              <table
                id="purchaseTable"
                v-for="purchase in purchases"
                :key="purchase.id"
              >
                <div id="header">
                  <td class="tdstyle">
                    {{ purchase.state }} &nbsp; &nbsp; &nbsp;
                  </td>
                  <td class="tdstyle">
                    Date: {{ purchase.dateOfPurchase }} &nbsp;
                  </td>
                  <td class="tdstyle">Order# {{ purchase.id }} &nbsp;</td>
                  <td class="tdstyle">
                    Order type: {{ orderType(purchase) }} &nbsp;
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
            </b-col>
          </b-row>
        </b-container>
      </div>
    </b-overlay>
  </div>
</template>

<script src="./ViewCompletedOrdersScript.js"></script>

<style scoped>
/* Styling for Page Title */
.header_style {
  padding-top: 15px;
  padding-left: 100px;
  border-color: #91c788;
  border-style: solid;
  border-width: 0px 0px 6px 0px;
  text-align: center;
  margin-bottom: 30px;
}
.tdstyle {
  border: 1px solid #dddddd;
  text-align: center;
  padding: 8px;
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
}
#view-completed-orders {
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
  text-align: left;
}
#totalPrice {
  position: absolute;
  right: 200px;
  bottom: 10px;
}
</style>
