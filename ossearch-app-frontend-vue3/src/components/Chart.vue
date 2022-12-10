<template>
  <div id="wrapper">
    <div :id="this.config.chart.id" ref="chart"></div>
  </div>
</template>

<script>
import ApexCharts from 'apexcharts';
import _ from 'lodash';

window.ApexCharts = ApexCharts; // return apex chart

export default {
  name: "Chart",
  props: {
    options: {
      type: Object
    },
    type: {
      type: String
    },
    series: {
      type: Array,
      required: true
    },
    width: {
      default: "100%"
    },
    height: {
      default: "auto"
    }
  },
  data() {
    return {
      events: [
        // "animationEnd",
        // "beforeMount",
        // "mounted",
        // "updated",
        // "click",
        // "mouseMove",
        // "mouseLeave",
        "legendClick",
        // "markerClick",
        // "selection",
        "dataPointSelection",
        "dataPointMouseEnter",
        "dataPointMouseLeave",
        // "beforeZoom",
        // "beforeResetZoom",
        // "zoomed",
        // "scrolled",
        // "brushScrolled"
      ]
    }
  },
  mounted() {
    this.init()
  },
  beforeUnmount() {
    if (!this.chart) {
      return;
    }
    this.destroy()
  },
  computed: {
    config() {
      const config = _.cloneDeep(this.options)
      config.chart.events = {}
      // emit events to the parent component
      // to allow for two-way data binding
      this.events.forEach(event => {
        let callback = (...args) => this.$emit(event, ...args); // args => chartContext, options
        config.chart.events[event] = callback;
      });

      config.chart.type = this.type || this.options.chart.type || "line"
      config.chart.height = this.height
      config.chart.width = this.width
      config.series = this.series
      return config
    }
  },
  watch: {
    options: {
      deep: true,
      handler: function () {
        // console.log("watch")
        if (!this.chart && this.options) {
          // console.log("watch init")
          this.init();
        } else {
          // console.log("watch updateOptions")
          this.chart.updateOptions(this.config);
        }
      }
    },
    series: {
      deep: true,
      handler: function () {
        // console.log("watch")
        if (!this.chart && this.series) {
          // console.log("watch init")
          this.init();
        } else {
          // console.log("watch updateSeries")
          this.chart.updateSeries(this.series);
        }
      }
    },
  },
  methods: {
    init() {
      this.$nextTick(() => {
        this.chart = new ApexCharts(this.$refs.chart, this.config)
        this.chart.render();
      })
    },
    destroy() {
      this.chart.destroy();
    },
    emit(event, args) {
      this.$emit(event, args)
    },
    toggleSeries(id, series, event) {
      console.log("id, series, event ->", id, series, event)
      /*const functionName = event.target.value === hideText ? "hideSeries" : "showSeries";
      event.target.value = event.target.value === hideText ? showText : hideText;
      const chartSeries = series ? series : undefined;
      const chartId = id ? id : "";

      chartSeries?.forEach((ser) => {
        ApexCharts.exec(chartId, functionName, ser.name);
      });

      return lineChartItems;*/
    }
  }
}
</script>

<style scoped>

</style>
