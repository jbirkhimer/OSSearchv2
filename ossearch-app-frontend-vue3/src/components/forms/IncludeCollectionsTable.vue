<template>
<!--  <div style="height: 300px; overflow: auto;">-->
    <ImportAddEditCheckTable
        :tableOptions="tableOptions.includeCollections"
        :tableData="collections"
        :selected="includedCollections"
        @selected="includedCollection"
    />
<!--  </div>-->
</template>

<script>
import ImportAddEditCheckTable from "./ImportAddEditCheckTable";
import CollectionService from "../../services/collection.service";

export default {
  name: "IncludeCollectionsTable",
  props: ['includedCollections'],
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
    getCollections(url = '/collection', params = {projection: 'includedCollectionIdNameInfo'}) {

      console.log("[getCollections] url: " + url + ", prams: " + JSON.stringify(params), null, 2)

      // this.tableData.draw++;
      CollectionService.getCollections(url, params)
          .then(response => {
            let data = response.data;

            this.collections = data._embedded.collection;
          })
          .catch(errors => {
            console.log(errors);
          });
    },
    includedCollection(collections) {
      //console.log("[includedCollections] collections",JSON.stringify(collections))

      collections.forEach(e => {
        delete e._links
      })
      //console.log("[includedCollections] newArray",JSON.stringify(collections))
      this.$emit('update:includedCollections', collections)
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