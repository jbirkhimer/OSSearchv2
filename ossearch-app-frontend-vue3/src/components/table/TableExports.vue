<template>
  <div class="btn-group flex-wrap">
    <a class="btn btn-secondary btn-sm btn-primary" :href="getUrl('csv')" target="_blank">
      <span><i class="fas fa-file-csv"></i> CSV</span>
    </a>
    <a class="btn btn-secondary btn-sm btn-primary" :href="getUrl('xlsx')" target="_blank">
      <span><i class="fas fa-file-excel"></i> Excel</span></a>
<!--    <a class="btn btn-secondary btn-sm btn-primary" title="PDF" :href="getUrl('pdf')" target="_blank">
      <span><i class="fas fa-file-pdf"></i> PDF</span>
    </a>-->
<!--    <button class="btn btn-secondary buttons-print btn-sm btn-primary" tabindex="0"
            aria-controls="keywordSearchTable" type="button" title="Print">
      <span><i class="fas fa-print"></i> Print</span>
    </button>-->

  </div>
</template>

<script>
export default {
  name: "TableExports",
  props: ['exportBaseUrl'],
  methods: {
    getUrl(type) {
      let url = null
      if (this.exportBaseUrl) {
        url = new URL(this.exportBaseUrl)
        url.searchParams.append("exportType", type)
        console.log("export url", url.toString())
      }
      return url
    }
  }
}
</script>

<style scoped>

</style>