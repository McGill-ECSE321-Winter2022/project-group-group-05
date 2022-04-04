import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";

export default {
  name: "Welcome",
  data() {
    return {
      msg: "Welcome to the Grocery Store",
      LOGIN_STATE,
      isLoading: false,
      isItemLoading: false,
      loadingMsg: "Waiting for database...",
      marqueePause: false,
      holidays: [],
      nextHolidayDate: "",
      nextHolidayName: "",
      cart: "",
      // item browsing
      itemListFields: [
        {
          key: "name",
          sortable: true,
        },
        {
          key: "image",
          label: "",
          sortable: false,
        },
        {
          key: "price",
          sortable: true,
        },
        {
          key: "inventory",
          label: "",
          sortable: false,
        },
        {
          key: "canDeliver",
          label: "",
          sortable: false,
        },
        {
          key: "canPickUp",
          label: "",
          sortable: false,
        },
      ],
      itemList: [],
      perPage: 3,
      currentPage: 1,
      clickedItem: "",
      addQuantity: 1,
      addItemError: "",
      addItemSuccess: "",
      // item filtering
      itemSearchQuery: "",
      categoriesList: [],
      categoriesOptions: [],
      selectedCategory: null,
      mustCanDeliver: false,
      mustCanPickUp: false,
      mustAvailableOnline: false,
      showOutOfStock: false,
    };
  },
  computed: {
    isStaff() {
      return (
        LOGIN_STATE.state.userType === "Employee" ||
        LOGIN_STATE.state.userType === "Owner"
      );
    },
    isCustomer() {
      return LOGIN_STATE.state.userType === "Customer";
    },
    numRows() {
      return this.filteredItemList.length;
    },
    filteredItemList() {
      return this.itemList.filter(item => {
        if (!item["discontinued"]) {
          if (!this.showOutOfStock && !(item["inventory"] > 0)) {
            return false;
          }
          if (this.mustCanDeliver && !item["canDeliver"]) {
            return false;
          }
          if (this.mustCanPickUp && !item["canPickUp"]) {
            return false;
          }
          if (
            this.mustAvailableOnline &&
            !(item["canDeliver"] || item["canPickUp"])
          ) {
            return false;
          }
          return item["name"]
            .toLowerCase()
            .includes(this.itemSearchQuery.trim().toLowerCase());
        }
        return false;
      });
    },
  },
  created: async function () {
    this.isLoading = true;
    // upon creation, verify if stored logged in user is still in the system
    let userType = LOGIN_STATE.state.userType;
    let username = LOGIN_STATE.state.username;
    if (LOGIN_STATE.state.isLoggedIn) {
      if (userType === "Owner") {
        await AXIOS.get("/owner/".concat(username), {}).catch(() => {
          this.logout();
        });
      } else if (userType === "Employee") {
        await AXIOS.get("/employee/".concat(username), {}).catch(() => {
          this.logout();
        });
      } else if (userType === "Customer") {
        await AXIOS.get("/customer/".concat(username), {}).catch(() => {
          this.logout();
        });
      } else {
        this.logout();
      }
    }
    // upon creation, fetch cart if customer is logged in and is not POS
    if (this.isCustomer && username !== "kiosk") {
      await AXIOS.post(
        "/purchase/cart",
        {},
        {
          params: {
            username: username,
          },
        }
      )
        .then(response => {
          this.cart = response.data;
          console.log("Successfully retrieved cart #" + this.cart["id"]);
        })
        .catch(e => {
          console.log(e);
        });
    }
    // upon creation, fetch holidays
    await AXIOS.get("holiday/getAll", {})
      .then(response => {
        this.holidays = response.data;
        if (this.holidays.length > 0) {
          this.nextHolidayDate = this.holidays[0]["date"];
          this.nextHolidayName = this.holidays[0]["name"];
        }
      })
      .catch(e => {
        console.log(e);
      });
    await this.fetchCategories();
    this.updateSelection();
    this.isLoading = false;
    this.isItemLoading = true;
    await this.fetchItems();
    this.isItemLoading = false;
  },
  methods: {
    logout: function () {
      LOGIN_STATE.commit("logout");
      window.location.reload();
    },
    // this method is used to populate the database for demo purposes
    genData: async function () {
      this.isLoading = true;
      this.isItemLoading = true;
      await createOwner();
      await createKiosk();
      await createCustomer();
      await createEmployee();
      await createHoliday();
      await createItem1();
      await createItem2();
      await createItem3();
      await createItem4();
      await createItem5();
      await createItemCategory();
      await createOpeningHours();
      this.isLoading = false;
      this.isItemLoading = false;
      // comment this out if need to examine console output
      window.location.reload();
    },
    addItemDialog: function (item) {
      this.clickedItem = "";
      this.addQuantity = 1;
      this.addItemError = "";
      this.addItemSuccess = "";
      if (!LOGIN_STATE.state.isLoggedIn) {
        this.$router.push("/LoginForm");
      } else if (!this.isCustomer || LOGIN_STATE.state.username === "kiosk") {
        this.$bvModal.show("add-item-denied");
      } else {
        if (item["canDeliver"] || item["canPickUp"]) {
          this.clickedItem = item;
          this.$bvModal.show("add-item-dialog");
        } else {
          this.addItemError =
            "Sorry, but this item is not available for purchase online. Please visit us in-store!";
          this.$bvModal.show("add-item-error");
        }
      }
    },
    addItemToCart: async function () {
      // this should never be called outside of the add-item-dialog
      this.isLoading = true;
      this.isItemLoading = true;
      await AXIOS.post(
        "/purchase/addItem/".concat(this.cart["id"]),
        {},
        {
          params: {
            itemName: this.clickedItem["name"],
            quantity: this.addQuantity,
          },
        }
      )
        .then(() => {
          let msg =
            "Successfully added " +
            this.addQuantity +
            " " +
            this.clickedItem["name"] +
            " to cart";
          console.log(msg);
          this.addItemSuccess = msg;
          this.$bvModal.show("add-item-success");
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
          this.addItemError = errorMsg;
          this.$bvModal.show("add-item-error");
        });
      this.isLoading = false;
      this.isItemLoading = false;
    },
    atCategorySelection: async function (value) {
      this.isItemLoading = true;
      if (value === null) {
        await this.fetchItems();
      } else {
        await this.fetchItemsInCategory(value);
      }
      this.isItemLoading = false;
    },
    atClearFilters: function () {
      this.mustCanDeliver = false;
      this.mustCanPickUp = false;
      this.mustAvailableOnline = false;
      this.showOutOfStock = false;
    },
    fetchItems() {
      return AXIOS.get("/item/getAll", {})
        .then(response => {
          this.itemList = response.data;
        })
        .catch(e => {
          console.log(e);
        });
    },
    fetchCategories() {
      return AXIOS.get("/itemCategory", {})
        .then(response => {
          this.categoriesList = response.data;
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
        });
    },
    updateSelection() {
      this.categoriesOptions = [];
      for (const category of this.categoriesList) {
        let name = category["name"];
        let option = { value: name, text: name };
        this.categoriesOptions.push(option);
      }
    },
    fetchItemsInCategory(categoryName) {
      return AXIOS.get(
        "/itemCategory/".concat(categoryName).concat("/getItems"),
        {}
      )
        .then(response => {
          this.itemList = response.data;
        })
        .catch(e => {
          let errorMsg = e.response.data.message;
          console.log(errorMsg);
        });
    },
  },
};

const daysOfWeekSorter = {
  monday: 1,
  tuesday: 2,
  wednesday: 3,
  thursday: 4,
  friday: 5,
  saturday: 6,
  sunday: 7,
};

function createOwner() {
  console.log("Attempting to create owner...");
  return AXIOS.post(
    "/owner/admin",
    {},
    {
      params: {
        password: "admin",
        email: "admin@gss.com",
      },
    }
  )
    .then(() => {
      console.log("Created owner 'admin' with password 'admin'");
    })
    .catch(() => {
      return AXIOS.patch(
        "/owner/admin",
        {},
        {
          params: {
            password: "admin",
          },
        }
      )
        .then(() => {
          console.log("Reset owner 'admin' password to 'admin'");
        })
        .catch(() => {
          console.log("Failed to set up owner account");
        });
    });
}

function createKiosk() {
  console.log("Attempting to create customer 'kiosk'...");
  return AXIOS.post(
    "/customer/kiosk",
    {},
    {
      params: {
        password: "kiosk",
        email: "kiosk@gss.com",
        address: "123 Grocery Store Ave.",
        isLocal: true,
      },
    }
  )
    .then(() => {
      console.log("Created customer 'kiosk' with password 'kiosk'");
    })
    .catch(() => {
      return AXIOS.patch(
        "/customer/kiosk",
        {},
        {
          params: {
            password: "kiosk",
          },
        }
      )
        .then(() => {
          console.log("Reset customer 'kiosk' password to 'kiosk'");
        })
        .catch(() => {
          console.log("Failed to set up customer 'kiosk' account");
        });
    });
}

function createCustomer() {
  console.log("Attempting to create customer 'loyalcustomer'...");
  return AXIOS.post(
    "/customer/loyalcustomer",
    {},
    {
      params: {
        password: "123456",
        email: "loyalcustomer@gmail.com",
        address: "12630 Younge St. Toronto, ON",
        isLocal: false,
      },
    }
  )
    .then(() => {
      console.log("Created customer 'loyalcustomer' with password '123456'");
    })
    .catch(() => {
      return AXIOS.patch(
        "/customer/loyalcustomer",
        {},
        {
          params: {
            password: "123456",
          },
        }
      )
        .then(() => {
          console.log("Reset customer 'loyalcustomer' password to '123456'");
        })
        .catch(() => {
          console.log("Failed to set up customer 'loyalcustomer' account");
        });
    });
}

function createEmployee() {
  console.log("Attempting to create employee...");
  return AXIOS.post(
    "/employee/worker1",
    {},
    {
      params: {
        password: "worker1",
        email: "worker1@gss.com",
      },
    }
  )
    .then(() => {
      console.log("Created employee 'worker1' with password 'worker1'");
    })
    .catch(() => {
      return AXIOS.patch(
        "/employee/worker1",
        {},
        {
          params: {
            password: "worker1",
          },
        }
      )
        .then(() => {
          console.log("Reset employee 'worker1' password to 'worker1'");
        })
        .catch(() => {
          console.log("Failed to set up employee 'worker1' account");
        });
    });
}

function createHoliday() {
  console.log("Attempting to create holiday...");
  return AXIOS.post(
    "/holiday/DummyHoliday",
    {},
    {
      params: {
        date: "2022-12-20",
      },
    }
  )
    .then(() => {
      console.log("Created holiday 'DummyHoliday' on '2022-12-20'");
    })
    .catch(() => {
      console.log("Holiday 'DummyHoliday' already exists");
    });
}

function createItemCategory() {
  console.log("Attempting to create item category 'Test Category x'");
  return AXIOS.post("/itemCategory/Test Category x", {}, {})
    .then(() => {})
    .catch(() => {})
    .finally(() => {
      return AXIOS.patch(
        "itemCategory/Test Category x/addItem",
        {},
        {
          params: {
            itemName: "Bar of Chocolate 100g",
          },
        }
      )
        .catch(() => {})
        .finally(() => {
          console.log("Finished creating item category 'Test Category x'");
        });
    });
}

function createOpeningHours() {
  console.log("Attempting to create opening hours for 'Friday'");
  return AXIOS.post(
    "/openingH/Friday",
    {},
    {
      params: {
        startH: "09:30",
        endH: "18:00",
      },
    }
  )
    .then(() => {
      console.log("Sucess");
    })
    .catch(() => {
      console.log("'Friday' already exists");
    })
    .finally(() => {
      console.log("Attempting to create opening hours for 'Tuesday'");
      return AXIOS.post(
        "/openingH/Tuesday",
        {},
        {
          params: {
            startH: "11:00",
            endH: "17:00",
          },
        }
      )
        .then(() => {
          console.log("Sucess");
        })
        .catch(() => {
          console.log("'Tuesday' already exists");
        });
    });
}

function createItem1() {
  let name = "Bar of Chocolate 100g";
  let imgURL = "https://i.ibb.co/0Qv0HFL/img-Bar-of-Chocolate-100g.jpg";
  let price = 3.49;
  let inventory = 500;
  let canDeliver = true;
  let canPickUp = true;
  return createItem(name, imgURL, price, inventory, canDeliver, canPickUp);
}

function createItem2() {
  let name = "Bag of Apples 1.36kg";
  let imgURL = "https://i.ibb.co/F34h8Yg/img-Bag-of-Apples-1-36kg.jpg";
  let price = 4.99;
  let inventory = 100;
  let canDeliver = false;
  let canPickUp = true;
  return createItem(name, imgURL, price, inventory, canDeliver, canPickUp);
}

function createItem3() {
  let name = "Steam Gift Card $100";
  let imgURL = "https://i.ibb.co/8cwSbZV/img-steam-gift-card-100.webp";
  let price = 99.99;
  let inventory = 2000;
  let canDeliver = false;
  let canPickUp = false;
  return createItem(name, imgURL, price, inventory, canDeliver, canPickUp);
}

function createItem4() {
  let name = "Steam Gift Card $50";
  let imgURL = "";
  let price = 49.99;
  let inventory = 900;
  let canDeliver = false;
  let canPickUp = false;
  return createItem(name, imgURL, price, inventory, canDeliver, canPickUp);
}

function createItem5() {
  let name = "Steam Gift Card $20";
  let imgURL = "";
  let price = 20;
  let inventory = 400;
  let canDeliver = false;
  let canPickUp = false;
  return createItem(name, imgURL, price, inventory, canDeliver, canPickUp);
}

function createItem(name, imageURL, price, inventory, canDeliver, canPickUp) {
  console.log("Attempting to create item '" + name + "'...");
  return AXIOS.post(
    "/item/".concat(name),
    {},
    {
      params: {
        image: imageURL,
        price: price,
        inventory: inventory,
        canDeliver: canDeliver,
        canPickUp: canPickUp,
      },
    }
  )
    .then(() => {
      console.log("Created item '" + name + "'");
    })
    .catch(() => {
      return AXIOS.patch(
        "/item/".concat(name).concat("/setImage"),
        {},
        {
          params: {
            image: imageURL,
          },
        }
      )
        .then(() => {})
        .catch(() => {})
        .finally(() => {
          return AXIOS.patch(
            "/item/".concat(name).concat("/setInventory"),
            {},
            {
              params: {
                inventory: inventory,
              },
            }
          )
            .then(() => {})
            .catch(() => {})
            .finally(() => {
              return AXIOS.patch(
                "/item/".concat(name).concat("/setCanDeliver"),
                {},
                {
                  params: {
                    canDeliver: canDeliver,
                  },
                }
              )
                .then(() => {})
                .catch(() => {})
                .finally(() => {
                  return AXIOS.patch(
                    "/item/".concat(name).concat("/setCanPickUp"),
                    {},
                    {
                      params: {
                        canPickUp: canPickUp,
                      },
                    }
                  )
                    .then(() => {})
                    .catch(() => {})
                    .finally(() => {
                      return AXIOS.patch(
                        "/item/".concat(name).concat("/setIsDiscontinued"),
                        {},
                        {
                          params: {
                            isDiscontinued: false,
                          },
                        }
                      )
                        .then(() => {})
                        .catch(() => {})
                        .finally(() => {
                          return AXIOS.patch(
                            "/item/".concat(name).concat("/setPrice"),
                            {},
                            {
                              params: {
                                price: price,
                              },
                            }
                          )
                            .then(() => {})
                            .catch(() => {})
                            .finally(() => {
                              console.log("Reset item '" + name + "'");
                            });
                        });
                    });
                });
            });
        });
    });
}
