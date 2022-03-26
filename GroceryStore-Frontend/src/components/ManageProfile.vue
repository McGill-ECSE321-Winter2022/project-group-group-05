<!--Visibility: customer-->
<!--Modify customer's own account infos-->
<template>
  <div>
    <div id="manageprofile">
      <h1>Manage Your Profile</h1>
      <br />
      <table>
        <tr>
          <td>Username:</td>
          <td>{{ username }}</td>
        </tr>
        <br />
        <tr>
          <td>Email:</td>
          <td>
            <input type="text" v-model="customer.email" placeholder="Email" />
          </td>
          <td>
            <b-button
              variant="outline-primary"
              v-bind:disabled="!customer.email"
              @click="updateCustomer(customer.email, null, null, null)"
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
              v-model="customer.password"
              placeholder="Password"
            />
          </td>
          <td>
            <b-button
              variant="outline-primary"
              v-bind:disabled="!customer.password"
              @click="updateCustomer(null, customer.password, null, null)"
            >
              Save
            </b-button>
          </td>
        </tr>
        <br />

        <tr>
          <td>Address:</td>
          <td>
            <input
              type="text"
              v-model="customer.address"
              placeholder="Address"
            />
          </td>
          <td>
            <b-button
              variant="outline-primary"
              v-bind:disabled="!customer.address"
              @click="updateCustomer(null, null, customer.address, null)"
            >
              Save
            </b-button>
          </td>
        </tr>
      </table>
      <br />
      <tr>
        <td>
          <h5>Is this address local?</h5>
          <input
            type="radio"
            id="True"
            value="true"
            name="isLocal"
            v-model="customer.local"
            v-on:click="updateCustomer(null, null, null, true)"
          />
          <label for="True">yes</label>
          <input
            type="radio"
            id="False"
            value="false"
            name="isLocal"
            v-model="customer.local"
            v-on:click="updateCustomer(null, null, null, false)"
          />
          <label for="False">no</label>
        </td>
      </tr>
    </div>
    <div>
      <span id="error" v-if="errorCustomer" style="color: red"
        >Error: {{ errorCustomer }}
      </span>
    </div>
  </div>
</template>

<script>
import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";
export default {
  name: "ManageProfile",
  data() {
    return {
      customer: AXIOS.get("/customer/".concat(LOGIN_STATE.state.username))
        .then(response => {
          this.customer = response.data;
        })
        .catch(e => {
          var errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.errorCustomer = errorMsg;
        }),
      username: LOGIN_STATE.state.username,
      errorCustomer: "",
      response: [],
    };
  },
  methods: {
    updateCustomer: function (email, password, address, isLocal) {
      AXIOS.patch(
        "/customer/".concat(LOGIN_STATE.state.username),
        {},
        {
          params: {
            email: email,
            password: password,
            address: address,
            isLocal: isLocal,
          },
        }
      )
        .then(response => {
          this.errorCustomer = "";
        })
        .catch(e => {
          var errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.errorCustomer = errorMsg;
        });
    },
  },
};
</script>

<style scoped>
div {
  line-height: 40px;
}
input[type="text"] {
  background: transparent;
  border: none;
  border-bottom: 1px solid #727272;
}
#error {
  position: absolute;
  top: 210px;
  right: 500px;
}
#manageprofile {
  text-align: center;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  height: 750px;
}
</style>
