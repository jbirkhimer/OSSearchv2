<template>
  <div v-if="items" class="container-fluid px-4">
    <!--    <h1 class="mt-4">Overview</h1>-->
    <Breadcrumb/>


<!--    <button type="button" class="btn btn-primary" @click="$router.push({name: 'crawlScheduleForm', query: { collectionId: 1, collectionName: 'collection1' }})">Test Create Form</button>-->

    <div class="card mb-4">
      <div class="card-header">
        <i class="fas fa-sitemap me-1"></i>
        Collections
        <div class="float-end">
          <router-link type="button" class="btn btn-sm btn-primary bi-plus-lg float-end" to="/scheduler/create">
            Crawl Schedule
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
            :tableData="tableData"
            :tableOptions="tableOptions"
                   id="crawlSchTable"
        >
          <template v-slot:table-body>
            <tr v-for="(job, i) in tableData" :key="i">
              <!-- Loop Row Cells -->
              <template v-for="(key, j) in tableOptions.columns" :key="j">
                <td v-if="!['jobName', 'actions', 'control', 'logs'].includes(key.name)">{{ job[key.name] }}</td>
                <td v-if="key.name === 'jobName'">
                  <router-link :to="{ name: 'crawlScheduleDetails', params: { jobName: job.jobName, groupName: job.groupName, id: job.id }}">{{ job[key.name] }}</router-link>
                </td>
                <td v-if="key.name === 'actions'" class="justify-content-evenly text-center">
                  <div class="btn-group btn-group-sm align-items-center">
                    <router-link class="btn link-primary p-0 m-2" :to="{ name: 'crawlScheduleDetails', params: { jobName: job.jobName, groupName: job.groupName, id: job.id }}"><i class="fa fa-edit"></i></router-link>
                    <a href="#" class="btn link-danger p-0" data-bs-toggle="modal" data-bs-target="#deleteModal" title="Delete" @click.prevent="selectedCrawlSchedule = job">
                      <i class="fa fa-times-circle"></i>
                    </a>
                  </div>
                </td>
                <td v-if="key.name === 'logs'" class="ps-4">
                  <router-link class="btn-link link-primary" :to="{name: 'crawlLogs', params: { jobName: job.jobName, jobGroup: job.groupName }}"><i class="fas fa-tasks"></i></router-link>
                </td>
                <td v-if="key.name === 'control'" class="justify-content-evenly text-center">
                  <div class="btn-group btn-group-sm align-items-center">
                    <a href="#" class="btn-link link-success p-0 m-2" @click="startCrawl(job)"><i class="fas fa-play-circle"></i></a>
                    <a href="#" class="btn-link link-warning p-0 m-2" @click="pauseCrawl(job)"><i class="fas fa-pause-circle"></i></a>
                    <a href="#" class="btn-link link-secondary p-0 m-2" @click="resumeCrawl(job)"><i class="fas fa-sync-alt"></i></a>
                    <a href="#" class="btn-link link-danger p-0" @click="stopCrawl(job)"><i class="fas fa-stop-circle"></i></a>
                  </div>
                </td>
              </template>

            </tr>
          </template>
        </Datatable>
      </div>
    </div>
  </div>


  <Modal v-if="selectedCrawlSchedule" id="deleteModal">
    <template v-slot:header>
      <h5 class="modal-title text-black">Delete Scheduled Crawl</h5>
    </template>
    <template v-slot:body>
      <p>Are you sure you want to delete scheduled crawl <b>{{ selectedCrawlSchedule.jobName }} {{ selectedCrawlSchedule.jobGroup }}</b>! This
        can not be undone!</p>
    </template>
    <template v-slot:button-action>
      <button type="button" class="btn btn-danger" data-bs-dismiss="modal"
              @click.prevent="deleteCrawlSchedule(selectedCrawlSchedule.jobName, selectedCrawlSchedule.groupName)">Delete
      </button>
    </template>
  </Modal>


</template>

<script>
import Datatable from "../../components/table/Datatable";
import Breadcrumb from "../../components/Breadcrumb";
import Modal from "../../components/Modal";
import SchedulerService from "../../services/scheduler.service";

export default {
  name: "CrawlScheduler",
  components: {
    Breadcrumb,
    Datatable,
    Modal,
  },
  data() {
    return {
      loading: false,
      error: null,
      tableOptions: {
        order: [[2, "asc"]],
        columns: [
          // {label: 'Id', name: 'id'},
          // {label: 'Job Id', name: 'jobId'},
          {label: 'Control', name: 'control', class: 'text-center'},
          {label: 'Logs', name: 'logs', class: 'text-center'},
          {label: 'Job Name', name: 'jobName'},
          {label: 'Job Group', name: 'groupName'},
          {label: 'Cron Schedule', name: 'cronExpression'},
          // {label: 'State', name: 'state'},
          {label: 'Status', name: 'jobStatus'},
          // {label: 'Progress', name: 'progress'},
          // {label: 'Description', name: 'description'},
          {label: 'Start Time', name: 'startTime'},
          {label: 'Last Run', name: 'lastFiredTime'},
          {label: 'Next Run', name: 'nextFireTime'},
          {label: 'Final Run', name: 'finalFiredTime'},
          {label: 'Actions', name: 'actions', class: 'text-center'}
        ],
      },
      tableData: [],
      items: [
        {
          id: 1,
          name: 'test_name',
          group: 'test_group',
          crawlCronSchedule: 'crawlCronSchedule',
          state: 'state',
          progress: 'progress',
          description: 'description',
          lastRun: 'lastRun',
          nextRun: 'nextRun',

        }
      ],
      selectedCrawlSchedule: {},
      itemIndex: null
    }
  },
  mounted() {
    console.log("process.env.BASE_URL", process.env.BASE_URL)
    this.getAllJobs()
  },
  methods: {
    getAllJobs() {
      this.loading = true
      SchedulerService.getAllJobs("/scheduler/getAllJobs2")
          .then(res => {
            this.tableData = res.data
            //console.log("schedule getAllJobs:", this.tableData)
            this.loading = false
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          })
    },
    async deleteCrawlSchedule(jobName, jobGroup) {
      await SchedulerService.deleteCrawlJob("/scheduler/delete", { jobName: jobName, jobGroup: jobGroup })
          .then(response => {
            let data = response.data;
            console.log("delete CrawlJobSchedule", JSON.stringify(data, null, 2))
            this.selectedCrawlSchedule = null
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
      this.getAllJobs()
    },
    async startCrawl(job) {
      await SchedulerService.startCrawlJob("/scheduler/start", {jobName: job.jobName, jobGroup: job.groupName})
          .then(response => {
            let data = response.data;
            console.log("start CrawlJobSchedule", JSON.stringify(data, null, 2))
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
      this.getAllJobs()
    },
    async pauseCrawl(job) {
      await SchedulerService.pauseCrawlJob("/scheduler/pause", {jobName: job.jobName, jobGroup: job.groupName})
          .then(response => {
            let data = response.data;
            console.log("pause CrawlJobSchedule", JSON.stringify(data, null, 2))
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
      this.getAllJobs()
    },
    async resumeCrawl(job) {
      await SchedulerService.resumeCrawlJob("/scheduler/resume", {jobName: job.jobName, jobGroup: job.groupName})
          .then(response => {
            let data = response.data;
            console.log("resume CrawlJobSchedule", JSON.stringify(data, null, 2))
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
      this.getAllJobs()
    },
    async stopCrawl(job) {
      await SchedulerService.stopCrawlJob("/scheduler/stop", { jobName: job.jobName, jobGroup: job.groupName })
          .then(response => {
            let data = response.data;
            console.log("stop CrawlJobSchedule", JSON.stringify(data, null, 2))
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
      this.getAllJobs()
    },
  }
}
</script>

<style scoped>

</style>