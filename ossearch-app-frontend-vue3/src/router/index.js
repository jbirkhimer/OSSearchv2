import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login';
import Register from '../views/Register';
// lazy-loaded
const Profile = () => import('../views/Profile.vue')
const BoardAdmin = () => import('../views/BoardAdmin.vue')
const BoardModerator = () => import('../views/BoardModerator.vue')
const BoardUser = () => import('../views/BoardUser.vue')
const Collections = () => import('../views/Collections')
const Crawler = () => import('../views/Crawler')
const Search = () => import('../views/Search')
const Users = () => import('../views/Users')
const ServerStatus = () => import('../views/ServerStatus')

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/home',
    component: Home,
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/register',
    component: Register
  },
  {
    path: '/profile',
    name: 'profile',
    // lazy-loaded
    component: Profile
  },
  {
    path: '/admin',
    name: 'admin',
    // lazy-loaded
    component: BoardAdmin,
  },
  {
    path: '/mod',
    name: 'moderator',
    // lazy-loaded
    component: BoardModerator,
  },
  {
    path: '/user',
    name: 'user',
    // lazy-loaded
    component: BoardUser,
  },
  {
    path: '/collections',
    name: Collections,
    // lazy-loaded
    component: Collections
  },
  {
    path: '/crawler',
    name: Crawler,
    // lazy-loaded
    component: Crawler
  },
  {
    path: '/search',
    name: Search,
    // lazy-loaded
    component: Search
  },
  {
    path: '/users',
    name: Users,
    // lazy-loaded
    component: Users
  },
  {
    path: '/backend',
    name: 'backend',
    // lazy-loaded
    component: ServerStatus
  },
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: 'about' */ '../views/About.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  // const publicPages = ['/login', '/register', '/home'];
  const publicPages = ['/login', '/register'];
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = localStorage.getItem('user');

  // trying to access a restricted page + not logged in
  // redirect to login page
  if (authRequired && !loggedIn) {
    next('/login');
  } else {
    next();
  }
});

export default router
