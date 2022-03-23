import api from "./api"

import axios from "axios";

class ServerStatusService {
    getServerStatus() {
        return axios.get(process.env.VUE_APP_API_BASE_URL+'/actuator/health', {
            validateStatus: function (status) {
                return (status >= 200 && status < 300) || status === 503;
            }
        })
    }
    getSchedulerStatus() {
        return api.get('/scheduler/metaData', {
            validateStatus: function (status) {
                return (status >= 200 && status < 300) || status === 503;
            }
        })
    }
    getSolr() {
        return api.get('/utils/solr/count', {
            validateStatus: function (status) {
                return (status >= 200 && status < 300) || status === 503;
            }
        })
    }
    getSolrCollectionCounts() {
        return api.get('/utils/solr/collection_counts', {
            validateStatus: function (status) {
                return (status >= 200 && status < 300) || status === 503;
            }
        })
    }
    getCrawlLogStats() {
        return api.get('/crawllog/search/getLatestCrawlLogStats', {
            params: {projection: 'crawlLogLatestStats'},
            validateStatus: function (status) {
                return (status >= 200 && status < 300) || status === 503;
            }
        })
    }
}

export default new ServerStatusService();