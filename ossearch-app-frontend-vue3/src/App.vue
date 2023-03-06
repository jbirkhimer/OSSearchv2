<template>
  <div v-if="!currentUser" class="container-fluid">
    <router-view/>
  </div>

  <template v-if="currentUser">
    <div class="sb-nav-fixed" :class="this.sidebarToggle ? 'sb-sidenav-toggled' : ''">
      <TopNav
          v-if="currentUser"
          v-model:sidebarToggle="sidebarToggle"
          :currentUser="currentUser"
          @logout="logOut()"
      />
      <div id="layoutSidenav">
        <div id="layoutSidenav_nav">
          <SideNav :currentUser="currentUser" :isAdmin="isAdmin"/>
        </div>
        <div id="layoutSidenav_content">
          <main>
            <router-view/>
            <ToastContainer :toasts="toasts"/>
          </main>
          <Footer/>
        </div>
      </div>
    </div>

  </template>
</template>

<script>
import EventBus from "./common/EventBus";

import api from "./services/api";
import ToastContainer from "./components/ToastContainer";
import TopNav from "./components/TopNav";
import SideNav from "./components/SideNav";
import Footer from "./components/Footer";

export default {
  components: {
    ToastContainer,
    TopNav,
    SideNav,
    Footer
  },
  data() {
    return {
      sidebarToggle: false,
      toasts: []
    }
  },
  created() {
    // To persist sidebar toggle between refreshes
    if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
      this.sidebarToggle = true
    }
  },
  mounted() {
    EventBus.on("logout", () => {
      this.logOut();
    });
    EventBus.on("toast", (data) => {
      data.id = Date.now().toString(36) + Math.random().toString(36).substring(2)
      this.toasts.push(data)
    });
  },
  beforeUnmount() {
    EventBus.remove("logout");
    EventBus.remove("toast");
  },
  watch: {
    sidebarToggle: {
      deep: true,
      handler: function () {
        localStorage.setItem('sb|sidebar-toggle', this.sidebarToggle)
      }
    }
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    isAdmin() {
      if (this.currentUser && this.currentUser['roles']) {
        return this.currentUser['roles'].includes('ROLE_ADMIN');
      }
      return false;
    },
    isModerator() {
      if (this.currentUser && this.currentUser['roles']) {
        return this.currentUser['roles'].includes('ROLE_MODERATOR');
      }
      return false;
    }
  },
  methods: {
    async logOut() {
      await api.post("/auth/logout", {userId: this.$store.state.auth.user.id});
      this.$store.dispatch('auth/logout');
      sessionStorage.setItem('redirectPath', this.$route.path);
      await this.$router.push({path: '/login'});
    },
  }
};
</script>

<style lang="scss">
@import "vue-multiselect/dist/vue-multiselect.css";
@import "@/assets/styles/scss/vue-multiselect-bootstrap5.scss";

// Import Bootstrap variables for use within theme
@import "~bootstrap/scss/functions.scss";
@import "~bootstrap/scss/variables.scss";

//
// Spacing
//

// Spacing variables for navigation
$topnav-base-height: 56px;
$sidenav-base-width: 225px;

//
// Navigation
//

// Z index variables
$zindex-content: 1037 !default;
$zindex-sidenav: 1038 !default;
$zindex-topnav: 1039 !default;

// Color variables for the sidenav

// -- Sidenav Dark
$sidenav-dark-bg: $gray-900;
$sidenav-dark-color: fade-out($white, 0.5);
$sidenav-dark-heading-color: fade-out($white, 0.75);
$sidenav-dark-link-color: fade-out($white, 0.5);
$sidenav-dark-link-active-color: $white;
$sidenav-dark-icon-color: fade-out($white, 0.75);
$sidenav-dark-footer-bg: $gray-800;

// -- Sidenav Light
$sidenav-light-bg: $gray-100;
$sidenav-light-color: $gray-900;
$sidenav-light-heading-color: $gray-500;
$sidenav-light-link-color: $sidenav-light-color;
$sidenav-light-link-active-color: $primary;
$sidenav-light-icon-color: $gray-500;
$sidenav-light-footer-bg: $gray-200;

// Color variables for the topnav
$topnav-dark-toggler-color: fade-out($white, 0.5);
$topnav-light-toggler-color: $gray-900;

// Import Bootstrap
@import "bootstrap/scss/bootstrap.scss";

#app {
  //font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

//
// Fixed dashboard layout
//

.sb-nav-fixed {
  .sb-topnav {
    @extend .fixed-top;
    z-index: $zindex-topnav;
  }

  #layoutSidenav {
    #layoutSidenav_nav {
      @extend .fixed-top;
      width: $sidenav-base-width;
      height: 100vh;
      z-index: $zindex-sidenav;

      .sb-sidenav {
        padding-top: $topnav-base-height;

        .sb-sidenav-menu {
          overflow-y: auto;
        }
      }
    }

    #layoutSidenav_content {
      padding-left: $sidenav-base-width;
      top: $topnav-base-height;
    }
  }
}

//
// Default layout
//

// Default behavior for the sidenav layout
// The default positioning for the sidenav is a static position

#layoutSidenav {
  display: flex;

  // Wraps the .sb-sidenav element and sets the size
  #layoutSidenav_nav {
    flex-basis: $sidenav-base-width;
    flex-shrink: 0;
    transition: transform 0.15s ease-in-out;
    z-index: $zindex-sidenav;
    // Mobile first transform - by default the sidenav will be moved off-canvas
    transform: translateX(-$sidenav-base-width);
  }

  // Wraps the content when using the sidenav layout
  #layoutSidenav_content {
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-width: 0;
    flex-grow: 1;
    min-height: calc(100vh - #{$topnav-base-height});
    margin-left: -$sidenav-base-width;
  }
}

// Default behavior for the static sidenav collapse
.sb-sidenav-toggled {
  #layoutSidenav {
    #layoutSidenav_nav {
      transform: translateX(0);
    }

    #layoutSidenav_content {
      &:before {
        content: "";
        display: block;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: $black;
        z-index: $zindex-content;
        opacity: 0.5;
        transition: opacity 0.3s ease-in-out;
      }
    }
  }
}

// Responsive styling for the sidenav layout
@include media-breakpoint-up(lg) {
  #layoutSidenav {
    #layoutSidenav_nav {
      transform: translateX(0);
    }

    #layoutSidenav_content {
      margin-left: 0;
      transition: margin 0.15s ease-in-out;
    }
  }

  // Behavior for the sidenav collapse on screens larger than the med breakpoint
  .sb-sidenav-toggled {
    #layoutSidenav {
      #layoutSidenav_nav {
        transform: translateX(-$sidenav-base-width);
      }

      #layoutSidenav_content {
        margin-left: -$sidenav-base-width;

        // Removes the sidenav overlay on screens larger than the med breakpoint
        &:before {
          display: none;
        }
      }
    }
  }
}

//
// Nav
//

// Add styling for icons used within nav links
.nav,
.sb-sidenav-menu {
  .nav-link .sb-nav-link-icon {
    margin-right: 0.5rem;
  }
}

</style>
