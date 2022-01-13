<template>
  <div class="table-wrapper">

    <div class="table-filter">
      <div class="row">
        <div class="col-sm-4">
          <div class="show-entries">
            <span>Show</span>
            <RowsPerPageSelect :rowsPerPage="tableOptions.rowsPerPage" :rowsPerPageSelect="tableOptions.rowsPerPageSelect" @rowsPerPage="$emit('rowsPerPage', $event)"/>
            <span>entries</span>
          </div>
        </div>
        <div class="col-sm-8">
          <button type="button" class="btn btn-primary" @search="$emit('search')"><i class="bi bi-search"></i></button>
          <div class="filter-group">
            <label>Name</label>
            <input class="input float-end" type="text" :value="tableOptions.search" placeholder="Search...">
          </div>
        </div>
      </div>
    </div>

    <table class="table table-hover align-middle">

      <thead class="table-primary">
      <slot name="table-head">
        <tr>
          <th v-for="column in tableOptions.columns"
              :key="column.name" @click="$emit('sort', column.name)"
              :class="tableOptions.sortKey === column.name ? (tableOptions.sortOrders[column.name] > 0 ? 'sorting_asc' : 'sorting_desc') : 'sorting'"
              :style="'width:'+(100/tableOptions.columns.length)+';'+'cursor:pointer;'">
            {{ column.label }}
          </th>
          <th class="text-center">Actions</th>
        </tr>
      </slot>
      </thead>

      <tbody>
        <slot name="table-body"/>
      </tbody>

    </table>
    <Pagination :pagination="tableOptions.pagination"
                :length="tableOptions.pagination.page.totalPages"
                @first="$emit('pagination', {'url': tableOptions.pagination._links.first.href, 'params': null})"
                @prev="$emit('pagination', {'url': tableOptions.pagination._links.prev.href, 'params': null})"
                @page="$emit('pagination', {'url':'/users', 'params': {'page': $event, 'size': tableOptions.pagination.page.size}})"
                @next="$emit('pagination', {'url': tableOptions.pagination._links.next.href, 'params': null})"
                @last="$emit('pagination', {'url': tableOptions.pagination._links.last.href, 'params': null})">
    </Pagination>
  </div>
</template>

<script>
import RowsPerPageSelect from "./RowsPerPageSelect";
import Pagination from "./Pagination";

export default {
  name: "Datatable",
  components: {
    RowsPerPageSelect,
    Pagination
  },
  props: ['tableOptions'],
  // data() {
  //   return {
  //     itemsPerPage: this.itemsPerPage
  //   }
  // }
}
</script>

<style scoped lang="scss">
//.table-wrapper {
//  background: #fff;
//  padding: 20px 25px;
//  margin: 30px auto;
//  border-radius: 3px;
//  box-shadow: 0 1px 1px rgba(0, 0, 0, .05);
//}

.table-wrapper .btn {
  float: right;
  //color: #333;
  //background-color: #fff;
  border-radius: 3px;
  border: none;
  outline: none !important;
  margin-left: 10px;
}

.table-wrapper .btn:hover {
  color: #333;
  background: #f2f2f2;
}

.table-wrapper .btn.btn-primary {
  color: #fff;
  background: #03A9F4;
}

.table-wrapper .btn.btn-primary:hover {
  background: #03a3e7;
}

.show-entries select.form-select {
  width: 80px;
  margin: 0 5px;
}

.table-filter .filter-group {
  float: right;
  margin-left: 15px;
}

.table-filter input, .table-filter select {
  height: 34px;
  border-radius: 3px;
  border-color: #ddd;
  box-shadow: none;
}

.table-filter {
  padding: 5px 0 15px;
  border-bottom: 1px solid #e9e9e9;
  margin-bottom: 5px;
}

.table-filter .btn {
  height: 34px;
}

.table-filter label {
  font-weight: normal;
  margin-left: 10px;
}

.table-filter select, .table-filter input {
  display: inline-block;
  margin-left: 5px;
}

.table-filter input {
  width: 200px;
  display: inline-block;
}

.filter-group select.form-select {
  width: 110px;
}

.filter-icon {
  float: right;
  margin-top: 7px;
}

.filter-icon i {
  font-size: 18px;
  opacity: 0.7;
}

//table.table tr th, table.table tr td {
//  border-color: #e9e9e9;
//  padding: 12px 15px;
//  vertical-align: middle;
//}
//
//table.table tr th:first-child {
//  width: 60px;
//}
//
//table.table tr th:last-child {
//  width: 80px;
//}
//
//table.table-striped tbody tr:nth-of-type(odd) {
//  background-color: #fcfcfc;
//}
//
//table.table-striped.table-hover tbody tr:hover {
//  background: #f5f5f5;
//}
//
//table.table th i {
//  font-size: 13px;
//  margin: 0 5px;
//  cursor: pointer;
//}
//
//table.table td a {
//  font-weight: bold;
//  //color: #566787;
//  display: inline-block;
//  text-decoration: none;
//}
//
//table.table td a:hover {
//  color: #2196F3;
//}
//
////table.table td a.view {
////  width: 30px;
////  height: 30px;
////  color: #2196F3;
////  border: 2px solid;
////  border-radius: 30px;
////  text-align: center;
////}
////
////table.table td a.view i {
////  font-size: 22px;
////  margin: 2px 0 0 1px;
////}
//
////table.table .avatar {
////  border-radius: 50%;
////  vertical-align: middle;
////  margin-right: 10px;
////}

.status {
  font-size: 30px;
  margin: 2px 2px 0 0;
  display: inline-block;
  vertical-align: middle;
  line-height: 10px;
}

.text-success {
  color: #10c469;
}

.text-info {
  color: #62c9e8;
}

.text-warning {
  color: #FFC107;
}

.text-danger {
  color: #ff5b5b;
}

</style>