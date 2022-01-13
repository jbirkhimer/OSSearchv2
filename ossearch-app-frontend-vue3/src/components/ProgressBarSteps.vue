<template>
  <ul class="progressbar">
    <li class="active" v-for="i in step" :key="i">Step {{i}}</li>
    <!--        <li class="active">Step 1</li>-->
    <!--        <li>Step 2</li>-->
    <!--        <li>Step 3</li>-->
  </ul>
</template>

<script>
export default {
  name: "ProgressBarSteps",
  props: ['step'],
}
</script>

<style scoped>
/*.container {*/
/*  width: 100%;*/
/*  position: absolute;*/
/*  z-index: 1;*/
/*  !*margin-top: 20px;*!*/
/*}*/

.progressbar {
  width: 100%;
  position: absolute;
  z-index: 1;
  margin-top: 30px;
  counter-reset: step;
}

.progressbar li {
  list-style-type: none;
  float: left;
  width: 50%;
  position: relative;
  text-align: center;
}

.progressbar li:before {
  content: counter(step);
  counter-increment: step;
  width: 30px;
  height: 30px;
  line-height: 30px;
  border: 1px solid #bebebe;
  display: block;
  text-align: center;
  margin: 0 auto 10px auto;
  border-radius: 50%;
  background: white;

  color: #bebebe;
  font-weight: bold;
}

.progressbar li:after {
  content: '';
  position: absolute;
  width: 100%;
  height: 1px;
  background: #ddd;
  top: 15px;
  left: -50%;
  z-index: -1;
}

.progressbar li:first-child:after {
  content: none;
}

.progressbar li.active {
  color: #3aac5d;
}

.progressbar li.active:before {
  border-color: #3aac5d;
  background: #3aac5d;
  color: white
}

.progressbar li.active + li:after {
  background: #3aac5d;
}

.progressbar li.active + li:before {
  border-color: #3aac5d;
  background: #3aac5d;
  color: white
}

.progressbar li.active:after {
  background: #3aac5d;
}



</style>