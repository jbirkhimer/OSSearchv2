<template>
  <div class="row g-3">
    <div class="col-md-6">
      <div class="form-floating">
        <input :value="name" @input="$emit('update:name', $event.target.value)" class="form-control" id="collectionName"
               required
               placeholder="Name">
        <label for="collectionName">Name</label>
      </div>
    </div>
    <div class="col-md-6">
      <div class="form-floating">
        <input :value="siteUrl" @input="$emit('update:siteUrl', $event.target.value)" type="text" class="form-control" id="siteUrl"
               required
               placeholder="Site Url">
        <label for="siteUrl" class="form-label">Base Site Url</label>
        <div class="form-text">Url used for initial crawling start point</div>
      </div>
    </div>
  </div>
  <div class="row g-3">
    <div class="col-md-12">
      <div class="form-floating">
            <textarea :value="description" @input="$emit('update:description', $event.target.value)" type="text" class="form-control"
                      style="height: 100px"
                      id="description" required
                      placeholder="Description"></textarea>
        <label for="description" class="form-label">Description</label>
      </div>
    </div>
  </div>
  <div class="row g-3">
    <div class="col-md-6">
<!--      <label for="keywords" class="form-label">Keywords</label>
      <div v-for="(keyword, i) in keywords"
           :key="`includeField-${i}`" class="input-group input-group-sm mb-1">
        <input v-model="keyword.name" type="text" class="form-control"
               id="keywords" required
               placeholder="Response Fields" aria-label="Include Response Field">
        <span v-show="keywords.length == i+1" @click="addKeyword(keyword, keywords)"
              class="btn btn-outline-secondary bi-plus-lg" type="button" id="button-addon1"></span>
        <span v-show="keywords.length > 1"
              @click="removeKeyword(i, keywords)" class="btn btn-outline-secondary bi-x-lg" type="button"
              id="button-addon2"></span>
      </div>-->
      <ImportAddEditCheckTable
          v-model:tableOptions="tableOptions"
          :tableData="keywords"
          @updateTableData="$emit('update:keywords', $event)"
          :selected="selected"
          @selected="updateSelected"
      />
    </div>
  </div>
</template>

<script>
import ImportAddEditCheckTable from "./ImportAddEditCheckTable";

export default {
  name: "CollectionBasicInfo",
  props: ['name', 'siteUrl', 'description', 'keywords'],
  components: {
    ImportAddEditCheckTable
  },
  data() {
    return {
      tableOptions: {
        enableImport: true,
        enableAddRow: true,
        enableActions: true,
        columns: [
          {label: 'Keywords', class: 'text-center', width: '33%'}
        ]
      },
      selected: []
    }
  },
  methods: {
    updateSelected(event) {
      this.selected = event
    },
    addKeyword(value, fieldType) {
      fieldType.push({});
    },
    removeKeyword(index, fieldType) {
      fieldType.splice(index, 1);
    },
  },
}
</script>

<style scoped>

</style>