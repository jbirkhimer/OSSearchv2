<template>
  <div class="modal fade" :id="id" data-bs-keyboard="false" tabindex="-1" :aria-labelledby="id" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ title }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="card-body">
            <h2 class="h4 mb-1">Urls in CrawlDB</h2>
            <ul class="list-group mb-3">
              <li class="list-group-item d-flex justify-content-between align-items-center" v-for="(value, i) in data?.dbStats?.status" :key="i" data-bs-toggle="tooltip" :title="desc[value.statusValue]">
                <div class="ms-2 me-auto">
                  <div class="fw-bold text-capitalize">{{value.statusValue.replace(/^db_/,'')}} <a href="#" class="stretched-link" @click.prevent="collapse(value.statusValue)"><i class="fas fa-info-circle fa-sm"></i></a></div>
                  <div class="collapse" :class="isActive === value.statusValue ? 'show' : ''" :id="'collapseDesc_'+value.statusValue">
                    <small class="text-muted">{{ desc[value.statusValue] }}</small>
                  </div>
                </div>
                <span>{{ numberComma(value.count) }}</span>
              </li>
            </ul>

            <!-- Generated markup by the plugin -->
            <div class="tooltip bs-tooltip-top" role="tooltip">
              <div class="tooltip-arrow"></div>
              <div class="tooltip-inner">
                Some tooltip text!
              </div>
            </div>

            <ul class="list-group">
              <li class="list-group-item d-flex justify-content-between align-items-center">
                <b>Total Urls in CrawlDB</b>
                <span class="badge bg-primary rounded-pill">{{ numberComma(data?.dbStats?.totalUrls) }}</span>
              </li>
              <li class="list-group-item d-flex justify-content-between align-items-center">
                <b>Total Urls Indexed & Available For Search</b>
                <span class="badge bg-primary rounded-pill">{{ numberComma(data?.solrCount) }}</span>
              </li>
            </ul>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-sm btn-primary" data-bs-dismiss="modal" @click.prevent="isActive = ''">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CrawlDbStatsModal",
  props: {id: String, title: { type: String, default: 'Crawl Stats'}, data: Object},
  data() {
    return {
      isActive: '',
      desc: {
        db_unfetched: "URL was not fetched yet.",
        db_fetched: "URL was successfully fetched, indexed, and is available for search.",
        db_gone: "URL no longer exists.",
        db_redir_temp: "URL temporarily redirects to other page.",
        db_redir_perm: "URL permanently redirects to other page.",
        db_notmodified: "URL was successfully fetched and found not modified.",
        db_duplicate: "URL was marked as being a duplicate of another page"
      }
    }
  },
  methods: {
    collapse(value) {
      if (this.isActive === value) {
        this.isActive = ''
      } else {
        this.isActive = value
      }
    },
    numberComma(value) {
      if (typeof value === 'number') {
        return value.toLocaleString()
      } else if (!isNaN(value)) {
        return Number(value).toLocaleString()
      }
      return value
    }
  }
}
</script>

<style scoped>

</style>
