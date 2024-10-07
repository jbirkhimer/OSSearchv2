<template>
  <div>
    <slot name="caption"></slot>
    <div id="keywordSearchTable_wrapper" class="dataTables_wrapper dt-bootstrap5 no-footer">
      <div class="row mb-3">

        <div class="col-sm-6">
          <div class="btn-toolbar" role="toolbar">
            <div class="btn-group btn-group-sm me-2" role="group">
              <div class="dataTables_length" id="keywordSearchTable_length">
                <label
                >Show
                  <select class="form-select form-select-sm" v-model="internalPageSize" @change="handlePageSizeChange">
                    <option v-for="option in tableOptions.lengthMenu[0]" :key="option" :value="option">
                      {{ option }}
                    </option>
                  </select>
                  entries</label
                >
              </div>
            </div>
            <div class="btn-group btn-group-sm me-2" role="group">
              <button type="button" class="btn btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                Export
              </button>
              <ul class="dropdown-menu dropdown-menu-dark">
                <li>
                  <button class="btn btn-sm btn-secondary dropdown-item" @click="exportData('csv')">
                    CSV
                  </button>
                </li>
                <li>
                  <button class="btn btn-sm btn-secondary dropdown-item" @click="exportData('xlsx')">
                    Excel
                  </button>
                </li>
              </ul>
            </div>
            <div class="btn-group btn-group-sm" role="group">
              <div class="d-grid gap-2 d-md-block">
                <button class="btn btn-sm btn-primary me-2" @click="showImportModal">Import</button>
  <!--              <ImportCsvList @load="importTableData"/>-->
                <button v-if="isEditing" class="btn btn-sm btn-primary me-2" @click="addRow">Add</button>
                <button v-if="isEditing && selectedRows.length > 0" class="btn btn-sm btn-danger" title="Delete" @click="deleteSelected"><i class="bi-trash-fill"></i></button>
              </div>
            </div>
          </div>
        </div>

        <div class="col-sm-6">
          <div class="input-group">
            <input type="search" class="form-control form-control-sm" v-model="searchInput" @input="handleSearch" placeholder="Search..." ref="searchInput"/>
            <button class="btn btn-sm btn-primary" type="button" @click="handleSearch">
              <i class="fas fa-search"></i>
            </button>
          </div>
        </div>
      </div>

      <table :id="tableOptions.id" class="table table-sm table-hover align-middle table-bordered">
        <thead class="table-primary">
          <tr class="text-center">
            <th v-if="isEditing" style="width: 5%">
              <input type="checkbox" v-model="selectAll" @change="toggleSelectAll"/>
            </th>
            <th v-for="column in tableOptions.columns" :key="column.name" class="d-sm-table-cell" :style="column.style" @click="handleSort(column.name)">
              {{ column.label }}
              <i v-if="sort.column === column.name" class="float-end ps-2 pe-2" :class="getSortIcon()"></i>
            </th>
            <th v-if="isEditing && tableOptions.enableActions" style="width: 5%">
              Action
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td :colspan="getColspan()">Loading...</td>
          </tr>
          <tr v-else v-for="(row, index) in tableData" :key="index">
            <td v-if="isEditing"  class="text-center">
              <input type="checkbox" v-model="selectedRows" :value="row">
            </td>
            <td v-for="column in tableOptions.columns" :key="column.name" :class="column.class" :style="column.style">
              <template v-if="isEditing">
                <select v-if="getInputType(column) === 'select'" v-model="row[column.name]" class="form-select form-select-sm">
                  <option v-for="option in column.options" :key="option.value" :value="option.value">{{ option.label }}</option>
                </select>
                <input v-else :type="getInputType(column)" v-model="row[column.name]" class="form-control form-control-sm">
              </template>
              <template v-else>
                {{ row[column.name] }}
              </template>
            </td>
            <td v-if="isEditing && tableOptions.enableActions" class="text-center">
              <a class="btn btn-sm btn-outline-light link-danger p-0" title="Delete" @click="deleteRow(row)"><i
                  class="fas fa-times-circle"></i></a>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="row mt-3">
        <div class="col-sm-6">
          <div class="dataTables_info">
            Showing {{ (page - 1) * pageSize + 1 }} to
            {{ Math.min(page * pageSize, recordsFiltered) }} of
            {{ recordsFiltered }} entries
          </div>
        </div>
        <div class="col-sm-6">
          <Pagination
            class="float-end"
            :total="Math.ceil(recordsFiltered / pageSize)"
            :value="page"
            :eachSide="2"
            @input="handlePageChange"
          />
        </div>
      </div>
    </div>
    <CsvImportModal
        ref="csvImportModal"
        :expectedHeaders="expectedHeaders"
        @importData="handleImportData"
    />
  </div>
</template>

<script>
import { nextTick } from 'vue';
// import ImportCsvList from "./ImportCsvList";
import Pagination from "@/components/table/Pagination.vue";
// import TableExports from "@/components/table/TableExports.vue";
import CsvImportModal from '@/components/CsvImportModal.vue';
import * as XLSX from 'xlsx';

export default {
  name: "ImportAddEditCheckTable",
  components: {
    // ImportCsvList,
    Pagination,
    // TableExports
    CsvImportModal
  },
  props: {
    tableOptions: { type: Object, required: true },
    tableData: { type: Array, required: true },
    isEditing: { type: Boolean, default: false },
    loading: { type: Boolean, default: false },
    page: { type: Number, default: 1 },
    pageSize: { type: Number, default: 10 },
    recordsFiltered: { type: Number, default: 0 },
    search: { type: String, default: "" },
    sort: { type: Object, default: () => ({ column: "", direction: "asc" }) },
    exportBaseUrl: { type: String, default: null },
    fetchAllData: {type: Function, required: true,},
  },
  emits: [
    "updateTableData",
    "update:page",
    "update:pageSize",
    "update:search",
    "update:sort",
    "deleteRows",
    "importData",
    "addRow",
  ],
  data() {
    return {
      selectedRows: [],
      selectAll: false,
      searchInput: this.search,
      internalPageSize: this.pageSize,
      isExporting: false,
    };
  },
  computed: {
    expectedHeaders() {
      return this.tableOptions.columns.map(column => column.name);
    }
  },
  mounted() {
    this.focusSearchInput();
  },
  methods: {
    getColspan() {
      return this.isEditing
        ? this.tableOptions.columns.length + 2
        : this.tableOptions.columns.length;
    },
    handlePageSizeChange() {
      this.$emit("update:pageSize", this.internalPageSize);
    },
    handleSearch() {
      this.$emit("update:search", this.searchInput);
      this.focusSearchInput();
    },
    focusSearchInput() {
      nextTick(() => {
        if (this.$refs.searchInput) {
          this.$refs.searchInput.focus();
        }
      });
    },
    handlePageChange(newPage) {
      this.$emit("update:page", newPage);
    },
    handleSort(column) {
      const newSort = {
        column,
        direction:
          this.sort.column === column && this.sort.direction === "asc"
            ? "desc"
            : "asc",
      };
      this.$emit("update:sort", newSort);
    },
    getSortIcon() {
      return this.sort.direction === "asc"
        ? "bi bi-sort-alpha-down text-secondary"
        : "bi bi-sort-alpha-up text-secondary";
    },
    toggleSelectAll() {
      this.selectedRows = this.selectAll ? [...this.tableData] : [];
    },
    deleteSelected() {
      this.$emit("deleteRows", this.selectedRows);
      this.selectedRows = [];
      this.selectAll = false;
    },
    addRow() {
      this.$emit('addRow');
    },
    deleteRow(row) {
      this.$emit("deleteRows", [row]);
    },
    async exportData(format) {
      this.isExporting = true;
      try {
        const allData = await this.fetchAllData();
        const data = this.prepareDataForExport(allData);
        if (format === 'csv') {
          this.exportCSV(data);
        } else if (format === 'xlsx') {
          this.exportXLSX(data);
        }
      } catch (error) {
        console.error('Error exporting data:', error);
        // You might want to show an error message to the user here
      } finally {
        this.isExporting = false;
      }
    },
    prepareDataForExport(data) {
      const headers = this.tableOptions.columns.map(column => column.name);
      const rows = data.map(row =>
          this.tableOptions.columns.map(column => row[column.name])
      );
      return [headers, ...rows];
    },
    exportCSV(data) {
      const csvContent = data.map(row =>
          row.map(cell =>
              typeof cell === 'string' && cell.includes(',') ? `"${cell}"` : cell
          ).join(',')
      ).join('\n');
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
      this.downloadFile(blob, 'export.csv');
    },
    exportXLSX(data) {
      const ws = XLSX.utils.aoa_to_sheet(data);
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
      XLSX.writeFile(wb, "export.xlsx");
    },
    downloadFile(blob, fileName) {
      const link = document.createElement('a');
      if (link.download !== undefined) {
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', fileName);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    },
    showImportModal() {
      this.$refs.csvImportModal.showModal();
    },
    handleImportData(importedData) {
      this.$emit("importData", importedData);
    },
    triggerFileImport() {
      this.showImportModal();
    },
    getInputType(column) {
      if (column.type === 'select') {
        return 'select';
      }

      if (column.inputType) {
        return column.inputType;
      }

      switch (column.name.toLowerCase()) {
        case 'email':
          return 'email';
        case 'url':
        case 'urlformatch':
        case 'imgurlformatch':
          return 'url';
        case 'date':
        case 'createddate':
        case 'updateddate':
          return 'date';
        case 'number':
        case 'id':
          return 'number';
        default:
          return 'text';
      }
    },
  },
  watch: {
    search(newSearch) {
      this.searchInput = newSearch;
    },
    pageSize(newPageSize) {
      this.internalPageSize = newPageSize;
    },
  },
};
</script>

<style scoped lang="scss">
@import "~bootstrap/scss/_functions";
@import "~bootstrap/scss/_variables";
@import "~bootstrap/scss/mixins/_breakpoints";
@import "bootstrap";

table {
  table-layout: fixed;
}

td {
  vertical-align: middle;
  overflow-wrap: break-word;
}

.table-header-hover {
  > thead > tr > th:hover {
    --bs-table-accent-bg: var(--bs-table-hover-bg);
    color: var(--bs-table-hover-color);
    cursor: pointer;
  }
}

.table th {
  cursor: pointer;
}
</style>
