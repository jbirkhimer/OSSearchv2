<template>
  <footer class="d-flex flex-wrap justify-content-between align-items-center py-3 my-4 border-top fixed-bottom">
    <div class="col-md-4 d-flex align-items-center justify-content-center">
      <span class="text-muted text-center">
        Office of The Chief Information Officer <br>
        System Architecture & Product Assurance <br>
        Web Services Division.
      </span>
    </div>
    <ul class="nav col-md-4 justify-content-end list-unstyled d-flex">
      <p>Version {{ app_version }} | {{ app_date }}</p>
    </ul>
  </footer>
</template>

<script>
export default {
  name: "Footer"
}
</script>

<style scoped>

</style>