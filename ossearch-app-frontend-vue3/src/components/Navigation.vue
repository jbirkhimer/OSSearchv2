<template>
  <nav v-if="currentUser"
       class="navbar navbar-expand-md navbar-dark bg-primary sticky-top"
       aria-label="navbar">

    <div class="container-fluid">

      <a href class="navbar-brand float-left" @click.prevent>
        <img class="float-left" href="#" src="@/assets/logo-si.png">
      </a>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse"
              aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarCollapse">

        <ul class="navbar-nav me-auto mb-2 mb-md-0 text-center">
          <li class="nav-item">
            <router-link to="/home" class="nav-link"><font-awesome-icon icon="home"/>Home</router-link>
          </li>

          <li v-if="showAdminBoard" class="nav-item">
            <router-link to="/collections" class="nav-link">Collections</router-link>
          </li>

          <li v-if="showAdminBoard" class="nav-item">
            <router-link to="/crawler" class="nav-link">Crawler</router-link>
          </li>

          <li v-if="showAdminBoard" class="nav-item">
            <router-link to="/search" class="nav-link">Search</router-link>
          </li>

          <li v-if="showAdminBoard" class="nav-item">
            <router-link to="/users" class="nav-link">Users</router-link>
          </li>

          <li v-if="showAdminBoard" class="nav-item">
            <router-link to="/admin" class="nav-link">Admin Board</router-link>
          </li>

          <!--            <li v-if="showModeratorBoard" class="nav-item">
                        <router-link to="/mod" class="nav-link">Moderator Board</router-link>
                      </li>

                      <li class="nav-item">
                        <router-link v-if="currentUser" to="/user" class="nav-link">User Content</router-link>
                      </li>-->

          <li class="nav-item">
            <router-link v-if="currentUser" to="/about" class="nav-link">About</router-link>
          </li>

          <li class="nav-item">
            <router-link to="/backend" class="nav-link position-relative">
              Backend Status
              <span
                  class="badge rounded-pill bg-danger bi-exclamation-triangle-fill position-absolute top-0 left-100 translate-middle"><span
                  class="visually-hidden">backend status</span></span>
            </router-link>

          </li>

          <!--            <router-link to="/home" class="nav-item nav-link"><font-awesome-icon icon="home"/>Home</router-link>
                      <router-link v-if="showAdminBoard" to="/admin" class="nav-item nav-link">Admin Board</router-link>
                      <router-link v-if="showModeratorBoard" to="/mod" class="nav-item nav-link">Moderator Board</router-link>
                      <router-link v-if="currentUser" to="/user" class="nav-item nav-link">User</router-link>-->
        </ul>

        <div v-if="!currentUser" class="navbar-nav float-right text-center">
          <li class="nav-item">
            <router-link to="/register" class="nav-link">
              <font-awesome-icon icon="user-plus"/>
              Sign Up
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/login" class="nav-link">
              <font-awesome-icon icon="sign-in-alt"/>
              Login
            </router-link>
          </li>
        </div>

        <div v-if="currentUser" class="navbar-nav float-right text-center">
          <li class="nav-item">
            <router-link to="/profile" class="nav-link">
              <font-awesome-icon icon="user"/>
              {{ currentUser.username }}
            </router-link>
          </li>
          <li class="nav-item">
            <a class="nav-link" href @click.prevent="$emit('logout');">
              <font-awesome-icon icon="sign-out-alt"/>
              LogOut
            </a>
          </li>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: "Navigation",
  props: ['currentUser', 'showAdminBoard', 'showModeratorBoard']
}
</script>

<style scoped>

</style>