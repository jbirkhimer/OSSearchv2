<template>

  <div id="createCollectionTutorial">

    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2>How To Create A Collection</h2>
      <DownloadButton class="btn btn-primary" data-html2canvas-ignore="true"
                      dom="#tutorial"
                      name="CreateCollectionTutorial.pdf"/>
    </div>

    <div class="card mb-3">
      <div class="card-header fw-bold">Step 1</div>
      <div class="card-body">
        <p class="card-text">Navigate to the <router-link to="/collections">Collections</router-link> page using the sidebar or by entering <router-link to="/collections">https://ossearch.si.edu/collections</router-link> in your browser.</p>
        <img class="img-fluid" src="../../../assets/images/tutorial/collections-view_step1.png" alt="../../../assets/images/tutorial/collections-view_step1.png">
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-header fw-bold">Step 2</div>
      <div class="card-body">
<!--        <h5 class="card-title">Step 2</h5>-->
        <p class="card-text">Click the <button type="button" class="btn btn-sm btn-primary bi-plus-lg" to="/collections/create">Collection</button> button to create a new collection.</p>
        <p><b class="text-danger"><i class="fas fa-exclamation-circle"></i> If you dont have ADMIN privileges you will not see the button to create a new collection. You must have ADMIN privileges to create a new collection!</b></p>
        <img src="../../../assets/images/tutorial/collections-view_step2.png" class="card-img-top" alt="../../../assets/images/tutorial/collections-view_step2.png">
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-header fw-bold">Step 3</div>
      <div class="card-body">
        <p class="card-text">Enter a unique name for the collection and a optionally a description. The collection name must not contain special characters or spaces.</p>
        <img src="../../../assets/images/tutorial/collections-view-basic-info.png" class="card-img-top" alt="../../../assets/images/tutorial/collections-view-basic-info.png">
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-header fw-bold">Step 4</div>
      <div class="card-body">
        <p class="card-text">Select the search response type, results per page, and result limit.</p>
        <p class="card-text">By default a search response will return the title, content, and url. If you want to include additional fields you can add them now or late.</p>
        <img src="../../../assets/images/tutorial/collections-view-search-rsp-cfg.png" class="card-img-top" alt="../../../assets/images/tutorial/collections-view-search-rsp-cfg.png">
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-header fw-bold">Step 5</div>
      <div class="card-body">
        <p class="card-text">Enter the site url for this collection. This is where crawling will start from.</p>
        <img src="../../../assets/images/tutorial/collections-view-seed-urls.png" class="card-img-top" alt="../../../assets/images/tutorial/collections-view-seed-urls.png">
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-header fw-bold">Step 6</div>
      <div class="card-body">
        <p class="card-text">Enter sitemap url paths if you have sitemaps that are not in the standard location. By default crawling will use the seed url and default sitemap.xml path.</p>
        <img src="../../../assets/images/tutorial/collections-view-sitemap-urls.png" class="card-img-top" alt="../../../assets/images/tutorial/collections-view-sitemap-urls.png">
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-header fw-bold">Step 7</div>
      <div class="card-body">
        <p class="card-text">Select the most appropriate schedule for crawling. If your site changes frequently you may want to crawl more frequently. However, depending on how large the site is you may want to start off crawling once a week and adjust how frequently the site is crawled after you have an idea of how long it will take to crawl the site. Typically the first few crawls will take longer as the crawler finds new urls. </p>
        <img src="../../../assets/images/tutorial/collections-view-crawl-shedule.png" class="card-img-top" alt="../../../assets/images/tutorial/collections-view-crawl-shedule.png">
      </div>
    </div>

<!--    <figure class="figure">
      <a href="../../../assets/images/tutorial/collections-view.png" data-bs-toggle="modal"
         data-bs-target="#collectionsViewImageModal">
        <img class="figure-img img-fluid img-thumbnail"
             src="../../../assets/images/tutorial/collections-view.png"
             style="width: 400px; height: 264px;"></a>
      <figcaption class="figure-caption">Click to Enlarge</figcaption>
    </figure>
    <div class="modal fade" id="collectionsViewImageModal" tabindex="-1"
         aria-labelledby="collectionsViewImageModal" aria-hidden="true">
      <div class="modal-dialog modal-xl modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-body">
            <img class="img-fluid rounded mx-auto d-block"
                 src="../../../assets/images/tutorial/collections-view.png">
          </div>
        </div>
      </div>
    </div>-->

  </div>
</template>

<script>
import DownloadButton from '../../../components/DownloadButton.vue'

export default {
  name: "TutorialCreateCollection",
  components: {
    DownloadButton
  }
}
</script>

<style scoped>

</style>
