import api from './api';

class CollectionService {

  getCollections(url, params) {
    //console.log('[collectionService] GET url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
    return api.get(url, {params: params});
  }

  addCollection(url, body) {
    // console.log('[collectionService] POST url: '+url+',\nbody: ' + body)
    return api.post(url, body, {headers: {"Content-Type": "application/json"}});
  }

  updateCollection(url, body) {
    // console.log('[collectionService] PATCH url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    return api.patch(url, body, {headers: {"Content-Type": "application/json"}});
  }

  changeOwner(url, body) {
    // console.log('[collectionService] PUT url: '+url+',\nbody: ' + body)
    return api.put(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  updateManagers(url, body) {
    // console.log('[collectionService] PUT url: '+url+',\nbody: ' + body)
    return api.put(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  addIncludedCollections(url, body) {
    //console.log('[collectionService] PUT url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    return api.put(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  addPartOfCollections(url, body) {
    //console.log('[collectionService] PUT url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    return api.put(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  addEdanFieldMappingCollections(url, body) {
    //console.log('[collectionService] PUT url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    return api.put(url, body, {headers: {"Content-Type": "application/json"}});
  }

  deleteCollection(url) {
    //console.log('[collectionService] DELETE url: '+url)
    return api.delete(url);
  }

}

export default new CollectionService();
