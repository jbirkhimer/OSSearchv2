<template>
      <input v-if="!reload"
        ref="file"
        type="file"
        :multiple="multiple"
        accept="application/json"
        @change.prevent="fileChange"
        hidden
    />

<!--  <div
      :class="dropActive ? ' upload-body-dragged ' : ''"
      @click="upload()"
      @dragover="dragover"
      @dragleave="dragleave"
      @drop="drop"
  >-->

    <slot v-if="dropActive" name="dropzone-active" :dropActive="dropActive">
      <div class="drag-drop-zone">
        <i class="fas fa-cloud-upload-alt icon"></i>
        <p>Drop to upload</p>
      </div>
    </slot>
    <slot v-else-if="!dropActive && files.length" name="content" :dropActive="dropActive"></slot>
    <slot v-else name="dropzone" :dropActive="dropActive">
      <div class="drag-drop-zone">
        <i class="fas fa-cloud-upload-alt icon"></i>
        <p>Click to upload</p>
        <p class="sub-text">or drag and drop backup files</p>
      </div>
    </slot>
<!--  </div>-->
</template>

<script>
export default {
  name: 'FileDropZone',
  emits: ['update:files'],
  props: {
    files: {
      required: false,
      default: () => []
    },
    dropElementId: {
      required: true
    },
    multiple: {
      required: false,
      default: false
    }
  },
  data () {
    return {
      dropActive: false,
      dropTimeout: undefined,
      reload: false
    }
  },
  computed: {
  },
  watch: {
    files: {
      handler: function () {
        if (!this.files?.length) {
          console.log('watch addEventListener')
          document.getElementById(this.dropElementId).addEventListener('click', this.upload, { capture: true })
        } else {
          console.log('watch removeEventListener')
          document.getElementById(this.dropElementId).removeEventListener('click', this.upload, { capture: true })
        }
      }
    }
  },
  mounted () {
    this.$nextTick(() => {
      // Drag and drop
      console.log('Drag and drop')
      this.watchDrop()
    })
  },
  methods: {
    upload () {
      this.$refs.file.click()
      // this.$emit("upload");
    },
    fileChange (e) {
      console.log('fileChange', e)
      let files = [...this.files]
      e.target.files.forEach(file => files.push(file))
      console.log('update:files', files)
      this.$emit('update:files', files)
    },
    watchDrop () {
      // remove mount
      if (this.dropElementId) {
        let el = document.getElementById(this.dropElementId)

        try {
          el.removeEventListener('click', this.upload, { capture: true })
          el.removeEventListener('dragenter', this.onDragenter, false)
          el.removeEventListener('dragover', this.onDragover, false)
          el.removeEventListener('dragleave', this.onDragleave, false)
          el.removeEventListener('drop', this.onDrop, false)
        } catch (e) {
          // ignore
        }

        try {
          el.addEventListener('click', this.upload, { capture: true })
          el.addEventListener('dragenter', this.onDragenter, false)
          el.addEventListener('dragover', this.onDragover, false)
          el.addEventListener('dragleave', this.onDragleave, false)
          el.addEventListener('drop', this.onDrop, false)
        } catch (e) {
          // ignore
        }
      }
    },
    onDragenter (e) {
      this.dropActive = true
      // highlight potential drop target when the draggable element enters it
      if (e.target.classList.contains('drag-drop-zone')) {
        e.target.classList.add('dragover')
      }
    },
    onDragleave (e) {
      this.dropActive = false
      // reset background of potential drop target when the draggable element leaves it
      if (e.target.classList.contains('drag-drop-zone')) {
        e.target.classList.remove('dragover')
      }
    },
    onDragover (e) {
      e.preventDefault()
      this.dropActive = true
      if (e.target.classList.contains('drag-drop-zone')) {
        e.target.classList.add('dragover')
      }
    },
    onDrop (e) {
      e.preventDefault()
      e.dataTransfer && this.addDataTransfer(e.dataTransfer)
      // move dragged element to the selected drop target
      if (e.target.classList.contains('drag-drop-zone')) {
        e.target.classList.remove('dragover')
      }
      // document.getElementById(this.dropElementId).removeEventListener('click', this.upload, {capture: true})
    },
    addDataTransfer (dt) {
      if (dt?.items?.length) {
        // this.$emit("selectFile", dt.files);
        let files = [...this.files]
        if (this.multiple) {
          dt.files.forEach(file => {
            let index = files.findIndex(f => f.name === file.name)
            if (index !== -1) {
              console.log('found index', index)
              files[index] = file
            } else {
              files.push(file)
            }
          })
        } else {
          files = [dt.files[0]]
        }

        console.log('update:files', files)
        this.$emit('update:files', files)
      }
      this.dropActive = false
    }
  }
}
</script>

<style scoped>
.drag-drop-zone {
  width: 100%;
  height: 250px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.drag-drop-zone.dragover {
  background-color: #b6d1ec;
}

.drag-drop-zone .preview {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.drag-drop-zone .icon {
  width: 25px;
  height: 20px;
}

.drag-drop-zone .sub-text {
  font-size: 12px;
}

/* Add basic styles for the upload container */
.upload-container {
  border-radius: 0.25rem;
  border: 1px solid#486684;
  display: block;
  margin: auto;
  width: 100%;
}

.upload-body {
  align-items: center;
  background-color: #fafafa;
  color: #486684;
  display: flex;
  font-size: 1.5rem;
  justify-content: center;
  min-height: 25vh;
}

.upload-body-dragged {
  color: #fff;
  background-color: #b6d1ec;
}
</style>