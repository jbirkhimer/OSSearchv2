import api from './api';

class UserService {

  getPublicContent() {
    return api.get('/test/all');
  }

  getUserBoard() {
    return api.get('/test/user');
  }

  getModeratorBoard() {
    return api.get('/test/mod');
  }

  getAdminBoard() {
    return api.get('/test/admin');
  }

  getUsers(url, params) {
    //console.log('[userService] url: '+url+',\nparams: ' + JSON.stringify(params, null, 2))
    // console.log("getUsers: " + url + ", " + params)
    return api.get(url, {params: params});
  }

  addUser(url, body) {
    //console.log('[userService] POST url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    return api.post(url, body);
  }

  updateUser(url, body) {
    //console.log('[userService] PATCH url: '+url+',\nbody: ' + body)
    return api.patch(url, body, {headers: {"Content-Type": "application/json"}});
  }

  addRole(url, body) {
    //console.log('[userService] PATCH url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    // let config = {
    //   headers: {
    //     "Content-Type": "text/uri-list"
    //   }
    // }
    return api.patch(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  updateRoles(url, body) {
    //console.log('[userService] PUT url: '+url+',\nbody: ' + body)
    return api.put(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  deleteUser(url) {
    //console.log('[userService] DELETE url: '+url)
    return api.delete(url);
  }

  getUserByUserName(username) {
    //console.log("getUserByUserName", username)
    return api.get('/users/search/findByUsername', {headers: {"Content-Type": "*/*"}, params: {username: username, projection: 'userIdNameEmailRoles'}});
  }

  addCollection(url, body) {
    //console.log('[userService] PATCH url: '+url+',\nbody: ' + JSON.stringify(body, null, 2))
    return api.patch(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }

  updateCollections(url, body) {
    //console.log('[userService] PUT url: '+url+',\nbody: ' + body)
    return api.put(url, body, {headers: {"Content-Type": "text/uri-list"}});
  }
}

export default new UserService();
