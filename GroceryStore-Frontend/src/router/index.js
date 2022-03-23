import Vue from "vue";
import Router from "vue-router";
import Welcome from "../components/Welcome";
import CreateCustomerForm from "../components/CreateCustomerForm";
import LoginForm from "../components/LoginForm";
import ManageCart from "../components/ManageCart";
import ManageCategories from "../components/ManageCategories";
import ManageCustomers from "../components/ManageCustomers";
import ManageEmployee from "../components/ManageEmployee";
import ManageItems from "../components/ManageItems";
import ManageOpeningHours from "../components/ManageOpeningHours";
import ManagePaidOrders from "../components/ManagePaidOrders";
import ManageProfile from "../components/ManageProfile";
import ManageSchedules from "../components/ManageSchedules";
import ManageStaffProfile from "../components/ManageStaffProfile";
import PointOfSale from "../components/PointOfSale";
import StaffDashboard from "../components/StaffDashboard";
import ViewCompletedOrders from "../components/ViewCompletedOrders";
import ViewHistory from "../components/ViewHistory";
import ViewSchedules from "../components/ViewSchedules";

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: "/",
      name: "Welcome",
      component: Welcome,
    },
    {
      path: "/CreateCustomerForm",
      name: "CreateCustomerForm",
      component: CreateCustomerForm,
    },
    {
      path: "/LoginForm",
      name: "LoginForm",
      component: LoginForm,
    },
    {
      path: "/ManageCart",
      name: "ManageCart",
      component: ManageCart,
    },
    {
      path: "/ManageCategories",
      name: "ManageCategories",
      component: ManageCategories,
    },
    {
      path: "/ManageCustomers",
      name: "ManageCustomers",
      component: ManageCustomers,
    },
    {
      path: "/ManageEmployee",
      name: "ManageEmployee",
      component: ManageEmployee,
    },
    {
      path: "/ManageItems",
      name: "ManageItems",
      component: ManageItems,
    },
    {
      path: "/ManageOpeningHours",
      name: "ManageOpeningHours",
      component: ManageOpeningHours,
    },
    {
      path: "/ManagePaidOrders",
      name: "ManagePaidOrders",
      component: ManagePaidOrders,
    },
    {
      path: "/ManageProfile",
      name: "ManageProfile",
      component: ManageProfile,
    },
    {
      path: "/ManageSchedules",
      name: "ManageSchedules",
      component: ManageSchedules,
    },
    {
      path: "/ManageStaffProfile",
      name: "ManageStaffProfile",
      component: ManageStaffProfile,
    },
    {
      path: "/PointOfSale",
      name: "PointOfSale",
      component: PointOfSale,
    },
    {
      path: "/StaffDashboard",
      name: "StaffDashboard",
      component: StaffDashboard,
    },
    {
      path: "/ViewCompletedOrders",
      name: "ViewCompletedOrders",
      component: ViewCompletedOrders,
    },
    {
      path: "/ViewHistory",
      name: "ViewHistory",
      component: ViewHistory,
    },
    {
      path: "/ViewSchedules",
      name: "ViewSchedules",
      component: ViewSchedules,
    },
  ],
});
