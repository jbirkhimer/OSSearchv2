<template>
  <slot name="caption"></slot>
  <!-- Check/Select All/None -->
  <div class="btn-toolbar mb-2" role="toolbar">
    <div class="btn-group btn-group-sm align-items-center me-2" role="group">
      <div class="btn btn-primary btn-checkbox">
        <input type="checkbox" :checked="selectedLocal.length > 0" @change.prevent="toggleAll"
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
      <button type="button" class="btn btn-sm btn-primary" @click.prevent="addRow">
        Add
      </button>
    </div>
  </div>

  <div class="tableFixHead" :style="'max-height: '+height+'px'" ref="tableDiv">
    <table class="table table-sm table-bordered">
      <thead class="table-primary">
        <!-- Column Headers -->
        <tr class="text-center">
          <th v-if="!isEditing" style="width: 5%">Select</th>
          <th v-for="(col, i) in tableOptions.columns" :key="i" :style="'width:' + col.width">{{ col.label }}</th>
          <th v-if="tableOptions.enableActions && !isEditing" style="width: 5%">Action</th>
        </tr>
      </thead>

      <tbody>
        <tr v-if="loading">
          <td :colspan="tableOptions.columns.length" class="text-center">
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
        <tr v-if="saving">
          <td :colspan="tableOptions.columns.length + 2" class="text-center">
            <div v-if="saving" class="d-flex flex-column align-items-center justify-content-center">
              <div class="row">
                <div class="spinner-border" role="status">
                  <span class="visually-hidden">Saving...</span>
                </div>
              </div>
              <div class="row">
                <strong>Saving</strong>
              </div>
            </div>
          </td>
        </tr>
        <template v-if="!saving && !loading">
          <template v-if="tableData.length > 0">
            <!-- Loop Over Row Entries  -->
            <tr v-for="(entry, i) in tableData" :key="i">

              <!-- Select Row -->
              <td v-show="!isEditing" class="text-center" style="width: 5%">
                <input type="checkbox" :checked="isChecked(entry)" @change="addSelected(entry, $event)">
              </td>

              <!-- Loop Row Cells -->
              <template v-for="(key, j) in tableOptions.columns" :key="j">
                <!-- Checkbox -->
                <template v-if="key.type === 'checkbox'">
                  <td class="text-center" :class="key.class" :style="{width: key.width}">
                    <input v-if="tableOptions.columns.length > 1" v-model="entry[key.name]" type="checkbox"
                           :checked="entry[key.name]" :disabled="!isEditMode(i)" :ref="'row_' + i +'col_' + j">
                    <input v-else :value="entry" @change="updateSingleValue($event.target.value, i)" type="checkbox"
                           :checked="entry" :disabled="!isEditMode(i)" :ref="'row_' + i +'col_' + j">
                  </td>
                </template>

                <!-- Select -->
                <template v-if="key.type === 'select'">
                  <td :class="key.class" :style="{width: key.width}">
                    <template v-if="isEditMode(i)">
                      <select v-model="entry[key.name]" class="form-control-sm" :ref="'row_' + i +'col_' + j">
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
                             class="form-control form-control-sm" :ref="'row_' + i +'col_' + j" :disabled="isDisabled(entry, key)" @keyup.enter="saveRow(entry, i)">
                      <input v-else :value="entry" @input="updateSingleValue($event.target.value, i)" type="text"
                             class="form-control form-control-sm" :ref="'row_' + i +'col_' + j" :disabled="isDisabled(entry, key)" @keyup.enter="saveRow(entry, i)">
                    </template>
                    <template v-if="!isEditMode(i)">
                      <span v-if="tableOptions.columns.length > 1">{{ entry[key.name] }}</span>
                      <span v-else>{{ entry }}</span>
                    </template>
                  </td>
                </template>
              </template>

              <!-- Row Edit/Delete Actions -->
              <template v-if="tableOptions.enableActions && !isEditing">
                <td v-if="tableOptions.actionDisabledDefaultValues ? !tableOptions.actionDisabledDefaultValues.includes(entry) : true" class="justify-content-evenly text-center" style="width: 5%">
                  <div class="btn-group btn-group-sm align-items-center">
                    <template v-if="!isEditMode(i)">
                      <a href="#" class="btn btn-outline-light p-0 m-1" :class="isEditing ? 'link-secondary' : 'link-primary'" title="Edit" @click.prevent="editRow(entry, i)"><i
                          class="fas fa-edit"></i></a>
                    </template>
                    <template v-else>
                      <a href="#" class="btn btn-outline-light p-0 m-1" :class="isEditing ? 'link-secondary' : 'link-success'" title="Save" @click.prevent="saveRow(entry, i)"><i
                          class="fas fa-check"></i></a>
                    </template>
                    <a class="btn btn-outline-light p-0" :class="isEditing ? 'link-secondary' : 'link-danger'" title="Delete" @click.prevent="deleteRow(entry, i)"><i
                        class="fas fa-times-circle"></i></a>
                  </div>
                </td>
              </template>
            </tr>
          </template>
          <template v-else>
            <tr>
              <td v-if="!isEditing" :colspan="tableOptions.columns.length+2" class="text-center">None</td>
              <td v-else :colspan="tableOptions.columns.length" class="text-center">None</td>
            </tr>
          </template>
        </template>
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
    height: {
      type: Number,
      default: 300
    },
    uniqueCheckField: {
      type: String
    },
    isEditing: {
      type: Boolean
    },
    loading: {
      type: Boolean
    },
    saving: {
      type: Boolean
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
        isChecked = this.selectedLocal.some(x => {
          // let resolvedX = this.resolve(this.uniqueCheckField, x);
          // let resolvedEntry = this.resolve(this.uniqueCheckField, entry)
          // console.log(">>>>> resolvedX", resolvedX, "resolvedEntry", resolvedEntry)
          return this.resolve(this.uniqueCheckField, x) === this.resolve(this.uniqueCheckField, entry)
        })
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
    toggleAll(event) {
      if (event.target.checked) {
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
      if (this.editList[i]) {
        this.editList[i].editMode = false
      }
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

      // let tableDiv = this.$refs.tableDiv;
      // this.$nextTick(() => tableDiv.scrollTop = tableDiv.scrollHeight)

      let refName = 'row_' + (merged.length - 1) + 'col_0'
      this.$nextTick(() => this.$refs[refName][0].focus())
    },
    deleteSelected(event) {
      event = JSON.parse(JSON.stringify(event))
      //console.log("deleteselected event:", event)
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
      try {
      let merged = this.tableData
      let rows = event.trim().split(/\r\n|\n/)

      for (let row in rows) {
        if (rows[row].trim() !== "") {
          let rowObject = {}
          let rowValues = rows[row].split(/,/)
          //console.log('rowValues.length: ' + rowValues.length)

          if (rowValues.length !== this.tableOptions.columns.length) throw this.errorMsg("Invalid number of columns, expected: " + this.tableOptions.columns.length + " got " + rowValues.length + "!\nPlease be sure to include only the following columns:\n")

          if (rowValues.length > 1) {
            for (let i = 0; i < rowValues.length; i++) {
              let columnName = this.tableOptions.columns[i].name
              rowObject[columnName] = rowValues[i].trim()
            }
          } else {
            rowObject = rows[row].trim()
          }

          console.log("import rowObject", JSON.stringify(rowObject))

          if (!merged.some(x => JSON.stringify(x) === JSON.stringify(rowObject))) {
            merged.push(rowObject)
            let index = merged.findIndex(x => JSON.stringify(x) === JSON.stringify(rowObject));
            this.editList[index] = {editMode: false}
          }
        }
      }
      // console.log('import merged: ' + JSON.stringify(merged, null, 2))
      // console.log('tableOptions.columns.length: ' + this.tableOptions.columns.length)
      this.$emit('updateTableData', merged)
      } catch (err) {
        alert("Error during import! "+err)
      }
    },
    errorMsg(msg) {
      let colList = []
      this.tableOptions.columns.forEach(col => {
        colList.push(col.label)
      })
      return msg + colList
    }
  }
}
</script>

<style scoped lang="scss">

/* Set a fixed scrollable wrapper */
.tableFixHead {
  //max-height: 500px;
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
