<template>
  <div class="container">
    <div class="row">
      <div class="col-md-12">
        <h1>Backend Status</h1>
      </div>
    </div>
    <div class="row clearfix">
      <div class="col-md-12 column">
<!--        <div class="panel alert-warning">
          <div class="panel-heading">
            <h3 class="panel-title">
              Not All Systems Operational
              <small class="float-sm-end">Refreshed 39 minutes ago</small>
            </h3>
          </div>
        </div>-->

        <ServiceStatus serviceName="Database"
                       v-bind:serviceStatus="db_status"/>
        <ServiceStatus serviceName="LDAP"
                       v-bind:serviceStatus="ldap"/>
        <ServiceStatus serviceName="Solr Master"
                       v-bind:serviceStatus="solr_master"/>

        <div :class="scheduler_status.started ? 'alert-success' : 'alert-danger'"  class="alert d-flex align-items-center" role="alert">
          <i :class="scheduler_status.started ? 'bi-check-circle-fill' : 'bi-x-circle-fill'"  style="font-size: x-large" class="bi bi-check-circle-fill flex-shrink-0 me-2" aria-label="Success:"></i>
          <div>
            Quartz Scheduler running since {{ scheduler_status.runningSince }}
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import ServerStatusService from "../services/server-status.service"
import ServiceStatus from "../components/ServiceStatus";

export default {
  name: "ServerStatus",
  data() {
    return {
      json: '',
      backend_status: '',
      db_status: '',
      schedulerStatus: '',
      ldap: '',
      solr_master: '',
      solrSlave: '',
      scheduler_status: '',
    }
  },
  components: {
    ServiceStatus
  },
  mounted() {
    this.getServerStatus()
    this.getSchedulerStatus()

  },
  methods: {
    async getServerStatus() {
      await ServerStatusService.getServerStatus().then(
          response => {
            //this.json = JSON.stringify(response.data, null, 2)
            this.json = response.data
            // console.log(JSON.stringify(this.json, null, 2))
            // this.backend_status = this.json.status
            this.db_status = this.json.components?.db?.status
            this.ldap = this.json.components?.ldap?.status
            this.solr_master = this.json.components?.solr?.status
            // console.log("db:" + this.db_status)
            // console.log("ldap: "+this.ldap)
            // console.log("solr: "+this.solr_master)
          }
      )
    },
    async getSchedulerStatus() {
      await ServerStatusService.getSchedulerStatus().then(
          response => {
            this.scheduler_status = response.data
            console.log(this.scheduler_status)

          }
      )
    }
  }
}
</script>

<style scoped>

</style>