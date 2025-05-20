<template>
  <div class="container-fluid px-4">
    <Breadcrumb/>

    <div class="card mb-4">
      <div class="card-header">
        <i class="fas fa-sitemap me-1"></i>
        <b>Search Report</b>
        <div class="float-end">
          <DateRangePicker :startDate="startDate" :endDate="endDate" @dateRange="updatePicker"/>
        </div>
      </div>

      <div class="card-body">
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
        <Chart id="mainChart" type="line" height="400" :series="chartSeries" :options="chartOptionsMain"
               @selection="handleMainChartSelection" @dataPointSelection="handleDataPointSelection"/>
        <Chart id="selectionChart" type="area" height="200" :series="chartSeries" :options="chartOptionsSelection"
               @legendClick="handleLegendClick" @selection="handleSelectionChartSelection"/>
      </div>
    </div>
  </div>
</template>

<script>
import moment from "moment/moment";
import SearchLogService from "../../../services/searchLog.service";
import Breadcrumb from "../../../components/Breadcrumb";
import DateRangePicker from "../../../components/DateRangePicker";
import Chart from "../../../components/Chart";
import _ from 'lodash';

export default {
  name: "SearchCounts",
  components: {
    Breadcrumb,
    DateRangePicker,
    Chart
  },
  data() {
    return {
      loading: false,
      error: null,
      startDate: moment().subtract(1, 'years').utc().startOf('day'),
      // startDate: moment('2019-03-01').utc().startOf('day'),
      endDate: moment().utc().endOf('day'),
      picker: {},
      apiParams: {
        // startDate: moment('2019-03-01').utc().startOf('day').format(),
        startDate: moment().subtract(1, 'years').utc().startOf('day').format('YYYY-MM-DD'),
        endDate: moment().utc().endOf('day').format('YYYY-MM-DD')
      },
      chartSeries: [],
      hiddenSeries: new Set(), // Track which series are hidden
      chartOptionsMain: {
        chart: {
          id: 'mainChart',
          type: 'line',
          height: 450,
          toolbar: {
            autoSelected: 'zoom',
            show: true,
            tools: {
              download: true,
              selection: true,
              zoom: true,
              zoomin: true,
              zoomout: true,
              pan: true,
              reset: true
            }
          },
          zoom: {
            enabled: true,
            type: 'x'
          },
          selection: {
            enabled: true,
            type: 'x'
          }
        },
        legend: {
          show: false // Hide legend in main chart
        },
        theme: {
          palette: 'palette1' // upto palette10
        },
        stroke: {
          width: 3,
          curve: 'smooth'
        },
        dataLabels: {
          enabled: false
        },
        fill: {
          opacity: 1,
        },
        markers: {
          size: 0
        },
        xaxis: {
          type: 'datetime'
        },
        yaxis: {
          logarithmic: true,
          forceNiceScale: true,
          labels: {
            formatter: function(val) {
              if (val === 0) return 0;
              if (val < 10) return val.toFixed(1);
              return val.toFixed(0);
            }
          },
          title: {
            text: 'Search Count (log scale)'
          }
        },
        tooltip: {
          shared: true,
          y: {
            formatter: function(val) {
              return val.toLocaleString();
            }
          }
        },
        noData: {
          text: 'Loading...'
        }
      },
      chartOptionsSelection: {
        chart: {
          id: 'selectionChart',
          type: 'area',
          height: 200,
          brush: {
            target: 'mainChart',
            enabled: true,
            autoScaleYaxis: true
          },
          selection: {
            enabled: true,
            type: 'x'
          }
        },
        legend: {
          show: true,
          position: 'bottom',
          horizontalAlign: 'center',
          onItemClick: {
            toggleDataSeries: false // Disable built-in toggle
          }
        },
        theme: {
          palette: 'palette1' // upto palette10
        },
        fill: {
          type: 'gradient',
          gradient: {
            opacityFrom: 0.91,
            opacityTo: 0.1,
          }
        },
        xaxis: {
          type: 'datetime',
          tooltip: {
            enabled: false
          }
        },
        yaxis: {
          logarithmic: true,
          forceNiceScale: true,
          tickAmount: 3,
          labels: {
            formatter: function(val) {
              if (val === 0) return 0;
              if (val < 1000) return val;
              if (val < 1000000) return (val/1000).toFixed(1) + 'K';
              return (val/1000000).toFixed(1) + 'M';
            }
          }
        },
        tooltip: {
          shared: true,
          y: {
            formatter: function(val) {
              return val.toLocaleString();
            }
          }
        },
        noData: {
          text: 'Loading...'
        }
      },
    }
  },
  async mounted() {
    this.loading = true
    await this.getSearchLogs()
    this.loading = false
  },
  computed: {
    rangeSelection() {
      if (!this.chartSeries.length || !this.chartSeries[0].data.length) {
        return {
          min: moment(this.endDate).subtract(1, 'months').toDate().getTime(),
          max: moment(this.endDate).toDate().getTime()
        }
      }

      // Find the min and max dates across all series
      let allDates = this.chartSeries.flatMap(series => series.data.map(point => point[0]))
      let seriesMax = Math.max(...allDates)
      let seriesMin = Math.min(...allDates)

      let xaxis = {
        max: seriesMax,
        min: seriesMin
      }

      // Adjust min based on the date range
      if (moment(seriesMax).diff(moment(seriesMin), 'years') > 1) {
        xaxis.min = moment(seriesMax).subtract(1, 'years').toDate().getTime()
      } else if (moment(seriesMax).diff(moment(seriesMin), 'months') > 1) {
        xaxis.min = moment(seriesMax).subtract(3, 'months').toDate().getTime()
      } else if (moment(seriesMax).diff(moment(seriesMin), 'weeks') > 1) {
        xaxis.min = moment(seriesMax).subtract(2, 'weeks').toDate().getTime()
      } else if (moment(seriesMax).diff(moment(seriesMin), 'days') > 1) {
        xaxis.min = moment(seriesMax).subtract(1, 'days').toDate().getTime()
      }

      return xaxis
    }
  },
  watch: {
    apiParams: {
      handler: async function () {
        this.loading = true
        await this.getSearchLogs()
        this.loading = false
      }
    },
    chartSeries: {
      handler: async function () {
        let chartOptionsSelection = _.cloneDeep(this.chartOptionsSelection)
        chartOptionsSelection.chart.selection = {
          enabled: true,
          xaxis: this.rangeSelection
        }
        this.chartOptionsSelection = chartOptionsSelection

        let chartOptionsMain = _.cloneDeep(this.chartOptionsMain)
        chartOptionsMain.chart.selection = {
          enabled: true,
          type: 'x'
        }

        let duration = moment.duration(this.endDate.diff(this.startDate))
        let msg = []
        if (duration.years()) {
          msg.push(duration.years() + " years")
        }
        if (duration.months()) {
          msg.push(duration.months() + " months")
        }
        if (duration.days()) {
          msg.push(duration.days() + " days")
        }

        chartOptionsMain.title = {
          text: 'Daily Search Counts',
          align: 'center'
        }
        chartOptionsMain.subtitle = {
          text: this.startDate.toDate().toLocaleDateString() +' - '+ this.endDate.toDate().toLocaleDateString() + ' (~'+msg.join(", ")+')',
          align: 'center'
        }
        this.chartOptionsMain = chartOptionsMain
      }
    }
  },
  methods: {
    async getSearchLogs() {
      let params = this.getParams()
      let chartSeries = []

      try {
        const response = await SearchLogService.get("/searchlogsummary/search/findByDateRange", params)

        if (!response.data || !response.data._embedded || !response.data._embedded.searchlogsummary) {
          console.error("Unexpected response format:", response.data)
          this.error = "Unexpected API response format"
          return
        }

        // Group by site
        const summariesBySite = _.groupBy(response.data._embedded.searchlogsummary, 'site')

        // Create series for each site
        Object.entries(summariesBySite).forEach(([site, entries]) => {
          if (!site) {
            return // Skip entries with no site
          }

          // Sort entries by date
          const sortedEntries = _.sortBy(entries, 'logDate')

          // Transform data for chart
          const data = sortedEntries.map(entry => [
            moment(entry.logDate).toDate().getTime(),
            entry.searchCount || 0.1 // Ensure minimum value for log scale
          ])

          chartSeries.push({
            name: site,
            data: data
          })
        })

      } catch (error) {
        console.error("Error fetching search logs:", error)
        this.error = error
      }

      // Update chart series
      if (chartSeries.length) {
        this.chartSeries = chartSeries
        this.hiddenSeries = new Set() // Reset hidden series
      } else {
        this.chartSeries = []

        const nodata = {
          text: 'No Data'
        }

        let chartOptionsSelection = _.cloneDeep(this.chartOptionsSelection)
        chartOptionsSelection.noData = nodata
        this.chartOptionsSelection = chartOptionsSelection

        let chartOptionsMain = _.cloneDeep(this.chartOptionsMain)
        chartOptionsMain.noData = nodata
        this.chartOptionsMain = chartOptionsMain
      }
    },
    updatePicker(picker) {
      this.apiParams = {
        startDate: picker.startDate.utc().startOf('day').format('YYYY-MM-DD'),
        endDate: picker.endDate.utc().endOf('day').format('YYYY-MM-DD')
      }
      this.startDate = picker.startDate.utc().startOf('day')
      this.endDate = picker.endDate.utc().endOf('day')
    },
    getParams() {
      return {
        startDate: this.apiParams.startDate,
        endDate: this.apiParams.endDate,
      }
    },
    // Handle legend click in selection chart
    handleLegendClick(_chartContext, seriesIndex) {
      // Get the series name
      const seriesName = this.chartSeries[seriesIndex].name;

      // Toggle visibility state in our tracking set
      if (this.hiddenSeries.has(seriesName)) {
        this.hiddenSeries.delete(seriesName);

        // Show the series in both charts
        window.ApexCharts.exec('mainChart', 'showSeries', seriesName);
        window.ApexCharts.exec('selectionChart', 'showSeries', seriesName);
      } else {
        this.hiddenSeries.add(seriesName);

        // Hide the series in both charts
        window.ApexCharts.exec('mainChart', 'hideSeries', seriesName);
        window.ApexCharts.exec('selectionChart', 'hideSeries', seriesName);
      }
    },
    // Handle selection on the main chart
    handleMainChartSelection(_chartContext, { xaxis }) {
      if (xaxis && xaxis.min && xaxis.max) {
        // Update selection chart to match
        window.ApexCharts.exec('selectionChart', 'updateOptions', {
          chart: {
            selection: {
              xaxis: {
                min: xaxis.min,
                max: xaxis.max
              }
            }
          }
        });
      }
    },
    // Handle selection on the selection chart
    handleSelectionChartSelection() {
      // Already handled by brush feature
    },
    // Handle data point clicks
    handleDataPointSelection(_event, _chartContext, config) {
      console.log("Data point selected:", config.dataPointIndex, config.seriesIndex);
    }
  }
}
</script>

<style scoped lang="scss">

</style>
