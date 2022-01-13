<template>
  <div v-bind:class="serviceStatus === 'UP' ? 'alert-success' : 'alert-danger'"  class="alert d-flex align-items-center" role="alert">
    <i v-bind:class="serviceStatus === 'UP' ? 'bi-check-circle-fill' : 'bi-x-circle-fill'"  style="font-size: x-large" class="bi bi-check-circle-fill flex-shrink-0 me-2" aria-label="Success:"></i>
    <div>
      {{ serviceName }}
    </div>
  </div>
</template>

<script>
export default {
  name: "ServiceStatus",
  props: {
    serviceName: String,
    serviceStatus: String
  },
  mounted() {
    console.log(this.serviceName +": "+this.serviceStatus)
  }
}
</script>

<style scoped>


</style>