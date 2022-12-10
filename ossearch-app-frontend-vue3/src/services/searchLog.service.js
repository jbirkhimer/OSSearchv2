import api from './api';

class SearchLogService {

  get(url, params) {
    // console.log('[SearchLogService.getSearchLogs] GET url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
    return api.get(url, {params: params});
  }

}

export default new SearchLogService();
