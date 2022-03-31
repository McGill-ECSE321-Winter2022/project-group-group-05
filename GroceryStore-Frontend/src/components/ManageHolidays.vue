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
                <b-table
                  id="holidays-table"
                  borderless
                  hover
                  :items="filteredHolidayList"
                  @row-clicked="editHolidayDialog"
                >
                </b-table>
              </div>
            </b-col>
          </b-row>
        </b-container>
      </div>
    </b-overlay>
  </div>
</template>

<script src="./ManageHolidaysScript.js"></script>

<style scoped></style>
