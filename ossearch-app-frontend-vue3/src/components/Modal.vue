<template>
  <!-- Modal -->
  <div class="modal fade" :id="id" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" :aria-labelledby="id" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" :class="modalClass">
      <div class="modal-content">
        <div class="modal-header">
          <slot name="header"></slot>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" @click="emitCancel"></button>
        </div>
        <div class="modal-body">
          <slot name="body"></slot>
        </div>
        <div class="modal-footer">
          <slot name="footer">
              <slot name="button-cancel">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" aria-label="Cancel" @click="emitCancel">Cancel</button>
              </slot>
              <slot name="button-action">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="emitConfirm" aria-label="Confirm">Confirm</button>
              </slot>
          </slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Modal",
  props: ['id', 'modalClass'],
  data() {
    return {}
  },
  methods: {
    emitCancel() {
      this.$emit('cancel');
    },
    emitConfirm() {
      this.$emit('confirm');
    }
  },
}
</script>

<style scoped lang="scss">
</style>