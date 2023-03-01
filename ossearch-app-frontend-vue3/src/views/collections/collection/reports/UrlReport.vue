<template>
  <h2 class="mt-3">Url Details</h2>

  <div class="input-group input-group-sm mb-3">
    <input type="text" class="form-control" placeholder="Site URL" aria-label="Recipient's username" aria-describedby="button-addon2" v-model="url" @keyup.enter="getUrl">
    <!--    <button class="btn btn-outline-secondary" type="button" id="button-addon2">Get Details</button>-->
    <button class="btn btn-primary btn-sm" type="button" id="button-addon2" @click="getUrl">Details</button>
  </div>



  <div class="card mt-3 mb-4">
    <div class="card-header">
      <i class="fas fa-search me-1"></i>
      <b>Solr URL Details</b>
    </div>

    <div class="card-body">
      <div class="container-fluid">
        <table class="table table-bordered table-striped" style="width: 100%">
          <tbody>
          <tr v-if="loading">
            <td :colspan="2" class="text-center">
              <div v-if="loading" class="d-flex flex-column align-items-center justify-content-center">
                <div class="row">
                  <div class="spinner-border" role="status">
                    <span class="visually-hidden">Loading...</span>
                  </div>
                </div>
                <div class="row">
                  <strong>Loading</strong>
                </div>
              </div>
            </td>
          </tr>
          <template v-if="!loading && solrData">
            <tr v-for="(value, field) in solrData" :key="field">
              <td v-if="field.startsWith('meta_')" class="text-nowrap"><b class="text-danger">(meta)</b> <b>{{ field.split('meta_').pop() }}</b></td>
              <td v-else class="text-nowrap"><b>{{ field }}</b></td>
              <td v-if="field.toLowerCase() === 'url'"><a :href="value" target="_blank" class="link-primary">{{ value }}</a></td>
              <td v-else-if="['inlinks', 'outlinks'].includes(field.toLowerCase())">
                <ul class="list-unstyled">
                  <li v-for="link in value.sort(Intl.Collator().compare)" :key="link">
                    <a :href="link" target="_blank" class="link-primary text-nowrap">{{ link }}</a>
                  </li>
                </ul>
              </td>
              <td v-else>{{ value }}</td>
            </tr>
          </template>
          <tr v-else>
            <td :colspan="2" class="text-center text-danger"><b>No Data Found</b></td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <div class="card mt-3 mb-4">
    <div class="card-header">
      <i class="fas fa-database me-1"></i>
      <b>CrawlDb URL Details</b>
    </div>

    <div class="card-body">
      <div class="container-fluid">
        <table class="table table-bordered table-striped" style="width: 100%">
          <tbody>
            <tr v-if="loading">
              <td :colspan="2" class="text-center">
                <div v-if="loading" class="d-flex flex-column align-items-center justify-content-center">
                  <div class="row">
                    <div class="spinner-border" role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                  </div>
                  <div class="row">
                    <strong>Loading</strong>
                  </div>
                </div>
              </td>
            </tr>
            <template v-if="!loading && crawlDbData">
              <tr v-for="(value, field) in crawlDbData" :key="field">
                <td class="text-capitalize"><b>{{ field }}</b></td>
                <td v-if="field.toLowerCase() === 'url'"><a :href="value" target="_blank" class="link-primary text-nowrap">{{ value }}</a></td>
                <td v-else>{{ value }}</td>
              </tr>
            </template>
            <tr v-else>
              <td :colspan="2" class="text-center text-danger"><b>No Data Found</b></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <div class="card mt-3 mb-4">
    <div class="card-header">
      <i class="fas fa-database me-1"></i>
      <b>LinkDb URL Details</b>
    </div>

    <div class="card-body">
      <div class="container-fluid">
        <table class="table table-bordered table-striped" style="width: 100%">
          <tbody>
          <tr v-if="loading">
            <td :colspan="2" class="text-center">
              <div v-if="loading" class="d-flex flex-column align-items-center justify-content-center">
                <div class="row">
                  <div class="spinner-border" role="status">
                    <span class="visually-hidden">Loading...</span>
                  </div>
                </div>
                <div class="row">
                  <strong>Loading</strong>
                </div>
              </div>
            </td>
          </tr>
          <template v-if="!loading && linkdbData">
            <tr v-for="(value, field) in linkdbData" :key="field">
              <td class="text-capitalize"><b>{{ field }}</b></td>
              <td>{{ value }}</td>
            </tr>
          </template>
          <tr v-else>
            <td :colspan="2" class="text-center text-danger"><b>No Data Found</b></td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

</template>

<script>
import CollectionService from "../../../../services/collection.service";
import ReportsService from "../../../../services/reports.service";

export default {
  name: "UrlReport",
  props: ['name', 'tabName'],
  data() {
    return {
      loading: false,
      error: null,
      url: '',
      collection:null,
      solrData: null,
      crawlDbData: null,
      linkdbData: null
    }
  },
  created() {
    // watch the params of the route to fetch the data again
    this.$watch(
        () => this.$route.query,
        async () => {
          if (this.$route.query.url) {
            this.url = this.$route.query.url
            console.log("this.$route.query.url", this.$route.query.url, this.url)
            this.loading = true
            await this.getCollection()
            await this.getSolr()
            await this.getCrawlDb()
            this.loading = false
          }
        },
        // fetch the data when the view is created and the data is
        // already being observed
        {immediate: true}
    )
  },
  async mounted() {
    this.loading = true
    await this.getCollection()
    await this.getSolr()
    await this.getCrawlDb()
    this.loading = false
  },
  methods: {
    getUrl() {
      this.$router.push({ name: 'urlReport', query: { url: this.url}, params: {name: this.name}})
    },
    async getCollection() {
      await CollectionService.getCollections('/collection/search/getCollectionByName', {name: this.name, projection: 'collectionFormData'})
          .then(response => {
            this.collection = response.data;
            // console.log("collection", this.collection)
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
    },
    async getSolr() {
      await ReportsService.get('/reports/solr/url/' + this.collection.id, {url: this.url})
          .then(response => {
            let data = response.data.data[0];
            this.solrData = Object.keys(data).sort().reverse().reduce((acc, key) => ({ ...acc, [key]: data[key] }), {})
            // console.log("solrData", this.solrData)
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
    },
    async getCrawlDb() {
      let params = {
        collectionId: this.collection.id,
        url: this.url,
        // projection: 'webpageInfoTableData'
      }

      // await ReportsService.get('/reports/crawldb/'+this.collectionId, params)
      await ReportsService.get('/webpage/search/findWebpageByCollectionIdAndAndUrl' , params)
          .then(response => {
            this.crawlDbData = response.data
            delete this.crawlDbData._links
            // console.log("crawlDbData", this.crawlDbData)
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
    },
  }
}
</script>

<style scoped>

</style>
