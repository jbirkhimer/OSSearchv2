// import api from "./api"

import axios from "axios";

class ServerStatusService {
    getServerStatus() {
        return axios.get('http://localhost:8484/actuator/health', {
            validateStatus: function (status) {
                return (status >= 200 && status < 300) || status === 503;
            }
        })
    }
}

export default new ServerStatusService();