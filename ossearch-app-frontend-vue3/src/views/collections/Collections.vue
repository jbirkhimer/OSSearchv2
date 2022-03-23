<template>

  <div class="container-fluid px-4">
<!--    <h1 class="mt-4">Overview</h1>-->
    <Breadcrumb/>

    <div class="card mb-4">
      <div class="card-header">
        <i class="fas fa-sitemap me-1"></i>
        Collections
        <div class="float-end">
          <router-link type="button" class="btn btn-sm btn-primary bi-plus-lg float-end" to="/collections/create">
            Collection
          </router-link>
        </div>
      </div>

      <div class="card-body">
        <div v-if="loading" class="d-flex justify-content-center">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>
        <Datatable v-if="!loading"
            :tableData="items"
            :tableOptions="tableOptions"
                   id="collectionsTable"
        >
          <template v-slot:table-body>
            <tr v-for="(item, i) in items" :key="i">
              <td>{{ item.id }}</td>
              <td><router-link :to="{ name: 'collectionDetails', params: { name: item.name, id: item.id }}">{{ item.name }}</router-link></td>
              <!--          <td>{{ item.description }}</td>-->
              <td>{{ item.crawlConfig.crawlCronSchedule }}</td>
              <td>
                <template v-if="item.owner">{{ item.owner.firstName }} {{ item.owner.lastName }}</template>
                <template v-else>None</template>
              </td>
              <td>
                <template v-if="item.users">
                  <div v-for="(user, i) in item.users" :key="i">
                    {{ user.firstName }} {{ user.lastName }}
                  </div>
                </template>
                <template v-else>None</template>
              </td>
              <td>{{ item.dateCreated }}</td>
              <td class="justify-content-evenly text-center">
                <div class="btn-group btn-group-sm align-items-center">
                  <router-link class="btn link-primary p-0 m-1" :to="{ name: 'collectionDetails', params: { name: item.name, id: item.id }}"><i class="fa fa-edit"></i></router-link>
                  <a href="#" class="btn link-danger p-0" data-bs-toggle="modal" data-bs-target="#deleteCollectionModal" title="Delete" @click="selectCollection(item)">
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

export default {
  name: "Collections",
  components: {
    Breadcrumb,
    Datatable,
    Modal,
  },
  mounted() {
    this.getCollections();
  },
  data() {
    return {
      loading: false,
      error: null,
      tableOptions: {
        order: [[0, "asc"]],
        columns: [
          {label: 'Id', name: 'id'},
          {label: 'Name', name: 'name'},
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
          {label: 'Crawl Cron Schedule', name: 'crawlCronSchedule'},
          {label: 'Owner', name: 'owner'},
          {label: 'Managers', name: 'users'},
          {label: 'Created', name: 'dateCreated'},
          {label: 'Actions', name: 'Created', class: 'text-center'},
        ],
      },
      items: [],
      selectedCollection: {},
      itemIndex: null
    }
  },
  methods: {
    async getCollections() {
      this.loading = true

      await CollectionService.getCollections('/collection', {size: 1000, projection: 'collectionTableData'})
          .then(response => {
            this.items = response.data._embedded.collection;
            this.loading = false
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
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
            this.getCollections();
            this.selectedCollection = null
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
      this.getCollections()
    },
    selectCollection(collection) {
      //console.log("selected collection", JSON.stringify(collection, null,2))
      this.selectedCollection = collection
    }
  }
}
</script>

<style scoped lang="scss">

</style>