<template>
  <div>
    <table :id="id" class="table table-sm table-hover align-middle" style="width:100%">
      <thead class="table-primary">
        <slot name="table-head">
          <tr>
            <th v-for="column in tableOptions.columns" :key="column.name" :class="column.class ? column.class : '' " :style="column.style ? column.style : ''">
              {{ column.label }}
            </th>
          </tr>
        </slot>
      </thead>

      <tbody>
        <slot name="table-body"/>
      </tbody>

    </table>
  </div>
</template>

<script>
import $ from 'jquery/dist/jquery';
import 'jquery/dist/jquery.min.js';
import "datatables.net-bs5/js/dataTables.bootstrap5"
import "datatables.net-responsive-bs5/js/responsive.bootstrap5"
import "datatables.net-bs5/css/dataTables.bootstrap5.css"
import "datatables.net-responsive-bs5/css/responsive.bootstrap5.css"
import * as bootstrap from "bootstrap";
window.bootstrap = bootstrap;


export default {
  name: "Datatable",
  components: {
  },
  props: ['tableOptions', 'tableData', 'id'],
  mounted() {
    this.initDatatable()
  },
  methods: {
    initDatatable() {
      setTimeout(() => {
        $('#'+this.id).DataTable({
          "pagingType": "full_numbers",
          "lengthMenu": [
            [10, 25, 50, -1],
            [10, 25, 50, "All"]
          ],
          order: this.getOrder(),
          // stateSave: true,
          responsive: true,
          destroy: true,
          // retrieve: true,
          // autoFill: true,
          // colReorder: true,
          // columnDefs: [ {
          //   orderable: false,
          //   className: 'select-checkbox',
          //   targets:   0
          // } ],
          // select: {
          //   style:    'os',
          //   selector: 'td:first-child'
          // },
        });
      }, 300)
    },
    getOrder() {
      if (this.tableOptions.order) {
        return this.tableOptions.order
      }
      return [[1, 'asc']]
    }
  }
}
</script>

<style scoped lang="scss">
@import "datatables.net-bs5";
@import "datatables.net-responsive-bs5";

</style>