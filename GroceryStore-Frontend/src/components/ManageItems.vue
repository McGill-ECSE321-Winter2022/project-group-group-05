<!--Visibility: owner (edit), employee (view)-->
<!--List of all items, create/change/delete items-->
<template>
  <b-container fluid>
    <b-row class="vh-100" align-v="stretch">
      <b-col>
        <h1 class="header_style">Manage Inventory</h1>
        <b-alert
          show
          variant="danger"
          v-if="!(isOwnerLoggedIn || isEmployeeLoggedIn)"
          ><h4 class="alert-heading">Error:</h4>
          You must be logged in as an owner or employee to access this page.
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
        <b-container fluid v-if="isOwnerLoggedIn || isEmployeeLoggedIn">
          <b-row
            ><b-col cols="7"
              ><b-button-toolbar
                aria-label="Toolbar with button groups and input groups"
              >
                <b-button-group size="sm" class="mr-1">
                  <b-button
                    class="button_style"
                    variant="outline-primary"
                    v-if="isOwnerLoggedIn"
                    v-b-modal.createItem
                    >Add new Item</b-button
                  >
                  <b-modal
                    id="createItem"
                    ref="modal"
                    title="Add New Item to System"
                    ok-title="Add Item"
                    size="lg"
                    @show="resetItemForm"
                    @hidden="resetItemForm"
                    @ok="handleOk"
                  >
                    <template>
                      <b-container
                        ><b-row
                          ><b-col align-self="center"
                            ><h5>Image Preview</h5>
                            <b-img
                              thumbnail
                              fluid
                              :src="
                                itemImage ? itemImage : '/static/no-image.jpg'
                              "
                            ></b-img></b-col
                          ><b-col
                            ><form ref="form" @submit.stop.prevent="addNewItem">
                              <b-form-group
                                label="Name"
                                label-for="name-input"
                                invalid-feedback="An item name is required"
                                :state="isItemNameValid"
                              >
                                <b-form-input
                                  id="name-input"
                                  v-model="itemName"
                                  :state="isItemNameValid"
                                  required
                                ></b-form-input>
                              </b-form-group>
                              <b-form-group
                                label="Image"
                                label-for="image-input"
                              >
                                <b-form-input
                                  id="image-input"
                                  v-model="itemImage"
                                ></b-form-input>
                              </b-form-group>
                              <b-form-group
                                label="Item Price"
                                label-for="item-price-input"
                                invalid-feedback="Item price must be greater than $ 0.00"
                                :state="isItemPriceValid"
                                ><b-input-group prepend="$"
                                  ><b-form-input
                                    id="item-price-input"
                                    v-model="itemPrice"
                                    :state="isItemPriceValid"
                                    :type="'number'"
                                    step="0.01"
                                    required
                                  ></b-form-input
                                ></b-input-group>
                              </b-form-group>
                              <b-form-group
                                label="Item Inventory"
                                label-for="item-inventory-input"
                                invalid-feedback="Initial item inventory must be greater than 0"
                                :state="isItemInventoryValid"
                                ><b-form-input
                                  id="item-inventory-input"
                                  v-model="itemInventory"
                                  :state="isItemInventoryValid"
                                  :type="'number'"
                                  required
                                ></b-form-input>
                              </b-form-group>
                              <b-form-group label="Online Ordering Options">
                                <b-form-checkbox
                                  v-model="canBeDelivered"
                                  value="true"
                                  unchecked-value="false"
                                >
                                  Item can be delivered
                                </b-form-checkbox>
                                <b-form-checkbox
                                  v-model="canBePickedUp"
                                  value="true"
                                  unchecked-value="false"
                                >
                                  Item can be picked up
                                </b-form-checkbox>
                              </b-form-group>
                            </form></b-col
                          ></b-row
                        ></b-container
                      >
                    </template>
                  </b-modal>
                </b-button-group>
                <b-form-group
                  ><b-form-input
                    v-model="searchQuery"
                    placeholder="Search"
                    class="text-left"
                  ></b-form-input
                ></b-form-group>
              </b-button-toolbar>
              <b-table
                id="item-table"
                :items="items"
                :fields="fields"
                :per-page="perPage"
                :current-page="currentPage"
                striped
                outlined
                v-if="isOwnerLoggedIn || isEmployeeLoggedIn"
                head-variant="light"
                sticky-header="70vh"
                :sort-compare-options="{
                  sensitivity: 'base',
                  ignorePunctuation: true,
                  numeric: true,
                }"
              >
                <template #cell(price)="row">
                  $ {{ row.item.price | formatCurrency }}
                </template>
                <template #cell(edit_item)="row">
                  <b-button size="sm" @click="modify(row.item)" class="mr-2">
                    Edit Item Details
                  </b-button>
                </template>
              </b-table>
              <b-pagination
                v-model="currentPage"
                class="pagination_style"
                :total-rows="items.length"
                :per-page="perPage"
                aria-controls="item-table"
              ></b-pagination>
            </b-col>
            <b-col cols="5" v-if="isOwnerLoggedIn">
              <div class="item_edit_style bg-light">
                <h2>Item Edit Panel</h2>
              </div>
              <b-container class="item_edit_style bg-light"
                ><b-row
                  ><b-col align-self="center"
                    ><h5>Image Preview</h5>
                    <b-img
                      thumbnail
                      fluid
                      style="width: 400px"
                      :src="
                        itemToBeModified.image
                          ? itemToBeModified.image
                          : '/static/no-image.jpg'
                      "
                    ></b-img></b-col
                  ><b-col
                    ><form ref="form">
                      <b-form-group
                        label="Image"
                        label-for="image-modify-input"
                      >
                        <b-form-input
                          id="image-modify-input"
                          v-model="itemToBeModified.image"
                        ></b-form-input>
                      </b-form-group>
                      <b-form-group
                        label="Item Price"
                        label-for="item-price-modify-input"
                        invalid-feedback="Item price must be greater than $ 0.00"
                        :state="isItemModifyPriceValid"
                        ><b-input-group prepend="$"
                          ><b-form-input
                            id="item-price-modify-input"
                            v-model="itemToBeModified.price"
                            :state="isItemModifyPriceValid"
                            :type="'number'"
                            step="0.01"
                            required
                          ></b-form-input
                        ></b-input-group>
                      </b-form-group>
                      <b-form-group
                        label="Item Inventory"
                        label-for="item-inventory-modify-input"
                        invalid-feedback="Item inventory must be greater than 0"
                        :state="isItemModifyInventoryValid"
                        ><b-form-input
                          id="item-inventory-modify-input"
                          v-model="itemToBeModified.inventory"
                          :state="isItemModifyInventoryValid"
                          :type="'number'"
                          required
                        ></b-form-input>
                      </b-form-group>
                      <b-form-group label="Online Ordering Options">
                        <b-form-checkbox
                          v-model="itemToBeModified.canDeliver"
                          value="true"
                          unchecked-value="false"
                        >
                          Item can be delivered
                        </b-form-checkbox>
                        <b-form-checkbox
                          v-model="itemToBeModified.canPickUp"
                          value="true"
                          unchecked-value="false"
                        >
                          Item can be picked up
                        </b-form-checkbox>
                      </b-form-group>
                    </form>
                    <b-button-group style="margin-bottom: 10px">
                      <b-button
                        size="sm"
                        v-b-popover.hover.top="
                          'Warning: This action will permanently remove this item from the system. Any references to this item, including purchase history, will not be saved!'
                        "
                        v-bind:disabled="this.itemToBeModified.length === 0"
                        variant="danger"
                        @click="deleteItem()"
                        >Delete</b-button
                      >
                      <b-button
                        size="sm"
                        v-bind:disabled="this.itemToBeModified.length === 0"
                        variant="outline-dark"
                        @click="discontinue()"
                        >{{
                          itemToBeModified.discontinued
                            ? "Set As Available"
                            : "Set as Discontinued"
                        }}</b-button
                      >
                      <b-button
                        size="sm"
                        v-bind:disabled="this.itemToBeModified.length === 0"
                        variant="outline-primary"
                        @click="saveItemChanges()"
                        >Save</b-button
                      >
                    </b-button-group></b-col
                  ></b-row
                ></b-container
              >
            </b-col>
          </b-row>
        </b-container>
      </b-col>
    </b-row>
  </b-container>
</template>
<script src="./ManageItemsScript.js"></script>
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
.item_edit_style {
  padding-top: 5px;
  border-radius: 3px;

  min-width: 200px;
  outline-style: solid;
  outline-color: #cccccc;
  outline-width: 1px;
}
.button_style {
  margin-bottom: 15px;
}
.pagination_style {
  display: flex;
  justify-content: center;
  padding-top: 10px;
}
</style>
