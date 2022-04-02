import { AXIOS } from "../common/AxiosScript";
import { LOGIN_STATE } from "../common/StateScript";
import StaffDashboard from "./StaffDashboard";

export default {
  name: "ManageCategories",
  components: { StaffDashboard },
  data() {
    return {
      LOGIN_STATE,
      isOwner: LOGIN_STATE.state.userType === "Owner",
      isLoading: false,
      categoriesError: "",
      // create new category
      newCategoryName: "",
      // browse categories
      categoriesList: [],
      categoriesOptions: [],
      selectedCategory: null,
      itemsInSelectedCategory: [],
      // browse items in category
      removeItem: "",
      // browse items
      itemList: [],
      itemFields: [
        {
          key: "name",
          sortable: true,
        },
        {
          key: "price",
          sortable: true,
        },
        {
          key: "inventory",
          sortable: true,
        },
      ],
      itemSearchQuery: "",
    };
  },
  computed: {
    filteredItemList() {
      return this.itemList.filter(item => {
        return item["name"]
          .toLowerCase()
          .includes(this.itemSearchQuery.trim().toLowerCase());
      });
    },
  },
  created: async function () {
    this.isLoading = true;
    await this.fetchCategories();
    await this.fetchItems();
    this.updateSelection();
    this.isLoading = false;
  },
  methods: {
    deleteCategory: async function () {
      if (this.selectedCategory !== null) {
        this.categoriesError = "";
        this.isLoading = true;
        let deleteName = this.selectedCategory;
        this.selectedCategory = null;
        await AXIOS.delete("/itemCategory/".concat(deleteName));
        await this.fetchCategories();
        this.updateSelection();
        this.isLoading = false;
      } else {
        this.categoriesError = "No selected category";
      }
    },
    createCategoryDialog: function () {
      this.$bvModal.show("create-category-dialog");
    },
    submitNewCategory: async function () {
      this.categoriesError = "";
      this.isLoading = true;
      await this.createCategory(this.newCategoryName);
      await this.fetchCategories();
      this.updateSelection();
      this.$bvModal.hide("create-category-dialog");
      this.newCategoryName = "";
      this.isLoading = false;
    },
    atSelction: async function (value) {
      if (value !== null) {
        this.categoriesError = "";
        this.isLoading = true;
        await this.fetchItemsInCategory(value);
        this.isLoading = false;
      } else {
        this.itemsInSelectedCategory = [];
      }
    },
    removeDialog: function (item) {
      this.removeItem = item;
      this.$bvModal.show("remove-item-dialog");
    },
    removeConfirm: async function () {
      this.categoriesError = "";
      this.isLoading = true;
      await this.removeItemFromCategory(
        this.selectedCategory,
        this.removeItem["name"]
      );
      await this.fetchItemsInCategory(this.selectedCategory);
      this.$bvModal.hide("remove-item-dialog");
      this.isLoading = false;
    },
    addDialog: function () {
      this.$bvModal.show("item-search");
    },
    addConfirm: async function (item) {
      this.categoriesError = "";
      this.isLoading = true;
      await this.addItemToCategory(this.selectedCategory, item["name"]);
      await this.fetchItemsInCategory(this.selectedCategory);
      this.isLoading = false;
    },
    fetchCategories: function () {
      return AXIOS.get("/itemCategory", {})
        .then(response => {
          this.categoriesList = response.data;
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
        });
    },
    fetchItems: function () {
      return AXIOS.get("/item/getAll", {})
        .then(response => {
          this.itemList = response.data;
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
        });
    },
    fetchItemsInCategory: function (categoryName) {
      return AXIOS.get(
        "/itemCategory/".concat(categoryName).concat("/getItems"),
        {}
      )
        .then(response => {
          this.itemsInSelectedCategory = response.data;
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.categoriesError = errorMsg;
        });
    },
    updateSelection: function () {
      this.categoriesOptions = [];
      for (const category of this.categoriesList) {
        let name = category["name"];
        let option = { value: name, text: name };
        this.categoriesOptions.push(option);
      }
    },
    removeItemFromCategory: function (categoryName, itemName) {
      return AXIOS.patch(
        "/itemCategory/".concat(categoryName).concat("/removeItem"),
        {},
        {
          params: {
            itemName: itemName,
          },
        }
      ).catch(e => {
        let errorMsg = e.response.data.message;
        console.log(errorMsg);
        this.categoriesError = errorMsg;
      });
    },
    addItemToCategory: function (categoryName, itemName) {
      return AXIOS.patch(
        "/itemCategory/".concat(categoryName).concat("/addItem"),
        {},
        {
          params: {
            itemName: itemName,
          },
        }
      ).catch(e => {
        let errorMsg = e.response.data.message;
        console.log(errorMsg);
        if (errorMsg.includes("ConstraintViolationException")) {
          errorMsg = "Item already belongs to another category";
        }
        this.categoriesError = errorMsg;
      });
    },
    createCategory: function (categoryName) {
      return AXIOS.post("/itemCategory/".concat(categoryName), {}, {})
        .then(() => {})
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.categoriesError = errorMsg;
        });
    },
  },
};
