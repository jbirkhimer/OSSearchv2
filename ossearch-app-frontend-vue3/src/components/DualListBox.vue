<template>
  <div class="row align-items-center flex-nowrap">
    <div class="col-6 flex-shrink-1">
      <h5>Collections Available for Backup</h5>
      <div class="input-group mb-2">
        <button class="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">Select</button>
        <ul class="dropdown-menu">
          <li><a class="dropdown-item" href="#" @click="selectAllSource">Select All</a></li>
          <li><a class="dropdown-item" href="#" @click="deSelectAllSource">None</a></li>
        </ul>
        <input v-model="searchSource" class="form-control" type="text" @keyup.esc=" searchSource='' " placeholder="Search"/>
        <button v-if="searchSource" type="button" class="btn btn-danger fw-bold" title="Clear Search" @click=" searchSource='' ">&times;</button>
      </div>
      <ul class="list-group border rounded overflow-auto" style="height: 250px">
        <li v-for="(item,key) in source.map((item,inx) => ({inx,...item})).filter(item => item[label in item ? label : 'label'].toLowerCase().includes(searchSource.toLowerCase()))" v-bind:key="key" :class="'list-group-item list-group-item-action'+ (item.selected ? ' active':'')" @click="selectSource(searchSource?item.inx:key)">{{ item[label in item ? label : 'label'] }}</li>
        <li v-if="source.filter(item => item[label in item ? label : 'label'].toLowerCase().includes(searchSource.toLowerCase())).length == 0 && source.length" class="list-group-item list-group-item-action">No results found</li>
      </ul>
    </div>

    <div class="col flex-shrink-1 d-grid gap-2 mb-0">
      <button type="button" class="btn btn-primary" @click="moveDestination">
        <i class="fas fa-angle-right"></i>
      </button>
      <button type="button" class="btn btn-primary" @click="moveAllDestination">
        <i class="fas fa-angle-double-right"></i>
      </button>
      <button type="button" class="btn btn-primary" @click="moveAllSource">
        <i class="fas fa-angle-double-left"></i>
      </button>
      <button type="button" class="btn btn-primary" @click="moveSource">
        <i class="fas fa-angle-left"></i>
      </button>
    </div>

    <div class="col-6 flex-shrink-1">
      <h5>Selected For Backup</h5>
      <div class="input-group mb-2">
        <button class="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">Select</button>
        <ul class="dropdown-menu">
          <li><a class="dropdown-item" href="#" @click="selectAllDestination">Select All</a></li>
          <li><a class="dropdown-item" href="#" @click="deSelectAllDestination">None</a></li>
        </ul>
        <input v-model="searchDestination" class="form-control" type="text" placeholder="Search"/>
        <button v-if="searchDestination" type="button" class="btn btn-danger fw-bold" title="Clear Search" @click=" searchDestination='' ">&times;</button>
      </div>
      <ul class="list-group border rounded overflow-auto" style="height: 250px">
        <li v-for="(item,key) in destination.map((item,inx) => ({inx,...item})).filter(item => item[label in item ? label : 'label'].toLowerCase().includes(searchDestination.toLowerCase()))" v-bind:key="key" :class="'list-group-item list-group-item-action'+ (item.selected ? ' active':'')" @click="selectDestination(searchDestination?item.inx:key)">{{ item[label in item ? label : 'label'] }}</li>
        <li v-if="destination.filter(item => item[label in item ? label : 'label'].toLowerCase().includes(searchDestination.toLowerCase())).length == 0 && destination.length" class="list-group-item list-group-item-action">No results found</li>
      </ul>
    </div>
  </div>

</template>

<script>
// import "../assets/style.css";

export default {
  name: 'DualListBox',
  props: {
    source: Array,
    destination: Array,
    label: String
  },
  data () {
    return {
      searchSource: '',
      searchDestination: ''
    }
  },
  methods: {
    moveDestination () {
      let selected = this.source.filter(f => f.selected)
      if (!selected.length) return
      selected = selected.map(item => ({...item, selected: false}))
      let destination = [...selected, ...this.destination]
      let source = this.source.filter(f => !f.selected)
      this.searchSource = ''
      this.searchDestination = ''
      this.$emit('onChangeList', {source, destination})
    },
    moveSource () {
      let selected = this.destination.filter(f => f.selected)
      if (!selected.length) return
      selected = selected.map(item => ({...item, selected: false}))
      let source = [...selected, ...this.source]
      let destination = this.destination.filter(f => !f.selected)
      this.searchSource = ''
      this.searchDestination = ''
      this.$emit('onChangeList', {source, destination})
    },
    moveAllDestination () {
      let destination = [
        ...this.source.map(item => ({ ...item, selected: false })),
        ...this.destination
      ]
      let source = []
      this.searchSource = ''
      this.searchDestination = ''
      this.$emit('onChangeList', {source, destination})
    },
    moveAllSource () {
      let source = [
        ...this.destination.map(item => ({ ...item, selected: false })),
        ...this.source
      ]
      let destination = []
      this.searchSource = ''
      this.searchDestination = ''
      this.$emit('onChangeList', {source, destination})
    },
    selectDestination (key) {
      let source = this.source
      let destination = this.destination.map((i, k) => {
        if (k === key) {
          i.selected = !i.selected
        }
        return i
      })
      this.$emit('onChangeList', {source, destination})
    },
    selectSource (key) {
      let destination = this.destination
      let source = this.source.map((i, k) => {
        if (k === key) {
          i.selected = !i.selected
        }
        return i
      })
      this.$emit('onChangeList', {source, destination})
    },
    selectAllSource () {
      let source = this.source.map(item => ({ ...item, selected: true }))
      let destination = this.destination
      this.$emit('onChangeList', {source, destination})
    },
    deSelectAllSource () {
      let source = this.source.map(item => ({ ...item, selected: false }))
      let destination = this.destination
      this.$emit('onChangeList', {source, destination})
    },
    selectAllDestination () {
      let destination = this.destination.map(item => ({...item, selected: true}))
      let source = this.source
      this.$emit('onChangeList', {
        source,
        destination
      })
    },
    deSelectAllDestination () {
      let destination = this.destination.map(item => ({...item, selected: false}))
      let source = this.source
      this.$emit('onChangeList', {source, destination})
    }
  }
}
</script>

<style lang="scss" scoped>

</style>