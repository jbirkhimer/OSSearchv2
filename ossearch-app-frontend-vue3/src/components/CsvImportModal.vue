<template>
  <div class="modal fade" id="csvImportModal" tabindex="-1" aria-labelledby="csvImportModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="csvImportModalLabel">Import CSV</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label for="csvFile" class="form-label">Select CSV file</label>
            <input class="form-control" type="file" id="csvFile" accept=".csv" @change="handleFileSelect">
          </div>
          <div class="form-check">
            <input class="form-check-input" type="checkbox" v-model="hasHeaders" id="hasHeadersCheck">
            <label class="form-check-label" for="hasHeadersCheck">
              CSV has headers
            </label>
          </div>
          <div v-if="headerValidationError" class="alert alert-danger mt-3">
            {{ headerValidationError }}
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary" @click="importCsv" :disabled="!selectedFile || !!headerValidationError">Import</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue';
import { Modal } from 'bootstrap';

export default {
  name: 'CsvImportModal',
  props: {
    expectedHeaders: {
      type: Array,
      required: true
    }
  },
  emits: ['importData'],
  setup(props, { emit }) {
    const selectedFile = ref(null);
    const hasHeaders = ref(true);
    const headerValidationError = ref(null);
    let modal = null;

    const handleFileSelect = (event) => {
      selectedFile.value = event.target.files[0];
      headerValidationError.value = null;
      if (selectedFile.value && hasHeaders.value) {
        validateHeaders();
      }
    };

    const camelToSnakeCase = (str) => str.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`);
    const snakeToCamelCase = (str) => str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());

    const validateHeaders = () => {
      const reader = new FileReader();
      reader.onload = (e) => {
        const csv = e.target.result;
        const lines = csv.split("\n");
        if (lines.length > 0) {
          const headers = lines[0].split(",").map(header => header.trim());
          const expectedSnakeCaseHeaders = props.expectedHeaders.map(camelToSnakeCase);
          const missingHeaders = props.expectedHeaders.filter((header, index) =>
            !headers.includes(header) && !headers.includes(expectedSnakeCaseHeaders[index])
          );
          if (missingHeaders.length > 0) {
            headerValidationError.value = `Missing required headers: ${missingHeaders.join(", ")}`;
          } else {
            headerValidationError.value = null;
          }
        }
      };
      reader.readAsText(selectedFile.value);
    };

    const importCsv = () => {
      if (selectedFile.value && !headerValidationError.value) {
        const reader = new FileReader();
        reader.onload = (e) => {
          const csv = e.target.result;
          const importedData = parseCSV(csv, hasHeaders.value);
          emit('importData', importedData);
          modal.hide();
        };
        reader.readAsText(selectedFile.value);
      }
    };

    const parseCSV = (csv, hasHeaders) => {
      const lines = csv.split("\n");
      const result = [];
      let headers = hasHeaders ? lines[0].split(",").map(header => header.trim()) : props.expectedHeaders;
      const startIndex = hasHeaders ? 1 : 0;

      // If has headers, convert snake_case to camelCase
      if (hasHeaders) {
        headers = headers.map(header =>
          props.expectedHeaders.includes(header) ? header : snakeToCamelCase(header)
        );
      }

      for (let i = startIndex; i < lines.length; i++) {
        const obj = {};
        const currentline = lines[i].split(",");

          for (let j = 0; j < headers.length; j++) {
          obj[headers[j]] = currentline[j] ? currentline[j].trim() : '';
        }

        result.push(obj);
      }

      return result;
    };

    const showModal = () => {
      modal = new Modal(document.getElementById('csvImportModal'));
      modal.show();
    };

    watch(hasHeaders, (newValue) => {
      if (selectedFile.value) {
        if (newValue) {
          validateHeaders();
        } else {
          headerValidationError.value = null;
        }
      }
    });

    return {
      selectedFile,
      hasHeaders,
      headerValidationError,
      handleFileSelect,
      importCsv,
      showModal
    };
  }
};
</script>
