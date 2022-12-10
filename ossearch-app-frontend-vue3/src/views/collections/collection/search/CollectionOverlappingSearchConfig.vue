<template>
  <div v-if="error" class="container-fluid px-4 error">
    {{ error }}
  </div>

  <div class="row mt-4 mb-4">
    <div class="col-xl-6">
      <div class="card">
        <div class="card-header">
          <i class="fas fa-search me-1"></i>
          <b>Include Other Collections</b>
          <div class="float-end">
            <button class="btn btn-sm btn-primary float-end" type="button" @click="beforeEditIncludeCollections = JSON.parse(JSON.stringify(collection)); isEditIncludeCollections = !isEditIncludeCollections" v-if="!isEditIncludeCollections">Edit</button>
            <button v-if="isEditIncludeCollections" class="btn btn-sm btn-success me-md-2" type="button" @click="saveIncludeCollections()">Save</button>
            <button v-if="isEditIncludeCollections" class="btn btn-sm btn-danger float-end" type="button" @click="collection = beforeEditIncludeCollections; isEditIncludeCollections = false">Cancel</button>
          </div>
        </div>
        <div class="card-body">
          <p>The collections selected below will be included in and be part of search results for <b>{{ collection?.name }}</b>.</p>
          <div v-if="loading" class="d-flex justify-content-center">
            <div class="spinner-border" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
          <template v-else>
            <fieldset :disabled="!isEditIncludeCollections">
              <Multiselect v-if="!loading" id="IncludeCollectionsMultiselect"
                           v-model="collection.includedCollections"
                           :options="collections"
                           :multiple="true"
                           trackBy="name"
                           label="name"
                           :searchable="false"
                           :placeholder="'Select Collections'"
                           :allowEmpty="true"
                           :loading="loading"
              />
            </fieldset>
          </template>
<!--          <div v-if="loading" class="d-flex justify-content-center">
            <div class="spinner-border" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
          <template v-else>
            <p class="text-danger"><b>Note: This may require the included collection(s) to be re-indexed to take effect</b></p>
            <IncludeCollectionsTable
                :isEditing="isEditIncludeCollections"
                :collections="collections"
                :includedCollectionId="collection.id"
                v-model:includedCollections="collection.includedCollections"
            />
          </template>-->
        </div>
      </div>
    </div>
    <div class="col-xl-6">
      <div class="card">
        <div class="card-header">
          <i class="fas fa-search me-1"></i>
          <b>Part Of Other Collections</b>
          <div class="float-end">
            <button class="btn btn-sm btn-primary float-end" type="button" @click="beforeEditPartOfCollections = JSON.parse(JSON.stringify(collection)); isEditPartOfCollections = !isEditPartOfCollections" v-if="!isEditPartOfCollections">Edit</button>
            <button v-if="isEditPartOfCollections" class="btn btn-sm btn-success me-md-2" type="button" @click="savePartOfCollections()">Save</button>
            <button v-if="isEditPartOfCollections" class="btn btn-sm btn-danger float-end" type="button" @click="collection = beforeEditPartOfCollections; isEditPartOfCollections = false">Cancel</button>
          </div>
        </div>
        <div class="card-body">
          <p><b>{{ collection?.name }}</b> will be included in and be part of search results for the collections selected below.</p>
          <div v-if="loading" class="d-flex justify-content-center">
            <div class="spinner-border" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
          <template v-else>
            <fieldset :disabled="!isEditPartOfCollections">
              <Multiselect v-if="!loading" id="PartOfCollectionsMultiselect"
                           v-model="collection.partOfCollections"
                           :options="collections"
                           :multiple="true"
                           trackBy="name"
                           label="name"
                           :searchable="false"
                           :placeholder="'Select Collections'"
                           :allowEmpty="false"
                           :loading="loading"
              />
            </fieldset>
          </template>
<!--          <div v-if="loading" class="d-flex justify-content-center">
            <div class="spinner-border" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
          <template v-else>
            <p class="text-danger"><b>Note: This may require this collection to be re-indexed to take effect</b></p>
            <IncludeCollectionsTable
                :isEditing="isEditPartOfCollections"
                :collections="collections"
                :includedCollectionId="collection.id"
                v-model:includedCollections="collection.partOfCollections"
            />
          </template>-->
        </div>
      </div>
    </div>
  </div>

  <div class="mb-3">
    <h3>Overlapping Search Collections</h3>
    <p>Search collections can “share” documents, which allows OSS to keep the overall search index much smaller than it would be otherwise. For example, the search collection for si.edu can include URLs from nationalzoo, siarchives, npg and other websites. This means that when nationalzoo’s search collection gets crawled, siedu’s search collection automatically receives the new content.</p>
    <p>The figure below illustrates the concept of overlapping search collections and the use of exclusions.</p>
    <div>
      <img src="@/assets/images/overlapping.png">
    </div>
  </div>
</template>

<script>
import CollectionService from "../../../../services/collection.service";
import Multiselect from "vue-multiselect";
import EventBus from "../../../../common/EventBus";

export default {
  name: "CollectionOverlappingSearchConfig",
  props: ['name', 'tabName'],
  components: {
    Multiselect
  },
  data() {
    return {
      loading: false,
      error: null,
      showJson: false,
      collection: null,
      collections: [],
      isEditIncludeCollections: false,
      isEditPartOfCollections: false,
      beforeEditIncludeCollections: null,
      beforeEditPartOfCollections: null,
    }
  },
  created () {
    // watch the params of the route to fetch the data again
    this.$watch(
        () => this.$route.params.name,
        async () => {
          await this.fetchData()
        },
        // fetch the data when the view is created and the data is
        // already being observed
        { immediate: true }
    )
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
  methods: {
    async fetchData() {
      this.loading = true
      if (Object.keys(this.$route.params).length !== 0) {
        this.error = this.collection = null
        await this.getCollection()
        await this.getCollections()
      }
      this.loading = false
    },
    async getCollection() {

      await CollectionService.getCollections('/collection/search/getCollectionByName', {name: this.$route.params.name, projection: 'collectionFormData'})
          .then(response => {
            let data = response.data;
            this.collection = data;
            console.log("data", data)
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
    },
    async getCollections() {
      await CollectionService.getCollections('/collection', {projection: 'collectionIdNameInfo', size: 1000})
          .then(response => {
            let data = response.data;

            if (this.collectionId) {
              this.collections = data._embedded.collection.filter(collection => collection.id !== this.collection.id );
            } else {
              this.collections = data._embedded.collection;
            }

            this.collections.sort((a, b) => a.name.localeCompare(b.name));
          })
          .catch(errors => {
            console.log(errors);
          });
    },
    async saveIncludeCollections() {
      let body = []

      this.collection.includedCollections.forEach(collection => {
        body.push(collection._links.self.href.replace('{?projection}',''))
      })

      await CollectionService.addIncludedCollections('/collection/'+this.collection.id+'/includedCollections', body.join("\n"))
          .then(response => {
            let data = response.data;
            // this.collection = data;
            console.log("data", data)
            EventBus.dispatch('toast', {
              type: 'success',
              msg: 'Included Collections Updated!'
            })
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
      await this.getCollection()
      this.isEditIncludeCollections = false
    },

    async savePartOfCollections() {
      let body = []

      this.collection.partOfCollections.forEach(collection => {
        body.push(collection._links.self.href.replace('{?projection}',''))
      })

      await CollectionService.addPartOfCollections('/collection/'+this.collection.id+'/partOfCollections', body.join("\n"))
          .then(response => {
            let data = response.data;
            // this.collection = data;
            console.log("data", data)
            EventBus.dispatch('toast', {
              type: 'success',
              msg: 'Part of Collections Updated!'
            })
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
      await this.getCollection()
      this.isEditIncludeCollections = false
    },
  }
}
</script>

<style scoped lang="scss">
//@import "~vue-multiselect/dist/vue-multiselect.css";
@import "@/assets/styles/scss/vue-multiselect-bootstrap5.scss";

/*.multiselect__spinner::before,
.multiselect__spinner::after {
  position: absolute;
  content: "";
  top: 50%;
  left: 50%;
  margin: -8px 0 0 -8px;
  width: 16px;
  height: 16px;
  border-radius: 100%;
  border-color: #41b883 transparent transparent;
  border-style: solid;
  border-width: 2px;
  box-shadow: 0 0 0 1px transparent;
}

.multiselect__tag {
  position: relative;
  display: inline-block;
  padding: 4px 26px 4px 10px;
  border-radius: 5px;
  margin-right: 10px;
  color: #fff;
  line-height: 1;
  background: #41b883;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  max-width: 100%;
  text-overflow: ellipsis;
}

.multiselect__option--highlight {
  background: #41b883;
  outline: none;
  color: white;
}

.multiselect__option--highlight::after {
  content: attr(data-select);
  background: #41b883;
  color: white;
}*/
</style>