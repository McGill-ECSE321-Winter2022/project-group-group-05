<!--Landing page of the application-->
<template>
  <div id="welcome-outer">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="welcome-inner">
        <!--Temporary skeleton to provide functionalities but not looks-->
        <h1>{{ msg }}</h1>

        <div id="holiday-marquee" v-if="nextHolidayDate">
          <marquee-text :duration="10" :repeat="10" :paused="marqueePause">
            <div
              @mouseenter="marqueePause = !marqueePause"
              @mouseleave="marqueePause = false"
            >
              The store will be <b>closed</b> on
              <b>{{ nextHolidayDate | formatDate }}</b> for
              <b>{{ nextHolidayName }}</b
              >.
              &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
              &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
              &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
            </div>
          </marquee-text>
        </div>
        <br />

        <router-link to="/LoginForm">
          <b-button variant="success" v-if="!LOGIN_STATE.state.isLoggedIn"
            >Sign in</b-button
          >
        </router-link>
        <b-button
          variant="outline-info"
          v-if="LOGIN_STATE.state.isLoggedIn"
          @click="logout()"
        >
          <b-icon icon="power" aria-hidden="true"></b-icon> Sign out</b-button
        >
        <router-link to="/CreateCustomerForm">
          <b-button variant="primary" v-if="!LOGIN_STATE.state.isLoggedIn"
            >Create account</b-button
          >
        </router-link>

        <hr />
        <router-link to="/ManageProfile">
          <b-button pill variant="info" v-if="isCustomer">My profile</b-button>
        </router-link>
        <router-link to="/ManageStaffProfile">
          <b-button pill variant="info" v-if="isStaff"
            >My staff profile</b-button
          >
        </router-link>
        <router-link to="/ViewHistory">
          <b-button pill variant="info" v-if="isCustomer"
            >My purchase history</b-button
          >
        </router-link>
        <router-link to="/ManageCart">
          <b-button pill variant="info" v-if="isCustomer">My cart</b-button>
        </router-link>

        <hr />
        <b-button
          id="populate-database-button"
          variant="warning"
          v-on:click="genData()"
          >Populate database</b-button
        >
        <b-tooltip target="populate-database-button" triggers="hover">
          <b>Development option:</b> populate database with some data
        </b-tooltip>

        <b-button v-b-modal.opening-hours>See our opening hours</b-button>
        <b-modal id="opening-hours" title="Opening Hours" ok-only scrollable>
          <div id="op-hours-outer">
            <b-table borderless hover :items="openingHours">
              <template #cell(startTime)="data">
                {{ data.value | formatTime }}
              </template>
              <template #cell(endTime)="data">
                {{ data.value | formatTime }}
              </template>
            </b-table>
          </div>
        </b-modal>

        <b-button v-b-modal.list-holidays>See all holidays</b-button>
        <b-modal id="list-holidays" title="Holidays" ok-only scrollable>
          <div id="list-holidays-outer">
            <b-table striped borderless :items="holidays">
              <template #cell(date)="data">
                {{ data.value | formatDate }}
              </template>
            </b-table>
          </div>
        </b-modal>

        <hr />
      </div>
      <template #overlay>
        <div class="text-center">
          <b-spinner></b-spinner>
          <p class="h2">{{ loadingMsg }}</p>
        </div>
      </template>
    </b-overlay>

    <b-overlay :show="isItemLoading" rounded="sm">
      <div :aria-hidden="isItemLoading ? 'true' : null" id="items-inner">
        <h2>Browse our in stock items</h2>
        <div id="in-stock-items-outer">
          <b-input
            type="text"
            v-model="itemSearchQuery"
            placeholder="Search item"
          ></b-input>
          <b-pagination
            v-model="currentPage"
            :total-rows="numRows"
            :per-page="perPage"
            aria-controls="in-stock-items-table"
          ></b-pagination>
          <b-table
            id="in-stock-items-table"
            borderless
            hover
            :per-page="perPage"
            :current-page="currentPage"
            :items="filteredItemList"
            @row-clicked="addItemDialog"
          >
            <template #cell(name)="data">
              <b>{{ data.value }}</b>
            </template>
            <template #cell(image)="data">
              <b-img-lazy
                thumbnail
                style="max-width: 150px"
                :src="data.value ? data.value : '/static/no-image.jpg'"
              ></b-img-lazy>
            </template>
            <template #cell(price)="data">
              <h5>${{ data.value | formatCurrency }}</h5>
            </template>
            <template #cell(inventory)="data">
              {{ data.value }} in stock
            </template>
          </b-table>

          <b-modal
            id="add-item-dialog"
            title="Add Item to Cart"
            centered
            @ok="addItemToCart"
          >
            <div id="add-item-dialog-outer">
              <p class="mb-3">
                Select quantity of <b>{{ clickedItem["name"] }}</b> to add to
                cart:
              </p>
              <div style="width: 200px; margin: auto; padding-bottom: 10px">
                <b-form-spinbutton
                  id="quantity-spin"
                  v-model="addQuantity"
                  wrap
                  min="1"
                  :max="clickedItem['inventory']"
                  size="lg"
                ></b-form-spinbutton>
              </div>
            </div>
          </b-modal>

          <b-modal
            id="add-item-denied"
            title="Add Item to Cart"
            centered
            ok-only
          >
            <div id="add-item-denied-outer">
              <p class="mt-2 mb-2">
                Sorry, but you cannot add items to your cart because you're not
                logged in as a customer.
              </p>
            </div>
          </b-modal>

          <b-modal id="add-item-success" title="Success" centered ok-only>
            <p class="mt-2 mb-2" style="color: green">{{ addItemSuccess }}</p>
          </b-modal>

          <b-modal id="add-item-error" title="Error" centered ok-only>
            <p class="mt-2 mb-2" style="color: red">{{ addItemError }}</p>
          </b-modal>
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

<script src="./WelcomeScript.js"></script>

<style scoped>
#welcome-inner {
  padding-top: 50px;
  padding-left: 50px;
  padding-right: 50px;
}
#items-inner {
  padding-left: 50px;
  padding-right: 50px;
  padding-bottom: 50px;
}
#holiday-marquee {
  background: lightblue;
}
</style>
