<template>
  <h2>TabContent | {{name}} | {{collectionId}} | {{tabName}}</h2>



</template>

<script>
import CollectionService from "../../services/collection.service";
// import Tabs from "./Tabs";
// import CollectionDetails from "../../views/collections/CollectionDetails";
// import AddRemoveUrls from "../../views/collections/utils/AddRemoveUrls";
// import KeywordReport from "../../views/reports/KeywordReport";
// import KeywordCountReport from "../../views/reports/KeywordCountReport";
// import IndexedUrlsReport from "../../views/reports/IndexedUrlsReport";
// import UrlReport from "../../views/reports/UrlReport";
// import CrawldbReport from "../../views/reports/CrawldbReport";
// import ChangeHistory from "../../views/collections/utils/ChangeHistory";
// import BackupRestoreCollection from "../../views/collections/utils/BackupRestoreCollection";

export default {
  name: "TabContent",
  props: ['name', 'tabName'],
  components: {
    // Tabs,
    // CollectionDetails,
    // AddRemoveUrls,
    // KeywordReport,
    // KeywordCountReport,
    // IndexedUrlsReport,
    // UrlReport,
    // CrawldbReport,
    // ChangeHistory,
    // BackupRestoreCollection
  },
  data() {
    return {
      collection: null,
      tabs: [
        {name: 'configuration', label: 'Configuration', icon: 'fas fa-tools'},
        {name: 'addRemoveUrls', label: 'Add/Remove URLs', icon: 'fas fa-filter'},
        {name: 'reportsTab', label: 'Reports', type: 'dropdown', icon: 'fas fa-chart-area', subTabs: [
            {name: 'keywordReport', label: 'Keyword Report'},
            {name: 'keywordCountReport', label: 'Keyword Count Report'},
            {name: 'indexedUrlsReport', label: 'Indexed URLs Report'},
            {name: 'urlReport', label: 'URL Report'},
            {name: 'crawldbReport', label: 'Crawldb Report'},
          ]},
        // {name: 'keywordReport', label: 'Keyword Report'},
        // {name: 'keywordCountReport', label: 'Keyword Count Report'},
        // {name: 'indexedUrlsReport', label: 'Indexed Urls Report'},
        // {name: 'urlReport', label: 'Url Report'},
        // {name: 'crawldbReport', label: 'Crawldb Report'},
        {name: 'changeHistory', label: 'Change History', icon: 'fas fa-history'},
        {name: 'backupRestore', label: 'Backup/Restore', icon: 'fas fa-database'}
      ],
    }
  },
  computed: {
    isActiveTab() {
      return this.activeTab === this.tabName
    },
    index() {
      let index = 0
      for (let i in this.tabs) {
        if (this.tabs[i].subTabs) {
          for (let subIndex in this.tabs[i].subTabs) {
            if (this.tabs[i].subTabs[subIndex].name === this.tabName) {
              index = i
            }
          }
        }
        if (this.tabs[index].name === this.tabName) {
          index = i
        }
      }
      return index
    }
  },
  created() {
    // watch the params of the route to fetch the data again
    this.$watch(
        () => this.$route.params.name,
        async () => {
          await this.fetchData()
        },
        // fetch the data when the view is created and the data is
        // already being observed
        {immediate: true}
    )
    // return this.v$.$touch;
  },
  methods: {
    async fetchData() {
      this.loading = true
      if (Object.keys(this.$route.params).length !== 0) {
        //console.log(">>>> params", this.$route.params, "query", this.$route.query)
        this.error = this.collection = null
        await this.getCollection()
        this.activeTab = this.tabs[0].name
      }
      this.loading = false
    },
    async getCollection() {
      await CollectionService.getCollections('/collection/search/getCollectionByName', {name: this.$route.params.name, projection: 'collectionFormData'})
          .then(response => {
            this.collection = response.data;
            // console.log("collection", this.collection)
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
    }
  }
}
</script>

<style scoped>

</style>