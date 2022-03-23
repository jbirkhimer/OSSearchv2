<template>

  <div class="row g-3 mb-0">
    <div class="col-md-12">
      <label for="testString" class="form-label">URL For Testing Regex</label>
      <div class="input-group mb-3">
        <input id="testString" type="text" class="form-control" v-model="testUrl" @keyup.enter="pushURLString()"
               placeholder="Add String" autofocus>
        <button class="btn btn-primary bi-plus" type="button" @click="pushURLString()"/>
      </div>
    </div>
  </div>
  <div class="row g-3 mb-3">
    <div class="col-md-12">
      <div class="form-floating">
        <html-textarea :rows="rows" class="form-control" style="height:100%;" placeholder="Leave a comment here"
                       id="floatingTextarea" v-html="highlight()"></html-textarea>
        <label for="floatingTextarea">Regex Result</label>
        <button class="btn btn-danger float-end" type="button" @click="resetURLString()">Reset</button>
      </div>
    </div>
  </div>

  <div class="row g-3 mb-1">
    <div class="col-md-12">
      <label for="inputString" class="form-label">Generated Regex</label>
      <div class="input-group mb-3">
        <input type="text" class="form-control" :value="regex" readonly>
        <button class="btn btn-danger" type="button" @click="reset()" title="Reset">Reset</button>
      </div>
    </div>
  </div>

  <div class="row g-3 mb-1">
    <div class="col-md-6">
      <transition name="fade">
        <div class="alert alert-danger" v-if="alert.displayed" v-text="alert.message"></div>
      </transition>

      <label for="inputString" class="form-label">Add String</label>
      <div class="input-group mb-3">
        <input id="inputString" type="text" class="form-control" v-model="input" @keyup.enter="pushString()"
               placeholder="Add String" autofocus>
        <button class="btn btn-primary bi-plus" type="button" @click="pushString()"/>
      </div>
    </div>

    <div class="col-md-6">
      <label v-if="strings.length > 0" class="form-label">URL Exclude Strings</label>
      <template v-for="(string, index) in strings" :key="index">
        <div class="input-group mb-1">
          <input type="text" class="form-control" :value="string" readonly>
          <button class="btn btn-danger bi-x" type="button" @click="removeString(index)"/>
        </div>
      </template>
    </div>
  </div>

<!--  <div class="col-md-6">

  </div>-->
  <ImportAddEditCheckTable
      v-model:tableOptions="tableOptions"
      :tableData="urlExclusionPatterns"
      @updateTableData="$emit('update:urlExclusionPatterns', $event)"
  />

</template>

<script>
// import { regexgen } from 'regexgen'
import ImportAddEditCheckTable from "../forms/ImportAddEditCheckTable";

const regexgen = require('regexgen');

export default {
  name: "CollectionCrawlExclusionsConfigurationForm",
  components: {
    ImportAddEditCheckTable
  },
  props: ['urlExclusionPatterns'],
  data() {
    let expressionTypes = [
      {label: 'contains', value: 'contains'},
      {label: 'regex', value: 'regex'}
    ]
    let columns = [
      {label: 'Expression', name: 'expression', class: 'text-center'},
      {label: 'Expression Type', name: 'type', class: 'text-center', type: 'select', options: expressionTypes, default: "regex"},
      {label: 'Ignore Case', name: 'ignoreCase', type: 'checkbox', class: 'text-center'}
    ]
    return {
      input: '',
      strings: [],
      urlStrings: '',
      regex: '',
      alert: {
        displayed: false,
        message: '',
      },
      query: '',
      testUrl: '',
      rows: 5,
      tableOptions: {
        enableImport: true,
        enableAddRow: true,
        enableActions: true,
        columns: columns
      },
      // urlExclusions: []
    }
  },
  watch: {
    strings: {
      deep: true,
      handler: function () {

        if (!this.strings.length) {
          this.regex = '';
          return;
        }
        this.regex = regexgen(this.strings)
      }
    }
  },
  methods: {
    reset() {
      var r = confirm('Are you sure to reset everything?');

      if (r) {
        this.strings = [];
        this.regex = '';
      }
    },
    pushURLString() {
      if (this.testUrl) {
        if (this.urlStrings) {
          this.urlStrings = this.urlStrings + '<br/>';
        }
        this.urlStrings = this.urlStrings + this.testUrl
        // this.urlStrings.push(this.testUrl)
        this.testUrl = '';
        this.rows += 1
      }
    },
    resetURLString() {
      this.urlStrings = ''
    },
    pushString() {
      if (this.input && this.checkDuplicate()) {
        this.strings.push(this.input);
        this.input = '';
      }
    },
    checkDuplicate() {
      var duplicate = this.strings.includes(this.input);
      if (duplicate) {
        return this.showAlert('String already in added string list');
      }
      return true;
    },
    removeString(index) {
      this.strings.splice(index, 1);
    },
    showAlert(message) {
      this.alert = {
        displayed: true,
        message: message,
      }

      setTimeout(() => {
        this.alert.displayed = false;
      }, 3000);
    },
    highlight() {
      if (!this.regex) {
        return this.urlStrings;
      }
      return this.urlStrings.replace(new RegExp(this.regex, "g"), match => {
        //console.log("match: " + match)
        return '<mark class="highlightText">' + match + '</mark>';
      });
    }
  }
}
</script>

<style lang="scss">
#search {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

.highlightText {
  background-color: limegreen;
}
</style>