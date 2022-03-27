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

// Vue filters
Vue.filter("formatTime", function (value) {
  if (value) {
    return moment(String(value), "HH:mm:ss").format("hh:mm A");
  }
});

// Don't use this filter on dates unless needed, the default display looks cleaner
Vue.filter("formatDate", function (value) {
  if (value) {
    return moment(String(value), "YYYY-MM-DD").format("MMMM Do, YYYY");
  }
});

// Format numbers rounded to 2 decimal places
Vue.filter("formatCurrency", function (value) {
  if (value) {
    return (Math.round(value * 100) / 100).toFixed(2);
  }
});
