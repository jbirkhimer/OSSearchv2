<template>
  <footer class="footer navbar-static-bottom mt-auto py-3 bg-light">
    <div class="container-fluid px-4">
      <div class="d-flex align-items-center justify-content-between small">
        <div class="text-muted"><a href="#">Contact</a></div>
        <div class="text-muted text-wrap">Office of The Chief Information Officer System Architecture & Product Assurance Web
          Services Division.
        </div>
        <div class="text-muted"><a href="#">Version: {{ app_version }} | Release Date: {{ app_date }}</a></div>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer2',
  data() {
    return {
      app_version: process.env.VUE_APP_VERSION,
      app_date: process.env.VUE_APP_BUILD_DATE
    }
  }
}
</script>

<style lang="scss" scoped>

</style>