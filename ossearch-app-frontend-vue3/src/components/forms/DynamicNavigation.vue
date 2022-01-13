<template>
  <!--  <h3>Search Dynamic Navigation Configuration</h3>-->
<!--  <div class="dynamic-navigation-wrapper">


  </div>-->
  <div class="form-check">
    <input class="form-check-input" type="checkbox" :checked="useFacets" :value="useFacets" id="useFacets"
           @change="$emit('update:useFacets', $event.target.checked)">
    <label class="form-check-label" for="useFacets">
      Use Facets
    </label>
  </div>

  <div v-show="useFacets" class="row">
    <legend class="col-form-label">Field Type&nbsp;
      <a role="button"
         class="bi-info-circle-fill" data-bs-toggle="collapse" data-bs-target="#collapseIncludeFieldsInfo"
         aria-expanded="false" aria-controls="collapseIncludeFieldsInfo"></a>
    </legend>
    <div class="collapse" id="collapseIncludeFieldsInfo">
      <div class="card card-body">
        <div class="form-text">
          <div><b>Required:</b> it will be use by one or more collections that has it configured</div>
          <div><b>Partial:</b> it will be use by all collections without configuration</div>
          <div><b>In Meta:</b> as partial field, in meta will be use by all collections without
            configuration
          </div>
        </div>
      </div>
    </div>
    <ImportAddEditCheckTable
        v-model:tableOptions="tableOptions"
        :tableData="dynamicNavigations"
        @updateTableData="$emit('update:dynamicNavigations', $event)"
    />
  </div>
</template>

<script>
import ImportAddEditCheckTable from "./ImportAddEditCheckTable";

export default {
  name: "DynamicNavigation",
  props: ['collectionId', 'useFacets', 'dynamicNavigations'],
  components: {
    ImportAddEditCheckTable
  },
  data() {
    const fieldTypes = [
      {label: 'Required', value: 'requiredField'},
      {label: 'Partial', value: 'partialField'},
      {label: 'In Meta', value: 'inMeta'}
    ]
    const sortByList = [
      {label: 'Count', value: 'count'},
      {label: 'Value', value: 'value'}
    ]
    const sortOrderList = [
      {label: 'Ascending', value: 'asc'},
      {label: 'Descending', value: 'desc'}
    ]
    return {
      tableOptions: {
        enableImport: true,
        enableAddRow: true,
        enableActions: true,
        columns: [
          {label: 'Metadata Attribute Name', name: 'attributeName', class: 'text-center', type: 'text'},
          {label: 'Display Name', name: 'displayName', class: 'text-center', type: 'text'},
          {label: 'Field Type', name: 'navigationType', type: 'select', options: fieldTypes, class: 'text-center', default: 'requiredField'},
          {label: 'Sort By', name: 'sortBy', type: 'select', options: sortByList, class: 'text-center', default: 'count'},
          {label: 'Sort Order', name: 'sort', type: 'select', options: sortOrderList, class: 'text-center', default: 'asc'},
          {label: 'Multivalued', name: 'multivalue', type: 'checkbox', class: 'text-center'},
          {label: 'Delimiter', name: 'delimiter', class: 'text-center', type: 'text', disabledBy: 'multivalue'}
        ]
      },
      dynamicNavigation: {
        attributeName: '',
        displayName: '',
        navigationType: 'requiredField',
        sortBy: 'count',
        sort: 'asc',
        multivalue: false,
        delimiter: ''
      }
    }
  },
  methods: {

  }
}
</script>

<style scoped lang="scss">


</style>