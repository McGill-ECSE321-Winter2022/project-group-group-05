<!--Visibility: owner, employee-->
<!--Show all paid orders and be able to change them to prepared-->
<template>
  <div id="manage-paid-orders" v-if="isEmployee || isOwner">
    <div class="back-to-previous-style">
      <a href="javascript:history.back()">Previous Page</a>
    </div>

    <h1 class="header_style">Manage Paid Orders</h1>
    <br />

    <table id="purchaseTable" v-for="purchase in purchases" :key="purchase.id">
      <div id="header">
        <td class="tdstyle">{{ purchase.state }} &nbsp; &nbsp; &nbsp;</td>
        <td class="tdstyle">Date: {{ purchase.dateOfPurchase }} &nbsp;</td>
        <td class="tdstyle">Order# {{ purchase.id }} &nbsp;</td>
        <td class="tdstyle">Order type: {{ orderType(purchase) }} &nbsp;</td>
        <td>
          <b-button
            pill
            variant="info"
            id="Prepare"
            v-on:click="prepare(purchase.id)"
            >Prepare Order</b-button
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
            {{
              addToTotal(
                specificItem.purchaseQuantity * specificItem.purchasePrice
              )
            }}
          </td>
        </tr>
      </table>
      <hr />
      <tr id="totalPrice">
        Total: ${{
          totalPrice | formatCurrency
        }}
        {{
          clearSum()
        }}
      </tr>
      <br />
    </table>
  </div>
</template>

<script src="./ManagePaidOrdersScript.js"></script>

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
  text-align: bottom;
}
#manage-paid-orders {
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
.back-to-previous-style {
  padding: 20px 1px;
  font-size: 20px;
  position: absolute;
  top: 0;
  left: 0;
  width: 180px;
  text-align: center;
}
a {
  color: #2410da;
}
</style>
