// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from "vue";
import { BootstrapVue, BootstrapVueIcons, IconsPlugin } from "bootstrap-vue";
import App from "./App";
import router from "./router";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import moment from "moment";
import MarqueeText from "vue-marquee-text-component";

Vue.use(BootstrapVue);
Vue.use(BootstrapVueIcons);
Vue.use(IconsPlugin);
Vue.config.productionTip = false;
Vue.component("marquee-text", MarqueeText);

/* eslint-disable no-new */
new Vue({
  el: "#app",
  router,
  template: "<App/>",
  components: { App },
});

/**
 * Vue filter: convert time string from HH:mm:ss to hh:mm A
 */
Vue.filter("formatTime", function (value) {
  if (value) {
    return moment(String(value), "HH:mm:ss").format("hh:mm A");
  }
});

/**
 * Vue filter: convert date string from YYYY-MM-DD to MMMM Do, YYYY
 */
Vue.filter("formatDate", function (value) {
  if (value) {
    return moment(String(value), "YYYY-MM-DD").format("MMMM Do, YYYY");
  }
});

/**
 * Vue filter: convert number string to rounded 2 decimal places
 */
Vue.filter("formatCurrency", function (value) {
  if (value) {
    return (Math.round(value * 100) / 100).toFixed(2);
  } else {
    let result = 0;
    return result.toFixed(2);
  }
});
