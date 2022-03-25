import api from './api';

class CollectionService {

  getCollections(url, params) {
    //console.log('[collectionService] GET url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
    // console.log("getCollections: " + url + ", " + params)
    return api.get(url, {params: params});
  }

  addCollection(url, body) {
    //console.log('[collectionService] POST url: '+url+',\nbody: ' + body)
    return api.post(url, body, {headers: {"Content-Type": "application/json"}});
  }

  updateCollection(url, body) {
    //console.log('[collectionService] PATCH url: '+url+',\nbody: ' + body)
    return api.patch(url, body, {headers: {"Content-Type": "application/json"}});
  }

  addRole(url, body) {
    //console.log('[collectionService] PATCH url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    // let config = {
    //   headers: {
    //     "Content-Type": "text/uri-list"
    //   }
    // }
    return api.patch(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  addIncludedCollections(url, body) {
    //console.log('[collectionService] PUT url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    // let config = {
    //   headers: {
    //     "Content-Type": "text/uri-list"
    //   }
    // }
    return api.put(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  addPartOfCollections(url, body) {
    //console.log('[collectionService] PUT url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    // let config = {
    //   headers: {
    //     "Content-Type": "text/uri-list"
    //   }
    // }
    return api.put(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  deleteCollection(url) {
    //console.log('[collectionService] DELETE url: '+url)
    return api.delete(url);
  }

}

export default new CollectionService();
