import axios from "axios";

const CONFIG = require("../../config");

/**
 * Get the backend URL for AXIOS, depending on whether the current environment is dev or prod
 * @returns {string}
 * @constructor
 */
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

/**
 * Get the frontend URL for AXIOS, depending on whether the current environment is dev or prod
 * @returns {string}
 * @constructor
 */
const FRONTEND_URL = function () {
  switch (process.env.NODE_ENV) {
    case "development":
      return "http://" + CONFIG.dev.host + ":" + CONFIG.dev.port;
    case "production":
      return "https://" + CONFIG.build.host + ":" + CONFIG.build.port;
  }
};

/**
 * The common AXIOS instance used by all components
 * @type {AxiosInstance}
 */
export const AXIOS = axios.create({
  baseURL: BACKEND_URL(),
  headers: { "Access-Control-Allow-Origin": FRONTEND_URL() },
});
