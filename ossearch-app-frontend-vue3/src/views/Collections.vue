<template>
  <div class="collections-wrapper">
    <div class="collections-title">
      <div class="row">

        <div class="col-sm-4">
          <h2><b>Collection Manager</b></h2>
        </div>

        <div class="col-sm-8">
          <!-- Button trigger modal -->
          <button type="button" class="btn btn-primary bi-plus-lg" data-bs-toggle="modal" data-bs-target="#addModal">
            Collection
          </button>
        </div>

      </div>
    </div>


    <Datatable :tableOptions="tableOptions"
               @sort="sortBy"
               @rowsPerPage="getSize"
               @search="getCollections()"
               @pagination="getCollections($event.url, $event.params)"
    >
      <!--      <template v-slot:table-head>

            </template>-->
      <template v-slot:table-body>
        <tr v-for="(item, i) in items" :key="i">
<!--          <td>{{ item.id }}</td>-->
          <td>{{ item.name }}</td>
          <td>{{ item.description }}</td>
          <td>{{ item.siteUrl }}</td>
          <td>{{ item.responseType }}</td>
          <td>{{ item.resultsPerPage }}</td>
          <td>{{ item.resultLimit }}</td>
          <td class="justify-content-evenly text-center">
            <!--          <a href="#" class="view" title="View" data-toggle="tooltip"><i class="bi-eye-fill"></i></a>-->
            <!--          <a href="#" class="edit link-primary" title="Edit" @click="isEditModalVisible === true && edit(item._links.self.href, $event.target)">-->
            <a href="#" class="edit link-primary" title="Edit" data-bs-toggle="modal" data-bs-target="#editModal" @click="selectCollection(item, i)">
              <i class="bi-pencil-square text-primary"></i> Edit</a> |
            <a href="#" class="delete link-danger" data-bs-toggle="modal" data-bs-target="#deleteModal" title="Delete"
               @click="selectCollection(item, i)">
              <i class="bi-x-square text-danger"></i> Delete</a>
          </td>
        </tr>
      </template>
    </Datatable>

    <Modal id="addModal" @cancel="step = 1">
      <template v-slot:header>
        <h5 v-if="step === 1" class="modal-title text-black">Basic Information</h5>
        <h5 v-if="step === 2" class="modal-title text-black">Search Configuration</h5>
        <h5 v-if="step === 3" class="modal-title text-black">Search Dynamic Navigation Configuration</h5>
        <h5 v-if="step === 4" class="modal-title text-black">Search Keymatch Configuration</h5>
        <h5 v-if="step === 5" class="modal-title text-black">Search Include Other Collections</h5>
        <h5 v-if="step === 6" class="modal-title text-black">Crawl Configuration</h5>
        <h5 v-if="step === 7" class="modal-title text-black">Crawl Schedule</h5>
        <h5 v-if="step === 8" class="modal-title text-black">Managers</h5>
        <h5 v-if="step === 9" class="modal-title text-black">Review</h5>
      </template>
      <template v-slot:body>
        <CollectionForm :step="step" v-model:collectionData="collectionData"/>
      </template>
      <template v-slot:button-action>
        <template id="step-1_add" v-if="step === 1">
          <button type="button" class="btn btn-secondary" @click.prevent="next()">Next</button>
        </template>
        <template id="step-2-8_add" v-if="step < 9 && step != 1">
          <button type="button" class="btn btn-secondary" @click.prevent="prev()">Previous</button>
          <button type="button" class="btn btn-secondary" @click.prevent="next()">Next</button>
        </template>
        <template id="step-9_add" v-if="step === 9">
          <button type="button" class="btn btn-secondary" @click.prevent="prev()">Previous</button>
          <button type="button" class="btn btn-danger" data-bs-dismiss="modal" @click="addCollection()">Create
          </button>
        </template>
      </template>
    </Modal>

    <Modal id="editModal" @cancel="step = 1">
      <template v-slot:header>
        <h5 v-if="step === 1" class="modal-title text-black">Basic Information</h5>
        <h5 v-if="step === 2" class="modal-title text-black">Search Configuration</h5>
        <h5 v-if="step === 3" class="modal-title text-black">Search Dynamic Navigation Configuration</h5>
        <h5 v-if="step === 4" class="modal-title text-black">Search Keymatch Configuration</h5>
        <h5 v-if="step === 5" class="modal-title text-black">Search Include Other Collections</h5>
        <h5 v-if="step === 6" class="modal-title text-black">Crawl Configuration</h5>
        <h5 v-if="step === 7" class="modal-title text-black">Crawl Schedule</h5>
        <h5 v-if="step === 8" class="modal-title text-black">Managers</h5>
        <h5 v-if="step === 9" class="modal-title text-black">Review</h5>
      </template>
      <template v-slot:body>
        <CollectionForm :step="step" v-model:collectionData="collectionData" v-model:selectedCollection="selectedCollection"/>
      </template>
      <template v-slot:button-action>
        <template id="step-1_edit" v-if="step === 1">
          <button type="button" class="btn btn-secondary" @click.prevent="next()">Next</button>
        </template>
        <template id="step-2-8_edit" v-if="step < 9 && step != 1">
          <button type="button" class="btn btn-secondary" @click.prevent="prev()">Previous</button>
          <button type="button" class="btn btn-secondary" @click.prevent="next()">Next</button>
        </template>
        <template id="step-9_edit" v-if="step === 9">
          <button type="button" class="btn btn-secondary" @click.prevent="prev()">Previous</button>
          <button type="button" class="btn btn-danger" data-bs-dismiss="modal" @click.prevent="addCollection()">Update
          </button>
        </template>
      </template>
    </Modal>

    <Modal id="deleteModal" @cancel="step = 1">
      <template v-slot:header>
        <h5 class="modal-title text-black">Delete Collection</h5>
      </template>
      <template v-slot:body>
        <p>Are you sure you want to delete collection <b>{{ collectionData.name }}</b>! This
          can not be undone!</p>
      </template>
      <template v-slot:button-action>
        <template name="button-action">
          <button type="button" class="btn btn-danger" data-bs-dismiss="modal"
                  @click.prevent="deleteCollection(collectionData.collection._links.self.href, itemIndex)">Delete
          </button>
        </template>
      </template>
    </Modal>
  </div>
</template>

<script>
import Datatable from "../components/table/Datatable";
import Modal from "../components/Modal";
import CollectionForm from "../components/forms/CollectionForm";
import CollectionService from "../services/collection.service"

export default {
  name: "Collections",
  components: {
    Datatable,
    Modal,
    CollectionForm,
  },
  created() {
    this.getCollections();
  },
  data() {
    let sortOrders = {};
    let columns = [
      // {label: 'Id', name: 'id'},
      {label: 'Name', name: 'name'},
      {label: 'Description', name: 'description'},
      {label: 'Site url', name: 'siteUrl'},
      // {label: 'Included site urls', name: 'includedSiteUrls'},
      // {label: 'Sitemap url', name: 'sitemapUrl'},
      // {label: 'Include site map urls', name: 'includeSiteMapUrls'},
      // {label: 'Use sitemap', name: 'useSitemap'},
      {label: 'Response type', name: 'responseType'},
      {label: 'Results per page', name: 'resultsPerPage'},
      {label: 'Result limit', name: 'resultLimit'},
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
      // {label: 'Creator', name: 'creator'},
      // {label: 'Users', name: 'users'},
      // {label: 'Date created', name: 'dateCreated'},

    ];
    columns.forEach((column) => {
      sortOrders[column.name] = -1; // sort descending by default
    });
    return {
      tableOptions: {
        columns: columns,
        sortKey: 'name',
        sortOrders: sortOrders,
        rowsPerPageSelect: ['10', '25', '50', '75', '100'],
        rowsPerPage: 10,
        search: '',
        sort: '',
        column: '',
        dir: '',
        pagination: {
          _links: '',
          page: ''
        },
      },
      step: 1,
      items: [],
      collectionData: {
        id: null,
        name: '',
        description: '',
        siteUrl: '',
        keywords: [],
        crawlDbPath: '',
        crawlSeedPath: '',
        includeSiteUrls: [],
        excludeSiteUrls: [],
        urlExclusionPatterns: [],
        sitemapUrl: '',
        useSitemap: true,
        includeSitemapUrls: [],
        excludeSitemapUrls: [],
        responseType: 'html',
        resultsPerPage: 10,
        resultLimit: 0,
        includeFields: ['title', 'content', 'url'],
        useFacets: false,
        dynamicNavigations: [],
        keymatches: [],
        includedCollections: [],
        users: [],
        owner: {},
        crawlCronSchedule: '0 0 0 ? * FRI *',
        cronEditorData: "{\"name\":\"Weekly\",\"editorData\":{\"dateTime\":\"00:00\",\"daysOfWeek\":[6]}}"
      },
      selectedCollection: {},
      itemIndex: null
    }
  },
  methods: {
    async getCollections(url = '/collection', params = {projection: 'collectionFormData'}) {
      console.log("[getCollections] url: " + url + ", prams: " + JSON.stringify(params), null, 2)

      // this.tableData.draw++;
      await CollectionService.getCollections(url, params)
          .then(response => {
            let data = response.data;

            //used during searching to get the last request we sent
            /*if (this.tableData.draw == data.draw) {
              this.items = data._embedded.collection;
              this.configPagination(data.data);
            }*/

            let collections = data._embedded.collection;
            // console.log('collection', collections)
            //
            // collections.forEach(collection => {
            //   collection.users.forEach(e => {
            //     // newArray.push({id: e.id, username: e.username})
            //     console.log('user', e)
            //     delete e._links
            //   })
            // })


            this.items = collections
            this.configPagination(data._links, data.page);
          })
          .catch(errors => {
            console.log(errors);
          });
    },
    configPagination(_links, page) {
      this.tableOptions.pagination._links = _links
      this.tableOptions.pagination.page = page

      // console.log('pagination: ' + JSON.stringify(this.tableOptions.pagination, null, 2))
    },
    getSize(rowsPerPage) {
      // alert("this.itemsPerPage: " +this.itemsPerPage +", param: " + itemsPerPage)
      // console.log("getSize value:" + rowsPerPage)
      this.tableOptions.rowsPerPage = Number(rowsPerPage)
      this.getProjects('/collection', {size: this.tableOptions.rowsPerPage})
    },
    sortBy(key) {
      this.tableOptions.sortKey = key;
      this.tableOptions.sortOrders[key] = this.sortOrders[key] * -1;
      this.tableOptions.column = this.getIndex(this.columns, 'name', key);
      this.tableOptions.dir = this.sortOrders[key] === 1 ? 'asc' : 'desc';
      this.getProjects();
    },
    getIndex(array, key, value) {
      return array.findIndex(i => i[key] == value)
    },
    getRoles(roles) {
      // console.log(JSON.stringify(roles, null, 2))
      // let test = ""
      return roles.map(function (role) {
        return role.name
      })//.join(", ")
    },
    prev() {
      this.step--;
    },
    next() {
      this.step++;
    },
    selectCollection(collection, itemIndex) {
      //TODO: check user is a manager or owner
      // this.collectionData = collection
      this.selectedCollection = collection
      this.itemIndex = itemIndex
    },
    addCollection() {
      CollectionService.addCollection("/collection2", JSON.stringify(this.collectionData))
          .then(response => {
            let data = response.data;
            console.log("addCollection response:",data)
          })
          .catch(errors => {
            console.log(errors);
          });

    }
  }
}
</script>

<style scoped lang="scss">
.collections-wrapper {
  background: #fff;
  //padding: 20px 25px;
  margin: 30px auto;
  border-radius: 3px;
  box-shadow: 0 1px 1px rgba(0, 0, 0, .05);
}

.collections-wrapper .btn {
  float: right;
  //color: #333;
  //background-color: #fff;
  border-radius: 3px;
  border: none;
  outline: none !important;
  margin-left: 10px;
}

.collections-wrapper .btn:hover {
  color: #333;
  background: #f2f2f2;
}

.collections-wrapper .btn.btn-primary {
  color: #fff;
  background: #03A9F4;
}

.collections-wrapper .btn.btn-primary:hover {
  background: #03a3e7;
}

.collections-title .btn {
  font-size: 13px;
  border: none;
}

.collections-title .btn i {
  float: left;
  font-size: 21px;
  margin-right: 5px;
}

.collections-title .btn span {
  float: left;
  margin-top: 2px;
}

.collections-title {
  color: #fff;
  background: #4b5366;
  padding: 16px 25px;
  margin: -20px -25px 10px;
  border-radius: 3px 3px 0 0;
}

.collections-title h2 {
  margin: 5px 0 0;
  font-size: 24px;
}





.pagination {
  float: right;
  margin: 0 0 5px;
}

.pagination li a {
  border: none;
  font-size: 13px;
  min-width: 30px;
  min-height: 30px;
  color: #999;
  margin: 0 2px;
  line-height: 30px;
  border-radius: 2px !important;
  text-align: center;
  padding: 0 6px;
}

.pagination li a:hover {
  color: #666;
}

.pagination li.active a {
  background: #03A9F4;
}

.pagination li.active a:hover {
  background: #0397d6;
}

.pagination li.disabled i {
  color: #ccc;
}

.pagination li i {
  font-size: 16px;
  padding-top: 6px
}

.hint-text {
  float: left;
  margin-top: 10px;
  font-size: 13px;
}
</style>