<!--Visibility: owner-->
<!--Create/Modify/Delete employee accounts-->
<!--Show list of all employees-->
<template>
  <div id="employees-outer" v-if="isOwner">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="employees-inner">
        <b-container fluid>
          <b-row>
            <b-col md="auto">
              <StaffDashboard></StaffDashboard>
            </b-col>

            <b-col>
              <h1 class="header_style">Manage Employee</h1>
              <div id="employees-col">
                <b-container fluid>
                  <b-row>
                    <b-col>
                      <b-input
                        type="text"
                        v-model="employeeSearchQuery"
                        placeholder="Search Employee by username"
                      >
                      </b-input>
                    </b-col>
                    <b-col md="auto">
                      <b-button
                        variant="primary"
                        v-bind:disabled="isLoading"
                        v-on:click="createEmployeeDialog"
                      >
                        Create New Employee
                      </b-button>
                    </b-col>
                  </b-row>
                </b-container>

                <br />

                <b-table
                  id="employees-table"
                  hover
                  fixed
                  :items="filteredEmployeeList"
                  :fields="employeeListFields"
                  @row-clicked="editEmployeeDialog"
                >
                </b-table>
              </div>
            </b-col>
          </b-row>
        </b-container>

        <b-modal id="edit-employee-dialog" title="Edit Employee" hide-footer>
          <b-form @submit="submitEdit">
            <b-form-group
              id="edit-username-group"
              label="Employee username"
              label-size="lg"
            >
              <b-form-input
                id="edit-username-input"
                v-model="editForm.username"
                required
                readonly
              >
              </b-form-input>
            </b-form-group>

            <b-form-group
              id="edit-password-group"
              label="Employee password"
              label-size="lg"
            >
              <b-form-input
                id="edit-password-input"
                v-model="editForm.password"
                required
              >
              </b-form-input>
            </b-form-group>

            <b-form-group
              id="edit-email-group"
              label="Employee email"
              label-size="lg"
            >
              <b-form-input
                id="edit-email-input"
                v-model="editForm.email"
                required
              >
              </b-form-input>
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
                class="m1-2"
                v-on:click="deleteEmployee"
              >
                Delete Employee
              </b-button>
            </div>
          </b-form>
        </b-modal>

        <b-modal
          id="create-employee-dialog"
          title="Create New Employee"
          hide-footer
        >
          <b-form @submit="submitCreate">
            <b-form-group
              id="new-username-group"
              label="Employee username"
              label-size="lg"
            >
              <b-form-input
                id="new-userame-input"
                v-model="createForm.username"
                placeholder="Enter username"
                required
              >
              </b-form-input>
            </b-form-group>

            <b-form-group
              id="new-password-group"
              label="Employee password"
              label-size="lg"
            >
              <b-form-input
                id="new-password-input"
                v-model="createForm.password"
                placeholder="Enter password"
                required
              >
              </b-form-input>
            </b-form-group>

            <b-form-group
              id="new-email-group"
              label="Employee email"
              label-size="lg"
            >
              <b-form-input
                id="new-email-input"
                v-model="createForm.email"
                placeholder="Enter email"
                required
              >
              </b-form-input>
            </b-form-group>

            <div class="text-center" v-show="createForm.error">
              <p style="color: red">{{ createForm.error }}</p>
            </div>
            <div class="test-center mt-5">
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

<script>
import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";
import StaffDashboard from "./StaffDashboard";

export default {
  name: "ManageEmployee",
  components: { StaffDashboard },
  data() {
    return {
      LOGIN_STATE,
      isOwner: LOGIN_STATE.state.userType === "Owner",
      isLoading: false,
      employeeList: [],
      employeeListFields: [
        {
          key: "username",
          sortable: true,
        },
        {
          key: "password",
          sortable: true,
        },
        {
          key: "email",
          sortable: true,
        },
      ],
      employeeSearchQuery: "",

      createForm: {
        username: "",
        password: "",
        email: "",
        error: "",
      },

      editForm: {
        username: "",
        password: "",
        email: "",
        error: "",
      },
      employeeError: "",
    };
  },
  computed: {
    filteredEmployeeList() {
      return this.employeeList.filter(employee => {
        return employee["username"]
          .toLowerCase()
          .includes(this.employeeSearchQuery.trim().toLowerCase());
      });
    },
  },
  created: async function () {
    this.isLoading = true;
    await AXIOS.get("/employee/getAll", {})
      .then(response => {
        this.employeeList = response.data;
      })
      .catch(e => {
        let errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.employeeError = errorMsg;
      });
    this.isLoading = false;
  },
  methods: {
    editEmployeeDialog: function (item) {
      this.resetForms();
      this.editForm.username = item["username"];
      this.editForm.password = item["password"];
      this.editForm.email = item["email"];
      this.$bvModal.show("edit-employee-dialog");
    },
    createEmployeeDialog: function () {
      this.resetForms();
      this.$bvModal.show("create-employee-dialog");
    },
    submitEdit: function (event) {
      event.preventDefault();
      this.editForm.error = "";
      if (this.editForm.password === "") {
        this.editForm.error = "Please enter password";
      } else if (this.editForm.email === "") {
        this.editForm.error = "Please enter email";
      } else {
        this.isLoading = true;
        AXIOS.patch(
          "/employee/".concat(this.editForm.username),
          {},
          {
            params: {
              password: this.editForm.password,
              email: this.editForm.email,
            },
          }
        )
          .then(() => {
            this.$bvModal.hide("edit-employee-dialog");
            this.resetForms();
            AXIOS.get("/employee/getAll", {})
              .then(response => {
                this.employeeList = response.data;
              })
              .catch(e => {
                let errorMsg = e.response.data.message;
                console.log(errorMsg);
                this.employeeError = errorMsg;
              });
          })
          .catch(e => {
            let errorMsg = e.response.data.message;
            console.log(errorMsg);
            this.editForm.error = errorMsg;
          })
          .finally(() => {
            this.isLoading = false;
          });
      }
    },

    deleteEmployee: function () {
      this.editForm.error = "";
      this.isLoading = true;
      AXIOS.delete("/employee/".concat(this.editForm.username))
        .then(() => {
          this.$bvModal.hide("edit-employee-dialog");
          this.resetForms();
          AXIOS.get("/employee/getAll", {}).then(response => {
            this.employeeList = response.data;
          });
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.editForm.error = errorMsg;
        })
        .finally(() => {
          this.isLoading = false;
        });
    },
    submitCreate: function (event) {
      event.preventDefault();
      this.createForm.error = "";
      if (this.createForm.password === "") {
        this.createForm.error = "Please enter password";
      } else if (this.createForm.email === "") {
        this.createForm.error = "Please enter email";
      } else {
        this.isLoading = true;
        AXIOS.post(
          "/employee/".concat(this.createForm.username),
          {},
          {
            params: {
              password: this.createForm.password,
              email: this.createForm.email,
            },
          }
        )
          .then(() => {
            this.$bvModal.hide("create-employee-dialog");
            this.resetForms();
            AXIOS.get("/employee/getAll", {})
              .then(response => {
                this.employeeList = response.data;
              })
              .catch(e => {
                let errorMsg = e.response.data.message;
                console.log(errorMsg);
                this.employeeError = errorMsg;
              });
          })
          .catch(e => {
            let errorMsg = e.response.data.message;
            console.log(errorMsg);
            this.createForm.error = errorMsg;
          })
          .finally(() => {
            this.isLoading = false;
          });
      }
    },
    resetForms: function () {
      this.createForm.username = "";
      this.createForm.password = "";
      this.createForm.email = "";
      this.createForm.error = "";
      this.editForm.username = "";
      this.editForm.password = "";
      this.editForm.email = "";
      this.editForm.error = "";
    },
  },
};
</script>

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
