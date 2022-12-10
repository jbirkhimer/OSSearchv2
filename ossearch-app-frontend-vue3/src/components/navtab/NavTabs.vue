<template>
  <nav>
    <ul class="nav nav-tabs" id="nav-tab" role="tablist">
      <template v-for="(tab, i) in tabs" :key="i">
        <li v-if="tab.type === 'dropdown'" class="nav-item dropdown">
          <button
              :id="'nav-'+tab.name.toLowerCase()+'-tab'"
              class="nav-link dropdown-toggle"
              :class="tab.subTabs.some(subTab => subTab.name === activeTab) ? 'active' : ''"
              type="button"
              data-bs-toggle="dropdown"
              :data-bs-target="'#nav-'+tab.name.toLowerCase()+'-subtab'"
              data-bs-auto-close="true"
              @click="show = 'nav-'+tab.name.toLowerCase()+'-subtab'">
            <i :class="tab.icon"></i>
            {{ tab.label }}
          </button>
          <ul :id="'nav-'+tab.name.toLowerCase()+'-subtab'" class="dropdown-menu" :class="show === 'nav-'+tab.name.toLowerCase()+'-subtab' ? 'show' : ''">
            <li v-for="subTab in tab.subTabs" :key="subTab.name">
              <button class="dropdown-item" type="button"
                      :class="subTab.name === activeTab ? 'bg-secondary bg-opacity-25' : ''"
                      @click="setActiveTab(subTab.name)">
                {{ subTab.label }}
              </button>
            </li>
          </ul>
        </li>
        <li v-else class="nav-item">
          <button
              :id="'nav-'+tab.name.toLowerCase()+'-tab'"
              class="nav-link"
              :class="activeTab === tab.name ? 'active' : ''"
              type="button" role="tab"
              data-bs-toggle="tab"
              :data-bs-target="'#nav-'+tab.name.toLowerCase()"
              :aria-controls="'nav-'+tab.name.toLowerCase()+'button'"
              :aria-selected="activeTab === tab.name"
              @click="setActiveTab(tab.name)">
            <i :class="tab.icon"></i>
            {{ tab.label }}
          </button>
        </li>
      </template>

    </ul>

    <div class="tab-content" id="nav-tabContent">
      <slot></slot>
    </div>
  </nav>
</template>

<script>

export default {
  name: "NavTabs",
  props: ['tabs', 'activeTab'],
  data() {
    return {
      show: ''
    }
  },
  methods: {
    setActiveTab(tabName) {
      this.show = ''
      this.$emit('activeTab', tabName)
    }
  }
}
</script>

<style scoped>

</style>