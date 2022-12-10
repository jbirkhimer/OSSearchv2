<template>
  <!--  <label for="basic-url" class="form-label">Choose Date Range</label>-->
  <div class="input-group input-group-sm">
    <span class="input-group-text" id="basic-addon1"><i class="fas fa-calendar-alt"></i></span>
    <input ref="datetimepicker" class="text form-control form-control-sm" type="text"/>
    <slot></slot>
  </div>
</template>

<script>
import $ from "jquery";
import moment from "moment";
import 'bootstrap-daterangepicker/daterangepicker';
import 'bootstrap-daterangepicker/daterangepicker.css'

export default {
  name: "DateRangePicker",
  props: ['startDate', 'endDate'],
  data() {
    return {
      start: moment().subtract(30, 'days'),
      end: moment()
    }
  },
  mounted() {
    if (this.startDate) {
      this.start = this.startDate
    }
    if (this.endDate) {
      this.end = this.endDate
    }
    const self = this;
    $(this.$refs.datetimepicker).daterangepicker(
        {
          "autoUpdateInput": true,
          "showDropdowns": true,
          ranges: {
            'All': [moment().subtract(moment().year() -1 , 'years').startOf('year'), moment()],
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
            'Last 30 Days': [moment().subtract(30, 'days'), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
            'Last Year (YTD)': [moment().subtract(1, 'years'), moment()]
          },
          "linkedCalendars": false,
          "alwaysShowCalendars": true,
          "startDate": this.start.format('MM/DD/YYYY'),
          "endDate": this.end.format('MM/DD/YYYY'),
          "maxDate": moment().format('MM/DD/YYYY'),
          "drops": "down"
        },
        /*(start, end, label) => {
          self.start = start
          self.end = end
          console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
        }*/
    );

    $(this.$refs.datetimepicker).on('apply.daterangepicker', (ev, picker) => {
      self.start = picker.startDate
      self.end = picker.endDate
      self.submit(picker)
      // console.log('New date range selected: ' + picker.startDate.format('YYYY-MM-DD') + ' to ' + picker.endDate.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
    });
  },
  methods: {
    submit(picker) {
      // console.log(">>", picker.startDate.format('YYYY-MM-DD'));
      // console.log(">>", picker.endDate.format('YYYY-MM-DD'));
      this.$emit('dateRange', picker)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "bootstrap-daterangepicker";
</style>
