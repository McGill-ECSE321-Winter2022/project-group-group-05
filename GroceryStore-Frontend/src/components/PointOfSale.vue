<!--Visibility: kiosk-->
<!--A in-store POS check out interface-->
<template>
  <div id="pos-interface-outer" v-if="isKiosk">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="pos-interface-inner">
        <p class="h2 text-center mb-5">In-Store Checkout System</p>

        <div class="text-center" v-show="posError">
          <p style="color: red">{{ posError }}</p>
        </div>

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
  width: 900px;
  margin-left: auto;
  margin-right: auto;
}
#pos-interface-inner {
  padding: 50px;
}
</style>
