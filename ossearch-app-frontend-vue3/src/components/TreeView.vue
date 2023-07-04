<template>
  <TreeNode v-for="(node, i) in tree" :key="i"
            :text="node.name"
            :date="node.date"
            :type="node.type"
            :nodes="node.children"
            :selected="selected"
            @selected="$emit('update:selected', $event)"
            class="treeview"
  ></TreeNode>
</template>

<script>
import TreeNode from './TreeNode.vue'

export default {
  name: 'TreeView',
  emits: ['update:selected'],
  components: {
    TreeNode
  },
  props: {
    tree: {
      type: Array,
      required: true,
      default: () => []
    },
    selected: {
      type: Object,
      required: true,
      default: () => {}
    }
  }
}
</script>

<style lang="scss">
// Import Bootstrap variables for use within theme
@import "~bootstrap/scss/functions.scss";
@import "~bootstrap/scss/variables.scss";

$border: 2px solid #607d8b;

.item {
  cursor: pointer;
}

.bold {
  font-weight: bold;
}

.fa {
  cursor: pointer;
}

.treeview {
  //background-color: #36404a;
  padding: 20px;
  border: none;
  border-radius: 10px;
  display: flex;
  height: 500px;
  overflow: auto;

  svg {
    margin-right: .5em;
  }

  ul {
    &.treeview > li:before,
    &.treeview > li:after {
      display: none;
    }

    //font-weight: bold;
    list-style: none;
    margin: 0;
    padding-left: 20px;
    white-space: nowrap;

    &.last {
      li:after {
        display: none;
      }
    }

    li {
      position: relative;

      .branch {
        //color: white;
        cursor: default;
        display: flex;
        align-items: center;
        width: 100%;

        .minus-square,
        .plus-square {
          cursor: pointer;
        }

        .value {
          &:hover {
            color: $primary;
            font-weight: bold;
          }
        }
      }

      .branch-empty {
        display: flex;
        align-items: center;
        width: 16px;

        &:after {
          position: absolute;
          content: "";
          top: 11px;
          //left: -13px;
          //border-left: $border;
          border-top: $border;
          //border-radius: 5px 0 0 0;
          width: 16px;
          height: 100%;
        }

        &:before {
          content: "";
          position: absolute;
          top: -2px;
          //left: -13px;
          //border-left: $border;
          border-bottom: $border;
          //border-radius: 0 0 0 5px;
          width: 16px;
          height: 15px;
        }
      }

      .node {
        height: 0;
        opacity: 0;
        visibility: hidden;
        transition: all .5s ease-in-out;

        &.open {
          height: 100%;
          opacity: 1;
          visibility: visible;
        }
      }

      &:after {
        position: absolute;
        content: "";
        top: 11px;
        left: -13px;
        border-left: $border;
        border-top: $border;
        //border-radius: 5px 0 0 0;
        width: 13px;
        height: 100%;
      }

      &:before {
        content: "";
        position: absolute;
        top: -4px;
        left: -13px;
        border-left: $border;
        border-bottom: $border;
        //border-radius: 0 0 0 5px;
        width: 13px;
        height: 17px;
      }
    }
  }
}
</style>