import AuthService from '../services/auth.service';

// const user = {
//     status: {
//         loggedIn: false
//     },
//     user: {
//         refreshToken: null,
//         id: null,
//         username: "admin",
//         email: "birkhimerj@quotient-inc.com",
//         roles: ["ROLE_ADMIN"],
//         accessToken: "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY0NjE4MDI2MywiZXhwIjoxNjQ2MTgwMzIzfQ.BagBbCy9-rCZunRWoJRD5RsmDFjo2KiFuoZWSKTjzHdQ5YDkGn0-zyzvAA9Xyh3WUiqDXTfkZ0eEnFr2GaOo5g",
//         tokenType: "Bearer"
//     }
// }

const user = JSON.parse(localStorage.getItem('user'));
const initialState = user
    ? {status: {loggedIn: true}, user}
    : {status: {loggedIn: false}, user: null};

export const auth = {
    namespaced: true,
    state: initialState,
    actions: {
        login({commit}, user) {
            return AuthService.login(user)
                .then(
                    user => {
                        commit('loginSuccess', user);
                        return Promise.resolve(user);
                    },
                    error => {
                        commit('loginFailure');
                        return Promise.reject(error);
                    }
                );
        },
        logout({commit}) {
            AuthService.logout();
            commit('logout');
        },
        register({commit}, user) {
            return AuthService.register(user)
                .then(
                    response => {
                        commit('registerSuccess');
                        return Promise.resolve(response.data);
                    },
                    error => {
                        commit('registerFailure');
                        return Promise.reject(error);
                    }
                );
        },
        refreshToken({commit}, accessToken) {
            commit('refreshToken', accessToken);
        }
    },
    mutations: {
        loginSuccess(state, user) {
            state.status.loggedIn = true;
            state.user = user;
        },
        loginFailure(state) {
            state.status.loggedIn = false;
            state.user = null;
        },
        logout(state) {
            state.status.loggedIn = false;
            state.user = null;
        },
        registerSuccess(state) {
            state.status.loggedIn = false;
        },
        registerFailure(state) {
            state.status.loggedIn = false;
        },
        refreshToken(state, accessToken) {
            state.status.loggedIn = true;
            state.user = {...state.user, accessToken: accessToken};
        },
        updateUsername(state, username) {
            state.user.username = username;
        }
    }
};
