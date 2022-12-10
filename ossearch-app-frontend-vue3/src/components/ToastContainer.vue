<template>
  <div class="toast-container position-fixed bottom-0 end-0 p-2">
    <Toast v-for="toast in toasts" :key="toast" :toast="toast"/>
  </div>
</template>

<script>
import Toast from "./Toast";

export default {
  name: "ToastContainer",
  props: ['toasts'],
  components: {
    Toast
  }
}
</script>

<style scoped>

</style>