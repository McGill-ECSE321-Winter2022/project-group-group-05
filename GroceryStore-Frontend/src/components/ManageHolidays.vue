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
              <h1>Manage Holidays</h1>
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
                  :items="filteredHolidayList"
                  :fields="holidayListFields"
                  @row-clicked="editHolidayDialog"
                >
                </b-table>
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

<script src="./ManageHolidaysScript.js"></script>

<style scoped></style>
