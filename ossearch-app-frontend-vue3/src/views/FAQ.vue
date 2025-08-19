<template>
  <div class="container-fluid px-4">
<!--    <h1 class="mt-4">FAQ</h1>-->
    <Breadcrumb :path="$route.fullPath"/>

    <div class="accordion mt-3" id="accordionFaq">
      <div v-for="(item, i) in faq" :key="i" class="accordion-item">
        <h2 class="accordion-header" :id="'headingQuestion_'+i">
          <button class="accordion-button" :class="$route.hash === '#collapseQuestion_'+i ? '' : 'collapsed'" type="button" data-bs-toggle="collapse" :data-bs-target="'#collapseQuestion_'+i" aria-expanded="false" :aria-controls="'collapseQuestion_'+i">
            {{  item.q }}
          </button>
        </h2>
        <div :id="'collapseQuestion_'+i" class="accordion-collapse collapse" :class="$route.hash === '#collapseQuestion_'+i ? 'show' : ''" :aria-labelledby="'headingAnswer_'+i" data-bs-parent="#accordionFaq">
          <div class="accordion-body">
            <span v-if="item.type === 'html'" v-html="item.a"></span>
            <span v-else>{{ item.a }}</span>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import Breadcrumb from "../components/Breadcrumb";

export default {
  name: "FAQ",
  components: {
    Breadcrumb
  },
  data() {
    return {
      faq: [
        {
          q: "Is there a way to see the \"path\" that the crawler has taken through the site?",
          a: "No, there is no way to see the \"path\" the crawler takes."
        },
        {
          q: "Are sitemaps used during crawling?",
          a: "Not by default. Sitemap processing of standard sitemap.xml i.e. http://mysite/sitemap.xml is configured in the crawl schedule under Basic Crawl Options > sitemaps_from_hostdb > never | always | once. Custom sitemap urls are configure in the collection's crawl configuration under Crawling Tab > Sitemaps > Include Sitemap URL's."
        },
        {
          q: "Is it possible to confirm that the crawler is using the sitemap.xml files, and following every link in the sitemap?",
          a: "You can check the Crawl History > Step history to see if the \" SITEMAP\" or \" SITEMAPS_FROM_HOSTDB\" step is listed. There will be a row with \" Step\" column value of \" SITEMAP\" or \" SITEMAPS_FROM_HOSTDB\" at round 1 or at the beginning of every round depending on what is configured. If sitemap processing is done all urls listed in the sitemap are added to the crawlDb for crawling."
        },
        {
          q: "Is it possible to get a timestamp of when the sitemap.xml files were crawled, just in case this predates updates made to those sitemaps?",
          a: "You can check the Crawl History > Step history to see if the \" SITEMAP\" or \" SITEMAPS_FROM_HOSTDB\" step is listed and see the time that step was run. There will be a row with \" Step\" column value of \" SITEMAP\" or \" SITEMAPS_FROM_HOSTDB\" at round 1 or at the beginning of every round depending on what is configured."
        },
        {
          q: "Is it possible to get a spreadsheet report that shows all of the URLs crawled, along with the last crawl date for each of those URLs?",
          a: "Yes from the Collection > Reports > Crawldb Report you can see that information as well as the status for each url; if it's fetched, unfetched, gone, duplicate, unmodified, redirected, etc. You can also view the details of the url there as well and see more information including the indexed content and additional crawldb information for the url. You are also able to search and filter results. Your are also able to download a csv/xls file of the entire crawldb for your own history or for further processing (caution the csv/xls could be several hundred MB to several GB)"
        },
        {
          q: "How can collections “share” documents",
          a: '<div class="mb-3"><p>You can configure Overlapping Search\'s from the collections page show below</p><div class="mb-3"><img class="img-fluid" src="'+require('@/assets/images/overlapping-searchs-config.png')+'"></div></div>',
          type: 'html'
        }
      ]
    }
  }
}
</script>

<style scoped>

</style>
