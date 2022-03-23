<template>
  <fieldset :disabled="!isEditing">
    <!--  <h3>Search Keymatch Configuration</h3>-->

    <ImportAddEditCheckTable
        v-model:tableOptions="tableOptions"
        :tableData="keymatches"
        @updateTableData="$emit('update:keymatches', $event)"
    />
  </fieldset>
</template>

<script>
import ImportAddEditCheckTable from "../forms/ImportAddEditCheckTable";

export default {
  name: "Keymatch",
  props: ['isEditing', 'collectionID', 'keymatches'],
  components: {
    ImportAddEditCheckTable
  },
  data() {
    let keymatchTypes = [
          {label: 'KeywordMatch', value: 'keyword'},
          {label: 'PhraseMatch', value: 'phrase'},
          {label: 'ExactMatch', value: 'exact'}
        ]
    let columns = [
      {label: 'Tilte for Match', name: 'titleForMatch', class: 'text-center'},
      {label: 'Search Term', name: 'searchTerm', class: 'text-center'},
      {label: 'Keymatch Type', name: 'keymatchType', type: 'select', options: keymatchTypes, class: 'text-center', default: 'keyword'},
      {label: 'URL for Match', name: 'urlForMatch', class: 'text-center'},
      {label: 'Creation Type', name: 'creationType', class: 'text-center', default: 'created', disabled: true},
    ]
    return {
      tableOptions: {
        enableImport: true,
        enableAddRow: true,
        enableActions: true,
        columns: columns
      },
      keymatch: {
        titleForMatch: '',
        searchTerm: '',
        keymatchType: '',
        urlForMatch: '',
        creationType: 'created',
      }
    }
  },
  methods: {

  }
}
</script>

<style scoped>

</style>