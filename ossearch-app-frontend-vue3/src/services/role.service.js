import api from "./api";

class RoleService {
    getRoles() {
        return api.get('/roles')
    }
}

export default new RoleService();