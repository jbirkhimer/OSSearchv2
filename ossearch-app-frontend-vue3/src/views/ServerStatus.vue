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
      solrSlave: ''
    }
  },
  components: {
    ServiceStatus
  },
  mounted() {
    ServerStatusService.getServerStatus().then(
        response => {
          //this.json = JSON.stringify(response.data, null, 2)
          //console.log(this.json)
          this.json = response.data
          // this.backend_status = this.json.status
          this.db_status = this.json.components.db.status
          this.ldap = this.json.components.ldap.status
          this.solr_master = this.json.components.solr.status
          // console.log("db:" + this.db_status)
          // console.log("ldap: "+this.ldap)
          // console.log("solr: "+this.solr_master)
        }
    )
  }
}
</script>

<style scoped>

</style>