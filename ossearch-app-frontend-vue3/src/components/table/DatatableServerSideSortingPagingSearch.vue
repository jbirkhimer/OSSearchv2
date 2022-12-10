<template>
  <div>
    <div id="keywordSearchTable_wrapper" class="dataTables_wrapper dt-bootstrap5 no-footer mb-2">
      <div class="row mb-3">
        <div class="col-sm-2">
          <div class="dataTables_length" id="keywordSearchTable_length">
            <label>Show
              <select name="keywordSearchTable_length" aria-controls="keywordSearchTable"
                      class="form-select form-select-sm" :value="pageSize"
                      @change="$emit('update:pageSize', parseInt($event.target.value))">
                <option v-for="(option, i) in tableOptions.lengthMenu[0]" :key="i"
                        :value="tableOptions.lengthMenu[0][i]">{{ tableOptions.lengthMenu[1][i].toLocaleString() }}
                </option>
              </select>
              entries</label>
          </div>
        </div>
        <div class="col-sm-4">
          <TableExports :exportBaseUrl="exportBaseUrl"/>
        </div>
        <div class="col-sm-6">
          <div id="keywordSearchTable_filter" class="dataTables_filter float-end">
            <div class="input-group">
              <input type="search" class="form-control form-control-sm" placeholder=""
                     aria-controls="keywordSearchTable" v-model="searchInput"
                     @keyup.enter="$emit('update:search', searchInput)"
                     aria-describedby="button-search">
              <button class="btn btn-sm btn-primary" type="button" id="button-search" @click="$emit('update:search', searchInput)"><i class="fas fa-search"></i></button>
            </div>
<!--            <label>Search:<input type="search" class="form-control form-control-sm" placeholder=""
                                 aria-controls="keywordSearchTable" :value="search"
                                 @keyup.enter="$emit('update:search', $event.target.value)"
                                 @click="$emit('update:search', '')"></label>-->
          </div>
        </div>
      </div>
    </div>

    <div id="keywordSearchTable_wrapper" class="dataTables_wrapper dt-bootstrap5 no-footer">
      <div class="row">
        <div class="col-sm-6">
          <div class="dataTables_info" id="keywordSearchTable_info" role="status" aria-live="polite">{{ info }}
          </div>
        </div>
        <div class="col-sm-6">
          <Pagination class="float-end" :total="Math.ceil(recordsFiltered / pageSize)" :eachSide="1" :value="page"
                      @input="$emit('update:page', $event)"/>
        </div>
      </div>
    </div>

    <!--    <div :id="id + 'exportButtons'" class="btn-primary"></div>-->
    <table :id="id" class="table table-sm table-hover align-middle table-header-hover" style="width:100%">
      <thead class="table-primary">
      <slot name="table-head">
        <tr>
          <th v-for="(column, i) in tableOptions.columns" :key="i" class="d-sm-table-cell" :class="column.class ? column.class : '' "
              :style="column.style ? column.style : ''" @click="sortBy(i)">
            {{ column.label }}
            <i v-if="sortEnabled[column.name]" class="float-end ps-2 pe-2" :class="getSortIcon(i)" aria-label='Sort Icon'></i>
            <!--            <div v-if="sortEnabled[column.name]" class="d-flex flex-column th-inner float-end">
                          <i class="up-arrow" :class="getSortIcon(i, 0)" aria-label='Sort Icon'></i>
                          <i class="down-arrow" :class="getSortIcon(i, 1)" aria-label='Sort Icon'></i>
                        </div>-->

          </th>
        </tr>
      </slot>
      </thead>

      <tbody>
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

    <div id="keywordSearchTable_wrapper" class="dataTables_wrapper dt-bootstrap5 no-footer">
      <div class="row">
        <div class="col-sm-6">
          <div class="dataTables_info" id="keywordSearchTable_info" role="status" aria-live="polite">{{ info }}
          </div>
        </div>
        <div class="col-sm-6">
          <Pagination class="float-end" :total="Math.ceil(recordsFiltered / pageSize)" :eachSide="1" :value="page"
                      @input="$emit('update:page', $event)"/>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import Pagination from "./Pagination";
import TableExports from "./TableExports";

export default {
  name: "DatatableServerSideSortingPagingSearch",
  components: {
    Pagination,
    TableExports
  },
  props: ['tableOptions', 'id', 'page', 'pageSize', 'search', 'recordsTotal', 'recordsFiltered', 'loading', 'exportBaseUrl'],
  data() {
    return {
      searchInput: this.search,
      sortIcon: {asc: 'bi bi-sort-alpha-down', desc: 'bi bi-sort-alpha-up'},
      // sortIcon: {asc: 'bi bi-caret-down-fill', desc: 'bi bi-caret-up-fill'},
      // sortIcon: {asc: 'fas fa-sort-alpha-down', desc: 'fas fa-sort-alpha-up'},
      // sortIcon: {asc: 'fas fa-sort-down', desc: 'fas fa-sort-up'},
    }
  },
  mounted() {

  },
  watch: {
    search: {
      handler: async function () {
        this.searchInput = this.search
      }
    },
  },
  computed: {
    info() {
      let start = (this.page - 1) * this.pageSize + 1
      let end = this.recordsFiltered

      if (this.pageSize < this.recordsFiltered) {
        end = this.pageSize * this.page
        if (end > this.recordsFiltered) {
          end = this.recordsFiltered;
        }
      }

      return "Showing " + start.toLocaleString() + " to " + end.toLocaleString() + " of " + this.recordsFiltered.toLocaleString() + " entries"
    },
    sortEnabled() {
      let sortEnabled = {}
      for (let col in this.tableOptions.columns) {
        if (Object.keys(this.tableOptions.columns[col]).includes('sortable')) {
          sortEnabled[this.tableOptions.columns[col].name] = this.tableOptions.columns[col].sortable
        } else {
          sortEnabled[this.tableOptions.columns[col].name] = true
        }
      }
      return sortEnabled
    }
  },
  methods: {
    /*sortBy(col) {
      let order = this.tableOptions.order

      let indexAsc = -1
      let indexDesc = -1

      for (let i in order) {
        if (order[i][0] === col && order[i][1] === 'asc') {
          indexAsc = i
        } else if (order[i][0] === col && order[i][1] === 'desc') {
          indexDesc = i
        }
      }

      if (indexAsc === -1 && indexDesc === -1) {
        order.push([col, 'asc'])
      }
      if (indexAsc > -1) {
        order[indexAsc][1] ='desc'
      }
      if (indexDesc > -1) {
        order.splice(indexDesc, 1)
      }

      order.sort((a, b) => a[0] - b[0])

      //console.log("emit order", JSON.stringify(this.tableOptions.order, null, 2))
      this.$emit('update:order', order)
    },
    getSortIcon(col, pos) {
      let order = this.tableOptions.order
      let index = -1

      for (let i in order) {
        if (order[i][0] === col) {
          index = i
        }
      }

      if (index > -1) {
        if (pos === 1) {
          return this.tableOptions.order[index][1] === 'asc' ?  this.sortIcon.asc : this.sortIcon.asc  + ' opacity-25'
        } else {
          return (this.tableOptions.order[index][1] === 'asc' ? this.sortIcon.desc  + ' opacity-25' : this.sortIcon.desc)
        }
      } else {
        return (pos === 0 ?  this.sortIcon.desc : this.sortIcon.asc) + ' opacity-25'
      }
    }*/
    sortBy(col) {
      let order = this.tableOptions.order
      let indexAsc = -1
      let indexDesc = -1

      for (let i in order) {
        if (order[i][0] === col && order[i][1] === 'asc') {
          indexAsc = i
        } else if (order[i][0] === col && order[i][1] === 'desc') {
          indexDesc = i
        }
      }

      if (indexAsc === -1 && indexDesc === -1) {
        order[0] = [ col, 'asc']
      }
      if (indexAsc > -1) {
        order[0] = [ col, 'desc']
      }
      if (indexDesc > -1) {
        order.splice(indexDesc, 1)
      }
      console.log("emit order", JSON.stringify(this.tableOptions.order, null, 2))
      this.$emit('update:order', order)
    },
    getSortIcon(col) {
      let order = this.tableOptions.order
      let index = -1
      for (let i in order) {
        if (order[i][0] === col) {
          index = i
        }
      }

      if (index > -1) {
        return this.tableOptions.order[index][1] === 'asc' ? this.sortIcon.asc : this.sortIcon.desc
      } else {
        return this.sortIcon.asc + ' text-secondary'
      }
    }
  }
}
</script>

<style scoped lang="scss">
@import '~bootstrap/scss/_functions';
@import '~bootstrap/scss/_variables';
@import '~bootstrap/scss/mixins/_breakpoints';
@import "bootstrap";
//.up-arrow {
//  cursor: pointer;
//  margin-top: -5px;
//  margin-bottom: -7px;
//}
//.down-arrow {
//  cursor: pointer;
//  margin-top: -7px;
//  margin-bottom: -6px;
//}
.table-header-hover {
  > thead > tr > th:hover {
    --bs-table-accent-bg: var(--bs-table-hover-bg);
    color: var(--bs-table-hover-color);
    cursor: pointer;
  }
}
</style>
