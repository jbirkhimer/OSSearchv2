<template>
  <div v-if="!currentUser" class="container-fluid">
    <router-view/>
  </div>

  <template v-if="currentUser">
    <Header
        v-if="currentUser"
        :currentUser="currentUser"
        :showAdminBoard="showAdminBoard"
        :showModeratorBoard="showModeratorBoard"
        @logout="logOut()"
        @toggleSidebar="toggleSidebar()"
    />


    <Sidebar/>
    <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
      <router-view/>
      <!--          <div class="container-fluid px-4">
                  <Breadcrumb/>
                  <router-view/>
                </div>-->
<!--      <Footer/>-->
    </main>

  </template>
</template>

<script>
import EventBus from "./common/EventBus"
import Header from "./components/Header";
import Sidebar from "./components/Sidebar";
// import Footer from "./components/Footer";

export default {
  components: {
    Header,
    // Footer,
    Sidebar,
  },
  data() {
    return {
      sbSidenavToggled: null
    }
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    showAdminBoard() {
      if (this.currentUser && this.currentUser['roles']) {
        return this.currentUser['roles'].includes('ROLE_ADMIN');
      }
      return false;
    },
    showModeratorBoard() {
      if (this.currentUser && this.currentUser['roles']) {
        return this.currentUser['roles'].includes('ROLE_MODERATOR');
      }
      return false;
    }
  },
  methods: {
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push({path: '/login'});
    },
    toggleSidebar() {
      if (this.sbSidenavToggled) {
        this.sbSidenavToggled = 'sb-sidenav-toggled'
      } else {
        this.sbSidenavToggled = null
      }

    }
  },
  mounted() {
    EventBus.on("logout", () => {
      this.logOut();
    });
  },
  beforeUnmount() {
    EventBus.remove("logout");
  }
};
</script>

<style lang="scss" scoped>
/*#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}*/

</style>
