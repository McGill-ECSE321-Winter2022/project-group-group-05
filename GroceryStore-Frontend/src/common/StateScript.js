import Vue from "vue";
import Vuex from "vuex";
import persistedState from "vuex-persistedstate";

Vue.use(Vuex);

// sessionStorage clears on new tab, localStorage never expires

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
    login(state, { userType, username }) {
      state.isLoggedIn = true;
      state.userType = userType;
      state.username = username;
    },
    logout(state) {
      state.isLoggedIn = false;
      state.userType = "";
      state.username = "";
    },
  },
  actions: {
    loginAsync(context, { userType, username }) {
      context.commit("login", { userType, username });
    },
  },
});
