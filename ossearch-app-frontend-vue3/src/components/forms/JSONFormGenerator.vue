<template>
  <!-- Loop Over Options  -->
  <template v-for="(key, i) in formOptions" :key="i">

    <!-- Checkbox -->
    <template v-if="['boolean','checkbox'].includes(key.type)">
      <div class="input-group-sm align-items-center">
        <div class="form-check me-2" :class="key.class">
          <input :checked="formData?.[key.name]" type="checkbox" :role="key.role" class="form-check-input" :id="key.name + 'check'" @change="updateCheckBox(key.name, $event.target.checked)">
          <label class="form-check-label" :for="key.name + 'check'">{{ key.name }}</label>
        </div>
      </div>
    </template>

    <!-- Select -->
    <template v-if="key.type === 'select'">
      <form class="row row-cols-lg-auto align-items-center">
        <div class="col">
          <div class="form-check">
            <input :checked="formData?.[key.name]" type="checkbox" class="form-check-input" :id="key.name + 'check'" @change="updateSelectCheckbox(key.name, $event.target.checked)">
            <label class="form-check-label" :for="key.name + 'check'">{{ key.name }}</label>
          </div>
        </div>
        <div class="col">
          <select v-if="isSelected(key.name)" :value="formData?.[key.name]" class="form-select form-select-sm" :class="!Boolean(formData?.[key.name]) ? 'is-invalid' : 'is-valid'" @input="updateText(key.name, $event.target.value)">
            <option v-for="(option, i) in key.options" :key="i" :value="option.value">{{ option.label }}</option>
          </select>
<!--          <div :id="'validation'+key.name+'Feedback'" class="invalid-feedback">
            Please select a value.
          </div>-->
        </div>
      </form>
    </template>

<!--    <template v-if="key.type === 'slot'">
      <td :class="key.class" :style="{width: key.width}">
        <slot :name="key.name" :entry="option[key.name]"/>
      </td>
    </template>-->

    <!-- sortable list -->
    <template v-if="key.type === 'sortable'">
      <form class="row row-cols-lg-auto">
        <div class="col">
          <div class="form-check">
            <input :checked="formData?.[key.name]" type="checkbox" class="form-check-input" :id="key.name + 'check'" @change="updateSortCheckbox(key.name, key.options, $event.target.checked)">
            <label class="form-check-label" :for="key.name + 'check'">{{ key.name }}</label>
          </div>
        </div>
        <div class="col-12">
          <draggable
              v-if="isSelected(key.name)"
              :list="formData?.[key.name]"
              item-key="key.options"
              class="list-group"
          >
            <template #item="{element}">
              <div class="list-group-item">{{element}}</div>
            </template>
          </draggable>
        </div>
      </form>

    </template>

    <!-- Text -->
    <template v-if="!['boolean', 'checkbox', 'select', 'sortable'].includes(key.type)">
      <form class="row row-cols-lg-auto align-items-center">
        <div class="col">
          <div class="form-check">
            <input :checked="formData?.[key.name]" type="checkbox" class="form-check-input" :id="key.name + 'check'" @change="updateTextCheckbox(key.name, $event.target.checked)">
            <label class="form-check-label" :for="key.name + 'check'">{{ key.name }}</label>
          </div>
        </div>
        <div class="col-12">
          <input v-if="isSelected(key.name)" :value="formData?.[key.name]" type="text" class="form-control form-control-sm" :class="!Boolean(formData?.[key.name]) ? 'is-invalid' : 'is-valid'" placeholder="value" aria-label="value" :aria-describedby="key.name + 'input'" @input="updateText(key.name, $event.target.value)">
<!--          <div id="validationServer03Feedback" class="invalid-feedback">
            Please provide a value.
          </div>-->
        </div>
      </form>
    </template>

    <div class="form-text mb-3" style="white-space: pre-line;" v-html="key.desc"></div>

  </template>
</template>

<script>
import draggable from "vuedraggable"

export default {
  name: "JSONFormGenerator",
  props: ['formData', 'step', 'formOptions', 'filterOptions'],
  emits: [ 'updateData', 'formData'],
  components: {
    draggable
  },
  data() {
    return {
      data: {},
      isChecked: [],
      list: ["score", "fetchTime", "httpsOverHttp", "urlLength"],
      list1: [
        { name: "John", id: 1 },
        { name: "Joao", id: 2 },
        { name: "Jean", id: 3 },
        { name: "Gerard", id: 4 }
      ],
    }
  },
  mounted() {
    //console.log("formData", this.formData)
  },
  methods: {
    isSelected(key) {
      // console.log("isSelected formData", this.formData, "key", key)
      if (this.formData) {
        //console.log("<<<<<<<<<<isSelected formData has value", Object.keys(this.formData).includes(key))
        return Object.keys(this.formData).includes(key)
      }
      return false
    },
    updateCheckBox(key, value) {
      let data = {}
      if (this.formData) {
        data = JSON.parse(JSON.stringify(this.formData))
      }
      if (value) {
        //console.log("????????????", key, value, data)
        // +++++++++++++++++++++++++++
        let kv = {[key]: value}
        data = {...data, ...kv}
        // +++++++++++++++++++++++++++
        // ---------------------------
        // data[key] = value
        // ---------------------------
      } else {
        delete data[key]
      }
      this.$emit('formData', data)
    },
    updateTextCheckbox(key, event) {
      //console.log("**************", key, event)
      let data = {}
      if (this.formData) {
        data = JSON.parse(JSON.stringify(this.formData))
      }
      if (event) {
        data[key] = ""
        this.$emit('formData', data)
      } else {
        delete data[key]
        this.$emit('formData', data)
      }
    },
    updateText(key, event) {
      let data = JSON.parse(JSON.stringify(this.formData))
      data[key] = event
      this.$emit('formData', data)
    },
    updateSelectCheckbox(key, event) {
      let data = {}
      if (this.formData) {
        data = JSON.parse(JSON.stringify(this.formData))
      }
      if (event) {
        data[key] = ""
        this.$emit('formData', data)
      } else {
        delete data[key]
        this.$emit('formData', data)
      }
    },


    updateSortCheckbox(key, value, event) {
      let data = {}
      if (this.formData) {
        data = JSON.parse(JSON.stringify(this.formData))
      }

      if (event) {
        data[key] = value
        this.$emit('formData', data)
      } else {
        delete data[key]
        this.$emit('formData', data)
      }
    },
    log(key, evt) {
      //console.log(evt);

      let data = {}
      if (this.formData) {
        data = JSON.parse(JSON.stringify(this.formData))
      }
      let oldIndex = evt.moved.oldIndex
      let newIndex = evt.moved.newIndex
      let element = data[key][oldIndex];
      data[key].splice(oldIndex, 1);
      data[key].splice(newIndex, 0, element);
      //console.log(data[key]);
      this.$emit('formData', data)
    },
    getFormDataOrDefault(key) {
      //console.log("getFormDataOrDefault key", key)
      // if (this.formData?.[key]) {
      //
      // }
      return key.options
    }
  }
}
</script>

<style scoped>

</style>