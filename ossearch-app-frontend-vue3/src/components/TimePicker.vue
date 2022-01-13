<template>
    <div class="time-picker-tooltip">
        <input class="time-picker-input-tooltip" :class="inputClass" :value="hours" readonly="true"/>
        <div class="time-picker-tooltiptext" @focusout="validateRange">
            <template v-if="range">
                <b>From:</b>
                <input type="number" v-model="from.hour">:
                <input type="number" v-model="from.minutes">
                <input type="button" v-if="!time_24hr" v-model="from.meridian" @click="meridian('from')">
                <b>To:</b>

                <input type="number" v-model="to.hour">:
                <input type="number" v-model="to.minutes">
                <input type="button" v-if="!time_24hr" v-model="to.meridian" @click="meridian('to')">
            </template>
            <template v-else>
                <input type="number" v-model="from.hour">:
                <input type="number" v-model="from.minutes">
                <input type="button" v-if="!time_24hr" v-model="from.meridian" @click="meridian('from')">
            </template>
        </div>
        <input type="button" class="time-picker-clear" value="x" @click="clearInput" v-if="clear">
    </div>
</template>

<script>
    // import trans from './Translations';

    export default {
        name: "TimePicker",
        created() {
            if (this.value) {
                let range = this.meridianFormat(this.value);
                this.from = range.from;
                this.to = range.to;
                this.validateRange();
            } else
                this.clearInput();

        },
        data() {
            return {
                // trans: trans,
                cleared: false,
                from: {
                    hour: "00",
                    minutes: "00",
                    meridian: "AM"
                },
                to: {
                    hour: "23",
                    minutes: "59",
                    meridian: "PM"
                },
            }
        },
        watch: {
            value: {
                deep: true,
                handler() {
                    if (this.value) {
                        let range = this.meridianFormat(this.value);
                        this.from = range.from;
                        this.to = range.to;
                        this.validateRange();
                    } else
                        this.clearInput();
                }
            }, from: {
                deep: true,
                handler() {
                    this.formatHour('from')
                }
            },
            to: {
                deep: true,
                handler() {
                    this.formatHour('to')
                }
            }
        },
        methods: {
            meridianFormat(range) {
                try {
                    let hours = range.trim().split(' - ');
                    let from = {
                        hour: hours[0].split(':')[0],
                        minutes: hours[0].split(':')[1].split(' ')[0],
                        meridian: hours[0].split(':')[1].split(' ')[1]
                    };
                    if (this.range)
                        return {
                            from: from
                            ,
                            to: {
                                hour: hours[1].split(':')[0],
                                minutes: hours[1].split(':')[1].split(' ')[0],
                                meridian: hours[1].split(':')[1].split(' ')[1]
                            }
                        };
                    else
                        return {
                            from: from,
                            to: from
                        };
                } catch (e) {
                    return this.meridianFormat("00:00 AM - 23:59 PM");
                }
            },
            meridian(at) {
                if (this[at].meridian == "AM")
                    this[at].meridian = "PM";
                else
                    this[at].meridian = "AM"
            },
            formatHour(at) {
                if (this[at].hour >= 13 && this.time_24hr == false)
                    this[at].hour = 1;
                else if (this[at].hour >= 24 && this.time_24hr == true)
                    this[at].hour = 0;

                if (this[at].hour <= 0 && this.time_24hr == false)
                    this[at].hour = 12;
                else if (this[at].hour <= -1 && this.time_24hr == true)
                    this[at].hour = 23;

                if (this[at].minutes >= 60) {
                    this[at].minutes = 0;
                    this[at].hour++;
                }

                if (this[at].minutes <= -1) {
                    this[at].minutes = 59;
                    this[at].hour--;
                }

                this[at].hour = ("0" + this[at].hour).slice(-2);
                this[at].minutes = ("0" + this[at].minutes).slice(-2);
            },
            validateRange() {
                this.cleared = false;
                if (!(this.range && this.continuous && !this.time_24hr)) {
                    this.$emit('input', this.hours);
                    return;
                }
                let from, to;
                if (!this.time_24hr) {
                    from = ((Number.parseInt(this.from.hour === "12" ? 0 : this.from.hour) + (this.from.meridian == "PM" ? 12 : 0)) * 60) + Number.parseInt(this.from.minutes);
                    to = ((Number.parseInt(this.to.hour === "12" ? 0 : this.to.hour) + (this.to.meridian == "PM" ? 12 : 0)) * 60) + Number.parseInt(this.to.minutes);
                } else {
                    from = ((Number.parseInt(this.from.hour === "00" ? 0 : this.from.hour) + (this.from.meridian == "PM" ? 12 : 0)) * 60) + Number.parseInt(this.from.minutes);
                    to = ((Number.parseInt(this.to.hour === "00" ? 0 : this.to.hour) + (this.to.meridian == "PM" ? 12 : 0)) * 60) + Number.parseInt(this.to.minutes);
                }

                if (from > to) {
                    let temp = this.from;
                    this.from = this.to;
                    this.to = temp;
                }
                this.$emit('input', this.hours);
            },
            clearInput() {
                this.cleared = true;
            }
        },
        computed: {
            hours() {
                if (this.cleared) {
                    this.$emit('input', '');
                    return ""
                }
                if (!this.time_24hr) {
                    if (this.range)
                        return `${this.from.hour}:${this.from.minutes} ${this.from.meridian} - ${this.to.hour}:${this.to.minutes} ${this.to.meridian}`;
                    else
                        return `${this.from.hour}:${this.from.minutes} ${this.from.meridian}`;
                } else {
                    if (this.range)
                        return `${this.from.hour}:${this.from.minutes} - ${this.to.hour}:${this.to.minutes}`;
                    else
                        return `${this.from.hour}:${this.from.minutes}`;
                }
            }
        },
        props: {
            continuous: {
                type: Boolean,
                default: false
            },
            range: {
                type: Boolean,
                default: false
            },
            clear: {
                type: Boolean,
                default: false
            },
            inputClass: String,
            lang: {
                type: String,
                default: "en"
            },
            time_24hr: {
                type: Boolean,
                default: true
            },
            value: {
                type: String,
                default: "00:00 AM - 23:59 PM"
            }
        }
    }
</script>

<style scoped>
    b {
        color: #555;

    }

    .time-picker-tooltiptext input {
        max-width: 40px;
        text-align: center;
        height: 38px;
        border: 1px solid #ced4da;
        border-radius: .25rem;
        display: inline;
        margin: 5px;
        background-color: transparent;
    }

    input[type="button"] {
        line-height: 0px;
    }

    .time-picker-tooltip {
        height: 38px;
        position: relative;
        display: flex;
    }

    .time-picker-tooltip .time-picker-tooltiptext {
        visibility: hidden;
        text-align: center;
        border: 1px solid #ced4da;
        border-radius: .25rem;
        background-color: white;
        padding: 5px;
        position: absolute;
        z-index: 1;
        top: 110%;
        line-height: 48px;
        left: 0%;
        display: flex;
        opacity: 0;
        transition: opacity 0.3s;
    }

    .time-picker-tooltip .time-picker-tooltiptext::after {
        content: "";
        position: absolute;
        bottom: 100%;
        left: 10%;
        margin-left: -5px;
        border-width: 5px;
        border-style: solid;
        border-color: transparent transparent #555 transparent;
    }

    .time-picker-input-tooltip:focus + .time-picker-tooltiptext, .time-picker-tooltiptext:hover {
        visibility: visible;
        opacity: 1;
    }

    .time-picker-input-tooltip {
        min-width: 150px;
        border: 1px solid #ced4da;
        border-radius: .25rem;
        height: 38px;
        padding: 0.375rem 1rem;
    }

    .time-picker-clear {
        border: 1px solid #ced4da;
        border-radius: .25rem;
        height: 38px;
        width: 38px;
        display: inline;
        background-color: white;
        padding: 0.375rem 1rem;
    }
</style>
