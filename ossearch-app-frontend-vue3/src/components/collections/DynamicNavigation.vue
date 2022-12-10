<template>
  <fieldset :disabled="!isEditing">
    <!--  <h3>Search Dynamic Navigation Configuration</h3>-->
  <!--  <div class="dynamic-navigation-wrapper">


    </div>-->
    <div class="row g-3 mb-3">
      <div class="col-md-12">
        <legend class="col-form-label">Dynamic Navigation (Faceting)&nbsp;
          <i class="fas fa-info-circle text-primary"></i>
        </legend>
        <div class="card card-body">
          <div class="form-text">

            <p>Dynamic navigation helps users explore search results by using specific metadata attributes.</p>

            <p>In many cases, content already has considerable metadata associated with it. As a administrator, you can use metadata to help users explore search results by using dynamic navigation. With dynamic navigation, when a user clicks on a metadata attribute value, the search results are filtered to contain results from the original search query that also have that specific attribute value</p>

            <h6><b>Setting up Dynamic Navigation (Faceting):</b></h6>

            <div><b>Display Name:</b> The display name for the attribute appears on the search results page. The display name can be different from the name of the attribute in HTML. For example, for "pub" in the following META tag, &lt;META NAME="pub" CONTENT="Google"&gt;, you might use the display label "Publisher."</div>
            <div><b>Metadata Attribute Name:</b> Attributes based on metadata, the name must match the NAME attribute in a META tag. For example, for the following META tag, &lt;META NAME="dept" CONTENT="sales"&gt;, the attribute name is dept and the value is sales.</div>
            <div><b>Field Type:</b> A description for Field Type.
              <ul>
                <li><b>Required:</b> it will be use by one or more collections that has it configured</li>
                <li><b>Partial:</b> it will be use by all collections without configuration</li>
                <li><b>In Meta:</b> as partial field, in meta will be use by all collections without configuration</li>
              </ul>
            </div>
            <div><b>Sort By:</b> A description for Sort By.</div>
            <div><b>Sort Order:</b> A description for Sort Ordere.</div>
            <div><b>Multivalued:</b> A description for Multivalued.</div>
            <div><b>Delimiter:</b> A description for Delimiter.</div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-3">
      <div class="col-md-12">
        <div class="form-check">
          <input class="form-check-input" type="checkbox" :checked="useFacets" :value="useFacets" id="useFacets"
                 @change="$emit('update:useFacets', $event.target.checked)">
          <label class="form-check-label" for="useFacets">
            Use Dynamic Navigation (Facets)
          </label>
        </div>
      </div>
    </div>

    <div class="row g-3">
      <div class="col-md-12">
        <ImportAddEditCheckTable
            v-model:tableOptions="tableOptions"
            :tableData="dynamicNavigations"
            @updateTableData="$emit('update:dynamicNavigations', $event)"
            :isEditing="!isEditing"
        />
      </div>
    </div>
  </fieldset>
</template>

<script>
import ImportAddEditCheckTable from "../forms/ImportAddEditCheckTable";

export default {
  name: "DynamicNavigation",
  props: ['isEditing', 'collectionId', 'useFacets', 'dynamicNavigations'],
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
