import { LOGIN_STATE } from "../common/StateScript";
import { AXIOS } from "../common/AxiosScript";

export default {
  name: "Welcome",
  data() {
    return {
      msg: "Welcome to the Grocery Store",
      LOGIN_STATE,
      isStaff:
        LOGIN_STATE.state.userType === "Employee" ||
        LOGIN_STATE.state.userType === "Owner",
      isCustomer: LOGIN_STATE.state.userType === "Customer",
      isLoading: false,
      loadingMsg: "Waiting for database...",
      openingHours: [],
    };
  },
  created: function () {
    // upon creation, fetch opening hours
    this.isLoading = true;
    AXIOS.get("/openingH/getAll", {})
      .then(response => {
        this.openingHours = response.data.sort((a, b) => {
          return (
            daysOfWeekSorter[a["daysOfWeek"].toLowerCase()] -
            daysOfWeekSorter[b["daysOfWeek"].toLowerCase()]
          );
        });
      })
      .catch(e => {
        console.log(e);
      })
      .finally(() => {
        // upon creation, verify if stored logged in user is still in the system
        let userType = LOGIN_STATE.state.userType;
        let username = LOGIN_STATE.state.username;
        if (LOGIN_STATE.state.isLoggedIn) {
          if (userType === "Owner") {
            AXIOS.get("/owner/".concat(username), {})
              .then(() => {})
              .catch(() => {
                this.logout();
              });
          } else if (userType === "Employee") {
            AXIOS.get("/employee/".concat(username), {})
              .then(() => {})
              .catch(() => {
                this.logout();
              });
          } else if (userType === "Customer") {
            AXIOS.get("/customer/".concat(username), {})
              .then(() => {})
              .catch(() => {
                this.logout();
              });
          } else {
            this.logout();
          }
        }
        this.isLoading = false;
      });
  },
  methods: {
    logout: function () {
      LOGIN_STATE.commit("logout");
      window.location.reload();
    },
    genData: async function () {
      this.isLoading = true;
      await createOwner();
      await createKiosk();
      await createEmployee();
      await createHoliday();
      await createItem1();
      await createItem2();
      await createItemCategory();
      await createOpeningHours();
      await sleep(200);
      this.isLoading = false;
      // comment this out if need to examine console output
      window.location.reload();
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
            newPassword: "kiosk",
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

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

function createItem1() {
  console.log("Attempting to create item 'Bar of Chocolate 100g'...");
  return AXIOS.post(
    "/item/Bar of Chocolate 100g",
    {},
    {
      params: {
        image: "https://i.ibb.co/ScXnkPY/img-Bar-of-Chocolate-100g.jpg",
        price: 3.49,
        inventory: 500,
        canDeliver: true,
        canPickUp: true,
      },
    }
  )
    .then(() => {
      console.log("Created item 'Bar of Chocolate 100g'");
    })
    .catch(() => {
      return AXIOS.patch(
        "/item/Bar of Chocolate 100g/setImage",
        {},
        {
          params: {
            image: "https://i.ibb.co/ScXnkPY/img-Bar-of-Chocolate-100g.jpg",
          },
        }
      )
        .then(() => {})
        .catch(() => {})
        .finally(() => {
          return AXIOS.patch(
            "/item/Bar of Chocolate 100g/setInventory",
            {},
            {
              params: {
                inventory: 500,
              },
            }
          )
            .then(() => {})
            .catch(() => {})
            .finally(() => {
              return AXIOS.patch(
                "/item/Bar of Chocolate 100g/setCanDeliver",
                {},
                {
                  params: {
                    canDeliver: true,
                  },
                }
              )
                .then(() => {})
                .catch(() => {})
                .finally(() => {
                  return AXIOS.patch(
                    "/item/Bar of Chocolate 100g/setCanPickUp",
                    {},
                    {
                      params: {
                        canPickUp: true,
                      },
                    }
                  )
                    .then(() => {})
                    .catch(() => {})
                    .finally(() => {
                      return AXIOS.patch(
                        "/item/Bar of Chocolate 100g/setIsDiscontinued",
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
                            "/item/Bar of Chocolate 100g/setPrice",
                            {},
                            {
                              params: {
                                price: 3.49,
                              },
                            }
                          )
                            .then(() => {})
                            .catch(() => {})
                            .finally(() => {
                              console.log("Reset item 'Bar of Chocolate 100g'");
                            });
                        });
                    });
                });
            });
        });
    });
}

function createItem2() {
  console.log("Attempting to create item 'Bag of Apples 1.36kg'...");
  return AXIOS.post(
    "/item/Bag of Apples 1.36kg",
    {},
    {
      params: {
        image: "https://i.ibb.co/G7bZHL7/img-Bag-of-Apples-1-36kg.jpg",
        price: 7.99,
        inventory: 100,
        canDeliver: false,
        canPickUp: true,
      },
    }
  )
    .then(() => {
      console.log("Created item 'Bag of Apples 1.36kg'");
    })
    .catch(() => {
      return AXIOS.patch(
        "/item/Bag of Apples 1.36kg/setImage",
        {},
        {
          params: {
            image: "https://i.ibb.co/G7bZHL7/img-Bag-of-Apples-1-36kg.jpg",
          },
        }
      )
        .then(() => {})
        .catch(() => {})
        .finally(() => {
          return AXIOS.patch(
            "/item/Bag of Apples 1.36kg/setInventory",
            {},
            {
              params: {
                inventory: 100,
              },
            }
          )
            .then(() => {})
            .catch(() => {})
            .finally(() => {
              return AXIOS.patch(
                "/item/Bag of Apples 1.36kg/setCanDeliver",
                {},
                {
                  params: {
                    canDeliver: false,
                  },
                }
              )
                .then(() => {})
                .catch(() => {})
                .finally(() => {
                  return AXIOS.patch(
                    "/item/Bag of Apples 1.36kg/setCanPickUp",
                    {},
                    {
                      params: {
                        canPickUp: true,
                      },
                    }
                  )
                    .then(() => {})
                    .catch(() => {})
                    .finally(() => {
                      return AXIOS.patch(
                        "/item/Bag of Apples 1.36kg/setIsDiscontinued",
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
                            "/item/Bag of Apples 1.36kg/setPrice",
                            {},
                            {
                              params: {
                                price: 7.99,
                              },
                            }
                          )
                            .then(() => {})
                            .catch(() => {})
                            .finally(() => {
                              console.log("Reset item 'Bag of Apples 1.36kg'");
                            });
                        });
                    });
                });
            });
        });
    });
}
