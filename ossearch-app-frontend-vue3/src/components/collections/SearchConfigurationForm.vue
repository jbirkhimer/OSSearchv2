<template>
  <fieldset :disabled="!isEditing">
    <div class="row g-3 mb-4">
      <div class="col-md-4">
        <div class="form-floating">
          <select :value="responseType" @input="$emit('update:responseType', $event.target.value)" class="form-select"
                  id="responseType" required
                  placeholder="Response Type">
            <option value="html" selected>html</option>
            <option value="json">json</option>
            <option value="xml">xml</option>
          </select>
          <label for="responseType" class="form-label">Response Type</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-floating">
          <input :value="resultsPerPage" @input="$emit('update:resultsPerPage', $event.target.value)" type="text"
                 class="form-control"
                 id="resultPerPage" required
                 placeholder="Results Per Page">
          <label for="resultPerPage" class="form-label">Results Per Page</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-floating">
          <input :value="resultLimit" @input="$emit('update:resultLimit', $event.target.value)" type="text"
                 class="form-control"
                 id="resultLimit" required
                 placeholder="Result Limit">
          <label for="resultLimit" class="form-label">Result Limit</label>
          <div class="form-text">0 = no limit</div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-md-12">
        <label for="validationDefault08" class="form-label">Include Response Fields</label>&nbsp;
  <!--      <a role="button" class="bi-info-circle-fill" data-bs-toggle="collapse" data-bs-target="#collapseIncludeFieldsInfo" aria-expanded="false" aria-controls="collapseIncludeFieldsInfo"/>-->
        <i class="fas fa-info-circle text-primary"></i>
  <!--      <div class="collapse" id="collapseIncludeFieldsInfo">

        </div>-->
        <div class="card card-body">
          <div class="form-text">
            <p class="lh-sm">
              By default search results will be returned with <b>Title</b>, <b>Snippet</b>, and <b>URL</b>.
              Site owners can optionally select <b>Anchor</b>, <b>Meta Description</b> and <b>Meta Keywords</b> to
              be
              returned with the search results.</p>
            <p class="lh-sm"><b>Meta Description</b> and <b>Meta Keywords</b> are metatags that are included on some
              pages; this is
              website-dependent.</p>

            <p class="lh-sm">
              <b>Anchor</b> is the text of the link that linked to the page. OSS won’t always find Anchors for all
              URLs,
              but here's an example:</p>

            <p class="lh-sm">The URL
              <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition"
                 target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition</a>
              has 2 anchors:</p>

            <p class="lh-sm">1. "Latin American Research", derived from the left-hand menu on this page:
              <a href="https://siarchives.si.edu/history/featured-topics" target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics</a>
            </p>
            <p class="lh-sm">2. "view in English", derived from the English language link on this page:
              <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0"
                 target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0</a>
            </p>
          </div>
        </div>
        <!--          <button class="btn btn-sm btn-primary mt-3" type="button" data-bs-toggle="collapse"
                data-bs-target="#collapseIncludeFieldsInfo" aria-expanded="false"
                aria-controls="collapseIncludeFieldsInfo">
          More Info
        </button>-->
        <!--          <a class="btn btn-sm btn-primary" data-bs-toggle="offcanvas" href="#offcanvasIncludeFields" role="button" aria-controls="offcanvasIncludeFields">
                    More Info
                  </a>
                  <div class="offcanvas offcanvas-start" tabindex="-1" id="offcanvasIncludeFields" aria-labelledby="offcanvasIncludeFieldsLabel">
                    <div class="offcanvas-header">
                      <h5 class="offcanvas-title" id="offcanvasIncludeFieldsLabel">Include Response Fields</h5>
                      <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                    </div>
                    <div class="offcanvas-body">
                      <div class="form-text">
                        <p class="lh-sm">
                          By default search results will be returned with <b>Title</b>, <b>Snippet</b>, and <b>URL</b>.
                          Site owners can optionally select <b>Anchor</b>, <b>Meta Description</b> and <b>Meta Keywords</b> to be
                          returned with the search results.</p>
                        <p class="lh-sm"><b>Meta Description</b> and <b>Meta Keywords</b> are metatags that are included on some pages; this is
                          website-dependent.</p>

                        <p class="lh-sm">
                          <b>Anchor</b> is the text of the link that linked to the page. OSS won’t always find Anchors for all URLs,
                          but here's an example:</p>

                        <p class="lh-sm">The URL
                          <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition" target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition</a>
                          has 2 anchors:</p>

                        <p class="lh-sm">1. "Latin American Research", derived from the left-hand menu on this page:
                          <a href="https://siarchives.si.edu/history/featured-topics" target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics</a></p>
                        <p class="lh-sm">2. "view in English", derived from the English language link on this page:
                          <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0" target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0</a>
                        </p>
                      </div>
                    </div>
                  </div>-->
        <!--          <div class="accordion" id="accordionIncludeFieldsInfo">
                    <div class="accordion-item">
                      <h2 class="accordion-header" id="headingOne">
                        <button class="accordion-button btn-sm" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                          More Info
                        </button>
                      </h2>
                      <div id="collapseOne" class="accordion-collapse collapse" aria-labelledby="headingOne" data-bs-parent="#accordionIncludeFieldsInfo">
                        <div class="accordion-body">
                          <div class="form-text">
                            <p class="lh-sm">
                              By default search results will be returned with <b>Title</b>, <b>Snippet</b>, and <b>URL</b>.
                              Site owners can optionally select <b>Anchor</b>, <b>Meta Description</b> and <b>Meta Keywords</b> to be
                              returned with the search results.</p>
                            <p class="lh-sm"><b>Meta Description</b> and <b>Meta Keywords</b> are metatags that are included on some pages; this is
                              website-dependent.</p>

                            <p class="lh-sm">
                              <b>Anchor</b> is the text of the link that linked to the page. OSS won’t always find Anchors for all URLs,
                              but here's an example:</p>

                            <p class="lh-sm">The URL
                              <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition" target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition</a>
                              has 2 anchors:</p>

                            <p class="lh-sm">1. "Latin American Research", derived from the left-hand menu on this page:
                              <a href="https://siarchives.si.edu/history/featured-topics" target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics</a></p>
                            <p class="lh-sm">2. "view in English", derived from the English language link on this page:
                              <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0" target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0</a>
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>-->
  <!--      <div v-for="(input, i) in includeFields"
             :key="`includeField-${i}`" class="input-group input-group-sm mb-1">
          <input v-model="input.name" type="text" class="form-control"
                 id="validationDefault08" required
                 placeholder="Response Fields" aria-label="Include Response Field">
          <span v-show="i < 3" class="input-group-text" id="basic-addon1">Default</span>
          <span v-show="includeFields.length == i+1" @click="addField(input, includeFields)"
                class="btn btn-outline-secondary bi-plus-lg" type="button" id="button-addon3"></span>
          <span v-show="includeFields.length > 3 && i > 2"
                @click="removeField(i, includeFields)" class="btn btn-outline-secondary bi-x-lg" type="button"
                id="button-addon4"></span>
        </div>-->

      </div>
    </div>
    <div class="row g-3">
      <div class="col-md-12">
        <ImportAddEditCheckTable
            v-model:tableOptions="tableOptions"
            :tableData="includeFields"
            @updateTableData="$emit('update:includeFields', $event)"
            :selected="selected"
            @selected="updateSelected"
        />
      </div>
    </div>
  </fieldset>
</template>

<script>
import ImportAddEditCheckTable from "../forms/ImportAddEditCheckTable";

export default {
  name: "SearchConfigurationForm",
  props: ['isEditing', 'responseType', 'resultsPerPage', 'resultLimit', 'includeFields'],
  components: {
    ImportAddEditCheckTable
  },
  data() {
    return {
      tableOptions: {
        enableImport: true,
        enableAddRow: true,
        enableActions: true,
        actionDisabledDefaultValues: ['title', 'content', 'url'],
        columns: [
          {label: 'Include Response Fields', class: 'text-center', width: '33%'}
        ]
      },
      selected: []
    }
  },
  methods: {
    updateSelected(event) {
      this.selected = event
    },
    addField(value, fieldType) {
      fieldType.push({});
    },
    removeField(index, fieldType) {
      fieldType.splice(index, 1);
    },
  },
}
</script>

<style scoped>

</style>