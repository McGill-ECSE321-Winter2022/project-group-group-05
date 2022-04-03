<!--Visibility: owner-->
<!--Create/Modify/Delete holidays-->
<!--Show list of all holidays-->
<template>
  <div id="holidays-outer" v-if="isOwner">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="holidays-inner">
        <b-container fluid>
          <b-row>
            <b-col md="auto">
              <StaffDashboard></StaffDashboard>
            </b-col>

            <b-col>
              <h1 class="header_style">Manage Holidays</h1>
              <div id="holidays-col">
                <b-container fluid>
                  <b-row>
                    <b-col>
                      <b-input
                        type="text"
                        v-model="holidaySearchQuery"
                        placeholder="Search holiday by name"
                      >
                      </b-input>
                    </b-col>
                    <b-col md="auto">
                      <b-button
                        variant="primary"
                        v-bind:disabled="isLoading"
                        v-on:click="createHolidayDialog"
                      >
                        Create New Holiday
                      </b-button>
                    </b-col>
                  </b-row>
                </b-container>

                <br />

                <b-table
                  id="holidays-table"
                  hover
                  fixed
                  :items="filteredHolidayList"
                  :fields="holidayListFields"
                  @row-clicked="editHolidayDialog"
                >
                </b-table>
              </div>
            </b-col>
          </b-row>
        </b-container>

        <b-modal id="edit-holiday-dialog" title="Edit Holiday" hide-footer>
          <b-form @submit="submitEdit">
            <b-form-group
              id="edit-name-group"
              label="Holiday name"
              label-size="lg"
            >
              <b-form-input
                id="edit-name-input"
                v-model="editForm.name"
                required
                readonly
              >
              </b-form-input>
            </b-form-group>
            <b-form-group
              id="edit-date-group"
              label="Holiday date"
              label-size="lg"
            >
              <b-form-datepicker
                id="edit-date-input"
                v-model="editForm.date"
                required
              >
              </b-form-datepicker>
            </b-form-group>
            <div class="text-center" v-show="editForm.error">
              <p style="color: red">{{ editForm.error }}</p>
            </div>
            <div class="text-center mt-5">
              <b-button
                type="submit"
                variant="primary"
                v-bind:disabled="isLoading"
                class="mr-2"
                >Save changes</b-button
              >
              <b-button
                variant="danger"
                v-bind:disabled="isLoading"
                class="ml-2"
                v-on:click="deleteHoliday"
              >
                Delete holiday
              </b-button>
            </div>
          </b-form>
        </b-modal>

        <b-modal
          id="create-holiday-dialog"
          title="Create New Holiday"
          hide-footer
        >
          <b-form @submit="submitCreate">
            <b-form-group
              id="new-name-group"
              label="Holiday name"
              label-size="lg"
            >
              <b-form-input
                id="new-name-input"
                v-model="createForm.name"
                placeholder="Enter name"
                required
              >
              </b-form-input>
            </b-form-group>
            <b-form-group
              id="new-date-group"
              label="Holiday date"
              label-size="lg"
            >
              <b-form-datepicker
                id="new-date-input"
                v-model="createForm.date"
                :min="minDate"
                required
              >
              </b-form-datepicker>
            </b-form-group>
            <div class="text-center" v-show="createForm.error">
              <p style="color: red">{{ createForm.error }}</p>
            </div>
            <div class="text-center mt-5">
              <b-button
                type="submit"
                variant="primary"
                v-bind:disabled="isLoading"
                >Create</b-button
              >
            </div>
          </b-form>
        </b-modal>
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

<script src="./ManageHolidaysScript.js"></script>

<style scoped>
/* Styling for Page Title */
.header_style {
  padding-top: 15px;
  border-color: #91c788;
  border-style: solid;
  border-width: 0px 0px 6px 0px;
  text-align: center;
  margin-bottom: 30px;
}
</style>
