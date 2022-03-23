<template>
  <template v-if="loading">
    <div class="d-flex justify-content-center">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </template>

  <template v-if="error">
    {{ error }}
  </template>

<!--  <div v-if="!loading" class="container-fluid px-4">-->
    <h3>step logs jobId: {{ $route.params.jobId }}</h3>

<!--    <div class="accordion" id="accordionSteps">
      <template v-for="(step, i) in crawlStepLogs" :key="step">
        <div class="accordion-item">
          <h2 class="accordion-header" :id="'stepHeading_'+i">
            <button class="accordion-button" type="button" data-bs-toggle="collapse" :data-bs-target="'#step'+i" aria-expanded="true" aria-controls="collapseOne">
              Round: {{ step['round'] }},  Step: {{ step.stepType.toUpperCase() }}, State: {{ step.state.toUpperCase() }}
            </button>
          </h2>
          <div :id="'step'+i" class="accordion-collapse collapse" :aria-labelledby="'step'+i" data-bs-parent="#accordionSteps">
            <div class="accordion-body">

              <pre>{{ print(step) }}</pre>

            </div>
          </div>
        </div>
      </template>
    </div>-->

    <!-- Crawl Scheduler Details -->
    <div class="card mb-4">
      <div class="card-header">
        <i class="fas fa-info-circle me-1"></i>
        Crawl Step Logs
      </div>
      <div class="card-body">
        <div v-if="loading" class="d-flex justify-content-center">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>
        <Datatable v-if="!loading"
                   :tableData="crawlStepLogs"
                   :tableOptions="tableOptions.crawlStepLogs"
                   id="crawlStepLogsTable"
        >
          <template v-slot:table-body>
            <tr v-for="(step, i) in crawlStepLogs" :key="i">
              <template v-for="column in tableOptions.crawlStepLogs.columns" :key="column">
                <td v-if="!['stepType', 'state', 'view', 'args', 'dbStats', 'errors', 'createdDate', 'updatedDate'].includes(column.name)">{{ step[column.name] }}</td>
                <td v-if="['createdDate', 'updatedDate'].includes(column.name)">{{ getLocalDateTime(step[column.name]) }}</td>
                <td v-else-if="column.name === 'stepType'">{{ step[column.name].toUpperCase() }}</td>

                <td v-else-if="column.name === 'state'">
                  <span v-if="step[column.name] === 'Failed'" class="badge rounded-pill bg-danger text-light">{{ step[column.name] }}</span>
                  <span v-else-if="step[column.name] === 'Stopped'" class="badge rounded-pill bg-warning text-dark">{{ step[column.name] }}</span>
                  <span v-else-if="step[column.name] === 'Finished'" class="badge rounded-pill bg-success text-light">{{ step[column.name] }}</span>
                  <span v-else class="badge rounded-pill bg-primary text-light">{{ step[column.name] }}</span>
                </td>
                <td v-else-if="column.name === 'args'"
                    data-bs-toggle="tooltip" data-bs-placement="top" :title="step[column.name]">
                  <a href="#" v-if="step[column.name]" class="link-primary"
                          data-bs-toggle="modal" data-bs-target="#crawlStepArgs" @click="selectedStep = step">view
                  </a>
                </td>
                <td v-else-if="column.name === 'dbStats'">
                  <a href="#" v-if="step[column.name]" class="link-primary" data-bs-toggle="modal"
                          data-bs-target="#crawlStepStats" @click="selectedStep = step">db: {{ step[column.name]?.status?.['2']?.['count'] }} / solr: {{ step?.solrCount }}
                  </a>
                </td>
                <td v-else-if="column.name === 'errors'" class="text-danger text-truncate" style="max-width: 250px;"
                    data-bs-toggle="tooltip" data-bs-placement="top" :title="step[column.name]">
                  <a href="#" v-if="step[column.name]" class="link-primary"
                          data-bs-toggle="modal" data-bs-target="#crawlStepErrors" @click="selectedStep = step">errors
                  </a>
                </td>
              </template>
            </tr>
          </template>
        </Datatable>
      </div>
    </div>

    <div class="modal fade" id="crawlStepArgs" tabindex="-1" aria-labelledby="crawlStepArgs" aria-hidden="true">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ selectedStep?.stepType.toUpperCase() }} Args</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <template v-for="arg in selectedStep?.args.split(', ')" :key="arg">
              <p>{{ arg }}</p>
            </template>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>

    <div class="modal fade" id="crawlStepStats" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="crawlStepStats" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Crawl Round {{selectedStep?.round }} Stats</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="card-body">
              <h2 class="h4 mb-1">Urls in CrawlDB</h2>
              <ul class="list-group mb-3">
                <li class="list-group-item d-flex justify-content-between align-items-center text-capitalize" v-for="(value, i) in selectedStep?.dbStats?.status" :key="i">
                  {{ value.statusValue.replace(/^db_/,'') }}
                  <span >{{ value.count }}</span>
                </li>
              </ul>

              <ul class="list-group">
                <li class="list-group-item d-flex justify-content-between align-items-center">
                  <b>Total Urls in CrawlDB</b>
                  <span class="badge bg-primary rounded-pill">{{ selectedStep?.dbStats?.totalUrls }}</span>
                </li>
                <li class="list-group-item d-flex justify-content-between align-items-center">
                  <b>Total Urls in Solr</b>
                  <span class="badge bg-primary rounded-pill">{{ selectedStep?.solrCount }}</span>
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

    <div class="modal fade" id="crawlStepErrors" tabindex="-1" aria-labelledby="crawlStepErrors" aria-hidden="true">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ selectedStep?.stepType }} Error</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p class="text-danger">{{ selectedStep?.errors }}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>

    <!-- JSON Review -->
    <div class="card mb-4">
      <div class="card-header">
        <i class="fas fa-info-circle me-1"></i>
        JSON Review
        <div class="form-check form-switch float-end">
          <input class="form-check-input" type="checkbox" role="switch" :id="'flexSwitchCheck_crawlStepJson'" v-model="showJson">
        </div>
      </div>
      <div class="card-body" v-if="showJson">
        <pre>{{ print(crawlStepLogs) }}</pre>
      </div>
    </div>
<!--  </div>-->
</template>

<script>
import SchedulerService from "../../services/scheduler.service";
import Datatable from "../../components/table/Datatable";

export default {
  name: "CrawlSteps",
  components: {
    Datatable
  },
  data() {
    return {
      loading: false,
      error: null,
      crawlStepLogs: null,
      showJson: false,
      selectedStep: null,
      tableOptions: {
        crawlStepLogs: {
          // enableImport: true,
          // enableAddRow: true,
          // enableActions: true,
          order: [[0, "desc"]],
          columns: [
            { label: 'Id', name: 'id'},
            // { label: 'JobId', name: 'jobId'},
            { label: 'Step', name: 'stepType'},
            { label: 'State', name: 'state'},
            { label: 'Round', name: 'round'},
            { label: 'Args', name: 'args'},
            { label: 'Round Stats', name: 'dbStats'},
            { label: 'Errors', name: 'errors'},
            { label: 'Start Time', name: 'createdDate'},
            { label: 'End Time', name: 'updatedDate'}
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
        this.error = this.crawlStepLogs = null
        await this.getCrawlStepLogs(this.$route.params.jobId)
      }
      this.loading = false
    },
    async getCrawlStepLogs(jobId) {
      // http://localhost:8484/api/crawlsteplog/search/getCrawlStepLogsByJobId?jobId=collection1_1646083098277&sort=updatedDate,asc
      await SchedulerService.getCrawlLogs('crawlsteplog/search/getCrawlStepLogsByJobId', {jobId: jobId, sort: 'updatedDate,asc', projection: 'crawlStepLogInfo'})
          .then(res => {
            this.crawlStepLogs = res.data._embedded.crawlsteplog
            //console.log("getCrawlStepLogs result:", this.crawlStepLogs)
            this.crawlStepLogs.forEach(crawlStepLog => {
              if (crawlStepLog.dbStats) {
                console.log("dbStats", crawlStepLog.dbStats)
                let json = JSON.parse(crawlStepLog.dbStats)
                console.log("JSON parse result", json)
                crawlStepLog.dbStats = json
              }
            })
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          })
    },
    print(value) {
      return JSON.stringify(value, null, 2)
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