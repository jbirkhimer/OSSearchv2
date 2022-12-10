<template>
  <div class="tab-pane fade"
       :class="isActiveTab ? index === 0 ? 'show active' : 'active' : ''"
       :id="'nav-'+tabName.toLowerCase()"
       role="tabpanel"
       :aria-labelledby="'nav-'+tabName+'-tab'">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: "Tabs",
  props: ['tabName', 'isActiveTab', 'index'],
}
</script>

<style scoped>

</style>