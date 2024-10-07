<template>
<!--  <fieldset :disabled="!isEditing">-->
    <ImportExportAddEditCheckSortSearchTable
      :key="tableKey"
      :tableOptions="tableOptions"
      :tableData="keymatches"
      :isEditing="isEditing"
      :page="page"
      :pageSize="pageSize"
      :recordsFiltered="totalKeymatches"
      :search="search"
      :sort="sort"
      :fetchAllData="fetchAllKeymatches"
      @updateTableData="updateKeymatches"
      @update:page="updatePage"
      @update:pageSize="updatePageSize"
      @update:search="updateSearch"
      @update:sort="updateSort"
      @deleteRows="deleteKeymatches"
      @importData="importKeymatches"
      @addRow="showAddModal = true"
    />
    <AddKeymatchModal
      v-if="showAddModal"
      :collectionId="collectionId"
      @close="showAddModal = false"
      @keymatchAdded="handleKeymatchAdded"
    />
<!--  </fieldset>-->
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue';
import ImportExportAddEditCheckSortSearchTable from "@/components/forms/ImportExportAddEditCheckSortSearchTable";
import AddKeymatchModal from "@/components/collections/AddKeymatchModal.vue";
import CollectionService from "@/services/collection.service";

export default {
  name: 'Keymatch',
  components: {
    ImportExportAddEditCheckSortSearchTable,
    AddKeymatchModal
  },
  props: {
    collectionName: {
      type: String,
      required: true
    },
    isEditing: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const keymatches = ref([]);
    const loading = ref(true);
    const page = ref(1);
    const pageSize = ref(10);
    const totalKeymatches = ref(0);
    const search = ref('');
    const sort = reactive({ column: '', direction: 'asc' });
    const showAddModal = ref(false);
    const collectionId = ref(null);
    const tableKey = ref(0);

    const tableOptions = {
        id: "keymatchTable",
        enableImport: true,
        enableAddRow: true,
        enableActions: true,
        columns: [
          {
            label: "Search Term",
            name: "searchTerm",
            class: "text-start",
            style: "width: 10%",
          },
          {
            label: "Title for Match",
            name: "titleForMatch",
            class: "text-start",
            style: "width: 20%",
          },
          {
            label: "URL for Match",
            name: "urlForMatch",
            class: "text-start",
            style: "width: 30%",
          },
          {
            label: "Image URL for Match",
            name: "imgUrlForMatch",
            class: "text-start",
            style: "width: 30%",
          },
          {
            label: "Keymatch Type",
            name: "keymatchType",
            type: "select",
            style: "width: 10%",
            options: [
              { label: "KeywordMatch", value: "keyword" },
              { label: "PhraseMatch", value: "phrase" },
              { label: "ExactMatch", value: "exact" },
            ],
            class: "text-center",
            default: "keyword",
          },
        ],
        lengthMenu: [
          [10, 25, 50, 75, 100, 500],
          [10, 25, 50, 75, 100, 500],
        ],
      };

    const getCollectionId = async () => {
      try {
        const response = await CollectionService.getCollections('/collection/search/getCollectionByName', {
          name: props.collectionName,
          projection: 'collectionIdNameInfo'
        });
        collectionId.value = response.data.id;
      } catch (error) {
        console.error('Error fetching collection ID:', error);
      }
    };

    const fetchKeymatches = async () => {
      if (!collectionId.value) {
        await getCollectionId();
      }
      loading.value = true;
      try {
        const response = await CollectionService.getKeymatches(
          props.collectionName,
          page.value,
          pageSize.value,
          search.value,
          sort.column,
          sort.direction
        );
        keymatches.value = response.data._embedded.keymatch;
        totalKeymatches.value = response.data.page.totalElements;
      } catch (error) {
        console.error('Error fetching keymatches:', error);
      } finally {
        loading.value = false;
        tableKey.value += 1;
      }
    };

    const fetchAllKeymatches = async () => {
      if (!collectionId.value) {
        await getCollectionId();
      }
      try {
        let allKeymatches = [];
        let currentPage = 0;
        const pageSize = 1000; // Adjust this value based on your API's capabilities
        let hasMoreData = true;

        while (hasMoreData) {
          const response = await CollectionService.getKeymatches(
              props.collectionName,
              currentPage + 1,
              pageSize,
              search.value,
              sort.column,
              sort.direction
          );

          allKeymatches = allKeymatches.concat(response.data._embedded.keymatch);

          if (response.data._embedded.keymatch.length < pageSize) {
            hasMoreData = false;
          } else {
            currentPage++;
          }
        }

        return allKeymatches;
      } catch (error) {
        console.error('Error fetching all keymatches:', error);
        throw error;
      }
    };

    const updateKeymatches = (newKeymatches) => {
      keymatches.value = newKeymatches;
    };

    const updatePage = (newPage) => {
      page.value = newPage;
      fetchKeymatches();
    };

    const updatePageSize = (newPageSize) => {
      pageSize.value = newPageSize;
      page.value = 1;
      fetchKeymatches();
    };

    const updateSearch = (newSearch) => {
      search.value = newSearch;
      page.value = 1;
      fetchKeymatches();
    };

    const updateSort = (newSort) => {
      sort.column = newSort.column;
      sort.direction = newSort.direction;
      fetchKeymatches();
    };

    const deleteKeymatches = async (keymatchesToDelete) => {
      loading.value = true;
      try {
        for (const keymatch of keymatchesToDelete) {
          // Extract the ID from the self link
          const selfLink = keymatch._links.self.href;
          const id = selfLink.split('/').pop();

          await CollectionService.deleteCollection(`/keymatch/${id}`);
        }
        await fetchKeymatches();
      } catch (error) {
        console.error('Error deleting keymatches:', error);
      } finally {
        loading.value = false;
      }
    };

    const importKeymatches = async (importedKeymatches) => {
      loading.value = true;
      try {
        for (const keymatch of importedKeymatches) {
          let body = {
            searchTerm: keymatch.searchTerm || keymatch.search_term || keymatch.column1,
            titleForMatch: keymatch.titleForMatch || keymatch.title_for_match || keymatch.column2,
            urlForMatch: keymatch.urlForMatch || keymatch.url_for_match || keymatch.column3,
            imgUrlForMatch: keymatch.imgUrlForMatch || keymatch.img_url_for_match || keymatch.column4,
            keymatchType: keymatch.keymatchType || keymatch.keymatch_type || keymatch.column5 || 'keyword',
            collection: `/collection/${collectionId.value}`
          };
          await CollectionService.addCollection('/keymatch', body);
        }
        await fetchKeymatches();
      } catch (error) {
        console.error('Error importing keymatches:', error);
      } finally {
        loading.value = false;
      }
    };

    const handleKeymatchAdded = async (newKeymatch) => {
      keymatches.value.push(newKeymatch);
      showAddModal.value = false;
      totalKeymatches.value += 1;

      // Optionally, you might want to refetch the data to ensure consistency
      // Uncomment the next line if you want to refetch
      await fetchKeymatches();

      // If you don't refetch, you might want to increment the tableKey to force a re-render
      // tableKey.value += 1;
    };

    const saveKeymatches = async () => {
      loading.value = true;
      try {
        for (const keymatch of keymatches.value) {
          // Extract the ID from the self link
          const selfLink = keymatch._links.self.href;
          const id = selfLink.split('/').pop();

          await CollectionService.updateCollection(
            `/keymatch/${id}`,
            keymatch
          );
        }
        await fetchKeymatches();
      } catch (error) {
        console.error('Error saving keymatches:', error);
        throw error;
      } finally {
        loading.value = false;
      }
    };

    const resetKeymatches = (originalKeymatches) => {
      keymatches.value = JSON.parse(JSON.stringify(originalKeymatches));
      tableKey.value += 1;
    };

    onMounted(() => {
      getCollectionId().then(() => fetchKeymatches());
    });

    watch(() => props.collectionName, () => {
      getCollectionId().then(() => fetchKeymatches());
    });

    return {
      keymatches,
      loading,
      page,
      pageSize,
      totalKeymatches,
      search,
      sort,
      showAddModal,
      collectionId,
      tableKey,
      tableOptions,
      updateKeymatches,
      updatePage,
      updatePageSize,
      updateSearch,
      updateSort,
      deleteKeymatches,
      importKeymatches,
      handleKeymatchAdded,
      saveKeymatches,
      resetKeymatches,
      fetchAllKeymatches,
    };
  }
};
</script>

<style scoped>
/* Add any scoped styles here */
</style>
