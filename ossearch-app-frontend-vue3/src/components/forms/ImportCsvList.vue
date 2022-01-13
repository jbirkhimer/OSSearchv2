<template>
  <label>
    <button class="btn btn-sm btn-primary" @click.prevent="$refs.file.click()">Import</button>
    <input @change="loadTextFromFile" @click="$refs.file.value = null" type="file" ref="file" accept="text/csv" :value="inputValue" hidden/>
  </label>
</template>

<script>
export default {
  name: "ImportCsvList",
  data() {
    return {
      inputValue: null
    }
  },
  methods: {
    loadTextFromFile(ev) {
      console.log(ev)
      const file = ev.target.files[0];
      const reader = new FileReader();

      reader.onload = e => this.$emit("load", e.target.result);
      reader.onerror = e => (new Error(`Error reading ${file.name}: ${e.target.result}`))
      reader.readAsText(file);
    }
  }
}
</script>

<style scoped>

</style>