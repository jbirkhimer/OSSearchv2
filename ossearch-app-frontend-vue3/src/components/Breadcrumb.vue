<template>
  <nav style="--bs-breadcrumb-divider: '>';" aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="/dashboard">Dashboard</a></li>
      <template v-for="(path, i) in pathArray" :key="i">
        <li class="breadcrumb-item" :class="i === pathArray.length - 1 ? 'active' : ''">
          <a v-if="i === pathArray.length - 1 && path.indexOf('?') > 0">{{ path.substr(0, path.indexOf('?')) }}</a>
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
      pathArray: []
    }
  },
  created() {
    let fullpath = this.$route.fullPath
    this.pathArray = fullpath.split('/')
    this.pathArray.shift()
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