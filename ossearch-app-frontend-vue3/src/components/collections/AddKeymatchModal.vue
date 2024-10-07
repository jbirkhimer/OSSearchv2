<template>
  <div class="modal" tabindex="-1" role="dialog" style="display: block; background-color: rgba(0,0,0,0.5);">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Add New Keymatch</h5>
          <button type="button" class="btn-close" @click="$emit('close')"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="addKeymatch">
            <div class="mb-3">
              <label for="searchTerm" class="form-label">Search Term</label>
              <input type="text" class="form-control" id="searchTerm" v-model="newKeymatch.searchTerm" required>
            </div>
            <div class="mb-3">
              <label for="titleForMatch" class="form-label">Title for Match</label>
              <input type="text" class="form-control" id="titleForMatch" v-model="newKeymatch.titleForMatch" required>
            </div>
            <div class="mb-3">
              <label for="urlForMatch" class="form-label">URL for Match</label>
              <input type="url" class="form-control" id="urlForMatch" v-model="newKeymatch.urlForMatch" required>
            </div>
            <div class="mb-3">
              <label for="imgUrlForMatch" class="form-label">Image URL for Match</label>
              <input type="url" class="form-control" id="imgUrlForMatch" v-model="newKeymatch.imgUrlForMatch">
            </div>
            <div class="mb-3">
              <label for="keymatchType" class="form-label">Keymatch Type</label>
              <select class="form-select" id="keymatchType" v-model="newKeymatch.keymatchType" required>
                <option value="keyword">Keyword</option>
                <option value="phrase">Phrase</option>
                <option value="exact">Exact</option>
              </select>
            </div>
            <button type="submit" class="btn btn-primary">Add Keymatch</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CollectionService from "@/services/collection.service";

export default {
  name: 'AddKeymatchModal',
  props: {
    collectionId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      newKeymatch: {
        searchTerm: '',
        titleForMatch: '',
        urlForMatch: '',
        imgUrlForMatch: '',
        keymatchType: 'keyword'
      }
    };
  },
  methods: {
    async addKeymatch() {
      try {
        const response = await CollectionService.addCollection('/keymatch', {
          ...this.newKeymatch,
          collection: `/collection/${this.collectionId}`
        });

        // Emit the newly added keymatch data
        this.$emit('keymatchAdded', response.data);

        // Reset the form
        this.newKeymatch = {
          searchTerm: '',
          titleForMatch: '',
          urlForMatch: '',
          imgUrlForMatch: '',
          keymatchType: 'keyword'
        };

        // Close the modal
        this.$emit('close');
      } catch (error) {
        console.error('Error adding keymatch:', error);
        // Handle error (e.g., show error message to user)
      }
    }
  }
};
</script>