<template>

  <div class="container-fluid px-4">
<!--    <h1 class="mt-4">Overview</h1>-->
    <Breadcrumb/>

    <div class="card mb-4">
      <div class="card-header">
        <i class="fas fa-sitemap me-1"></i>
        <b>Collections</b>
        <div v-if="isAdmin" class="float-end">
          <router-link type="button" class="btn btn-sm btn-primary bi-plus-lg float-end" to="/collections/create">
            Collection
          </router-link>
        </div>
      </div>

      <div class="card-body">
        <Datatable :loading="loading"
            :tableData="items"
            :tableOptions="tableOptions"
                   id="collectionsTable"
        >
          <template v-slot:table-body>
            <tr v-for="(item, i) in items" :key="i">
              <td><router-link :to="{ name: 'collection', params: { name: item.name, id: item.id }}">{{ item.name }}</router-link></td>
              <!--          <td>{{ item.description }}</td>-->
              <td>{{ getCollectionSolrCount(item.name).toLocaleString() }}</td>
              <td>
                <template v-if="item.users">
                  <div v-for="(user, i) in item.users" :key="i">
                    {{ user.firstName }} {{ user.lastName }}
                  </div>
                </template>
                <template v-else>None</template>
              </td>
              <td>{{ explain(item.crawlConfig.crawlCronSchedule) }}</td>
              <td>
                <template v-if="item.owner">{{ item.owner.firstName }} {{ item.owner.lastName }}</template>
                <template v-else>None</template>
              </td>
              <td>{{ item.dateCreated }}</td>
              <td>{{ item.id }}</td>
              <td class="justify-content-evenly text-center">
                <div class="btn-group btn-group-sm align-items-center">
                  <router-link class="btn link-primary p-0 m-1" :to="{ name: 'collection', params: { name: item.name, id: item.id}}"><i class="fa fa-edit"></i></router-link>
                  <a v-if="isAdmin" href="#" class="btn link-danger p-0" data-bs-toggle="modal" data-bs-target="#deleteCollectionModal" title="Delete" @click="selectCollection(item)">
                    <i class="fa fa-times-circle"></i>
                  </a>
                </div>
              </td>
            </tr>
          </template>
        </Datatable>
      </div>
    </div>

    <!-- Modal -->
    <Modal v-if="selectedCollection" id="deleteCollectionModal">
      <template v-slot:header>
        <h5 class="modal-title text-black">Delete Collection</h5>
      </template>
      <template v-slot:body>
        <p>Are you sure you want to delete collection <b>{{ selectedCollection.name }}</b>!</p>
        <p class="text-danger"><b>This can not be undone!</b></p>
      </template>
      <template v-slot:button-action>
        <button type="button" class="btn btn-danger" data-bs-dismiss="modal"
                @click.prevent="deleteCollection(selectedCollection._links.self.href)">Delete
        </button>
      </template>
    </Modal>

  </div>

</template>

<script>
import Datatable from "../../components/table/Datatable";
import Modal from "../../components/Modal";
import Breadcrumb from "../../components/Breadcrumb";
import CollectionService from "../../services/collection.service"
import EventBus from "../../common/EventBus";
import cronstrue from 'cronstrue';
import ServerStatusService from "../../services/server-status.service";

export default {
  name: "Collections",
  components: {
    Breadcrumb,
    Datatable,
    Modal,
  },
  async mounted() {
    this.loading = true
    await this.getCollections();
    await this.getSolrCollectionCounts()
    this.loading = false
  },
  data() {
    return {
      loading: false,
      error: null,
      tableOptions: {
        order: [[0, "asc"]],
        columns: [
          {label: 'Collection Name', name: 'name'},
          {label: 'Indexed URLs', name: 'urlCount'},
          // {label: 'Description', name: 'description'},
          // {label: 'Site url', name: 'siteUrl'},
          // {label: 'Included site urls', name: 'includedSiteUrls'},
          // {label: 'Sitemap url', name: 'sitemapUrl'},
          // {label: 'Include site map urls', name: 'includeSiteMapUrls'},
          // {label: 'Use sitemap', name: 'useSitemap'},
          // {label: 'Response type', name: 'responseType'},
          // {label: 'Results per page', name: 'resultsPerPage'},
          // {label: 'Result limit', name: 'resultLimit'},
          // {label: 'Exclude urls', name: 'excludeUrls'},
          // {label: 'Url exclusion patterns', name: 'urlExclusionPatterns'},
          // {label: 'Exclude sitemap urls', name: 'excludeSitemapUrls'},
          // {label: 'Exclude duplicate results', name: 'excludeDuplicateResults'},
          // {label: 'Include fields', name: 'includeFields'},
          // {label: 'Required field', name: 'requiredField'},
          // {label: 'Keywords', name: 'keywords'},
          // {label: 'Metadata base', name: 'metadataBase'},
          // {label: 'Included collections', name: 'includedCollections'},
          // {label: 'Part of collections', name: 'partOfCollections'},
          // {label: 'Crawdb path', name: 'crawDbPath'},
          {label: 'Managers', name: 'users'},
          {label: 'Crawl Schedule', name: 'crawlCronSchedule'},
          {label: 'Created By', name: 'owner'},
          {label: 'Created Date', name: 'dateCreated'},
          {label: 'ID', name: 'id'},
          {label: 'Actions', name: 'Created', class: 'text-center'},
        ],
      },
      items: [],
      selectedCollection: {},
      itemIndex: null
    }
  },
  watch: {
    error: {
      deep: true,
      handler: function () {
        let content = (this.error.response && this.error.response.data && this.error.response.data.message) || this.error.message || this.error.toString();
        if (this.error.response && this.error.response.status === 403) {
          EventBus.dispatch("logout");
        } else {
          alert("ERROR: " + content)
        }
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
    }
  },
  methods: {
    async getCollections() {
      await CollectionService.getCollections('/collection', {size: 1000, projection: 'collectionTableData'})
          .then(response => {
            console.log(">>>>>>collections", response.data);
            this.items = response.data._embedded.collection;

          })
          .catch(errors => {
            console.log(">>>>>>collections", errors);
            this.error = errors
          });
    },
    async getSolrCollectionCounts() {
      await ServerStatusService.getSolrCollectionCounts()
          .then(response => {
            this.solrCounts = response.data.data;
            /*this.solrCount = this.solrCounts.reduce((accumulator, object) => {
              return accumulator + object.count;
            }, 0);*/
          })
          .catch(errors => {
            this.error = errors
          })
    },
    getCollectionSolrCount(name) {
      let collectionSolrCount = this.solrCounts.find(x => x.name === name)
      if (collectionSolrCount) {
        return collectionSolrCount.count
      }
      return 0
    },
    deleteCollection(url) {
      CollectionService.deleteCollection(url)
          .then(response => {
            console.log("deleteCollection response:", response.data)
            // for (let i = 0; i < this.tableData.length; i++) {
            //   if (this.tableData[i].id == res.data) {
            //     this.tableData.splice(i, 1);
            //   }
            // }

            // this.getCollections();
            this.items.splice(this.items.findIndex(({id}) => id == this.selectedCollection.id), 1);
            this.selectedCollection = null
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
      // this.getCollections()
    },
    selectCollection(collection) {
      //console.log("selected collection", JSON.stringify(collection, null,2))
      this.selectedCollection = collection
    },
    explain(cronExpression) {
      return cronstrue.toString(cronExpression, {
        verbose: true,
        use24HourTimeFormat: true,
        dayOfWeekStartIndexZero: false
      })
    },
  }
}
</script>

<style scoped lang="scss">

</style>
