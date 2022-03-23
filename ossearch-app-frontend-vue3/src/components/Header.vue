<template>
  <header class="navbar navbar-expand-sm navbar-dark sticky-top bg-dark flex-md-wrap p-0 shadow">
    <!-- Navbar Brand-->
    <a class="navbar-brand col-md-3 col-lg-2 me-0 px-3" href="#"><img height="24" href="#" src="@/assets/si-logo.svg"></a>
    <button class="navbar-toggler position-absolute d-md-none collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#sidebarMenu" aria-controls="sidebarMenu" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="sidebarMenu">
      <form class="d-sm-inline-block form-inline ms-auto me-0 me-sm-3 my-2 my-sm-0">
        <div class="input-group flex-nowrap">
          <input class="form-control-sm form-control-dark" type="text" placeholder="Search" aria-label="Search" aria-describedby="btnNavbarSearch" />
          <button class="btn btn-primary" id="btnNavbarSearch" type="button"><i class="fas fa-search"></i></button>
        </div>
      </form>
      <ul class="navbar-nav ms-auto ms-sm-0 me-3 me-lg-4">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" id="navbarDropdown" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false"><i class="fas fa-user fa-fw"></i></a>
          <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
            <li><router-link :to="{ name: 'userDetails', params: { name: currentUser.username, id: currentUser.id }}" class="dropdown-item"><font-awesome-icon icon="user"/>Profile</router-link></li>
            <li><a class="dropdown-item" href="#"><font-awesome-icon icon="cog"/>Settings</a></li>
            <li><hr class="dropdown-divider" /></li>
            <li><a class="dropdown-item" href @click.prevent="$emit('logout');"><font-awesome-icon icon="sign-out-alt"/>Sign Out</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </header>

</template>

<script>
export default {
  name: "Header",
  props: ['currentUser', 'showAdminBoard', 'showModeratorBoard'],
  // computed: {
  //   currentUser() {
  //     return this.$store.state.auth.user;
  //   }
  // },
}
</script>

<style lang="scss" scoped>
body {
  font-size: .875rem;
}

/*
 * Navbar
 */
//.navbar {
//  margin-bottom: 20px;
//}

.navbar-brand {
  padding-top: .75rem;
  padding-bottom: .75rem;
  font-size: 1rem;
  background-color: rgba(0, 0, 0, .25);
  box-shadow: inset -1px 0 0 rgba(0, 0, 0, .25);
}

.navbar .navbar-toggler {
  top: .25rem;
  right: 1rem;
}

.navbar .form-control {
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

</style>