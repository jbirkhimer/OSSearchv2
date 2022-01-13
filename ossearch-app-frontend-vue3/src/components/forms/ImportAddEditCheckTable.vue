<template>
  <!-- Check/Select All/None -->
  <div class="btn-toolbar" role="toolbar">
    <div class="btn-group btn-group-sm align-items-center me-2" role="group">
      <div class="btn btn-primary btn-checkbox">
        <input type="checkbox" :checked="tableData.length === selectedLocal.length && tableData.length > 0" @change.prevent="toggleAll"
               class="checkbox-inline"/>
      </div>
      <button type="button" class="btn btn-primary dropdown-toggle dropdown-toggle-split" data-bs-toggle="dropdown"
              aria-expanded="false">
        <span class="visually-hidden">Toggle Dropdown</span>
      </button>
      <ul class="dropdown-menu">
        <li><a class="dropdown-item" @click.prevent="selectAll(true)">All</a></li>
        <li><a class="dropdown-item" @click.prevent="selectAll(false)">None</a></li>
      </ul>
      <button v-if="selectedLocal.length > 0 && tableOptions.enableActions" class="btn btn-sm btn-danger" title="Delete"
              @click.prevent="deleteSelected(selectedLocal)">
        <i class="bi-trash-fill"></i>
      </button>
    </div>
    <div v-if="tableOptions.enableImport" class="btn-group btn-group-sm align-items-center me-2" role="group">
      <ImportCsvList @load="importTableData"/>
    </div>
    <div v-if="tableOptions.enableAddRow" class="btn-group btn-group-sm align-items-center" role="group">
      <button type="button" class="btn btn-sm btn-primary" @click="addRow">
        Add
      </button>
    </div>
  </div>

  <div class="tableFixHead">
    <table class="table table-sm table-bordered">
      <thead class="table-primary">
      <!-- Column Headers -->
      <tr class="text-center">
        <th style="width: 5%">Select</th>
        <th v-for="(col, i) in tableOptions.columns" :key="i" :style="{width: col.width}">{{ col.label }}</th>
        <th v-if="tableOptions.enableActions" style="width: 5%">Action</th>
      </tr>
      </thead>

      <tbody v-if="tableData.length > 0">
      <!-- Loop Over Row Entries  -->
      <tr v-for="(entry, i) in tableData" :key="i">

        <!-- Select Row -->
        <td class="text-center" style="width: 5%">
          <input type="checkbox" :checked="isChecked(entry)" @change="addSelected(entry, $event)">
        </td>

        <!-- Loop Row Cells -->
        <template v-for="(key, j) in tableOptions.columns" :key="j">
          <!-- Checkbox -->
          <template v-if="key.type == 'checkbox'">
            <td class="text-center" :class="key.class" :style="{width: key.width}">
              <input v-if="tableOptions.columns.length > 1" v-model="entry[key.name]" type="checkbox"
                     :checked="entry[key.name]" :disabled="!isEditMode(i)">
              <input v-else :value="entry" @change="updateSingleValue($event.target.value, i)" type="checkbox"
                     :checked="entry" :disabled="!isEditMode(i)">
            </td>
          </template>

          <!-- Select -->
          <template v-if="key.type === 'select'">
            <td :class="key.class" :style="{width: key.width}">
              <template v-if="isEditMode(i)">
                <select v-model="entry[key.name]" class="form-control-sm">
                  <option v-for="(option, i) in key.options" :key="i" :value="option.value">{{ option.label }}
                  </option>
                </select>
              </template>
              <template v-if="!isEditMode(i)">
                <span v-if="tableOptions.columns.length > 1">{{ entry[key.name] }}</span>
                <span v-else>{{ entry }}</span>
              </template>
            </td>
          </template>

          <template v-if="key.type === 'slot'">
            <td :class="key.class" :style="{width: key.width}">
              <slot :name="key.name" :entry="entry[key.name]"/>
            </td>
          </template>

          <!-- Text -->
          <template v-else-if="!['checkbox', 'select'].includes(key.type)">
            <td :class="key.class" :style="{width: key.width}">
              <template v-if="isEditMode(i)">
                <input v-if="tableOptions.columns.length > 1" v-model="entry[key.name]" type="text"
                       class="form-control form-control-sm" :id="entry[key.name]" :disabled="isDisabled(entry, key)">
                <input v-else :value="entry" @input="updateSingleValue($event.target.value, i)" type="text"
                       class="form-control form-control-sm" :id="entry" :disabled="isDisabled(entry, key)">
              </template>
              <template v-if="!isEditMode(i)">
                <span v-if="tableOptions.columns.length > 1">{{ entry[key.name] }}</span>
                <span v-else>{{ entry }}</span>
              </template>
            </td>
          </template>
        </template>

        <!-- Row Edit/Delete Actions -->
        <template v-if="tableOptions.enableActions">
        <td v-if="tableOptions.actionDisabledDefaultValues ? !tableOptions.actionDisabledDefaultValues.includes(entry) : true" class="justify-content-evenly text-center" style="width: 5%">
          <div class="btn-group btn-group-sm align-items-center">
            <template v-if="!isEditMode(i)">
              <a href="#" class="edit link-primary m-1" title="Edit" @click="editRow(entry, i)"><i
                  class="bi-pencil-fill text-primary"></i></a>
            </template>
            <template v-else>
              <a href="#" class="save link-success m-1" title="Save" @click="saveRow(entry, i)"><i
                  class="bi-check-lg text-success"></i></a>
            </template>
            <a href="#" class="delete link-danger" title="Delete" @click="deleteRow(entry, i)"><i
                class="bi-x-circle-fill text-danger"></i></a>
          </div>
        </td>
        </template>
      </tr>
      </tbody>
      <tbody v-else>
      <tr>
        <td :colspan="tableOptions.columns.length + 2" class="text-center">None</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import ImportCsvList from "./ImportCsvList";

export default {
  name: "ImportAddEditCheckTable",
  props: {
    tableOptions: {
      type: Object,
      required: true
    },
    tableData: {
      type: Object,
      required: true
    },
    selected: {
      type: Array,
      default() {
        return []
      }
    },
    uniqueCheckField: {
      type: String
    }
  },
  components: {
    ImportCsvList
  },
  emits: [
    "updateTableData",
    "selected"
  ],
  data() {
    return {
      selectedLocal: JSON.parse(JSON.stringify(this.selected)),
      editList: {}
    }
  },
  methods: {
    resolve(path, obj, separator = '.') {
      let properties = Array.isArray(path) ? path : path.split(separator)
      return properties.reduce((prev, curr) => prev && prev[curr], obj)
    },
    isChecked(entry) {
      // console.log("uniqueCheckField", this.uniqueCheckField, "selectedLocal", this.selectedLocal, "entry", entry)
      let isChecked = false
      if (this.uniqueCheckField) {
        isChecked = this.selectedLocal.some(x => this.resolve(this.uniqueCheckField, x) === this.resolve(this.uniqueCheckField, entry))
      } else {
        isChecked = this.selectedLocal.some(x => JSON.stringify(x) === JSON.stringify(entry))
      }
      return isChecked
    },
    addSelected(entry, event) {
      // this.selectedLocal = JSON.parse(JSON.stringify(this.selected))
      if (event.target.checked) {
        this.selectedLocal.push(entry)
      } else {
        let index = this.selectedLocal.findIndex(x => JSON.stringify(x) === JSON.stringify(entry))
        this.selectedLocal.splice(index, 1)
      }
      this.$emit('selected', this.selectedLocal)
    },
    selectAll(isAll) {
      if (isAll) {
        this.selectedLocal = JSON.parse(JSON.stringify(this.tableData))
      } else {
        this.selectedLocal = []
      }
      this.$emit('selected', this.selectedLocal)
    },
    toggleAll() {
      if (this.tableData.length > this.selectedLocal.length) {
        this.selectAll(true)
      } else {
        this.selectAll(false)
      }
    },
    isEditMode(i) {
      if (!this.editList[i]) {
        return false
      }
      return this.editList[i].editMode
    },
    isDisabled(entry, key) {
      if (key.disabled) {
        return true
      } else if (key.disabledBy) {
        return !entry[key.disabledBy]
      } else {
        return false
      }
    },
    // isSelected(option) {
    //   console.log("isSelected: value: " + option.value + ", option.selected: " + option.selected)
    //   if (option.selected) {
    //     console.log("isSelected: value: " + option.value + ", has option.selected: " + option.selected)
    //     return option.value
    //   }
    // },
    updateSingleValue(value, index) {
      let merged = JSON.parse(JSON.stringify(this.tableData))
      merged[index] = value
      this.$emit('updateTableData', merged)
    },
    editRow(entry, i) {
      for (let row in this.editList) {
        if (row !== i) {
          delete this.editList[row]
        }
      }
      this.editList[i] = {editMode: true}
    },
    saveRow(entry, i) {
      this.editList[i].editMode = false
      let merged = JSON.parse(JSON.stringify(this.tableData))
      merged[i] = entry
      this.$emit('updateTableData', merged)
    },
    deleteRow(entry, i) {
      this.editList[i].editMode = false
      this.deleteSelected([entry])
    },
    addRow() {
      let merged = JSON.parse(JSON.stringify(this.tableData))
      if (this.tableOptions.columns.length > 1) {
        let rowObject = {}
        for (let key in this.tableOptions.columns) {
          let column = this.tableOptions.columns[key]
          if (column.default) {
            rowObject[column.name] = column.default
          } else {
            rowObject[column.name] = ''
          }
        }
        merged.push(rowObject)
      } else {
        merged.push('')
      }
      this.editList[merged.length - 1] = {editMode: true}
      this.$emit('updateTableData', merged)
    },
    deleteSelected(event) {
      event = JSON.parse(JSON.stringify(event))
      console.log("deleteselected event:", event)
      //remove any default values that are not to be deleted
      if (this.tableOptions.actionDisabledDefaultValues) {
        event = event.filter(i => !this.tableOptions.actionDisabledDefaultValues.filter(f => JSON.stringify(f) === JSON.stringify(i)).length)
      }
      let answer = [];
      let tableData = JSON.parse(JSON.stringify(this.tableData))
      answer = tableData.filter(i => !event.filter(f => JSON.stringify(f) === JSON.stringify(i)).length)
      this.$emit('updateTableData', answer)
      this.selectAll(false)
    },
    importTableData(event) {
      let merged = this.tableData
      let rows = event.trim().split(/\r\n|\n/)

      for (let row in rows) {
        let rowObject = {}
        let rowValues = rows[row].split(/,/)
        console.log('rowValues.length: ' + rowValues.length)

        if (rowValues.length > 1) {
          for (let i = 0; i < rowValues.length; i++) {
            let columnName = this.tableOptions.columns[i].name
            rowObject[columnName] = rowValues[i]
          }
        } else {
          rowObject = rows[row]
        }
        if (!merged.some(x => JSON.stringify(x) === JSON.stringify(rowObject))) {
          merged.push(rowObject)
          let index = merged.findIndex(x => JSON.stringify(x) === JSON.stringify(rowObject));
          this.editList[index] = {editMode: false}
        }
      }
      // console.log('import merged: ' + JSON.stringify(merged, null, 2))
      // console.log('tableOptions.columns.length: ' + this.tableOptions.columns.length)
      this.$emit('updateTableData', merged)
    },
  }
}
</script>

<style scoped lang="scss">

/* Set a fixed scrollable wrapper */
.tableFixHead {
  max-height: 300px;
  overflow-y: auto;
  width: 100%;

  /* Set header to stick to the top of the container. */
  thead tr th {
    position: sticky;
    top: 0;
    z-index: 1;
  }

  table {
    table-layout: fixed;
  }

  td {
    vertical-align: middle;
    //white-space: nowrap;
    //text-overflow: ellipsis;
    //overflow: hidden;
    overflow-wrap: break-word;
  }
}

</style>