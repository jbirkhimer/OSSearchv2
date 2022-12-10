<template>
  <fieldset :disabled="!isEditing">
    <div>
      <nav>
        <div class="nav nav-tabs mb-3" id="nav-tab" role="tablist">
          <template v-for="(tab, i) in tabs" :key="i">
            <button @click="setActiveTab(tab.name)" :class="activeTab === tab.name ? 'nav-link active' : 'nav-link'"
                    id="nav-minutes-tab" data-bs-toggle="tab"
                    :data-bs-target="'#nav-'+tab.name.toLowerCase()"
                    type="button" role="tab" aria-controls="nav-minutes" aria-selected="true">{{ tab.name }}
            </button>
          </template>
        </div>
      </nav>

      <div class="tab-content" id="nav-tabContent">

        <div class="tab-pane fade" :class="activeTab === tabs[0].name ? 'show show active' : ''" id="nav-minutes"
             role="tabpanel"
             aria-labelledby="nav-minutes-tab">
          <template v-if="activeTab === tabs[0].name">
            <div class="col-sm-3">
              <div class="input-group input-group-sm mb-3">
                <span class="input-group-text">Every</span>
                <!--            <input type="text" style="text-align: center" class="form-control" :value="editorData.minuteInterval">-->
                <select class="form-select form-select-sm" style="text-align: center; max-width: 75px;"
                        :value="editorData.minuteInterval" @change="updateMinuteInterval" id="minuteInterval">
                  <option v-for="(pos, i) in 59" :key="i" :value="pos">
                    <span>{{ pos }}</span>
                  </option>
                </select>
                <span class="input-group-text">minute(s)</span>
              </div>
            </div>
          </template>
        </div>

        <div class="tab-pane fade" :class="activeTab === tabs[1].name ? 'show active' : ''" id="nav-hourly"
             role="tabpanel"
             aria-labelledby="nav-hourly-tab">
          <template v-if="activeTab === tabs[1].name">
          <div class="col-sm-4">
            <div class="input-group input-group-sm mb-3">
              <span class="input-group-text">Every</span>
              <!--            <input type="text" style="text-align: center" class="form-control" :value="editorData.hourInterval">-->
              <select class="form-select form-select-sm" style="text-align: center; max-width: 75px;"
                      :value="editorData.hourInterval" @change="updateHourInterval" id="hourInterval">
                <option v-for="(pos, i) in 23" :key="i" :value="pos">
                  <span>{{ pos }}</span>
                </option>
              </select>
              <span class="input-group-text">hour(s) on minute</span>
              <!--            <input type="text" style="text-align: center" class="form-control" :value="editorData.minuteInterval">-->
              <select class="form-select form-select-sm" style="text-align: center; max-width: 75px;"
                      :value="editorData.minute" @change="updateMinute" id="minutes">
                <option v-for="(pos, i) in 60" :key="i" :value="i">
                  <span>{{ i }}</span>
                </option>
              </select>
            </div>
          </div>
          </template>
        </div>

        <div class="tab-pane fade" :class="activeTab === tabs[2].name ? 'show active' : ''" id="nav-daily" role="tabpanel"
             aria-labelledby="nav-daily-tab">
          <template v-if="activeTab === tabs[2].name">
          <div class="col-sm-4">
            <div class="input-group input-group-sm mb-3">
              <div class="input-group-text">
                <input class="form-check-input mt-0" type="radio" :value="1" :checked="editorData.dayOption === 1"
                       @change="updateDayOption">
              </div>
              <span class="input-group-text">Every</span>
              <!--            <input type="text" style="text-align: center" class="form-control" :value="editorData.dayInterval">-->
              <select class="form-select form-select-sm" style="text-align: center; max-width: 75px;"
                      :value="editorData.dayInterval" @change="updateDayInterval" id="dayInterval">
                <option v-for="(pos, i) in 31" :key="i" :value="pos">
                  <span>{{ pos }}</span>
                </option>
              </select>
              <span class="input-group-text">day(s) at</span>
              <input type="time" style="text-align: center" class="form-control" :value="editorData.dateTime"
                     @input="updateDateTime">
            </div>
          </div>
          <div class="col-sm-5">
            <div class="input-group input-group-sm mb-3">
              <div class="input-group-text">
                <input class="form-check-input mt-0" type="radio" :value="2" :checked="editorData.dayOption === 2"
                       @change="updateDayOption">
              </div>
              <span class="input-group-text">Every week day (Monday-Friday) at</span>
              <input type="time" style="text-align: center; max-width: 110px" class="form-control"
                     :value="editorData.dateTime" @input="updateDateTime">
            </div>
          </div>
          </template>
        </div>

        <div class="tab-pane fade" :class="activeTab === tabs[3].name ? 'show active' : ''" id="nav-weekly"
             role="tabpanel"
             aria-labelledby="nav-weekly-tab">
          <template v-if="activeTab === tabs[3].name">
          <div class="input-group input-group-sm mb-3">
            <span class="input-group-text">Every</span>
            <div class="input-group-text">
              <template v-for="(day, i) in dayOfWeek" :key="i">
                <input class="form-check-input mt-0" type="checkbox" :id="'dayOfWeekInlineCheckbox'+day.value"
                       :checked="editorData.daysOfWeek.includes(day.value)" @change="updateDaysOfWeek(day.value, $event.target.checked)">
                <label class="form-check-label me-sm-2" :for="'dayOfWeekInlineCheckbox'+i">{{ day.label }}</label>
              </template>
            </div>
            <span class="input-group-text">at</span>
            <input type="time" style="text-align: center;" class="form-control-sm" :value="editorData.dateTime"
                   @input="updateDateTime">
          </div>
          </template>
        </div>

        <div class="tab-pane fade" :class="activeTab === tabs[4].name ? 'show active' : ''" id="nav-monthly"
             role="tabpanel"
             aria-labelledby="nav-monthly-tab">
          <template v-if="activeTab === tabs[4].name">
          <div class="col-sm-7">
            <div class="input-group input-group-sm mb-3">
              <div class="input-group-text">
                <input class="form-check-input mt-0" type="radio"
                       :value="1" :checked="editorData.monthOption == 1" @change="updateMonthOption">
              </div>
              <span class="input-group-text">On the</span>
              <select class="form-select form-select-sm" style="max-width: 150px" :value="editorData.dayOfMonth"
                      @change="updateDayOfMonth"
                      id="dayOfMonth">
                <option value="1W">First Weekday</option>
                <option v-for="(pos, i) in 31" :key="i" :value="pos">
                  <span v-if="[1,21, 31].includes(pos)">{{ pos + 'st Day' }}</span>
                  <span v-else-if="[2,22].includes(pos)">{{ pos + 'nd Day' }}</span>
                  <span v-else-if="[3,23].includes(pos)">{{ pos + 'rd Day' }}</span>
                  <span v-else>{{ pos + 'th Day' }}</span>
                </option>
                <option value="LW">Last Weekday</option>
                <option value="L">Last Day</option>
              </select>
              <span class="input-group-text">of every</span>
              <!--            <input type="text" style="text-align: center" class="form-control" :value="editorData.monthInterval">-->
              <select class="form-select form-select-sm" style="max-width: 75px" :value="editorData.monthInterval"
                      @change="updateMonthInterval"
                      id="monthInterval">
                <option v-for="(pos, i) in 12" :key="i" :value="pos">
                  <span>{{ pos }}</span>
                </option>
              </select>
              <span class="input-group-text">month(s), at</span>
              <input type="time" style="max-width: 130px; text-align: center" class="form-control"
                     :value="editorData.dateTime" @input="updateDateTime">
            </div>
          </div>
          <div class="col-sm-8">
            <div class="input-group input-group-sm mb-3">
              <div class="input-group-text">
                <input class="form-check-input mt-0" type="radio"
                       :value="2" :checked="editorData.monthOption == 2" @change="updateMonthOption">
              </div>
              <span class="input-group-text">On the</span>
              <select class="form-select form-select-sm" style="max-width: 105px" :value="editorData.dayPos"
                      @change="updateDayPos" id="dayPos">
                <option v-for="(pos, i) in dayPositions" :key="i" :value="pos.value">
                  {{ pos.label }}
                </option>
              </select>
              <select class="form-select form-select-sm" style="max-width: 130px" :value="editorData.dayOfWeek"
                      @change="updateDayOfWeek"
                      id="dayOfWeek">
                <option v-for="(day, i) in dayOfWeek" :key="i" :value="day.label">
                  {{ days[i] }}
                </option>
              </select>
              <span class="input-group-text">of every</span>
              <!--            <input type="text" style="text-align: center" class="form-control" :value="editorData.monthInterval">-->
              <select class="form-select form-select-sm" style="max-width: 75px" :value="editorData.monthInterval"
                      @change="updateMonthInterval"
                      id="monthInterval">
                <option v-for="(pos, i) in 12" :key="i" :value="pos">
                  <span>{{ pos }}</span>
                </option>
              </select>
              <span class="input-group-text">month(s), at</span>
              <input type="time" style="max-width: 130px; text-align: center;" class="form-control"
                     :value="editorData.dateTime" @input="updateDateTime">
            </div>
          </div>
          </template>
        </div>

        <div class="tab-pane fade" :class="activeTab === tabs[5].name ? 'show active' : ''" id="nav-yearly"
             role="tabpanel"
             aria-labelledby="nav-yearly-tab">
          <template v-if="activeTab === tabs[5].name">
          <div class="col-sm-6">
            <div class="input-group input-group-sm mb-3">
              <div class="input-group-text">
                <input class="form-check-input mt-0" type="radio"
                       :value="1" :checked="editorData.yearOption === 1" @change="updateYearOption"/>
              </div>
              <span class="input-group-text">Every</span>
              <select class="form-select" style="max-width: 130px" :value="editorData.month" @change="updateMonth"
                      id="month">
                <option v-for="(month, i) in months" :key="i" :value="month.value">
                  {{ month.label }}
                </option>
              </select>
              <span class="input-group-text">on the</span>
              <select class="form-select" style="max-width: 150px" :value="editorData.dayOfMonth"
                      @change="updateDayOfMonth" id="dayOfMonth">
                <option value="1W">First Weekday</option>
                <option v-for="(pos, i) in 31" :key="i" :value="pos">
                  <span v-if="[1,21, 31].includes(pos)">{{ pos + 'st day' }}</span>
                  <span v-else-if="[2,22].includes(pos)">{{ pos + 'nd day' }}</span>
                  <span v-else-if="[3,23].includes(pos)">{{ pos + 'rd day' }}</span>
                  <span v-else>{{ pos + 'th day' }}</span>
                </option>
                <option value="LW">Last Weekday</option>
                <option value="L">Last Day</option>
              </select>
              <span class="input-group-text">at</span>
              <input type="time" style="text-align: center" class="form-control" :value="editorData.dateTime"
                     @input="updateDateTime">
            </div>
          </div>
          <div class="col-sm-7">
            <div class="input-group input-group-sm mb-3">
              <div class="input-group-text">
                <input class="form-check-input mt-0" type="radio"
                       :value="2" :checked="editorData.yearOption === 2" @change="updateYearOption"/>
              </div>
              <span class="input-group-text">On the</span>
              <select class="form-select" style="max-width: 105px" :value="editorData.dayPos" @change="updateDayPos"
                      id="dayPos">
                <option v-for="(pos, i) in dayPositions" :key="i" :value="pos.value">
                  {{ pos.label }}
                </option>
              </select>
              <select class="form-select" style="max-width: 130px" :value="editorData.dayOfWeek" @change="updateDayOfWeek"
                      id="dayOfWeek">
                <option v-for="(day, i) in dayOfWeek" :key="i" :value="day.label">
                  {{ days[i] }}
                </option>
              </select>
              <span class="input-group-text">of</span>
              <select class="form-select" style="max-width: 130px" :value="editorData.month" @change="updateMonth"
                      id="month">
                <option v-for="(month, i) in months" :key="i" :value="month.value">
                  {{ month.label }}
                </option>
              </select>
              <span class="input-group-text">at</span>
              <input type="time" style="text-align: center; max-width: 130px;" class="form-control"
                     :value="editorData.dateTime" @input="updateDateTime">
            </div>
          </div>
          </template>
        </div>

        <div class="tab-pane fade" :class="activeTab === tabs[6].name ? 'show active' : ''" id="nav-advanced"
             role="tabpanel"
             aria-labelledby="nav-advanced-tab">
          <template v-if="activeTab === tabs[6].name">
          <div class="col-sm-6">
            <label class="form-check-label me-sm-2">Cron Expression</label>
            <input type="text" style="text-align: center" class="form-control" :value="editorData.cronExpression"
                   @input="updateCronExpression">
          </div>
          <span>More details about how to create these expressions can be found <a
              href="http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html#format"
              target="_blank">here</a>.</span>
          </template>
        </div>

      </div>
      <br/>
      <table class="table table-sm table-bordered border-secondary">
        <thead class="table-primary">
        <tr class="text-center">
          <td>Second</td>
          <td>Minute</td>
          <td>Hour</td>
          <td>Day of month</td>
          <td>Month</td>
          <td>Day of week</td>
          <td>Year</td>
        </tr>
        </thead>
        <tbody>
        <tr class="text-center">
          <template v-for="(col, i) in cronExpression.split(' ')" :key="i">
            <td>{{ col }}</td>
          </template>
        </tr>
        </tbody>
      </table>
      <div class="row-cols-sm-2">
        <div class="col-sm-auto">
          Generated Cron Expression:
          <span class="text-success"><b>{{ cronExpression }}</b></span>
        </div>
        <div class="col-sm-auto">
          Describe Expression:
          <span class="text-success"><b>{{ explain }}</b></span>
        </div>
        <div class="col-sm-auto">Is Valid:
          <!--        <span v-if="!isValid && errorMessage" class="text-danger" style="text-transform: capitalize"><b></b></span>-->
          <span v-if="isValid === true" class="text-success"
                style="text-transform: capitalize"><b>{{ isValid }}</b></span>
          <span v-else class="text-danger" style="text-transform: capitalize"><b>{{ isValid.isValid }}</b></span>
          <pre v-if="isValid.errorMessage" class="text-danger">{{ print(isValid.errorMessage) }}</pre>
        </div>
      </div>
    </div>
  </fieldset>
</template>

<script>
import cronstrue from 'cronstrue';
import cronValidator from 'cron-expression-validator';
// import * as cronValidator from 'cron-validator';

export default {
  name: "CollectionCrawlScheduleForm",
  props: ['isEditing', 'crawlScheduleCron', 'cronEditorData', 'activeTab', 'editorData'],
  data() {
    return {
      isValid: null,
      // activeTab: 'Weekly',
      // editorData: {dateTime: '00:00', daysOfWeek: [6]},
      tabs: [
        {name: 'Minutes', editorData: {minuteInterval: 1}},
        {name: 'Hourly', editorData: {minute: 0, hourInterval: 1}},
        {name: 'Daily', editorData: {dateTime: '00:00', dayOption: 1, dayInterval: 1}},
        {name: 'Weekly', editorData: {dateTime: '00:00', daysOfWeek: [6]}},
        {name: 'Monthly', editorData: {dateTime: '00:00', dayPos: '#1', daysOfWeek: [6], dayOfWeek: 'Sat', monthOption: 1, dayOfMonth: 1, monthInterval: 1}},
        {name: 'Yearly', editorData: {dateTime: '00:00', dayPos: '#1', dayOfWeek: 'Sat', month: 1, dayOfMonth: 1, yearOption: 1,}},
        {name: 'Advanced', editorData: {cronExpression: '* * * ? * * *'}}
      ],
      frequency: [
        {label: 'Hourly', value: 'hourly'},
        {label: 'Daily', value: 'daily'},
        {label: 'Weekly', value: 'weekly'},
        {label: 'Monthly', value: 'monthly'},
        {label: 'Yearly', value: 'yearly'},
        {label: 'Custom', value: 'custom'}
      ],
      dayPositions: [
        {label: 'First', value: '#1'},
        {label: 'Second', value: '#2'},
        {label: 'Third', value: '#3'},
        {label: 'Fourth', value: '#4'},
        {label: 'Fifth', value: '#5'},
        {label: 'Last', value: 'L'}
      ],
      days: [
        'Sunday',
        'Monday',
        'Tuesday',
        'Wednesday',
        'Thursday',
        'Friday',
        'Saturday'
      ],
      dayOfWeek: [
        {label: 'Sun', value: 1},
        {label: 'Mon', value: 2},
        {label: 'Tue', value: 3},
        {label: 'Wed', value: 4},
        {label: 'Thu', value: 5},
        {label: 'Fri', value: 6},
        {label: 'Sat', value: 7}
      ],
      months: [
        {label: 'January', value: '1'},
        {label: 'February', value: '2'},
        {label: 'March', value: '3'},
        {label: 'April', value: '4'},
        {label: 'May', value: '5'},
        {label: 'June', value: '6'},
        {label: 'July', value: '7'},
        {label: 'August', value: '8'},
        {label: 'September', value: '9'},
        {label: 'October', value: '10'},
        {label: 'November', value: '11'},
        {label: 'December', value: '12'}
      ]
    }
  },
  computed: {
    explain() {
      return cronstrue.toString(this.cronExpression, {
        verbose: true,
        use24HourTimeFormat: true,
        dayOfWeekStartIndexZero: false
      })
    },
    cronExpression() {
      let cronExpression = ''
      if (this.activeTab == 'Minutes') {

        cronExpression = '0 0/' + this.editorData.minuteInterval + ' * ? * * *'
        // console.log("computed cronExpression minutes: ", cronExpression)

      } else if (this.activeTab == 'Hourly') {

        cronExpression = '0 ' + this.editorData.minute + ' 0/' + this.editorData.hourInterval + ' 1/1 * ? *'
        // console.log("computed cronExpression hourly: ", cronExpression)

      } else if (this.activeTab == 'Daily') {

        let time = this.editorData.dateTime.split(":")
        let hours = parseInt(time[0], 10)
        let minutes = parseInt(time[1], 10)

        if (this.editorData.dayOption == 1) {
          cronExpression = '0 ' + minutes + ' ' + hours + ' 1/' + this.editorData.dayInterval + ' * ? *'
        } else if (this.editorData.dayOption == 2) {
          cronExpression = '0 ' + minutes + ' ' + hours + ' ? * MON-FRI *'
        }

        // console.log("computed cronExpression daily: ", cronExpression)

      } else if (this.activeTab == 'Weekly') {

        let time = this.editorData.dateTime.split(":")
        let hours = parseInt(time[0], 10)
        let minutes = parseInt(time[1], 10)

        let sortedDaysOfWeek = this.editorData.daysOfWeek
        sortedDaysOfWeek.sort()
        let daysOfWeek = []

        sortedDaysOfWeek.forEach((day) => {
          // console.log(day)
          let obj = this.dayOfWeek.find(o => o.value === day);
          daysOfWeek.push(obj.label.toUpperCase())
        })

        let daysOfWeekString = daysOfWeek.join(',')

        cronExpression = '0 ' + minutes + ' ' + hours + ' ? * ' + daysOfWeekString + ' *'

        // console.log("computed cronExpression weekly: ", cronExpression)

      } else if (this.activeTab == 'Monthly') {

        let time = this.editorData.dateTime.split(":")
        let hours = parseInt(time[0], 10)
        let minutes = parseInt(time[1], 10)

        if (this.editorData.monthOption == 1) {
          cronExpression = '0 ' + minutes + ' ' + hours + ' ' + this.editorData.dayOfMonth + ' 1/' + this.editorData.monthInterval + ' ? *'
        } else if (this.editorData.monthOption == 2) {
          let dayNum = this.dayOfWeek.find(o => o.label === this.editorData.dayOfWeek);
          cronExpression = '0 ' + minutes + ' ' + hours + ' ? ' + '1/' + this.editorData.monthInterval + ' ' + dayNum.value + this.editorData.dayPos + ' *'
        }
        // console.log("computed cronExpression monthly: ", cronExpression)

      } else if (this.activeTab == 'Yearly') {

        let time = this.editorData.dateTime.split(":")
        let hours = parseInt(time[0], 10)
        let minutes = parseInt(time[1], 10)

        if (this.editorData.yearOption == 1) {
          cronExpression = '0 ' + minutes + ' ' + hours + ' ' + this.editorData.dayOfMonth + ' ' + this.editorData.month + ' ? *'
        } else if (this.editorData.yearOption == 2) {
          let dayNum = this.dayOfWeek.find(o => o.label === this.editorData.dayOfWeek);
          cronExpression = '0 ' + minutes + ' ' + hours + ' ? ' + this.editorData.month + ' ' + dayNum.value + this.editorData.dayPos + ' *'
        }
        // console.log("computed cronExpression yearly: ", cronExpression)

      } else if (this.activeTab == 'Advanced') {
        cronExpression = this.editorData.cronExpression
        // console.log("computed cronExpression advanced: ", cronExpression)
      }

      this.validate(cronExpression)
      //console.log("computed cronExpression: ", cronExpression, 'isValid:', this.isValid)
      this.$emit('update:crawlScheduleCron', cronExpression)
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: this.editorData}))
      return cronExpression
    },
  },
  watch: {
  //   cronEditorData: {
  //     deep: true,
  //     handler() {
  //       let cronEditorData = JSON.parse(this.cronEditorData)
  //       this.editorData = cronEditorData.editorData
  //       this.activeTab = cronEditorData.name
  //     }
  //   }
    isValid: {
      deep: true,
      handler() {
        this.$emit('isValid', this.isValid)
      }
    }
  },
  methods: {
    validate(expr) {
      this.isValid = cronValidator.isValidCronExpression(expr, {error: true})
      if (this.isValid.errorMessage && this.isValid.errorMessage instanceof Array) {
        this.isValid.errorMessage = [...new Set(this.isValid.errorMessage)]
      }
    },
    setActiveTab(activeTab) {
      this.isValid = null
      let tab = this.tabs.find(o => o.name === activeTab)
      this.$emit('updateCronEditorData', JSON.stringify(tab))
    },
    print(value) {
      return JSON.stringify(value, null, 2)
    },
    updateMinuteInterval(event) {
      let editorData = this.editorData
      editorData.minuteInterval = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateHourInterval(event) {
      let editorData = this.editorData
      editorData.hourInterval = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateMinute(event) {
      let editorData = this.editorData
      editorData.minute = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateDayInterval(event) {
      let editorData = this.editorData
      editorData.dayInterval = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateDayOfMonth(event) {
      let editorData = this.editorData
      editorData.dayOfMonth = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateMonthInterval(event) {
      let editorData = this.editorData
      editorData.monthInterval = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateDayPos(event) {
      let editorData = this.editorData
      editorData.dayPos = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateDayOfWeek(event) {
      let editorData = this.editorData
      editorData.dayOfWeek = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateMonth(event) {
      let editorData = this.editorData
      editorData.month = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateDateTime(event) {
      let editorData = this.editorData
      editorData.dateTime = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateDayOption(event) {
      let editorData = this.editorData
      editorData.dayOption = parseInt(event.target.value)
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateMonthOption(event) {
      let editorData = this.editorData
      editorData.monthOption = parseInt(event.target.value)
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateYearOption(event) {
      let editorData = this.editorData
      editorData.yearOption = parseInt(event.target.value)
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateCronExpression(event) {
      let editorData = this.editorData
      editorData.cronExpression = event.target.value
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
    updateDaysOfWeek(day, event) {
      let editorData = this.editorData
      //console.log("updateDaysOfWeek event", event, "day", day)
      if (event) {
        editorData.daysOfWeek.push(day)
      } else {
        const index = editorData.daysOfWeek.indexOf(day);
        if (index > -1) {
          editorData.daysOfWeek.splice(index, 1); // 2nd parameter means remove one item only
        }
      }
      this.$emit('updateCronEditorData', JSON.stringify({name: this.activeTab, editorData: editorData}))
    },
  }
}
</script>

<style scoped>

</style>