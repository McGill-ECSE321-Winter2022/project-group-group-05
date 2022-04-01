<!--Visibility: kiosk-->
<!--A in-store POS check out interface-->
<template>
  <div id="pos-interface-outer" v-if="isKiosk">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="pos-interface-inner">
        <p class="h2 text-center mb-4">In-Store Checkout System</p>

        <div id="pos-start-button" v-if="!inProgress">
          <b-button
            block
            variant="outline-success"
            size="lg"
            v-on:click="startNewOrder()"
          >
            <p class="h4 mb-1 mt-1">
              <b-icon icon="cart3" aria-hidden="true"></b-icon>
              Start New Order
            </p>
          </b-button>
        </div>

        <div id="pos-started" v-if="inProgress">
          <b-button
            block
            variant="danger"
            size="lg"
            v-on:click="cancelNewOrder()"
          >
            <p class="h4 mb-1 mt-1">
              <b-icon icon="cart-x" aria-hidden="true"></b-icon>
              Cancel Order #{{ cart.id }}
            </p>
          </b-button>
          <hr>

          <b-container fluid class="mb-4">
            <b-row>
              <b-col sm="8">
                <b-input-group>
                  <b-input-group-prepend>
                    <b-button variant="primary"><b-icon icon="search" aria-hidden="true" class="mr-2"></b-icon>Item Lookup</b-button>
                  </b-input-group-prepend>
                  <b-input placeholder="Enter item name" v-model="addItemName"></b-input>
                </b-input-group>
              </b-col>
              <b-col sm="2">
                <b-spinbutton v-model="addItemQty" min="1" max="100" wrap></b-spinbutton>
              </b-col>
              <b-col sm="2">
                <b-button block variant="outline-primary" @click="addItem">Add Item</b-button>
              </b-col>
            </b-row>
          </b-container>

          <b-table
            id="cart-items-table"
            hover
            fixed
            :items="cart['specificItems']"
            :fields="spItemFields"
          >
            <template #cell(item)="data">
              <div class="h5"><b>{{ data.value['name'] }}</b></div>
              <b-img-lazy
                thumbnail
                style="max-width: 75px"
                :src="data.value['image'] ? data.value['image'] : '/static/no-image.jpg'"></b-img-lazy>
            </template>
            <template #cell(purchasePrice)="data">
              <div class="h5">${{ data.value | formatCurrency }}</div>
            </template>
            <template #cell(purchaseQuantity)="data">
              <div class="h5">{{data.value}}</div>
            </template>
            <template #cell(cost)="data">
              <div class="h5"><b>${{ data.value | formatCurrency }}</b></div>
            </template>
          </b-table>

          <b-container fluid>
            <b-row>
              <b-col sm="8"></b-col>
              <b-col sm="2" style="text-align: right">
                <h2>Subtotal:</h2>
              </b-col>
              <b-col sm="2" style="text-align: left">
                <h2><b>${{subtotal | formatCurrency }}</b></h2>
              </b-col>
            </b-row>
            <b-row>
              <b-col sm="8"></b-col>
              <b-col sm="4">
                <b-button variant="primary" size="lg">
                  <b-icon icon="credit-card" aria-hidden="true" size="lg"></b-icon>
                  &nbsp&nbspPay now
                </b-button>
              </b-col>
            </b-row>
          </b-container>

        </div>





        <div class="text-center h5" v-show="posError">
          <b style="color: red">{{ posError }}</b>
        </div>

      </div>

      <template #overlay>
        <div class="text-center">
          <b-spinner></b-spinner>
          <p class="h2">Loading...</p>
        </div>
      </template>
    </b-overlay>
  </div>
</template>

<script src="./PointOfSaleScript.js"></script>

<style scoped>
#pos-interface-outer {
  width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
#pos-interface-inner {
  padding: 50px;
}
</style>
