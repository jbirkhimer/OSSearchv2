// vue.config.js
process.env.VUE_APP_VERSION = require('./package.json').version
process.env.VUE_APP_BUILD_DATE = new Date().toUTCString()

module.exports = {
    // By default, Vue CLI assumes your app will be deployed at the root of a domain, e.g. https://www.my-app.com/.
    // If your app is deployed at a sub-path, you will need to specify that sub-path using this option.
    // For example, if your app is deployed at https://www.foobar.com/my-app/, set publicPath to '/my-app/'
    // see https://cli.vuejs.org/config/#publicpath
    // publicPath: process.env.NODE_ENV === 'production'
    //     ? '/ossearchv2/'
    //     : '/',
    // proxy all webpack dev-server requests starting with /api
    // to our Spring Boot backend (localhost:8484) using http-proxy-middleware
    // see https://cli.vuejs.org/config/#devserver-proxy
    devServer: {
        host: '0.0.0.0',
        port: 3000,
        proxy: {
            '/api': {
                target: process.env.VUE_APP_API_BASE_URL, // this configuration needs to correspond to the Spring Boot backends' application.properties server.port
                ws: true,
                changeOrigin: true
            }
        },
        // disableHostCheck: true,
        allowedHosts: ['all'],
    },

    // Change build paths to make them Maven compatible
    // see https://cli.vuejs.org/config/
    outputDir: 'target/ossearch',
    assetsDir: 'static',
};
