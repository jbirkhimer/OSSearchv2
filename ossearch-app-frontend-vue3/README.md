# ossearch-app-frontend-vue3

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).


### Customize Modes and Environment Variables
See [Modes and Environment Variables](https://cli.vuejs.org/guide/mode-and-env.html).

For production set `VUE_APP_API_BASE_URL` in `.env.production` for backend api 


## Configure Backend Auth Headers:
Open `src/services/setupInterceptors.js` and modify `config.headers` for appropriate back-end.

```js
instance.interceptors.request.use(
  (config) => {
    const token = TokenService.getLocalAccessToken();
    if (token) {
      config.headers["Authorization"] = 'Bearer ' + token;  // for Spring Boot back-end
      // config.headers["x-access-token"] = token; // for Node.js Express back-end
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
```