<template>
  <fieldset :disabled="!isEditing">
    <!--  <div style="height: 300px; overflow: auto;">-->
    <ImportAddEditCheckTable
        :tableOptions="{
          enableImport: false,
          enableAddRow: false,
          enableActions: false,
          actionDisabledDefaultValues: [this.collectionId],
          columns: [
            {label: 'Collection ID', name: 'id', width: '15%;', class: 'text-center'},
            {label: 'Name', name: 'name', width: '85%', class: 'text-center'}
          ]
        }"
        :tableData="collections"
        :selected="includedCollections"
        @selected="$emit('update:includedCollections', $event)"
        uniqueCheckField="id"
    />
    <!--  </div>-->
  </fieldset>
</template>

<script>
import ImportAddEditCheckTable from "../forms/ImportAddEditCheckTable";
import CollectionService from "../../services/collection.service";

export default {
  name: "IncludeCollectionsTable",
  props: ['isEditing', 'includedCollections', 'collectionId'],
  components: {
    ImportAddEditCheckTable
  },
  data() {
    return {
      collections: [],
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
  },
  methods: {
    getCollections(url = '/collection', params = {projection: 'collectionIdNameInfo'}) {

      //console.log("[getCollections] url: " + url + ", prams: " + JSON.stringify(params), null, 2)

      // this.tableData.draw++;
      CollectionService.getCollections(url, params)
          .then(response => {
            let data = response.data;

            if (this.collectionId) {
              this.collections = data._embedded.collection.filter(collection => collection.id !== this.collectionId );
            } else {
              this.collections = data._embedded.collection;
            }
          })
          .catch(errors => {
            console.log(errors);
          });
    }
  },
  mounted() {
    this.getCollections()
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