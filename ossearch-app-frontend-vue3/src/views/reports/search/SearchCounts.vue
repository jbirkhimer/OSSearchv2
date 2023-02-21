<template>
  <div class="container-fluid px-4">
<!--    <h1 class="mt-4">Search Reports</h1>-->
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
        <Chart id="mainChart" type="line" height="400" :series="chartSeries" :options="chartOptionsMain"/>
        <Chart id="selectionChart" type="area" height="200" :series="chartSeries" :options="chartOptionsSelection" @legendClick="chartEvent"/>
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
        startDate: moment().subtract(1, 'years').utc().startOf('day').format(),
        endDate: moment().utc().endOf('day').format()
      },
      chartSeries: [{name: 'siarchives', data: []}],
      chartOptionsMain: {
        chart: {
          id: 'mainChart',
          type: 'line',
          height: 400,
          toolbar: {
            autoSelected: 'pan',
            show: false
          },
        },
        legend: {
          show: false
        },
        theme: {
          palette: 'palette1' // upto palette10
        },
        stroke: {
          width: 3
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
          forceNiceScale: true
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
          /*selection: {
            enabled: true,
            xaxis: this.rangeSelection
          },*/
          // events: {
          //   legendClick: function(chartContext, seriesIndex, config) {
          //     let value = {chartContext: chartContext, seriesIndex: seriesIndex, config: config}
          //     this.chartEvent(value)
          //     // console.log("chartContext", chartContext, "seriesIndex", seriesIndex, "config", config)
          //   }
          // }
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
          tickAmount: 3,
          forceNiceScale: true
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
      //const seriesMax = Math.max(...this.chartSeries.map(series => Math.max(...series.data.map(o => o[0]))))

      const xaxis = {
        min: moment(this.endDate).subtract(1, 'months').toDate().getTime(),
        max: moment(this.endDate).toDate().getTime()
      }

      let seriesMax = Math.max(...this.chartSeries[0].data.map(o => o[0]))
      let seriesMin = Math.min(...this.chartSeries[0].data.map(o => o[0]))

      xaxis.max = seriesMax
      xaxis.min = seriesMin

      // console.log("startDate", this.startDate.toISOString(), "endDate", this.endDate.toISOString(), "seriesMax", seriesMax)
      // console.log("diff years", this.endDate.diff(this.startDate, 'years'))
      // console.log("diff months", this.endDate.diff(this.startDate, 'months'))
      // console.log("diff weeks", this.endDate.diff(this.startDate, 'weeks'))
      // console.log("diff days", this.endDate.diff(this.startDate, 'days'))

      if (moment(seriesMax).diff(moment(seriesMin), 'years') > 1) {
        xaxis.min = moment(seriesMax).subtract(1, 'years').toDate().getTime()
        console.log("years", xaxis.min)
      } else if (moment(seriesMax).diff(moment(seriesMin), 'months') > 1) {
        xaxis.min = moment(seriesMax).subtract(3, 'months').toDate().getTime()
        console.log("months", xaxis.min)
      } else if (moment(seriesMax).diff(moment(seriesMin), 'weeks') > 1) {
        xaxis.min = moment(seriesMax).subtract(2, 'weeks').toDate().getTime()
        console.log("weeks", moment.unix(xaxis.min/1000).toISOString())
      } else if (moment(seriesMax).diff(moment(seriesMin), 'days') > 1) {
        xaxis.min = moment(seriesMax).subtract(1, 'days').toDate().getTime()
        console.log("days", xaxis.min)
      }

      //console.log("xaxis", xaxis)
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

        // console.log("msg >>>>>>>>>>>>>>>>>>>", msg.join(","))

        let chartOptionsMain = _.cloneDeep(this.chartOptionsMain)
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
    // tableOptions: {
    //   deep: true,
    //   handler: async function () {
    //     console.log(">>> order", this.tableOptions.order)
    //     this.loading = true
    //     await this.getSearchLogs()
    //     this.loading = false
    //   }
    // },
  },
  methods: {
    async getSearchLogs() {
      let params = this.getParams()
      let chartSeries = []
      let datasets = []
      await SearchLogService.get("/searchlog/search/searchLogChartData", params)
          .then(response => {
            // console.log("searchLogChartData", JSON.stringify(response.data, null, 2))
            Object.keys(response.data).forEach(site => {
              // console.log("searchLogChartData site:", site, "data:", JSON.stringify(response.data[site], null, 2))
              if (['siarchives','sil'].includes(site)) {
                datasets.push({
                  label: site,
                  data: response.data[site],
                })

                let data = []
                response.data[site].forEach(row => {
                  // console.log("row", row)
                  data.push([moment(row.date).toDate().getTime(),
                    row.count])
                })

                let series = {
                  name: site,
                  data: data,
                }
                chartSeries.push(series)
              }
            })
          }).catch(errors => {
            //console.log(errors);
            this.error = errors
          });

      if (chartSeries && chartSeries.length) {
        this.chartSeries = chartSeries
      } else {
        this.chartSeries = [{name: this.name, data: []}]

        let nodata = {
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
      // console.log("updatePicker")
      this.apiParams = {
        startDate: picker.startDate.utc().startOf('day').format(),
        endDate: picker.endDate.utc().endOf('day').format()
      }
      this.startDate = picker.startDate.utc().startOf('day')
      this.endDate = picker.endDate.utc().endOf('day')
    },
    getParams() {
      let params = {
        projection: 'searchLogChart',
        startDate: this.apiParams.startDate,
        endDate: this.apiParams.endDate,
      }

      return params
    },
    chartEvent(value) {
      console.log("chart event", value)
    }
}
}
</script>

<style scoped lang="scss">

</style>
