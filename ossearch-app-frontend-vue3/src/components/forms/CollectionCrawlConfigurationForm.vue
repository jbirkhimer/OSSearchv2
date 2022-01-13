<template>
  <div class="row g-3 mb-3">
    <div class="col-md-6">
      <div class="form-floating">
        <input :value="crawlDbPath" @input="$emit('update:crawlDbPath', $event.target.value)" class="form-control"
               id="crawlDbPath"
               required
               :placeholder="'./' + collectionName + '/db'">
        <label for="crawlDbPath">Crawl db path</label>
      </div>
    </div>
    <div class="col-md-6">
      <div class="form-floating">
        <input :value="crawlSeedPath" @input="$emit('update:crawlSeedPath', $event.target.value)" type="text"
               class="form-control" id="crawlSeedPath"
               required
               placeholder="'./'+ collectionName + '/seeds.txt'">
        <label for="crawlSeedPath" class="form-label">Crawl Seed Path</label>
      </div>
    </div>
  </div>

  <div class="row g-3 mb-3">
    <div class="col-md-12">
      <ImportAddEditCheckTable
          v-model:tableOptions="tableOptions.urlExclusionPatterns"
          :tableData="urlExclusionPatterns"
          @updateTableData="$emit('update:urlExclusionPatterns', $event)"
      />
    </div>
  </div>

  <div class="row g-3 mb-3">
    <div class="col-md-12">
      <ImportAddEditCheckTable
          v-model:tableOptions="tableOptions.siteUrls"
          :tableData="siteUrls"
          @updateTableData="updateSiteUrls($event)"
      />
    </div>
  </div>

  <div class="row g-3 mb-3">
    <div class="col-md-6">
      <div class="form-check">
        <input class="form-check-input" type="checkbox" :value="useSitemap" :checked="useSitemap"
               @change="$emit('update:useSitemap', $event.target.checked)"
               id="useSitemap">
        <label class="form-check-label" for="useSitemap">
          Use Sitemap
        </label>
      </div>
      <div class="form-floating mb-3">
        <input type="text" class="form-control" id="sitemapUrl"
               :disabled="!useSitemap" placeholder="Sitemap URL" :value="sitemapUrl"
               @input="$emit('update:sitemapUrl', $event.target.value)">
        <label for="sitemapUrl" class="form-label">Sitemap URL (default <b>{{ sitemapUrlComp }}</b>)</label>
      </div>
    </div>
  </div>
  <div v-if="useSitemap" class="row g-3 mb-3">
    <div class="col-md-12">
      <ImportAddEditCheckTable
          v-model:tableOptions="tableOptions.sitemapUrls"
          :tableData="sitemapUrls"
          @updateTableData="updateSitemapUrls($event)"
      />
    </div>
  </div>

</template>

<script>
import ImportAddEditCheckTable from "./ImportAddEditCheckTable";

export default {
  name: "CollectionCrawlConfigurationForm",
  props: ['siteUrl', 'crawlDbPath', 'collectionName', 'crawlSeedPath', 'useSitemap', 'sitemapUrl', 'includeSiteUrls', 'excludeSiteUrls', 'includeSitemapUrls', 'excludeSitemapUrls', 'urlExclusionPatterns'],
  components: {
    ImportAddEditCheckTable
  },
  emits: [
    'addKeymatch',
    'deleteKeymatch',
    'updateKeymatch'
  ],
  data() {
    let inclusionType = [
      {label: 'include', value: 'include'},
      {label: 'exclude', value: 'exclude'}
    ]
    let patternTypes = [
      {label: 'contains', value: 'contains'},
      {label: 'regex', value: 'regex'}
    ]
    return {
      tableOptions: {
        siteUrls: {
          enableImport: true,
          enableAddRow: true,
          enableActions: true,
          columns: [
            {label: 'Include/Exclude Site URL\'s', name: 'siteUrl', width: '75%'},
            {label: 'Type', name: 'type', class: 'text-center', type: 'select', options: inclusionType, default: "include", width: '8%'},
          ]
        },
        sitemapUrls: {
          enableImport: true,
          enableAddRow: true,
          enableActions: true,
          columns: [
            {label: 'Include/Exclude Sitemap URL\'s', name: 'sitemapUrl', width: '75%'},
            {label: 'Type', name: 'type', class: 'text-center', type: 'select', options: inclusionType, default: "include", width: '8%'},
          ]
        },
        urlExclusionPatterns: {
          enableImport: true,
          enableAddRow: true,
          enableActions: true,
          columns: [
            {label: 'URL Exclusion Patterns', name: 'expression', width: '70%'},
            {label: 'Type', name: 'type', class: 'text-center', type: 'select', options: patternTypes, default: "regex", width: '10%'},
            {label: 'Ignore Case', name: 'ignoreCase', type: 'checkbox', class: 'text-center', width: '9%'}
          ]
        }
      },
    }
  },
  computed: {
    sitemapUrlComp() {
      let url = "./sitemap.xml"
      if (this.sitemapUrl !== '') {
        url = this.sitemapUrl
      } else if (this.siteUrl !== '') {
        url = this.siteUrl + "/sitemap.xml"
        this.$emit('update:sitemapUrl', url)
      }
      return url
    },
    siteUrls() {
      let siteUrls = []
      for (let item in this.includeSiteUrls) {
        siteUrls.push({siteUrl: this.includeSiteUrls[item], type: 'include'})
      }
      for (let item in this.excludeSiteUrls) {
        siteUrls.push({siteUrl: this.excludeSiteUrls[item], type: 'exclude'})
      }
      return siteUrls
    },
    sitemapUrls() {
      let sitemapUrls = []
      for (let item in this.includeSitemapUrls) {
        sitemapUrls.push({sitemapUrl: this.includeSitemapUrls[item], type: 'include'})
      }
      for (let item in this.excludeSitemapUrls) {
        sitemapUrls.push({sitemapUrl: this.excludeSitemapUrls[item], type: 'exclude'})
      }
      return sitemapUrls
    }
  },
  methods: {
    updateSiteUrls(siteUrls) {
      console.log("update: ", siteUrls)
      let includeSiteUrls = []
      let excludeSiteUrls = []
      for (let item in siteUrls) {
        if (siteUrls[item].type === 'include') {
          includeSiteUrls.push(siteUrls[item].siteUrl)
        } else if (siteUrls[item].type === 'exclude') {
          excludeSiteUrls.push(siteUrls[item].siteUrl)
        }
      }
      this.$emit('update:includeSiteUrls', includeSiteUrls)
      this.$emit('update:excludeSiteUrls', excludeSiteUrls)
    },
    updateSitemapUrls(sitemapUrls) {
      console.log("update: ", sitemapUrls)
      let includeSitemapUrls = []
      let excludeSitemapUrls = []
      for (let item in sitemapUrls) {
        if (sitemapUrls[item].type === 'include') {
          includeSitemapUrls.push(sitemapUrls[item].sitemapUrl)
        } else if (sitemapUrls[item].type === 'exclude') {
          excludeSitemapUrls.push(sitemapUrls[item].sitemapUrl)
        }
      }
      this.$emit('update:includeSitemapUrls', includeSitemapUrls)
      this.$emit('update:excludeSitemapUrls', excludeSitemapUrls)
    }
  }
}
</script>

<style scoped>

</style>