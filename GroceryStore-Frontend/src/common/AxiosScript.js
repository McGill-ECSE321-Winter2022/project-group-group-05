import axios from "axios";

const CONFIG = require("../../config");

const BACKEND_URL = function () {
  switch (process.env.NODE_ENV) {
    case "development":
      // use https if developing using hosted backend, else use http
      if (
        CONFIG.dev.backendHost === "grocery-backend-g05-mcgill.herokuapp.com"
      ) {
        return (
          "https://" + CONFIG.dev.backendHost + ":" + CONFIG.dev.backendPort
        );
      } else {
        return (
          "http://" + CONFIG.dev.backendHost + ":" + CONFIG.dev.backendPort
        );
      }
    case "production":
      return (
        "https://" + CONFIG.build.backendHost + ":" + CONFIG.build.backendPort
      );
  }
};

const FRONTEND_URL = function () {
  switch (process.env.NODE_ENV) {
    case "development":
      return "http://" + CONFIG.dev.host + ":" + CONFIG.dev.port;
    case "production":
      return "https://" + CONFIG.build.host + ":" + CONFIG.build.port;
  }
};

export const AXIOS = axios.create({
  baseURL: BACKEND_URL(),
  headers: { "Access-Control-Allow-Origin": FRONTEND_URL() },
});
