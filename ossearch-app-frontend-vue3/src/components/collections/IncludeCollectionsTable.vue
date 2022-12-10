<template>
  <fieldset :disabled="!isEditing">
    <!--  <div style="height: 300px; overflow: auto;">-->
    <ImportAddEditCheckTable
        :tableOptions="{
          enableImport: false,
          enableAddRow: false,
          enableActions: false,
          actionDisabledDefaultValues: [this.includedCollectionId],
          columns: [
            {label: 'Collection ID', name: 'id', width: '15%;', class: 'text-center'},
            {label: 'Name', name: 'name', width: '85%', class: 'text-center'}
          ]
        }"
        :tableData="collections"
        :selected="includedCollections"
        @selected="$emit('update:includedCollections', $event)"
        uniqueCheckField="id"
        :isEditing="isEditing"
    />
    <!--  </div>-->
  </fieldset>
</template>

<script>
import ImportAddEditCheckTable from "../forms/ImportAddEditCheckTable";

export default {
  name: "IncludeCollectionsTable",
  props: ['isEditing', 'includedCollections', 'collections', 'includedCollectionId'],
  components: {
    ImportAddEditCheckTable
  },
  data() {
    return {
      tableOptions: {
        includeCollections: {
          enableImport: false,
          enableAddRow: false,
          enableActions: false,
          columns: [
            {label: 'Collection ID', name: 'id', width: '15%;', class: 'text-center'},
            {label: 'Name', name: 'name', width: '85%', class: 'text-center'}
          ]
        }
      }
    }
  }
}
</script>

<style scoped lang="scss">
/*.list-group {
  max-height: 300px;
  margin-bottom: 10px;
  overflow-y: scroll;
  -webkit-overflow-scrolling: touch;
}*/
</style>
