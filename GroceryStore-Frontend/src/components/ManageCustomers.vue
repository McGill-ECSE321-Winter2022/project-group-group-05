<!--Visibility: owner, employee-->
<!--Create/Modify/Delete customer accounts-->
<!--Show list of all customers-->
<template>
  <div v-if="isOwner || isEmployee">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="customers-inner">
        <b-container fluid>
          <b-row>
            <b-col md="auto">
              <StaffDashboard></StaffDashboard>
            </b-col>

            <b-col>
              <h1>Manage Customer</h1>
              <div>
                <b-container fluid>
                  <b-row>
                    <b-col>
                      <b-input
                        type="text"
                        v-model="customerSearchQuery"
                        placeholder="Search Customer by username"
                      >
                      </b-input>
                    </b-col>
                    <b-col md="auto">
                      <b-button
                        variant="primary"
                        v-bind:disabled="isLoading"
                        v-on:click="createCustomerDialog"
                      >
                        Create New Customer
                      </b-button>
                    </b-col>
                  </b-row>
                </b-container>

                <br />

                <b-table
                  id="customers-table"
                  hover
                  fixed
                  :items="filteredCustomerList"
                  :fields="customerListFields"
                  @row-clicked="editCustomerDialog"
                >
                </b-table>
              </div>
            </b-col>
          </b-row>
        </b-container>

        <b-modal id="edit-customer-dialog" title="Edit Customer" hide-footer>
          <b-form @submit="submitEdit">
            <b-form-group
              id="edit-username-group"
              label="Customer username"
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
              label="Customer password"
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
              label="Customer email"
              label-size="lg"
            >
              <b-form-input
                id="edit-email-input"
                v-model="editForm.email"
                required
              >
              </b-form-input>
            </b-form-group>

            <b-form-group
              id="edit-address-group"
              label="Customer address"
              label-size="lg"
            >
              <b-form-input
                id="edit-address-input"
                v-model="editForm.address"
                required
              >
              </b-form-input>
            </b-form-group>

            <b-form-group
              label="Is this address local?"
              v-slot="{ ariaDescribedby }"
            >
              <b-form-radio
                v-model="editForm.local"
                :aria-describedby="ariaDescribedby"
                name="true"
                value="yes"
                >Yes</b-form-radio
              >
              <b-form-radio
                v-model="editForm.local"
                :aria-describedby="ariaDescribedby"
                name="false"
                value="no"
                >No</b-form-radio
              >
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
                v-on:click="deleteCustomer"
              >
                Delete Customer
              </b-button>
            </div>
          </b-form>
        </b-modal>

        <b-modal
          id="create-customer-dialog"
          title="Create New Customer"
          hide-footer
        >
          <b-form @submit="submitCreate">
            <b-form-group
              id="new-username-group"
              label="Customer username"
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
              label="Customer password"
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
              label="Customer email"
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

            <b-form-group
              id="new-address-group"
              label="Customer address"
              label-size="lg"
            >
              <b-form-input
                id="new-address-input"
                v-model="createForm.address"
                placeholder="Enter address"
                required
              >
              </b-form-input>
            </b-form-group>

            <b-form-checkbox v-model="createForm.local"
              >This is a local address</b-form-checkbox
            >

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
  name: "ManageCustomers",
  components: { StaffDashboard },
  data() {
    return {
      LOGIN_STATE,
      isOwner: LOGIN_STATE.state.userType === "Owner",
      isEmployee: LOGIN_STATE.state.userType === "Employee",
      isLoading: false,
      customerList: [],
      customerListFields: [
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
        {
          key: "address",
          sortable: true,
        },
        {
          key: "local",
          sortable: false,
        },
      ],
      customerSearchQuery: "",

      createForm: {
        username: "",
        password: "",
        email: "",
        address: "",
        local: false,
        error: "",
      },

      editForm: {
        username: "",
        password: "",
        email: "",
        address: "",
        local: "",
        error: "",
      },
      customerError: "",
    };
  },
  computed: {
    filteredCustomerList() {
      console.log(this.customerList);
      return this.customerList.filter(customer => {
        return customer["username"]
          .toLowerCase()
          .includes(this.customerSearchQuery.trim().toLowerCase());
      });
    },
  },
  created: async function () {
    this.isLoading = true;
    await AXIOS.get("/customer", {})
      .then(response => {
        this.customerList = response.data;
      })
      .catch(e => {
        let errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.customerError = errorMsg;
      })
      .then(response => {
        this.customerList.forEach(function (customer) {
          if (customer.local) {
            customer.local = "yes";
          } else {
            customer.local = "no";
          }
        });
      })
      .finally(() => {
        this.isLoading = false;
      });
  },
  methods: {
    editCustomerDialog: function (item) {
      this.resetForms();
      this.editForm.username = item["username"];
      this.editForm.password = item["password"];
      this.editForm.email = item["email"];
      this.editForm.address = item["address"];
      this.editForm.local = item["local"];
      this.$bvModal.show("edit-customer-dialog");
    },
    createCustomerDialog: function () {
      this.resetForms();
      this.$bvModal.show("create-customer-dialog");
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
          "/customer/".concat(this.editForm.username),
          {},
          {
            params: {
              password: this.editForm.password,
              email: this.editForm.email,
              address: this.editForm.address,
              isLocal: this.editForm.local,
            },
          }
        )
          .then(() => {
            this.$bvModal.hide("edit-customer-dialog");
            this.resetForms();
            AXIOS.get("/customer", {})
              .then(response => {
                this.customerList = response.data;
              })
              .catch(e => {
                let errorMsg = e.response.data.message;
                console.log(errorMsg);
                this.customerError = errorMsg;
              })
              .then(response => {
                this.customerList.forEach(function (customer) {
                  if (customer.local) {
                    customer.local = "yes";
                  } else {
                    customer.local = "no";
                  }
                });
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

    deleteCustomer: function () {
      this.editForm.error = "";
      this.isLoading = true;
      AXIOS.delete("/customer/".concat(this.editForm.username))
        .then(() => {
          this.$bvModal.hide("edit-customer-dialog");
          this.resetForms();
          AXIOS.get("/customer", {})
            .then(response => {
              this.customerList = response.data;
            })
            .then(response => {
              this.customerList.forEach(function (customer) {
                if (customer.local) {
                  customer.local = "yes";
                } else {
                  customer.local = "no";
                }
              });
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
        console.log(this.createForm.local);
        AXIOS.post(
          "/customer/".concat(this.createForm.username),
          {},
          {
            params: {
              password: this.createForm.password,
              email: this.createForm.email,
              address: this.createForm.address,
              isLocal: this.createForm.local,
            },
          }
        )
          .then(() => {
            this.$bvModal.hide("create-customer-dialog");
            this.resetForms();
            AXIOS.get("/customer", {})
              .then(response => {
                this.customerList = response.data;
              })
              .catch(e => {
                let errorMsg = e.response.data.message;
                console.log(errorMsg);
                this.customerError = errorMsg;
              })
              .then(response => {
                this.customerList.forEach(function (customer) {
                  if (customer.local) {
                    customer.local = "yes";
                  } else {
                    customer.local = "no";
                  }
                });
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
      this.createForm.address = "";
      this.createForm.local = false;
      this.createForm.error = "";
      this.editForm.username = "";
      this.editForm.password = "";
      this.editForm.email = "";
      this.editForm.address = "";
      this.editForm.local = "";
      this.editForm.error = "";
    },
  },
};
</script>

<style scoped></style>
