<template>
  <nav style="--bs-breadcrumb-divider: '>';" aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="/dashboard">Dashboard</a></li>
      <template v-for="(path, i) in pathArray" :key="i">
        <li class="breadcrumb-item text-capitalize" :class="i === pathArray.length - 1 ? 'active' : ''">
          <a v-if="i === pathArray.length - 1 && query">{{ path+'?'+query }}</a>
          <a v-else-if="i === pathArray.length - 1">{{ path }}</a>
          <a v-else :href="getHref(i)">{{ path }}</a>
        </li>
      </template>
    </ol>
  </nav>
</template>

<script>
export default {
  name: "Breadcrumb",
  data() {
    return {
      pathArray: [],
      query: ''
    }
  },
  created() {
    // watch the params of the route to fetch the data again
    this.$watch(
        () => this.$route.fullPath,
        async () => {
          let fullpath = this.$route.fullPath
          // this.query = fullpath.substring(0, fullpath.indexOf('?'))
          let pathQuery = fullpath.split("?")
          this.pathArray = pathQuery[0].split('/')
          this.pathArray.shift()
          this.query = pathQuery[1]
        },
        // fetch the data when the view is created and the data is
        // already being observed
        {immediate: true}
    )
    // let fullpath = this.$route.fullPath
    // this.pathArray = fullpath.split('/')
    // this.pathArray.shift()
  },
  methods: {
    getHref(i) {
      let href = "/"+this.pathArray.slice(0,i+1).join('/')
      //console.log("href",href)
      return href
    }
  }
}
</script>

<style scoped>

</style>