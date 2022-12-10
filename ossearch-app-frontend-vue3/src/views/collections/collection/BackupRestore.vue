<template>
  <h2>BackupRestoreCollection</h2>

  <div class="btn-toolbar justify-content-between mb-3 mt-3" role="toolbar" aria-label="Toolbar with button groups">
    <div class="btn-toolbar" role="toolbar" aria-label="Toolbar with button groups">
      <button type="button" class="btn btn-warning me-2" data-bs-toggle="modal" data-bs-target="#backupModal">Backup</button>
      <button type="button" class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#restoreModal">Restore</button>
    </div>
  </div>

  <!-- Backup Collection Modal -->
  <Modal id="backupModal">
    <template v-slot:header>
      <h5 class="modal-title text-black">Backup Collection</h5>
    </template>
    <template v-slot:body>
      <!--        <p>Are you sure you want to delete collection <b>{{ collection.name }}</b>!</p>
              <p class="text-danger"><b>This can not be undone!</b></p>-->
      <h5>Backup Coming Soon</h5>
    </template>
    <template v-slot:button-action>
      <button type="button" class="btn btn-success" data-bs-dismiss="modal"
              @click.prevent="backupCollection()">Backup
      </button>
    </template>
  </Modal>

  <!-- Restore Collection Modal -->
  <Modal id="restoreModal">
    <template v-slot:header>
      <h5 class="modal-title text-black">Restore Collection</h5>
    </template>
    <template v-slot:body>
      <!--        <p>Are you sure you want to delete collection <b>{{ collection.name }}</b>!</p>
      <p class="text-danger"><b>This can not be undone!</b></p>-->
      <h5>Restore Coming Soon</h5>
    </template>
    <template v-slot:button-action>
      <button type="button" class="btn btn-danger" data-bs-dismiss="modal"
              @click.prevent="restoreCollection()">Restore
      </button>
    </template>
  </Modal>

</template>

<script>
import Modal from "../../../components/Modal";
import EventBus from "../../../common/EventBus";

export default {
  name: "BackupRestore",
  props: ['name', 'tabName'],
  components: {
    Modal
  },
  watch: {
    error: {
      deep: true,
      handler: function () {
        let content = (this.error.response && this.error.response.data && this.error.response.data.message) || this.error.message || this.error.toString();
        if (this.error.response && this.error.response.status === 403) {
          EventBus.dispatch("logout");
        } else {
          alert("ERROR: " + content)
        }
      }
    }
  },
}
</script>

<style scoped>

</style>