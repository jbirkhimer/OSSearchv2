<template>
  <button @click="downloadFile">Download PDF</button>
</template>

<script>
import html2pdf from 'html2pdf.js'

export default {
  name: 'DownloadButton',
  props: ['dom', 'name'],
  methods: {
    downloadFile () {
      let view = document.querySelector(this.dom)
      let opt = {
        margin: 1,
        filename: this.name,
        image: { type: 'jpeg', quality: 0.98 },
        pagebreak: { mode: ['avoid-all', 'css', 'legacy'], after: ['.card'] }
      }
      html2pdf().from(view).set(opt).save()
    }
  }
}
</script>

<style scoped>

</style>