<template>
  <nav class="sb-topnav navbar navbar-expand navbar-dark bg-dark">
    <!-- Navbar Brand-->
    <a class="navbar-brand ps-3" href="/"><img height="24" href="#" src="@/assets/si-logo.svg"></a>
    <!-- Sidebar Toggle-->
    <button class="btn btn-link btn-sm order-1 order-lg-0 me-4 me-lg-0" id="sidebarToggle" href="#!" @click.prevent="$emit('update:sidebarToggle', !sidebarToggle)"><i
        class="fas fa-bars"></i></button>

    <!-- System Status -->
    <!--      <ul class="navbar-nav justify-content-end float-end">
        <li class="nav-item ms-auto me-0 me-sm-3 my-2 my-sm-0">
          <div class="navbar-text">
            <span class="badge">
              &lt;!&ndash;            <i class="bi bi-cone-striped text-warning float-start"></i>&ndash;&gt;
              <i class="bi bi-exclamation-circle-fill text-warning"></i>
            </span>
            System
          </div>
        </li>
        <li class="nav-item ms-auto me-0 me-sm-3 my-2 my-sm-0">
          <div class="navbar-text">
            <span class="badge">
              <i class="bi bi-exclamation-circle-fill text-warning"></i>
            </span>
            Scheduler
          </div>
        </li>
      </ul>-->

    <!-- Navbar Search-->
    <form class="d-none d-md-inline-block form-inline ms-auto me-0 me-md-3 my-2 my-md-0">
      <div class="input-group">
        <input class="form-control-sm form-control-dark" type="text" placeholder="Search for..." aria-label="Search for..."
               aria-describedby="btnNavbarSearch"/>
        <button class="btn btn-primary" id="btnNavbarSearch" type="button"><i class="fas fa-search"></i></button>
      </div>
    </form>
    <!-- Navbar-->
    <ul class="navbar-nav ms-auto ms-md-0 me-3 me-lg-4">
      <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" id="navbarDropdown" href="#" role="button" data-bs-toggle="dropdown"
           aria-expanded="false"><i class="fas fa-user fa-fw"></i></a>
        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
          <li><router-link :to="{ name: 'userDetails', params: { name: currentUser.username, id: currentUser.id }}" class="dropdown-item"><font-awesome-icon icon="user"/>Profile</router-link></li>
          <li><a class="dropdown-item" href="#"><font-awesome-icon icon="cog"/>Settings</a></li>
          <li><hr class="dropdown-divider" /></li>
          <li><a class="dropdown-item" href @click.prevent="$emit('logout');"><font-awesome-icon icon="sign-out-alt"/>Sign Out</a></li>
        </ul>
      </li>
    </ul>
  </nav>
</template>
<script>
export default {
  name: 'TopNav',
  props: ['sidebarToggle', 'currentUser'],
  // data () {
  //   return {
  //     polling: null,
  //     systemStatus: {},
  //     schedulerStatus: null
  //   }
  // },
  // created () {
  //   this.pollData()
  // },
  // beforeUnmount () {
  //   clearInterval(this.polling)
  // },
  // methods: {
  //   pollData () {
  //     this.polling = setInterval(() => {
  //       // this.$store.dispatch('RETRIEVE_DATA_FROM_BACKEND')
  //       this.getServerStatus()
  //       this.getSchedulerStatus()
  //     }, 3000)
  //   },
  //   async getServerStatus() {
  //     await ServerStatusService.getServerStatus().then(
  //         response => {
  //           this.systemStatus = response.data
  //           // this.db_status = this.json.components?.db?.status
  //           // this.ldap = this.json.components?.ldap?.status
  //           // this.solr_master = this.json.components?.solr?.components?.master?.status
  //           // this.solr_slave = this.json.components?.solr?.components?.slave?.status
  //         }
  //     )
  //   },
  //   async getSchedulerStatus() {
  //     await ServerStatusService.getSchedulerStatus().then(response => this.scheduler_status = response.data)
  //   }
  // },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
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

//
// Topnav
//

.sb-topnav {
  padding-left: 0;
  height: $topnav-base-height;
  z-index: $zindex-topnav;

  .navbar-brand {
    width: $sidenav-base-width;
    padding-left: $navbar-padding-x;
    padding-right: $navbar-padding-x;
    margin: 0;
  }

  .form-control {
    padding: .75rem 1rem;
    border-width: 0;
    border-radius: 0;
  }

  .form-control-dark {
    color: #fff;
    background-color: rgba(255, 255, 255, .1);
    border-color: rgba(255, 255, 255, .1);
  }

  .form-control-dark:focus {
    border-color: transparent;
    box-shadow: 0 0 0 3px rgba(255, 255, 255, .25);
  }

  &.navbar-dark {
    #sidebarToggle {
      color: $topnav-dark-toggler-color;
    }
  }

  &.navbar-light {
    #sidebarToggle {
      color: $topnav-light-toggler-color;
    }
  }
}


</style>
