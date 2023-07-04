<template>
  <!-- Modal -->
  <div class="modal fade" :id="id" :data-bs-backdrop="data_bs_backdrop" data-bs-keyboard="false" tabindex="-1" :aria-labelledby="id" aria-hidden="true">
    <div :class="'modal-dialog modal-dialog-centered '+modalClass">
      <div class="modal-content">
        <div class="modal-header">
          <slot name="header"></slot>
          <slot name="header-button-close">
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" @click="emitCancel"></button>
          </slot>
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
  props: { id: String, modalClass: { type: String, default: '' }, data_bs_backdrop: { type: String, default: 'static' } },
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