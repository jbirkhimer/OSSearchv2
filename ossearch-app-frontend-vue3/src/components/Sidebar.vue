<template>

  <nav class="col-md-3 col-lg-2 d-md-block bg-dark sidebar collapse" id="sidebarMenu">
    <div class="position-sticky pt-3">
      <ul class="nav flex-column mb-auto list-group">

        <li class="nav-item">
          <router-link class="nav-link" to="/dashboard">
            <i class="fas fa-tachometer-alt me-2"></i>
            Dashboard
          </router-link>
        </li>

<!--        <li class="nav-item">
          <router-link to="/collections" class="nav-link collapsed" @click.prevent="this.$router.push({path: '/collections'})" data-bs-toggle="collapse" data-bs-target="#collapseCollections" aria-expanded="false" aria-controls="collapseCollections">
            <i class="fas fa-sitemap me-2"></i>
            Collections
            <i class="fas fa-angle-down float-end sidenav-collapse-arrow"></i>
          </router-link>
        </li>
        <div class="collapse" id="collapseCollections">
          <ul class="nav flex-column mb-auto">
            <template v-for="(collection, i) in collections" :key="i">
              <li class="nav-item">
                <router-link class="nav-link ms-3" :to="{ name: 'collectionDetails', params: { name: collection.name, id: collection.id }}">
                  {{ collection.name }}
                </router-link>
              </li>
            </template>
          </ul>
        </div>-->

        <li class="nav-item">
          <router-link class="nav-link" to="/collections">
            <i class="fas fa-sitemap me-2"></i>
            Collections
<!--            <router-link type="button" class="link-light float-end" to="/collections/create">
              <i class="fas fa-plus-circle text-light"></i>
            </router-link>-->
          </router-link>
        </li>

        <li class="nav-item">
          <router-link class="nav-link" to="/scheduler">
            <i class="fas fa-spider me-2"></i>
            Crawl Scheduler
          </router-link>
        </li>

        <li class="nav-item">
          <router-link class="nav-link" to="/search">
            <i class="fas fa-search me-2"></i>
            Search
<!--            <span class="badge rounded-pill bg-warning text-dark float-end">comming soon!</span>-->
            <i class="bi bi-cone-striped text-warning float-end"></i>
          </router-link>
        </li>

        <li class="nav-item">
          <router-link class="nav-link" to="/reports">
            <i class="fas fa-chart-area me-2"></i>
            Reports
<!--            <span class="badge rounded-pill bg-warning text-dark float-end">comming soon!</span>-->
            <i class="bi bi-cone-striped text-warning float-end"></i>
          </router-link>
        </li>

        <li class="nav-item">
          <router-link class="nav-link" to="/users">
            <i class="fas fa-users me-2"></i>
            Users
          </router-link>
        </li>

        <li class="nav-item">
          <router-link class="nav-link" to="/backend">
            <i class="fas fa-server me-2"></i>
            System
          </router-link>
        </li>

        <li class="nav-item">
          <router-link class="nav-link" to="/about">
            <i class="fas fa-question me-2"></i>
            About
          </router-link>
        </li>

      </ul>
    </div>
  </nav>
</template>

<script>
import CollectionService from "../services/collection.service";

export default {
  name: "Sidebar",
  data() {
    return {
      collections: {},
      collectionsVisable: false
    }
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  mounted() {
    // this.getCollections()
  },
  methods: {
    async getCollections(url = '/collection', params = {size: 100, projection: 'collectionFormData'}) {
      //console.log("[getCollections] url: " + url + ", prams: " + JSON.stringify(params), null, 2)

      // this.tableData.draw++;
      await CollectionService.getCollections(url, params)
          .then(response => {
            let data = response.data;
            this.collections = data._embedded.collection;
          })
          .catch(errors => {
            console.log(errors);
          });
    },
  }
}
</script>

<style lang="scss" scoped>
@import '~bootstrap/scss/_functions';
@import '~bootstrap/scss/_variables';
@import '~bootstrap/scss/mixins/_breakpoints';

// -- Sidenav Dark
$sidenav-dark-link-color: fade-out($white, 0.5);
$sidenav-dark-link-active-color: $white;
$sidenav-dark-icon-color: fade-out($white, 0.75);

/*
 * Sidebar
 */

.sidebar {
  position: fixed;
  top: 0;
  /* rtl:raw:
  right: 0;
  */
  bottom: 0;
  /* rtl:remove */
  left: 0;
  z-index: 100; /* Behind the navbar */
  padding: 48px 0 0; /* Height of navbar */
  box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1);
}

@media (max-width: 767.98px) {
  .sidebar {
    top: 5rem;
  }
}

.sidebar-sticky {
  position: relative;
  top: 0;
  height: calc(100vh - 48px);
  padding-top: .5rem;
  overflow-x: hidden;
  overflow-y: auto; /* Scrollable contents if viewport is shorter than content. */
}

.sidebar .nav-link {
  color: $sidenav-dark-link-color;

  .fas {
    color: $sidenav-dark-icon-color;
  }

  .sidenav-collapse-arrow {
    display: inline-block;
    margin-left: auto;
    transition: transform 0.15s ease;
    color: $sidenav-dark-icon-color;
  }

  &.collapsed {
    .sidenav-collapse-arrow {
      transform: rotate(-90deg);
    }
  }

  &:hover {
    color: $sidenav-dark-link-active-color;
  }

  &.active {
    color: $sidenav-dark-link-active-color;

    .fas {
      color: $sidenav-dark-link-active-color;
    }
  }
}

</style>
