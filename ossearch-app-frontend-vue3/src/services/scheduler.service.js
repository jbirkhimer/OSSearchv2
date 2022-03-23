import api from './api';

class SchedulerService {

    getAllJobs(url, params) {
        //console.log('[SchedulerService] GET url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
        return api.get(url, {params: params});
    }

    getJob(url, params) {
        //console.log('[SchedulerService] GET url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
        return api.get(url, {params: params});
    }

    createCrawlJob(url, body) {
        //console.log('[crawlJobService addCrawlJob] POST url: '+url+',\nbody: ' + body)
        return api.post(url, body, {headers: {"Content-Type": "application/json"}});
    }

    updateCrawlJob(url, body) {
        //console.log('[crawlJobService updateCrawlJob] put url: '+url+',\nbody: ' + body)
        return api.put(url, body, {headers: {"Content-Type": "application/json"}});
    }

    deleteCrawlJob(url, params) {
        //console.log('[crawlJobService deleteCrawlJob] DELETE url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
        return api.delete(url, {params: params});
    }

    pauseCrawlJob(url, params) {
        //console.log('[crawlJobService pauseCrawlJob] PATCH url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
        return api.patch(url, null, {params: params});
    }

    resumeCrawlJob(url, params) {
        //console.log('[crawlJobService resumeCrawlJob] PATCH url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
        return api.patch(url, null, {params: params});
    }

    startCrawlJob(url, params) {
        //console.log('[crawlJobService startCrawlJob] PATCH url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
        return api.patch(url, null, {params: params});
    }

    stopCrawlJob(url, params) {
        //console.log('[crawlJobService stopCrawlJob] PATCH url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
        return api.patch(url, null, {params: params});
    }

    getCrawlLogs(url, params) {
        //console.log('[crawlJobService getCrawlLogs] GET url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
        return api.get(url, {params: params});
    }

}

export default new SchedulerService();