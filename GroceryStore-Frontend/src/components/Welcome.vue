<!--Landing page of the application-->
<template>
  <div id="welcome-outer">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="welcome-inner">
        <!--Temporary skeleton to provide functionalities but not looks-->
        <h1>{{ msg }}</h1>

        <div id="holiday-marquee" v-if="nextHolidayDate" class="mb-4">
          <marquee-text :duration="10" :repeat="10" :paused="marqueePause">
            <div
              @mouseenter="marqueePause = !marqueePause"
              @mouseleave="marqueePause = false"
            >
              The store will be <b>closed</b> on
              <b>{{ nextHolidayDate | formatDate }}</b> for
              <b>{{ nextHolidayName }}</b
              >.
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </div>
          </marquee-text>
        </div>

        <hr />
        <b-button
          id="populate-database-button"
          variant="warning"
          class="mb-4"
          v-on:click="genData()"
          >Populate database</b-button
        >
        <b-tooltip target="populate-database-button" triggers="hover">
          <b>Development option:</b> populate database with some data
        </b-tooltip>

        <b-button
          id="show-instructions-button"
          variant="info"
          class="mb-4"
          v-b-modal.instructions-modal
          >Read Me!</b-button
        >
        <b-tooltip target="show-instructions-button" triggers="hover">
          <b>Development option:</b> view instructions for testing the frontend
        </b-tooltip>
        <b-modal
          id="instructions-modal"
          title="Frontend Instructions"
          ok-only
          scrollable
        >
          <p class="mb-3">
            The database is wiped each time the project is built. Clicking on
            <b>Populate database</b> will fill it with necessary data to test
            the frontend. To see in detail what's being created, see function
            <code>genData()</code> in <code>WelcomeScript.js</code>.
          </p>
          <p class="mb-3">
            Clicking on the logo at top left corner will always redirect back to
            this Welcome page.
          </p>
          <h5><b>Owner Account Credentials</b></h5>
          <ul>
            <li>username: admin</li>
            <li>password: admin</li>
          </ul>
          <p class="mb-3">
            After signing in as owner (top right corner), go to My Profile to
            manage the store.
          </p>
          <h5><b>Employee Account Credentials</b></h5>
          <ul>
            <li>username: worker1</li>
            <li>password: worker1</li>
          </ul>
          <h5><b>Customer Account Credentials</b></h5>
          <ul>
            <li>username: loyalcustomer</li>
            <li>password: 123456</li>
          </ul>
          <p class="mb-3">
            Customers can be created via Create Account on the navbar or in
            Manage Customers after signing in as owner or employee.
          </p>
          <h5><b>In-Store Purchases</b></h5>
          <p>
            To execute in-store purchases, first sign in with this special
            customer account:
          </p>
          <ul>
            <li>username: kiosk</li>
            <li>password: kiosk</li>
          </ul>
          <p class="mb-3">
            The page to execute in-store orders is located at
            <code>/PointOfSale</code>
          </p>
          <h5><b>Adding Items to Cart</b></h5>
          <p class="mb-3">
            Once signed in as a customer (except 'kiosk'), simply click the
            items displayed on this Welcome page.
          </p>
          <h5><b>About the Marquee</b></h5>
          <p class="mb-3">
            The marquee near the top of this Welcome page will display the
            earliest Holiday (by date) in the system.
          </p>
        </b-modal>
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
        <h2>Browse items</h2>

        <b-container fluid>
          <b-row>
            <b-col md="auto" style="text-align: left">
              <h5>Filters</h5>
              <b-checkbox v-model="mustCanDeliver"
                >available for delivery</b-checkbox
              >
              <b-checkbox v-model="mustCanPickUp"
                >available for pick-up</b-checkbox
              >
              <b-checkbox v-model="mustAvailableOnline"
                >available online</b-checkbox
              >
              <b-checkbox v-model="showOutOfStock"
                >show out-of-stock items</b-checkbox
              >

              <div class="mt-4">
                <b-button block variant="danger" v-on:click="atClearFilters"
                  >Clear filters</b-button
                >
              </div>

              <h5 class="mt-4">Select a category</h5>
              <b-form-select
                v-model="selectedCategory"
                :options="categoriesOptions"
                @input="atCategorySelection"
              >
                <template #first>
                  <b-form-select-option :value="null">
                    ---
                  </b-form-select-option>
                </template>
              </b-form-select>
            </b-col>

            <b-col>
              <div id="in-stock-items-outer">
                <b-input
                  type="text"
                  v-model="itemSearchQuery"
                  placeholder="Search item name"
                ></b-input>
                <br />

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
                  :busy="isLoading"
                  :per-page="perPage"
                  :current-page="currentPage"
                  :items="filteredItemList"
                  :fields="itemListFields"
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
                  <template #cell(canDeliver)="data">
                    <div v-if="data.value" v-b-tooltip title="Can be delivered">
                      <b-icon
                        icon="truck"
                        font-scale="1.5"
                        variant="success"
                      ></b-icon>
                    </div>
                  </template>
                  <template #cell(canPickUp)="data">
                    <div
                      v-if="data.value"
                      v-b-tooltip
                      title="Available for pick-up"
                    >
                      <b-icon
                        icon="bag-check"
                        font-scale="1.5"
                        variant="success"
                      ></b-icon>
                    </div>
                    <div v-else v-b-tooltip title="Not available for pick-up">
                      <b-icon
                        icon="bag-x"
                        font-scale="1.5"
                        variant="danger"
                      ></b-icon>
                    </div>
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
                      Select quantity of <b>{{ clickedItem["name"] }}</b> to add
                      to cart:
                    </p>
                    <div
                      style="width: 200px; margin: auto; padding-bottom: 10px"
                    >
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
                      Sorry, but you cannot add items to your cart because
                      you're not logged in as a customer.
                    </p>
                  </div>
                </b-modal>

                <b-modal id="add-item-success" title="Success" centered ok-only>
                  <p class="mt-2 mb-2" style="color: green">
                    {{ addItemSuccess }}
                  </p>
                </b-modal>

                <b-modal id="add-item-error" title="Error" centered ok-only>
                  <p class="mt-2 mb-2" style="color: red">{{ addItemError }}</p>
                </b-modal>
              </div>
            </b-col>
          </b-row>
        </b-container>
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
