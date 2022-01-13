<template>
  <select v-model.number="selectedRowsPerPage" @input="$emit('rowsPerPage', $event.target.value)"
          class="form-select form-select">
    <option v-for="numRows in rowsPerPageSelect" :key="numRows" :value="numRows" :selected="rowsPerPage">
      {{ numRows }}
    </option>
  </select>
</template>

<script>
export default {
  name: "RowsPerPageSelect",
  props: {
    rowsPerPageSelect: {
      type: Array,
      default: () => [5, 10, 20, 50, 100]
    },
    rowsPerPage: {
      type: Number,
      default: 10
    }
  },
  data() {
    return {
      selectedRowsPerPage: this.rowsPerPage
    }
  }
}
</script>

<style scoped lang="scss">
</style>