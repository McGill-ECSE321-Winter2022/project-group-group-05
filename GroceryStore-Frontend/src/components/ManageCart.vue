<!--Visibility: customer-->
<!--View the cart of the customer, set delivery/pick-up only if available-->
<!--See total cost, implement flat shipping fee, pay button-->
<template>
  <b-container fluid>
    <b-row class="vh-100" align-v="stretch">
      <b-col>
        <h1 class="header_style">View Cart</h1>
        <!-- Error Message for Login Check -->
        <b-alert show variant="danger" v-if="!isCustomerLoggedIn"
          ><h4 class="alert-heading">Error:</h4>
          You must be logged in as a customer to access this page.
        </b-alert>
        <b-alert
          :show="dismissCountDown"
          dismissible
          variant="danger"
          @dismissed="dismissCountDown = 0"
          @dismiss-count-down="countDownChanged"
        >
          Error: {{ errorMessage }}
        </b-alert>
        <!-- Item Table -->
        <b-table
          id="item-table"
          class="table_style"
          :items="items"
          :fields="fields"
          striped
          outlined
          v-if="isCustomerLoggedIn"
          head-variant="light"
          sticky-header="75vh"
        >
          <!-- Name Column -->
          <template #cell(name)="row">
            <b-img-lazy
              thumbnail
              style="max-width: 150px"
              :src="
                row.item.item.image
                  ? row.item.item.image
                  : '/static/no-image.jpg'
              "
            ></b-img-lazy>
            <label style="font-size: 20px">{{ row.item.item.name }}</label>
          </template>
          <!-- Quantity Column -->
          <template #cell(quantity)="row">
            <b-form-spinbutton
              id="quantity-spin"
              v-model="row.item.purchaseQuantity"
              wrap
              min="1"
              :max="row.item.item.inventory"
              size="lg"
              @change="updateItemQuantity(row.index)"
            ></b-form-spinbutton>
          </template>
          <!-- Price Column -->
          <template #cell(price)="row">
            <label style="font-size: 28px">
              ${{
                (row.item.purchasePrice * row.item.purchaseQuantity)
                  | formatCurrency
              }}</label
            >
          </template>
          <!-- Availability Column -->
          <template #cell(availability)="row">
            <label style="font-size: 18px">
              Delivery:
              {{
                row.item.item.canDeliver ? "Available" : "Not Available"
              }}</label
            >
            <label style="font-size: 18px">
              Pick Up:
              {{
                row.item.item.canPickUp ? "Available" : "Not Available"
              }}</label
            >
          </template>
          <!-- Remove Button -->
          <template #cell(remove)="row">
            <b-button
              size="md"
              variant="outline-primary"
              @click="removeItem(row.index)"
              class="mr-2"
            >
              Remove
            </b-button>
          </template>
        </b-table>
        <!-- Options Bar -->
        <div v-if="isCustomerLoggedIn" class="table_style">
          <b-button-toolbar justify>
            <router-link to="/">
              <b-button size="lg" variant="outline-primary"
                >Back to Store</b-button
              >
            </router-link>
            <b-input-group prepend="Total">
              <b-form-input
                :value="'$' + totalCost"
                size="lg"
                readonly
                class="text-right"
              ></b-form-input>
              <div class="fee_label_style"><i>{{ !isLocal && isDelivery ? "+$10.00 Delivery Fee" : "" }}</i></div>
            </b-input-group>
            <b-input-group prepend="Order Type: "
              ><b-form
                ><b-form-select v-model="isDelivery" size="lg">
                  <b-form-select-option :value="true"
                    >Delivery</b-form-select-option
                  >
                  <b-form-select-option :value="false"
                    >Pick Up</b-form-select-option
                  >
                </b-form-select></b-form
              ></b-input-group
            >
            <b-button
              size="lg"
              variant="outline-success"
              @click="orderPurchase()"
              >Order</b-button
            >
          </b-button-toolbar>
        </div>
        <!-- Order Completed Notification -->
        <b-modal
          id="order-complete"
          hide-header-close
          hide-footer
          no-close-on-backdrop
        >
          <h3 class="text-success">Success! Your order has been received!</h3>
          <hr />
          <b-button variant="outline-primary" @click="$router.push('/')"
            >Return to Store</b-button
          >
        </b-modal>
      </b-col>
    </b-row>
  </b-container>
</template>
<script src="./ManageCartScript.js"></script>
<style scoped>
/* Styling for Page Title */
.header_style {
  padding-top: 15px;
  padding-left: 100px;
  border-color: #0d6efd;
  border-style: solid;
  border-width: 0px 0px 6px 0px;
  text-align: left;
  margin-bottom: 30px;
}
.table_style {
  min-width: 600px;
  width: 60%;
  margin: auto;
  margin-bottom: 20px;
}
.fee_label_style {
  margin-left: 5px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
}
</style>
