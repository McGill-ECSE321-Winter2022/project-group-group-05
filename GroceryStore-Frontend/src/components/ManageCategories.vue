<!--Visibility: owner-->
<!--Create/Delete categories, add/remove items to categories-->
<!--View all items in a given category-->
<template>
  <div id="categories-outer" v-if="isOwner">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="categories-inner">
        <b-container fluid>
          <b-row>
            <b-col md="auto">
              <StaffDashboard></StaffDashboard>
            </b-col>

            <b-col>
              <h1>Manage Item Categories</h1>
              <div id="categories-control" class="mb-3">
                <b-container fluid>
                  <b-row>
                    <b-col sm="2">
                      <b-button block v-on:click="addDialog">Add Item</b-button>
                    </b-col>
                    <b-col sm="4">
                      <b-form-select v-model="selectedCategory"
                                :options="categoriesOptions"
                                v-bind:disabled="isLoading"
                                @input="atSelction">
                        <template #first>
                          <b-form-select-option :value="null" disabled>
                            --- please select a category ---
                          </b-form-select-option>
                        </template>
                      </b-form-select>
                    </b-col>
                    <b-col sm="3">
                      <b-button block v-on:click="createCategoryDialog">Create New</b-button>
                    </b-col>
                    <b-col sm="3">
                      <b-button block>Delete Category</b-button>
                    </b-col>
                  </b-row>
                </b-container>
              </div>

              <div class="text-center h5" v-show="categoriesError">
                <b style="color: red">{{ categoriesError }}</b>
              </div>

              <h3 class="mt-3">List of Items in Selected Category</h3>
              <div id="selected-category-info">
                <b-table
                  id="category-item-table"
                  hover
                  fixed
                  :items="itemsInSelectedCategory"
                  :fields="itemFields"
                  @row-clicked="removeDialog"
                >
                  <template #cell(name)="data">
                      <b>{{ data.value }}</b>
                  </template>
                  <template #cell(price)="data">
                    ${{ data.value | formatCurrency }}
                  </template>
                </b-table>

              </div>

            </b-col>
          </b-row>
        </b-container>

        <b-modal
          id="create-category-dialog"
          title="Create new category"
          hide-footer>
          <b-form @submit="submitNewCategory">
            <b-form-group
              label="Category name"
              label-size="lg">
              <b-form-input
                v-model="newCategoryName"
                placeholder="Enter name"
                required>
              </b-form-input>
            </b-form-group>
            <div class="text-center mt-5">
              <b-button
                type="submit"
                variant="primary"
                v-bind:disabled="isLoading"
              >Create Category</b-button
              >
            </div>
          </b-form>
        </b-modal>

        <b-modal
          id="item-search"
          title="Click to add item to this category"
          hide-footer
          scrollable
        >
          <b-input
            v-model="itemSearchQuery"
            placeholder="Search item name"
            class="mb-2"
          ></b-input>
          <div class="text-center">
            <b-table
              hover
              fixed
              :items="filteredItemList"
              :fields="itemFields"
              @row-clicked="addConfirm"
            >
              <template #cell(price)="data">
                ${{ data.value | formatCurrency }}
              </template>
            </b-table>
          </div>
        </b-modal>

        <b-modal
          id="remove-item-dialog"
          title="Remove item"
          hide-footer
          centered>
          <p class="mt-2 mb-2">
            Confirm remove <b>{{ removeItem["name"]}}</b> from this category?
          </p>
          <div class="text-center">
            <b-button variant="danger" v-on:click="removeConfirm" v-bind:disabled="isLoading">Confirm Remove</b-button>
          </div>
        </b-modal>

      </div>
    </b-overlay>
  </div>
</template>

<script src="./ManageCategoriesScript.js"></script>

<style scoped></style>
