<!--Visibility: owner, employee-->
<!--Modify the staff's own account infos-->
<!--Landing page of logging in as a staff-->
<template>
  <div id="manage-outer">
    <b-overlay :show="isLoading" rounded="sm">
      <div :aria-hidden="isLoading ? 'true' : null" id="manage-inner">
        <h1>Manage Your Profile</h1>
        <br>

        <div v-if="userType === 'Employee'">
          <table>
            <tr>
              <td>Username:</td>
              <td>{{ username }}</td>
            </tr>
            <br />
            <tr>
              <td>Email:</td>
              <td>
                <input type="text" v-model="employee.email" placeholder="Email" />
              </td>
              <td>
                <b-button
                  variant="outline-primary"
                  v-bind:disabled="!employee.email"
                  @click="updateEmployee(employee.email, null)"
                >
                  Save
                </b-button>
              </td>
            </tr>
            <br />
            <tr>
              <td>Password:</td>
              <td>
                <input
                  type="text"
                  v-model="employee.password"
                  placeholder="Password"
                />
              </td>
              <td>
                <b-button
                  variant="outline-primary"
                  v-bind:disabled="!employee.password"
                  @click="updateEmployee(null, employee.password)"
                >
                  Save
                </b-button>
              </td>
            </tr>
          </table>
          <div class="text-center" v-show="errorEmployee">
            <p style="color: red">{{ errorEmployee }}</p>
          </div>
        </div>

        <div v-if="userType === 'Owner'">
          <table>
            <tr>
              <td>Username:</td>
              <td>{{ username }}</td>
            </tr>
            <br />
            <tr>
              <td>Email:</td>
              <td>
                <input type="text" v-model="owner.email" placeholder="Email" />
              </td>
              <td>
                <b-button
                  variant="outline-primary"
                  v-bind:disabled="!owner.email"
                  @click="updateOwner(owner.email, null)"
                >
                  Save
                </b-button>
              </td>
            </tr>
            <br />
            <tr>
              <td>Password:</td>
              <td>
                <input
                  type="text"
                  v-model="owner.password"
                  placeholder="Password"
                />
              </td>
              <td>
                <b-button
                  variant="outline-primary"
                  v-bind:disabled="!owner.password"
                  @click="updateOwner(null, owner.password)"
                >
                  Save
                </b-button>
              </td>
            </tr>
          </table>
          <div class="text-center" v-show="errorOwner">
            <p style="color: red">{{ errorOwner }}</p>
          </div>
        </div>

      </div>
    </b-overlay>
  </div>
</template>

<script>
import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";

export default {
  name: "ManageStaffProfile",
  data() {
    return {
      employee: "",
      owner: "",
      username: LOGIN_STATE.state.username,
      userType: LOGIN_STATE.state.userType,
      isLoading: false,
      errorEmployee: "",
      errorOwner: "",
      variant: "light",
      response: [],
    };
  },

  created: function() {
    this.isLoading = true;
    AXIOS.get("/employee/".concat(LOGIN_STATE.state.username))
      .then(response => {
        this.employee = response.data;
        this.isLoading = false;
      })

      .catch(e => {
        AXIOS.get("/owner/".concat(LOGIN_STATE.state.username)).then(
          response => {
            this.owner = response.data;
            this.isLoading = false;
          },
        );
      })

      .catch(e => {
        var errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.errorEmployee = errorMsg;
      })

      .catch(e => {
        var errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.errorOwner = errorMsg;
      });
  },
  methods: {
    updateEmployee: function(email, password) {
      this.isLoading = true;
      AXIOS.patch(
        "/employee/".concat(LOGIN_STATE.state.username),
        {},
        {
          params: {
            email: email,
            password: password,
          },
        },
      )
        .then(response => {
          this.errorEmployee = "";
        })
        .catch(e => {
          var errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.errorEmployee = errorMsg;
        })
        .finally(() => {
          if (this.errorEmployee === "") {
            window.location.reload();
          }
          this.isLoading = false;
        });
    },
    updateOwner: function(email, password) {
      this.isLoading = true;
      AXIOS.patch(
        "/owner/".concat(LOGIN_STATE.state.username),
        {},
        {
          params: {
            email: email,
            password: password,
          },
        },
      )
        .then(response => {
          this.errorOwner = "";
        })
        .catch(e => {
          var errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.errorOwner = errorMsg;
        })
        .finally(() => {
          if (this.errorOwner === "") {
            window.location.reload();
          }
          this.isLoading = false;
        });
    },
  },
};
</script>

<style scoped>
input[type="text"] {
  background: transparent;
  border: none;
  border-bottom: 1px solid #727272;
}

#manage-outer {
  width: 600px;
  margin-left: auto;
  margin-right: auto;
  text-align: left;
}

#manage-inner {
  padding: 100px;
}
</style>
