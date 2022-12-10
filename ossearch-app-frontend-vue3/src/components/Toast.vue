<template>
  <div :id="toast.id" class="toast" role="alert" aria-live="assertive" aria-atomic="true" :ref="toast.id">
    <div class="toast-header border-0" :class="type[toast.type].bg + ' ' + type[toast.type].text">
      <i class="me-2" :class="type[toast.type].icon  + ' ' + type[toast.type].text"></i>
      <strong class="me-auto text-uppercase">{{ type[toast.type].title }}</strong>
      <small :class="type[toast.type].text">Just Now</small>
      <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
    <div class="toast-body" :class="type[toast.type].bg + ' ' + type[toast.type].text">
      {{ toast.msg }}
    </div>
  </div>

<!--  <div :id="toast.id" :ref="toast.id" class="toast align-items-center border-0" :class="type[toast.type].bg + ' ' + type[toast.type].text" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body">
        <i class="me-2" :class="type[toast.type].icon  + ' ' + type[toast.type].text"></i>
        <strong class="me-auto text-capitalize">{{ toast.title }}</strong>
        <small :class="type[toast.type].text">{{ toast.time }}</small>
        {{ toast.msg }}
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  </div>-->

<!--  <div :id="toast.id" :ref="toast.id" class="toast align-items-center border-0" :class="type[toast.type].bg + ' ' + type[toast.type].text" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="d-flex flex-row align-items-center">
      <i class="flex-shrink-0 p-2" :class="type[toast.type].icon  + ' ' + type[toast.type].text"></i>
      <div class="d-flex flex-column">
        <Strong class="text-capitalize p-2">{{ toast.title }}</Strong>
        <p class="flex-grow p-2">{{ toast.msg }} By default search results will be returned with Title, Snippet, and URL. Site owners can optionally select Anchor, Meta Description and Meta Keywords to be returned with the search results.</p>
      </div>
&lt;!&ndash;      <small :class="type[toast.type].text">{{ toast.time }}</small>&ndash;&gt;
      <button type="button" class="btn-close btn-close-white flex-shrink-0 p-2" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  </div>-->

</template>

<script>
import { Toast } from 'bootstrap'

export default {
  name: "Toast",
  props: ['toast'],
  data() {
    return {
      type: {
        success: { title: 'Success', text: 'text-white', bg: 'bg-success bg-opacity-75', icon: 'fas fa-check-circle fa-xl'},
        danger: { title: 'Error', text: 'text-white', bg: 'bg-danger bg-opacity-75', icon: 'fas fa-exclamation-circle fa-xl'},
        info: { title: 'Info', text: 'text-white', bg: 'bg-primary bg-opacity-75', icon: 'fas fa-info-circle fa-xl'},
        warning: { title: 'Warning', text: 'text-muted', bg: 'bg-warning bg-opacity-75', icon: 'fas fa-exclamation-triangle fa-xl'}
      }
    }
  },
  mounted() {
    this.$watch(
        () => this.toast,
        async () => {
          console.log("(1) show toast", this.toast.id)
          let toast = new Toast(this.$refs[this.toast.id])
          toast.show()
          console.log("(1) show toast", this.toast.id)
        },
        // fetch the data when the view is created and the data is
        // already being observed
        { immediate: true }
    )
  },
  watch: {
    toast: {
      deep: true,
      handler: async function () {
        let toast = new Toast(this.$refs[this.toast.id])
        toast.show()
      }
    }
  }
}
</script>

<style scoped lang="scss">
.text-muted {
  --bs-text-opacity: 1;
  color: #6c757d !important;
}
</style>