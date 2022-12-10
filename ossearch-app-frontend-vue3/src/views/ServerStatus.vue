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
        <ServiceStatus serviceName="Solr Slave"
                       v-bind:serviceStatus="solr_slave"/>

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
import EventBus from "../common/EventBus";

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
      solr_slave: '',
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
    async getServerStatus() {
      await ServerStatusService.getServerStatus().then(
          response => {
            this.json = response.data
            this.db_status = this.json.components?.db?.status
            this.ldap = this.json.components?.ldap?.status
            this.solr_master = this.json.components?.solr?.components?.master?.status
            this.solr_slave = this.json.components?.solr?.components?.slave?.status
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