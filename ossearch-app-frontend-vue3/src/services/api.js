import axios from "axios";

const instance = axios.create({
  // baseURL: "http://10.0.0.95:8484/api",
  baseURL: process.env.VUE_APP_API_BASE_URL+"/api",
  headers: {
    "Content-Type": "application/json",
  },
});

export default instance;
