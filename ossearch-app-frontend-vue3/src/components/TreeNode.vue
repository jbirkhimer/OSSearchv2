<template>
  <ul class="list-unstyled">

    <li>
      <div :id="'branch_'+text" class="branch" :class="{ bold: isFolder }" @click="toggle">

        <template v-if="isFolder">
          <i v-if="hasChildren" :class="open ? 'minus-square bi-dash-square' : 'plus-square bi-plus-square-dotted'"></i>
          <span v-else class="branch-empty"></span>
          <i class="text-warning ms-1" :class="open ? 'fa fa-folder-open' : 'fa fa-folder'" aria-hidden="true"></i>
          {{ text }}
          ({{nodes.length}})
        </template>

        <template v-else>
          <span class="form-check value ms-1" @click="updateSelected({target: {value: {name: text, date: date}, checked: !isSelected({name: text, date: date})}})">
            <input @contextmenu.prevent :checked="isSelected({name: text, date: date})" :value="{name: text, date: date}" class="form-check-input" type="checkbox" ref="checkbox" />
            <!--            <i v-if="data.type === '.json'" class="bi bi-filetype-json text-primary me-2" aria-hidden="true"/>-->
            <label class="form-check-label" for="checkbox">{{ text }}</label>
          </span>
        </template>
      </div>

      <template :id="'node_'+text" v-if="isFolder">
        <TreeNode v-show="open"
                  v-for="(node, index) in nodes" :key="index"
                  :parent="text"
                  :text="node.name"
                  :date="node.date"
                  :type="node.type"
                  :nodes="node.children"
                  :selected="selected"
                  @selected="$emit('selected', $event)"
                  class="node"
                  :class="{ open: open, first: index === 0 && !checkLast(index), last: checkLast(index) }"
        ></TreeNode>
      </template>

    </li>

  </ul>
</template>

<script>

export default {
  name: 'TreeNode',
  emits: ['selected'],
  props: {
    text: {
      type: String,
      required: true,
      default: ''
    },
    date: {
      type: String,
      required: true,
      default: ''
    },
    parent: {
      type: String,
      required: false,
      default: ''
    },
    type: {
      type: String,
      required: false,
      default: ''
    },
    nodes: {
      type: Array,
      default: () => []
    },
    selected: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      open: true,
      selectedNode: false
    }
  },
  /* created() {
    this.$watch(
        () => this.selected,
        async (val) => {
          this.selectedNode = val
          this.$emit('update:selected', val)
        },
        {immediate: true}
    )
  }, */
  computed: {
    isFolder () {
      // return this.data.children && this.data.children.length > 0;
      return this.type === 'folder'
    },
    hasChildren () {
      return this.nodes && this.nodes.length > 0
    }
  },
  methods: {
    async toggle () {
      // console.log("this.isFolder", this.isFolder, "open", this.open)
      if (this.isFolder && this.hasChildren) {
        this.open = !this.open
        // console.log("this.isFolder", this.isFolder, "open", this.open)
      }
    },
    checkLast (i) {
      return (i + 1) === this.nodes.length
    },
    isSelected (value) {
      // console.log("isSelected", this.selected[this.parent], value)
      let obj = this.selected.find(o => Object.keys(o)[0] === this.parent)
      // console.log("isSelected", obj, value)
      return obj ? JSON.stringify(obj[this.parent]) === JSON.stringify(value) : false
    },
    updateSelected (event) {
      let updatedValue = this.selected

      let obj = updatedValue.find(o => Object.keys(o)[0] === this.parent)

      let value = event.target.value
      // console.log("updateSelected",value)
      // remove name if checked, else add name
      if (event.target.checked) {
        obj ? obj[this.parent] = value : updatedValue.push({ [this.parent]: value })
      } else {
        delete obj[this.parent]
      }
      // emit the updated value
      this.$emit('selected', updatedValue)
    }
  }
}
</script>

<style lang="scss" scoped>
</style>