import api from './api';

class CrawlUtilsService {

  get(url, params) {
    //console.log('[crawlUtilsService] GET url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
    return api.get(url, {params: params});
  }

  post(url, params, body, contentType = "application/json") {
    // console.log('[crawlUtilsService] POST url: '+url+',\nbody: ' + body)
    return api.post(url, body, {headers: {"Content-Type": contentType}, params: params});
  }

  patch(url, body) {
    // console.log('[crawlUtilsService] PATCH url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    return api.patch(url, body, {headers: {"Content-Type": "application/json"}});
  }

  put(url, body) {
    // console.log('[crawlUtilsService] PUT url: '+url+',\nbody: ' + body)
    return api.put(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  delete(url) {
    //console.log('[crawlUtilsService] DELETE url: '+url)
    return api.delete(url);
  }

}

export default new CrawlUtilsService();
