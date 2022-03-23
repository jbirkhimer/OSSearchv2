import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import "bootstrap-icons/font/bootstrap-icons.css";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap";

import {FontAwesomeIcon} from './plugins/font-awesome'

import setupInterceptors from "./services/setupInterceptors";

setupInterceptors(store);

createApp(App)
    .use(store)
    .use(router)
    .component("font-awesome-icon", FontAwesomeIcon)
    .mount('#app')
