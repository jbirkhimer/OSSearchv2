<template>
  <div>
    <table :id="id" class="table table-sm align-middle table-header-hover" :class="(tableHover ? 'table-hover ' : '') + (tableBorder ? ' table-bordered' : '')"  style="width:100%">
      <slot name="caption"/>
      <thead class="table-primary">
        <slot name="table-head">
          <tr>
            <template v-for="column in tableOptions.columns" :key="column.name">
              <template v-if="column.adminOnly">
                <th v-if="isAdmin" :class="column.class ? column.class : '' " :style="column.style ? column.style : ''">
                  {{ column.label }}
                </th>
              </template>
              <th v-else :class="column.class ? column.class : '' " :style="column.style ? column.style : ''">
                {{ column.label }}
              </th>
            </template>
          </tr>
        </slot>
      </thead>

      <tbody :id="id+'_tbody'">
        <tr v-if="loading">
          <td :colspan="tableOptions.columns.length + 2" class="text-center">
            <div v-if="loading" class="d-flex flex-column align-items-center justify-content-center">
              <div class="row">
                <div class="spinner-border" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
              </div>
              <div class="row">
                <strong>Loading</strong>
              </div>
            </div>
          </td>
        </tr>
        <template v-if="!loading">
          <slot name="table-body"/>
        </template>
      </tbody>

    </table>
  </div>
</template>

<script>
import $ from 'jquery/dist/jquery';
// import 'jquery/dist/jquery.min.js';
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
  props: {
    tableOptions: Object,
    tableData: Array,
    id: String,
    loading: {
      type: Boolean,
      default: false
    },
    responsive: {
      type: Boolean,
      default: true
    },
    tableHover: {
      type: Boolean,
      default: true
    },
    tableBorder: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      ordering: true
    }
  },
  /*async mounted() {
    await this.initDatatable()
  },*/
  watch: {
    // tableData() {
    //   this.$nextTick (() => {this.initDatatable()})
    // }
    loading: {
      deep: true,
      handler: async function () {
        if (this.loading) {
          $('#'+this.id).DataTable().clear()
          $('#'+this.id).DataTable().destroy()
        } else {
          // $('#' + this.id).DataTable().ajax.reload();
          // $('#'+this.id).DataTable().ajax.reload();
          // $('#'+this.id).DataTable().draw();
          // $('#'+this.id).DataTable().clear().draw()
          await this.initDatatable()
        }
      }
    },
    /*tableData: {
      deep: true,
      handler: async function () {
      this.$nextTick(() => {
        $('#' + this.id).DataTable().draw()
      })
      }
    }*/
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    isAdmin() {
      if (this.currentUser && this.currentUser['roles']) {
        return this.currentUser['roles'].includes('ROLE_ADMIN');
      }
      return false;
    }
  },
  methods: {
    async initDatatable() {
      setTimeout(() => {
        $('#' + this.id).DataTable({
          "pagingType": "full_numbers",
          "lengthMenu": [
            [5, 10, 25, 50, -1],
            [5, 10, 25, 50, "All"]
          ],
          pageLength: 10,
          order: this.getOrder(),
          ordering: this.ordering,
          // stateSave: true,
          responsive: this.responsive,
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
      }, 500)
    },
    getOrder() {
      if (this.tableOptions.order) {
        if (this.tableOptions.order.length === 0) {
          this.ordering = false
        }
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
.table-header-hover {
  > thead > tr > th:hover {
    --bs-table-accent-bg: var(--bs-table-hover-bg);
    color: var(--bs-table-hover-color);
    cursor: pointer;
  }
}
</style>