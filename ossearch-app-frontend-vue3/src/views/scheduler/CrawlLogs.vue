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

  <div v-if="!loading" class="container-fluid px-4">
    <!--    <h1 class="mt-4">{{ jobData.jobName }}</h1>-->
    <h1 class="mt-4">Crawl Logs for {{ $route.params.jobGroup }}.{{ $route.params.jobName }}</h1>
    <Breadcrumb/>
<!--    <button type="button" class="btn btn-danger" data-bs-dismiss="modal" @click="createCrawlSchedule()">Create
    </button>-->

    <!-- Crawl Scheduler Details -->
    <div class="card mb-4">
      <div class="card-header">
        <i class="fas fa-info-circle me-1"></i>
        Crawl Logs
      </div>
      <div class="card-body">
        <div v-if="loading" class="d-flex justify-content-center">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>
        <Datatable v-if="!loading"
            :tableData="crawlLogs"
            :tableOptions="tableOptions.crawlLogs"
                   id="crawlLogsTable"
        >
          <template v-slot:table-body>
            <tr v-for="(crawLog, i) in crawlLogs" :key="i">
              <template v-for="column in tableOptions.crawlLogs.columns" :key="column">
                <td v-if="!['state', 'progress', 'view', 'dbStats', 'errors', 'rounds', 'currentRound', 'currentStep', 'createdDate', 'updatedDate'].includes(column.name)">{{ crawLog[column.name] }}</td>
                <td v-if="column.name === 'currentStep'">{{ JSON.parse(crawLog?.jobConfig).reindex ? 'Reindex' : crawLog[column.name] }}</td>
                <td v-if="['rounds', 'currentRound'].includes(column.name)">{{ !JSON.parse(crawLog?.jobConfig).reindex ? crawLog[column.name] : '' }}</td>
                <td v-if="column.name === 'dbStats'">
                  <a href="#" v-if="crawLog[column.name]" class="link-primary" data-bs-toggle="modal"
                          data-bs-target="#crawlStats" @click="selectedJob = crawLog">db:
                    {{ crawLog[column.name]?.status?.['2']?.['count'] }} / solr: {{ crawLog?.solrCount }}
                  </a>
                </td>
                <td v-if="column.name === 'state'">
                  <span v-if="crawLog[column.name] === 'Failed'" class="badge rounded-pill bg-danger text-light">{{ crawLog[column.name] }}</span>
                  <span v-else-if="crawLog[column.name] === 'Stopped'" class="badge rounded-pill bg-warning text-dark">{{ crawLog[column.name] }}</span>
                  <span v-else-if="crawLog[column.name] === 'Finished'" class="badge rounded-pill bg-success text-light">{{ crawLog[column.name] }}</span>
                  <span v-else class="badge rounded-pill bg-primary text-light">{{ crawLog[column.name] }}</span>
                </td>
                <td v-if="column.name === 'progress'">
                  <div class="progress position-relative">
                    <template v-if="JSON.parse(crawLog?.jobConfig).reindex">
                      <div class="progress-bar" role="progressbar"
                           :style="'width: '+(crawLog.state === 'Running' ? 0 : 1)*100+'%'"
                           :aria-valuenow="(crawLog.state === 'Running' ? 0 : 1)*100" aria-valuemin="0"
                           :aria-valuemax="1"><small class="justify-content-center d-flex position-absolute w-100"
                                                     :class="((crawLog.state === 'Running' ? 0 : 1)*100) < 50 ? 'text-dark' : ''">{{
                          (crawLog.state === 'Running' ? 0 : 1) * 100
                        }}%</small></div>
                    </template>
                    <template v-else>
                      <div class="progress-bar" role="progressbar" :style="'width: '+getProgress(crawLog)+'%'"
                           :aria-valuenow="crawLog['currentRound']" aria-valuemin="0"
                           :aria-valuemax="crawLog['rounds']"><small
                          class="justify-content-center d-flex position-absolute w-100"
                          :class="getProgress(crawLog) < 50 ? 'text-dark' : ''">{{ getProgress(crawLog) }}%</small>
                      </div>
                    </template>
                  </div>
                </td>
                <td v-if="column.name === 'view'" class="justify-content-evenly text-center">
                  <div class="btn-group btn-group-sm align-items-center">
                    <router-link class="btn link-primary p-0 m-1"
                                 :to="{ name: 'crawlSteps', params: { jobName: $route.params.jobName, jobGroup: $route.params.jobGroup, jobId: crawLog.jobId }}"
                                 @click="selectedJobId = crawLog['jobId']"><i class="fas fa-tasks"></i></router-link>
                  </div>
                </td>
                <td v-if="column.name === 'errors'" class="text-danger text-truncate" style="max-width: 250px;"
                    data-bs-toggle="tooltip" data-bs-placement="top" :title="crawLog[column.name]">
                  <a href="#" v-if="crawLog[column.name]" class="link-primary"
                          data-bs-toggle="modal" data-bs-target="#crawlErrors" @click="selectedJob = crawLog">errors
                  </a>
                </td>
                <td v-if="['createdDate', 'updatedDate'].includes(column.name)">{{ getLocalDateTime(crawLog[column.name]) }}</td>
              </template>
            </tr>
          </template>
        </Datatable>
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
                <li class="list-group-item d-flex justify-content-between align-items-center text-capitalize" v-for="(value, i) in selectedJob?.dbStats?.status" :key="i">
                  {{value.statusValue.replace(/^db_/,'')}}
                  <span >{{ value.count }}</span>
                </li>
              </ul>

              <ul class="list-group">
                <li class="list-group-item d-flex justify-content-between align-items-center">
                  <b>Total Urls in CrawlDB</b>
                  <span class="badge bg-primary rounded-pill">{{ selectedJob?.dbStats?.totalUrls }}</span>
                </li>
                <li class="list-group-item d-flex justify-content-between align-items-center">
                  <b>Total Urls in Solr</b>
                  <span class="badge bg-primary rounded-pill">{{ selectedJob?.solrCount }}</span>
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

    <div class="modal fade" id="crawlErrors" tabindex="-1" aria-labelledby="crawlErrors" aria-hidden="true">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Error</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p class="text-danger">{{ selectedJob?.errors }}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>

    <router-view></router-view>

  </div>
</template>

<script>
import Breadcrumb from "../../components/Breadcrumb";
import Datatable from "../../components/table/Datatable";
import SchedulerService from "../../services/scheduler.service";

export default {
  name: "CrawlLogs",
  components: {
    Breadcrumb,
    Datatable
  },
  data() {
    return {
      loading: false,
      error: null,
      crawlLogs: null,
      selectedJobId: null,
      selectedJob: null,
      tableOptions: {
        crawlLogs: {
          // enableImport: true,
          // enableAddRow: true,
          // enableActions: true,
          order: [[0, "desc"]],
          columns: [
            { label: 'JobId', name: 'jobId'},
            { label: 'State', name: 'state'},
            { label: 'On Step', name: 'currentStep'},
            { label: 'Progress', name: 'progress'},
            { label: 'Round', name: 'currentRound'},
            { label: 'MaxRounds', name: 'rounds'},
            { label: 'Total URL\'s', name: 'dbStats'},
            { label: 'Errors', name: 'errors'},
            { label: 'Start Time', name: 'createdDate'},
            { label: 'End Time', name: 'updatedDate'},
            { label: 'Step Logs', name: 'view'},
          ]
        },
      },
    }
  },
  created() {
    // watch the params of the route to fetch the data again
    this.$watch(
        () => this.$route.params,
        () => {this.fetchData()},
        // fetch the data when the view is created and the data is
        // already being observed
        { immediate: true }
    )
  },
  watch: {
    error: {
      deep: true,
      handler: function () {
        alert("ERROR: " + this.error)
      }
    }
  },
  methods: {
    async fetchData() {
      this.loading = true
      if (Object.keys(this.$route.params).length !== 0) {
        //console.log(">>>> params", this.$route.params, "query", this.$route.query)
        this.error = this.crawlLogs = null
        await this.getCrawlLogs(this.$route.params.jobName, this.$route.params.jobGroup)
      }
      this.loading = false
    },
    async getCrawlLogs(jobName, jobGroup) {
      // http://localhost:8484/api/crawllog/search/getCrawlLogsByJobKey?jobKey=scheduled_crawl.collection1
      let jobKey = jobGroup + "." + jobName
      await SchedulerService.getCrawlLogs('crawllog/search/getCrawlLogsByJobKey', {jobKey: jobKey, projection: 'crawlLogInfo'})
          .then(res => {
            console.log("getCrawlLogs result:", res.data)
            this.crawlLogs = res.data._embedded.crawllog
            console.log("getCrawlLogs result:", this.crawlLogs)
            this.crawlLogs.forEach(crawlLog => {
              if (crawlLog.dbStats) {
                console.log("dbStats", crawlLog.dbStats)
                let json = JSON.parse(crawlLog.dbStats)
                console.log("JSON parse result", json)
                crawlLog.dbStats = json
              }
            })
          })
          .catch(errors => {
            console.log(errors);
            this.error = errors
          })
    },
    getProgress(crawLog) {
      if (crawLog['currentRound'] === crawLog['rounds'] && crawLog['state'] !== 'Finished') {
        return 99
      }
      let percent = Math.trunc( (crawLog['currentRound'] / crawLog['rounds']) * 100)
      return percent
    },
    getLocalDateTime(utc) {
      let u = utc+'Z'
      return new Date(u).toLocaleString()
    }
  },

}
</script>

<style scoped>

</style>