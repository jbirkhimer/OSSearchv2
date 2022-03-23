<template>
  <div v-if="loading" class="container-fluid px-4 loading">
    <div class="d-flex justify-content-center">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </div>

  <div v-if="error" class="container-fluid px-4 error">
    {{ error }}
  </div>

  <div class="container-fluid px-4">
    <h1 class="mt-4">Dashboard</h1>
    <Breadcrumb/>
    <div class="row">
      <div class="col-md-4 col-xl-3">
        <div class="card bg-primary text-white mb-4">
          <div class="card-body">
            <h5 class="card-title text-uppercase">Scheduled Crawls</h5>
            <h2 class="card-text text-end"><i class="fas fa-spider float-start"></i><span>{{ scheduledCrawls }}</span></h2>
<!--            <span class="card-text">Number of Crawls Executed:<span class="float-end">{{ numberOfJobsExecuted }}</span></span>-->
          </div>
          <div class="card-footer d-flex align-items-center justify-content-between">
            <router-link class="small text-white stretched-link" to="/scheduler">View Details</router-link>
            <div class="small text-white"><i class="fas fa-angle-right"></i></div>
          </div>
        </div>
      </div>
      <div class="col-md-4 col-xl-3">
        <div class="card bg-primary text-white mb-4">
          <div class="card-body">
            <h5 class="card-title text-uppercase">Collections</h5>
            <h2 class="card-text text-white text-end"><i class="fas fa-sitemap float-start"></i><span>{{ collectionCount }}</span></h2>
<!--            <span>Number of Crawls Executed:<span class="float-end">{{ numberOfJobsExecuted }}</span></span>-->
          </div>
          <div class="card-footer d-flex align-items-center justify-content-between">
            <router-link class="small text-white stretched-link" to="/collections">View Details</router-link>
            <div class="small text-white"><i class="fas fa-angle-right"></i></div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="card bg-primary text-white mb-4">
          <div class="card-body">
            <h5 class="card-title text-uppercase">Searches</h5>
            <h2 class="card-text text-white text-end"><i class="fas fa-search float-start"></i><span>{{ searchesCount }}</span></h2>
<!--            <span>Number of Crawls Executed:<span class="float-end">{{ numberOfJobsExecuted }}</span></span>-->
          </div>
          <div class="card-footer d-flex align-items-center justify-content-between">
            <router-link class="small text-white stretched-link" to="/search">View Details</router-link>
            <div class="small text-white"><i class="fas fa-angle-right"></i></div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="card bg-primary text-white mb-4">
          <div class="card-body">
            <h5 class="card-title text-uppercase">Solr Records</h5>
            <h2 class="card-text text-white text-end"><i class="fas fa-users float-start"></i><span>{{ solrCount }}</span></h2>
            <!--            <span>Number of Crawls Executed:<span class="float-end">{{ numberOfJobsExecuted }}</span></span>-->
          </div>
          <div class="card-footer d-flex align-items-center justify-content-between">
            <router-link class="small text-white stretched-link" to="/reports">View Details</router-link>
            <div class="small text-white"><i class="fas fa-angle-right"></i></div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="card bg-primary text-white mb-4">
          <div class="card-body">
            <h5 class="card-title text-uppercase">Users</h5>
            <h2 class="card-text text-white text-end"><i class="fas fa-users float-start"></i><span>{{ userCount }}</span></h2>
            <!--            <span>Number of Crawls Executed:<span class="float-end">{{ numberOfJobsExecuted }}</span></span>-->
          </div>
          <div class="card-footer d-flex align-items-center justify-content-between">
            <router-link class="small text-white stretched-link" to="/users">View Details</router-link>
            <div class="small text-white"><i class="fas fa-angle-right"></i></div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="card text-white mb-4" :class="systemStatus === 'UP' ? 'bg-primary' : 'bg-danger'">
          <div class="card-body">
            <h5 class="card-title text-uppercase">System Status</h5>
            <h2 class="card-text text-white text-end"><i class="fas fa-server float-start"></i><span>{{ systemStatus }}</span></h2>
            <!--            <span>Number of Crawls Executed:<span class="float-end">{{ numberOfJobsExecuted }}</span></span>-->
          </div>
          <div class="card-footer d-flex align-items-center justify-content-between">
            <router-link class="small text-white stretched-link" to="/backend">View Details</router-link>
            <div class="small text-white"><i class="fas fa-angle-right"></i></div>
          </div>
        </div>
      </div>
<!--        <div class="col-xl-3 col-md-6">
            <div class="card bg-success text-white mb-4">
                <div class="card-body">Collections</div>
                <div class="card-footer d-flex align-items-center justify-content-between">
                    <router-link class="small text-white stretched-link" to="/collections">View Details</router-link>
                    <div class="small text-white"><i class="fas fa-angle-right"></i></div>
                </div>
            </div>
        </div>-->
    </div>
    <div class="row">
        <div class="col-xl-6">
            <div class="card mb-4">
                <div class="card-header">
                  <i class="fas fa-chart-bar me-1"></i>
                    Solr Collection Record Counts
                </div>
                <div class="card-body">

                  <Datatable v-if="!loading"
                             :tableData="solrCounts"
                             :tableOptions="tableOptions.solrCounts"
                             id="solrRecordTable"
                  >
                    <template v-slot:table-body>
                      <tr v-for="(collection, i) in solrCounts" :key="i">
                        <td>{{ collection.id }}</td>
                        <td><router-link :to="{ name: 'collectionDetails', params: { name: collection.name, id: collection.id }}">{{ collection.name }}</router-link></td>
                        <td>{{ collection.count }}</td>
                      </tr>
                    </template>
                  </Datatable>

                </div>
            </div>
        </div>
        <div class="col-xl-6">
            <div class="card mb-4">
                <div class="card-header">
                  <i class="fas fa-chart-area me-1"></i>
                    Latest CrawlDb Counts
                </div>
                <div class="card-body">
                  <Datatable v-if="!loading"
                             :tableData="crawlDbStats"
                             :tableOptions="tableOptions.crawlDbStats"
                             id="dbStatsTable"
                  >
                    <template v-slot:table-body>
                      <tr v-for="(crawLog, i) in crawlDbStats" :key="i">
                        <template v-for="column in tableOptions.crawlDbStats.columns" :key="column">
                          <td v-if="column.name === 'id'">{{ crawLog[column.name] }}</td>
                          <td v-if="column.name === 'jobKey'">
                            <router-link :to="{name: 'crawlLogs', params: { jobName: crawLog[column.name].split('.')[1], jobGroup: crawLog[column.name].split('.')[0] }}">{{ crawLog[column.name] }}</router-link>
                          </td>
                          <td v-if="column.name === 'fetched'">{{ crawLog.dbStats?.status?.['2']?.['count'] }}
                            <a class="link-primary" href="#" data-bs-toggle="modal" data-bs-target="#crawlStats" @click="selectedDbStats = crawLog"> (details)</a>
                          </td>
                          <td v-if="column.name === 'unfetched'">{{ crawLog.dbStats?.status?.['1']?.['count'] }}
                            <a class="link-primary" href="#" data-bs-toggle="modal" data-bs-target="#crawlStats" @click="selectedDbStats = crawLog"> (details)</a>
                          </td>
                          <td v-if="column.name === 'solrCount'">{{ crawLog[column.name] }}</td>
                        </template>
                      </tr>
                    </template>
                  </Datatable>
                </div>
            </div>
        </div>
    </div>
  </div>

  <div class="modal fade" id="crawlStats" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Crawl Stats</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="card-body">
            <h2 class="h4 mb-1">Urls in CrawlDB</h2>
            <ul class="list-group mb-3">
              <li class="list-group-item d-flex justify-content-between align-items-center text-capitalize" v-for="(value, i) in selectedDbStats?.dbStats?.status" :key="i">
                {{value.statusValue.replace(/^db_/,'')}}
                <span >{{ value.count }}</span>
              </li>
            </ul>

            <ul class="list-group">
              <li class="list-group-item d-flex justify-content-between align-items-center">
                <b>Total Urls in CrawlDB</b>
                <span class="badge bg-primary rounded-pill">{{ selectedDbStats?.dbStats?.totalUrls }}</span>
              </li>
              <li class="list-group-item d-flex justify-content-between align-items-center">
                <b>Total Urls in Solr</b>
                <span class="badge bg-primary rounded-pill">{{ selectedDbStats?.solrCount }}</span>
              </li>
            </ul>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ServerStatusService from "../services/server-status.service";
import SchedulerService from "../services/scheduler.service";
import CollectionService from "../services/collection.service";
import UserService from "../services/user.service";
import Breadcrumb from "../components/Breadcrumb";
import Datatable from "../components/table/Datatable";

export default {
  name: "Dashboard",
  components: {
    Breadcrumb,
    Datatable
  },
  data() {
    return {
      loading: false,
      error: null,
      scheduledCrawls: null,
      numberOfJobsExecuted: null,
      collectionCount: null,
      searchesCount: 0,
      userCount: null,
      schedulerStatus: null,
      backedStatus: null,
      solrCount: null,
      solrCounts: null,
      crawlDbStats: null,
      selectedDbStats: null,
      tableOptions: {
        solrCounts: {
          order: [[0, "asc"]],
          columns: [
            {label: 'CollectionId', name: 'id'},
            {label: 'Name', name: 'name'},
            {label: 'Records', name: 'count'},
          ],
        },
        crawlDbStats: {
          order: [[0, "desc"]],
          columns: [
            { label: 'Id', name: 'id'},
            { label: 'Job Key', name: 'jobKey'},
            { label: 'Fetched', name: 'fetched'},
            { label: 'Unfetched', name: 'unfetched'},
            { label: 'Solr Count', name: 'solrCount'},
          ]
        },
      },
    }
  },
  computed: {
    systemStatus() {
      if (this.schedulerStatus && this.backedStatus === "UP") {
        return "UP"
      } else {
        return "DOWN"
      }

    }
  },
  async mounted() {
    this.loading = true
    await this.getScheduledCrawls()
    await this.getSchedulerStatus()
    await this.getCollections()
    await this.getUsers()
    await this.getServerStatus()
    await this.getSolrCounts()
    await this.getSolrCollectionCounts()
    await this.getCrawlLogStats()
    this.loading = false
  },
  methods: {
    async getScheduledCrawls() {
      await SchedulerService.getAllJobs("/scheduler/getAllJobs2")
          .then(res => {
            this.scheduledCrawls = res.data.length
          })
          .catch(errors => {
            this.error = errors
          })
    },
    async getSchedulerStatus() {
      await ServerStatusService.getSchedulerStatus()
          .then(
              response => {
                let rsp = response.data
                this.numberOfJobsExecuted = rsp.numberOfJobsExecuted
                this.schedulerStatus = rsp.started
              }
          )
          .catch(errors => {
            this.error = errors
          })
    },
    async getCollections() {
      await CollectionService.getCollections("/collection", {size: 1, projection: 'collectionIdNameInfo'})
          .then(response => {
            this.collectionCount = response.data.page.totalElements;
          })
          .catch(errors => {
            this.error = errors
          })
    },
    async getServerStatus() {
      await ServerStatusService.getServerStatus()
          .then(response => {
            this.backedStatus = response.data.status
          })
          .catch(errors => {
            this.error = errors
          })
    },
    async getUsers(url = '/users', params = {size: 1, projection: 'userIdNameEmailRoles'}) {
      await UserService.getUsers(url, params)
          .then(response => {
            this.userCount = response.data.page.totalElements;
          })
          .catch(errors => {
            this.error = errors
          })
    },
    async getSolrCounts() {
      await ServerStatusService.getSolr()
          .then(response => {
            this.solrCount = response.data.numDocs;
          })
          .catch(errors => {
            this.error = errors
          })
    },
    async getSolrCollectionCounts() {
      await ServerStatusService.getSolrCollectionCounts()
          .then(response => {
            this.solrCounts = response.data;
          })
          .catch(errors => {
            this.error = errors
          })
    },
    async getCrawlLogStats() {
      await ServerStatusService.getCrawlLogStats()
          .then(response => {
            let data = response.data._embedded.crawllog;

            data.forEach(crawlLog => {
              crawlLog.dbStats = JSON.parse(crawlLog.dbStats)
            })

            this.crawlDbStats = data
          })
          .catch(errors => {
            this.error = errors
          })
    },
  }
}
</script>

<style lang="sass" scoped>
$card-height: 100%
/*.card {*/
/*  position: relative;*/
/*  display: flex;*/
/*  flex-direction: column;*/
/*  min-width: 0;*/
/*  word-wrap: break-word;*/
/*  border: 1px solid rgba(0, 0, 0, .05);*/
/*  border-radius: .375rem;*/
/*  background-color: #fff;*/
/*  background-clip: border-box;*/
/*}*/

/*.card-body {*/
/*  padding: 1.5rem;*/
/*  flex: 1 1 auto;*/
/*}*/

/*.card-title {*/
/*  margin-bottom: 1.25rem;*/
/*}*/

/*.rounded-circle {*/
/*  border-radius: 50% !important;*/
/*}*/

/*.shadow {*/
/*  box-shadow: 0 0 2rem 0 rgba(136, 152, 170, .15) !important;*/
/*}*/

/*.icon {*/
/*  width: 3rem;*/
/*  height: 3rem;*/
/*}*/

/*.icon i {*/
/*  font-size: 2.25rem;*/
/*}*/

/*.icon-shape {*/
/*  display: inline-flex;*/
/*  padding: 12px;*/
/*  text-align: center;*/
/*  border-radius: 50%;*/
/*  align-items: center;*/
/*  justify-content: center;*/
/*}*/

/*.icon-shape i {*/
/*  font-size: 1.25rem;*/
/*}*/
</style>