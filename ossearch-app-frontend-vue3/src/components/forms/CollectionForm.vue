<template>
  <template v-if="step === 1">
    <CollectionBasicInfo
        v-model:name="collection.name"
        v-model:siteUrl="collection.siteUrl"
        v-model:description="collection.description"
        v-model:keywords="collection.keywords"
    />
  </template>
  <template v-if="step === 2">
    <SearchConfigurationForm
        v-model:responseType="collection.responseType"
        v-model:resultsPerPage="collection.resultsPerPage"
        v-model:resultLimit="collection.resultLimit"
        v-model:includeFields="collection.includeFields"
    />
  </template>
  <template v-if="step === 3">
    <DynamicNavigation
        :collectionId="collection.id"
        v-model:useFacets="collection.useFacets"
        v-model:dynamicNavigations="collection.dynamicNavigations"
    />
  </template>
  <template v-if="step === 4">
    <Keymatch
        :collectionId="collection.id"
        v-model:keymatches="collection.keymatches"
    />
  </template>
  <template v-if="step === 5">
    <IncludeCollectionsTable
        v-model:includedCollections="collection.includedCollections"
    />
  </template>
  <template v-if="step === 6">
    <CollectionCrawlConfigurationForm
        :collectionName="collection.name"
        :siteUrl="collection.siteUrl"
        v-model:crawlDbPath="collection.crawlDbPath"
        v-model:crawlSeedPath="collection.crawlSeedPath"
        v-model:includeSiteUrls="collection.includeSiteUrls"
        v-model:excludeSiteUrls="collection.excludeSiteUrls"
        v-model:useSitemap="collection.useSitemap"
        v-model:sitemapUrl="collection.sitemapUrl"
        v-model:includeSitemapUrls="collection.includeSitemapUrls"
        v-model:excludeSitemapUrls="collection.excludeSitemapUrls"
        v-model:urlExclusionPatterns="collection.urlExclusionPatterns"
    />
  </template>
  <!--  <template v-if="step === 7">
      <CollectionCrawlExclusionsConfigurationForm
          :collectionName="collection.name"
          v-model:urlExclusionPatterns="collection.urlExclusionPatterns"
      />-->
  <template v-if="step === 7">
    <CollectionCrawlScheduleForm
        :collectionName="collection.name"
        v-model:crawlScheduleCron="collection.crawlCronSchedule"
        :activeTab="getActiveTab()"
        :editorData="getEditorData()"
        @updateCronEditorData="updateCronEditorData"
    />
  </template>
  <template v-if="step === 8">
    <CollectionManagers
        :collectionId="collection.id"
        v-model:users="collection.users"
        v-model:owner="collection.owner"
    />
  </template>
  <template v-if="step === 9">
    <h3>Review</h3>
    <pre>{{ print(collection) }}</pre>
  </template>
</template>

<script>
import CollectionBasicInfo from "./CollectionBasicInfo";
import SearchConfigurationForm from "./SearchConfigurationForm";
import DynamicNavigation from "./DynamicNavigation";
import Keymatch from "./Keymatch";
import IncludeCollectionsTable from "./IncludeCollectionsTable";
import CollectionCrawlConfigurationForm from "./CollectionCrawlConfigurationForm"
import CollectionManagers from "./CollectionManagers";
import CollectionCrawlScheduleForm from "./CollectionCrawlScheduleForm"
import TokenService from "../../services/token.service";
import UserService from "../../services/user.service";

export default {
  name: "CollectionForm",
  components: {
    CollectionBasicInfo,
    SearchConfigurationForm,
    DynamicNavigation,
    Keymatch,
    IncludeCollectionsTable,
    CollectionCrawlConfigurationForm,
    CollectionCrawlScheduleForm,
    CollectionManagers,
  },
  props: ['step', 'collectionData', 'selectedCollection'],
  data() {
    return {
      collection: {
        id: null,
        name: '',
        description: '',
        siteUrl: '',
        keywords: [],
        crawlDbPath: '',
        crawlSeedPath: '',
        includeSiteUrls: [],
        excludeSiteUrls: [],
        urlExclusionPatterns: [],
        sitemapUrl: '',
        useSitemap: true,
        includeSitemapUrls: [],
        excludeSitemapUrls: [],
        responseType: 'html',
        resultsPerPage: 10,
        resultLimit: 0,
        includeFields: ['title', 'content', 'url'],
        useFacets: false,
        dynamicNavigations: [],
        keymatches: [],
        includedCollections: [],
        users: [],
        owner: {},
        crawlCronSchedule: '0 0 0 ? * FRI *',
        cronEditorData: "{\"name\":\"Weekly\",\"editorData\":{\"dateTime\":\"00:00\",\"daysOfWeek\":[6]}}"
      },
    }
  },
  watch: {
    collection: {
      deep: true,
      handler: function () {
        console.log(">>>>>>>>>>>collection: " + JSON.stringify(this.collection, null, 2))
        this.$emit('update:collectionData', this.collection)
        console.log(">>>>>>>>>>>collectionData: " + JSON.stringify(this.collectionData, null, 2))
      }
    },
    selectedCollection: {
      deep: true,
      handler: function () {
        this.collection = JSON.parse(JSON.stringify(this.selectedCollection))
        console.log(">>>>>>>>>>>selectedCollection(1): " + JSON.stringify(this.selectedCollection, null, 2))
        // this.$emit('update:collectionData', this.collection)
      }
    },
  },
  mounted() {
    let loggedInUser = TokenService.getUser()
    let owner = UserService.getUserByUserName(loggedInUser.username)
    // this.userList.forEach(user => {console.log("user", user)})
    console.log("loggedInUser", loggedInUser, "owner", owner)
    // this.$emit('update:owner', owner)
  },
  methods: {
    // addField({name, index, add}) {
    //   console.log('name: ' + name + ', index:' + index + ", includeFields.length: " + this.collection.includeFields.length)
    //   if (add) {
    //     this.collection.includeFields.push({});
    //   }
    //   this.collection.includeFields[index].name = name
    // },
    // removeField(index) {
    //   this.collection.includeFields.splice(index, 1)
    // },
    // addDynamicNavigation(entry) {
    //   console.log('[add DynamicNavigation] entry: ' + JSON.stringify(entry, null, 2))
    //   this.collection.dynamicNavigations.push(entry)
    // },
    // deleteDynamicNavigation(index) {
    //   console.log('[delete DynamicNavigation] index: ' + index)
    //   this.collection.dynamicNavigations.splice(index, 1)
    // },
    // updateDynamicNavigation(entry) {
    //   console.log('[update DynamicNavigation] entry: ' + JSON.stringify(entry.value, null, 2) + ', index: ' + entry.index)
    //   this.collection.dynamicNavigations[entry.index] = entry.value
    // },
    // addKeymatch(entry) {
    //   console.log('[add keymatch] entry: ' + JSON.stringify(entry, null, 2))
    //   this.collection.keymatches.push(entry)
    // },
    // deleteKeymatch(index) {
    //   console.log('[delete keymatch] index: ' + index)
    //   this.collection.keymatches.splice(index, 1)
    // },
    // updateKeymatch(entry) {
    //   console.log('[update keymatch] entry: ' + JSON.stringify(entry.value, null, 2) + ', index: ' + JSON.stringify(entry.index, null, 2))
    //   this.collection.keymatches[entry.index] = entry.value
    // },
    // addIncludeSitemapUrls(event) {
    //   const urls = event.trim().split(/\r\n|\n/)
    //   console.log(urls)
    //   this.collection.includeSiteUrls.push(...urls)
    // },
    getActiveTab() {
      let cronEditorData = JSON.parse(this.collection.cronEditorData)
      console.log('getActiveTab', cronEditorData.name)
      return cronEditorData.name
    },
    getEditorData() {
      let cronEditorData = JSON.parse(this.collection.cronEditorData)
      console.log('getEditorData', cronEditorData.editorData)
      return cronEditorData.editorData
    },
    print(value) {
      return JSON.stringify(value, null, 2)
    },
    updateCronEditorData(event) {
      console.log('updateCronEditorData', event)
      this.collection.cronEditorData = event;
    }
  }

}
</script>

<style scoped>

</style>