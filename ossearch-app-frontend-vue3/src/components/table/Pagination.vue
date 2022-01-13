<template>
  <div class="clearfix" aria-label="Page navigation example">
    <div class="hint-text">Showing <b>{{ getShowing() }}</b> out of <b>{{ pagination.page.totalElements }}</b>
      entries
    </div>
    <ul class="pagination">
      <li class="page-item">
        <a v-if="pagination._links.first" @click="$emit('first');" class="page-link" href="#" aria-label="First Page">
          <span aria-hidden="true">&laquo;</span>
        </a>
      </li>
      <li class="page-item">
        <a v-if="pagination._links.prev" @click="$emit('prev');" class="page-link" href="#" aria-label="Previous">
          <span aria-hidden="true">&lsaquo;</span>
        </a>
      </li>

      <template v-for="(item, index) in items" :key="index">
        <li :class="{ active: (item - 1) === pagination.page.number }" class="page-item">
          <span v-if="isNaN(Number(item))" class="page-link">{{ item }}</span>
          <a v-else class="page-link" href="#" @click="$emit('page', item-1);">{{ item }}</a>
        </li>
      </template>

      <li class="page-item">
        <a v-if="pagination._links.next" @click="$emit('next');" class="page-link" href="#" aria-label="Next">
          <span aria-hidden="true">&rsaquo;</span>
        </a>
      </li>
      <li class="page-item">
        <a v-if="pagination._links.last" @click="$emit('last');" class="page-link" href="#" aria-label="Last Page">
          <span aria-hidden="true">&raquo;</span>
        </a>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "Pagination",
  props: ['pagination', 'client', 'filtered', 'length'],
  data() {
    return {
      maxButtons: 7,
      totalVisible: 7
    }
  },
  computed: {
    items() {
      const maxLength = this.totalVisible > this.maxButtons
          ? this.maxButtons
          : this.totalVisible || this.maxButtons;

      if (this.pagination.page.totalPages <= maxLength || maxLength < 1) {
        return this.getRange(1, this.pagination.page.totalPages);
      }

      const even = maxLength % 2 === 0 ? 1 : 0;
      const left = Math.floor(maxLength / 2);
      const right = this.pagination.page.totalPages - left + 1 + even;

      if (this.pagination.page.number > left && this.pagination.page.number < right) {
        const start = this.pagination.page.number - left + 2;
        const end = this.pagination.page.number + left - 2 - even;
        return [1, '...', ...this.getRange(start, end), '...', this.pagination.page.totalPages];
      } else if (this.pagination.page.number === left) {
        const end = this.pagination.page.number + left - 1 - even;
        return [...this.getRange(1, end), '...', this.pagination.page.totalPages];
      } else if (this.pagination.page.number === right) {
        const start = this.pagination.page.number - left + 1;
        return [1, '...', ...this.getRange(start, this.pagination.page.totalPages)];
      } else {
        return [...this.getRange(1, left), '...', ...this.getRange(right, this.pagination.page.totalPages)];
      }
    }
  },
  methods: {
    getRange(from, to) {
      const range = [];
      from = from > 0 ? from : 1;
      for (let i = from; i <= to; i++) {
        range.push(i);
      }
      return range;
    },
    getShowing() {
      if ( (this.pagination.page.number+1) === this.pagination.page.totalPages) {
        return this.pagination.page.totalElements
      } else {
        return this.pagination.page.size * (this.pagination.page.number+1)
      }

    }
  }
}
</script>

<style scoped lang="scss">
.hint-text {
  float: left;
  margin-top: 10px;
  font-size: 13px;
}

.pagination {
  float: right;
  margin: 0 0 5px;
}
</style>