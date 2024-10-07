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

  getKeymatches(collectionName, page, size, search, sortColumn, sortDirection) {
    let url = `/keymatch/search/byCollection`;
    const params = {
      collectionName,
      page: page - 1, // Spring Data REST uses 0-based page indexing
      size,
      sort: sortColumn ? `${sortColumn},${sortDirection}` : undefined,
    };

    if (search) {
      url = `/keymatch/search/byCollectionAndSearchTerm`;
      params.searchTerm = search;
    }

    return api.get(url, { params });
  }

  updateKeymatches(collectionId, keymatches) {
    const url = `/collection/${collectionId}/keymatches`;
    return api.put(url, keymatches, {
      headers: { "Content-Type": "application/json" }
    });
  }


}

export default new CollectionService();
