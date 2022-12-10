<template>
  <fieldset :disabled="!isEditing">
    <!--  <h3>Search Keymatch Configuration</h3>-->

    <ImportAddEditCheckTable
        v-model:tableOptions="tableOptions"
        :tableData="keymatches"
        @updateTableData="$emit('update:keymatches', $event)"
        :isEditing="!isEditing"
        :loading="loading"
        :saving="saving"
    />
  </fieldset>
</template>

<script>
import ImportAddEditCheckTable from "../forms/ImportAddEditCheckTable";

export default {
  name: "Keymatch",
  props: ['isEditing', 'collectionId', 'keymatches', 'loading', 'saving'],
  components: {
    ImportAddEditCheckTable
  },
  data() {
    return {
      tableOptions: {
        id: 'keymatchTable',
        enableImport: true,
        enableAddRow: true,
        enableActions: true,
        order: [[1, 'asc']],
        responsive: false,
        columns: [
          {label: 'Search Term', name: 'searchTerm', class: 'text-center', width: '25%'},
          {label: 'Title for Match', name: 'titleForMatch', class: 'text-center', width: '25%'},
          {label: 'URL for Match', name: 'urlForMatch', class: 'text-center', width: this.isEditing ? '40%' : '35%'},
          {label: 'Keymatch Type', name: 'keymatchType', type: 'select', width: this.isEditing ? '10%' : '15%', options: [
              {label: 'KeywordMatch', value: 'keyword'},
              {label: 'PhraseMatch', value: 'phrase'},
              {label: 'ExactMatch', value: 'exact'}
            ], class: 'text-center', default: 'keyword'},
          // {label: 'Creation Type', name: 'creationType', class: 'text-center', default: 'created', disabled: true},
        ]
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
