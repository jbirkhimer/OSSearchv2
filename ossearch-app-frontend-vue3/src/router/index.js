import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login';
// import Register from '../views/other/Register';
// lazy-loaded
// const Profile = () => import('../views/other/Profile.vue')
// const BoardAdmin = () => import('../views/other/BoardAdmin.vue')
// const BoardModerator = () => import('../views/other/BoardModerator.vue')
// const BoardUser = () => import('../views/other/BoardUser.vue')
const Collections = () => import('../views/collections/Collections')
import CollectionDetails from "../views/collections/CollectionDetails";
import Dashboard from "../views/Dashboard";
import CollectionForm from "../views/collections/CollectionForm";
import UserForm from "../views/users/UserForm";
import UserDetails from "../views/users/UserDetails";
import Reports from "../views/Reports";
import CrawlScheduleDetails from "../views/scheduler/CrawlScheduleDetails";
import CrawlScheduleForm from "../views/scheduler/CrawlScheduleForm";
import CrawlLogs from "../views/scheduler/CrawlLogs";
import CrawlSteps from "../views/scheduler/CrawlSteps";
const CrawlScheduler = () => import('../views/scheduler/CrawlScheduler')
const Search = () => import('../views/Search')
const Users = () => import('../views/users/Users')
const ServerStatus = () => import('../views/ServerStatus')

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    linkActiveClass: "active",
    component: Dashboard
  },
  {
    path: '/dashboard',
    component: Dashboard,
  },
  {
    path: '/login',
    component: Login
  },
  // {
  //   path: '/register',
  //   component: Register
  // },
  // {
  //   path: '/profile',
  //   name: 'profile',
  //   // lazy-loaded
  //   component: Profile
  // },
  // {
  //   path: '/admin',
  //   name: 'admin',
  //   // lazy-loaded
  //   component: BoardAdmin,
  // },
  // {
  //   path: '/mod',
  //   name: 'moderator',
  //   // lazy-loaded
  //   component: BoardModerator,
  // },
  // {
  //   path: '/user',
  //   name: 'user',
  //   // lazy-loaded
  //   component: BoardUser,
  // },
  {
    path: '/collections',
    name: Collections,
    // lazy-loaded
    component: Collections,
  },
  {
    path: '/collections/:name',
    name: 'collectionDetails',
    component: CollectionDetails
  },
  {
    path: '/collections/create',
    name: 'collectionForm',
    component: CollectionForm
  },
  {
    path: '/scheduler',
    name: 'crawlScheduler',
    // lazy-loaded
    component: CrawlScheduler
  },
  {
    path: '/scheduler/create',
    name: 'crawlScheduleForm',
    component: CrawlScheduleForm
  },
  {
    path: '/scheduler/:jobName',
    name: 'crawlScheduleDetails',
    component: CrawlScheduleDetails
  },
  {
    path: '/scheduler/logs/:jobGroup/:jobName',
    name: 'crawlLogs',
    component: CrawlLogs,
    children: [
      {
        // CrawlSteps will be rendered inside CrawlLogs's <router-view>
        // when /scheduler/logs/:jobGroup.:jobName/:jobId is matched
        path: ':jobId',
        name: 'crawlSteps',
        component: CrawlSteps,
      }
    ]
  },
  {
    path: '/reports',
    name: Reports,
    // lazy-loaded
    component: Reports
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
    path: '/users/:name',
    name: 'userDetails',
    component: UserDetails
  },
  {
    path: '/users/create',
    name: 'userForm',
    component: UserForm
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
  linkActiveClass: "active",
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// see the following for auth and navgard info
// https://github.com/vuejs/vue-router/tree/dev/examples/auth-flow
// https://router.vuejs.org/guide/advanced/navigation-guards.html
// https://router.vuejs.org/guide/advanced/meta.html
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
