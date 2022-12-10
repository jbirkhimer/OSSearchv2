<template>
  <div v-if="error" class="container-fluid px-4 error">
    {{ error }}
  </div>

  <div class="card mt-4 mb-4">
    <div class="card-header">
      <i class="fas fa-search me-1"></i>
      <b>Search Keymatch Configuration</b>
      <div class="float-end">
        <button class="btn btn-sm btn-primary float-end" type="button" @click="beforeEditKeymatch = JSON.parse(JSON.stringify(collection)); isEditKeymatch = !isEditKeymatch;" v-if="!isEditKeymatch">Edit</button>
        <button v-if="isEditKeymatch" class="btn btn-sm btn-success me-md-2" type="button" @click="saveKeymatches()">Save</button>
        <button v-if="isEditKeymatch" class="btn btn-sm btn-danger float-end" type="button" @click="collection = beforeEditKeymatch; isEditKeymatch = false">Cancel</button>
      </div>
    </div>
    <div class="card-body">
      <div class="row g-3 mb-3">
        <div class="col-md-12">
          <legend class="col-form-label">Keymatchs&nbsp;
            <i class="fas fa-info-circle text-primary"></i>
          </legend>
          <div class="card card-body">
            <div class="form-text">
              <p>Keymatches enable you to force certain documents to the top of search results. When users search with a
                term that you specify, the search appliance always presents the KeyMatch first. Users can navigate
                immediately to the featured document and spend less time searching and viewing less relevant
                documents. You also might consider using KeyMatches to promote documents that are too new to be in the search index or might not appear among the highest search results.</p>

              <p>A KeyMatch associates a specific term with a particular result, enabling you to direct users to a result that is especially relevant but that might not appear at the top of the results page. For example, if a department is releasing a new Operations web page that should be returned for certain types of queries, you can direct users to that new web page by associating specific search terms, such as operations, with the new web page. A link for that page is returned at the top of search results for queries containing the term operations.</p>

              <p>This feature is especially useful for directing users to web pages that are not yet part of the production index or that have very few links to them, causing them to appear further down in the results list than you would like.</p>
              <h6><b>Setting Up KeyMatches</b></h6>
              <p>You set up a KeyMatch by matching a search term to a specific URL and specifying a title for the
                match</p>
              <p>To create a KeyMatch, you must specify the word, phrase, or exact match criteria for which a specific result will be returned. The rules for creating a KeyMatch are described in the following table.</p>
              <div><b>Title for Match:</b> A description for Title for Match.</div>
              <div><b>Search Term:</b> A description for Search Term.</div>
              <div><b>Keymatch Type:</b> A description for Keymatch Type.</div>
              <ul>
                <li><b>KeywordMatch:</b> A word that must appear anywhere in query.</li>
                <li><b>PhraseMatch:</b> A phrase that appears anywhere in query. For the phrase to match, all of the words must be present, the order of the words must be the same with no intervening words, and any hyphens in the query must be matched.</li>
                <li><b>ExactMatch:</b> Phrase must exactly match the query.</li>
              </ul>
              <div><b>URL for Match:</b> A description for URL for Match.</div>
            </div>
          </div>
        </div>
      </div>
      <div class="row g-3">
        <div class="col-md-12">
          <Keymatch
              :isEditing="isEditKeymatch"
              :loading="loading"
              :saving="saving"
              :collectionId="collection.id"
              v-model:keymatches="collection.keymatches"
          />
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import CollectionService from "../../../../services/collection.service";
import Keymatch from "../../../../components/collections/Keymatch";
import EventBus from "../../../../common/EventBus";

export default {
  name: "CollectionSearchKeymatchConfig",
  props: ['name', 'tabName'],
  components: {
    Keymatch
  },
  data() {
    return {
      loading: false,
      saving: false,
      error: null,
      showJson: false,
      collection: {id: '', keymatches: []},
      collectionId: null,
      isEditKeymatch: false,
      beforeEditKeymatch: null,
    }
  },
  async mounted() {
    this.loading = true
    await this.getCollection()
    this.loading = false
  },
  watch: {
    error: {
      deep: true,
      handler: function () {
        let content = (this.error.response && this.error.response.data && this.error.response.data.message) || this.error.message || this.error.toString();
        if (this.error.response && this.error.response.status === 403) {
          EventBus.dispatch("logout");
        } else {
          alert("ERROR: " + content)
        }
      }
    }
  },
  methods: {
    async getCollection() {
      await CollectionService.getCollections('/collection/search/getCollectionByName', {name: this.name, projection: 'collectionFormData'})
          .then(response => {
            let data = response.data;
            this.collection = data;
            this.collectionId = this.collection.id
            // console.log("data", data)
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });
    },
    async saveKeymatches() {
      this.saving = true

      let origKeymatches = []

      await CollectionService.getCollections('/collection/'+this.collection.id+"/keymatches")
          .then(response => {
            let data = response.data;
            data._embedded.keymatch.forEach(keymatch => {
              origKeymatches.push(keymatch._links.self.href)
            });
            //console.log("origKeymatches", origKeymatches, "currentKeymatches", this.collection.keymatches)
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });

      let keymatches = await this.updateKeymatches()

      //console.log("(2) updated keymatches", keymatches)

      let deleteKeymatches = origKeymatches.filter((keymatch) => !keymatches.includes(keymatch))

      //console.log(">>> for delete keymatches", deleteKeymatches)

      await this.deleteKeymatches(deleteKeymatches)

      EventBus.dispatch('toast', {
        type: 'success',
        msg: 'Keymatch Config Updated!'
      })

      await this.getCollection()
      this.saving = false
      this.isEditKeymatch = false
    },
    async updateKeymatches() {
      let keymatches = []
      let promises = []

      this.collection.keymatches.forEach(keymatch => {
        //console.log("keymatch:", JSON.stringify(keymatch))
        //let url = "/keymatch"
        let body = keymatch

        if (keymatch._links && keymatch.id) {
          // url = keymatch._links.self.href
          promises.push(CollectionService.updateCollection(keymatch._links.self.href, JSON.stringify(body)))
        } else {
          body.collection = this.collection._links.self.href
          promises.push(CollectionService.addCollection('/keymatch', JSON.stringify(body)))
        }
      })

      await Promise.all(promises)
          .then(results => {
            results.forEach(response => {
              let data = response.data;
              let keymatch_link = data._links.self.href
              //console.log("keymatch_link", keymatch_link)
              keymatches.push(keymatch_link)
            })
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          })

      //console.log("(1) return keymatches", keymatches)
      return keymatches;
    },
    async deleteKeymatches(deleteKeymatches) {
      let promises = []

      deleteKeymatches.forEach(keymatch => {
        console.log(">>> DELETE keymatch:", JSON.stringify(keymatch))
        promises.push(CollectionService.deleteCollection(keymatch))
      })

      await Promise.all(promises)
          .then(results => {
            results.forEach(response => {
              let data = response.data;
              console.log("delete resp", data)
            })
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          })
    },
  }
}
</script>

<style scoped>

</style>