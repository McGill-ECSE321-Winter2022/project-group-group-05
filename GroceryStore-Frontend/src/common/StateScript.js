import Vue from "vue";
import Vuex from "vuex";
import persistedState from "vuex-persistedstate";

Vue.use(Vuex);

/**
 * Store state information about the currently logged in user
 * @type {Store<{isLoggedIn: boolean, userType: string, username: string}>}
 */
export const LOGIN_STATE = new Vuex.Store({
  plugins: [
    persistedState({
      key: "vuex-login-state",
      storage: window.localStorage,
    }),
  ],
  state: {
    isLoggedIn: false,
    userType: "",
    username: "",
  },
  mutations: {
    /**
     * Set the state to logged in
     */
    login(state, { userType, username }) {
      state.isLoggedIn = true;
      state.userType = userType;
      state.username = username;
    },
    /**
     * Set the state to logged out
     */
    logout(state) {
      state.isLoggedIn = false;
      state.userType = "";
      state.username = "";
    },
  },
  actions: {
    /**
     * Perform an asynchronous log in action
     */
    loginAsync(context, { userType, username }) {
      context.commit("login", { userType, username });
    },
  },
});
