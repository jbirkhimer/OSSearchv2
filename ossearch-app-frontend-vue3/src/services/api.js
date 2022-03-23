import axios from "axios";

const instance = axios.create({
  // baseURL: "http://192.168.1.128:8484/api",
  baseURL: process.env.VUE_APP_API_BASE_URL+"/api",
  headers: {
    "Content-Type": "application/json",
  },
});

export default instance;
