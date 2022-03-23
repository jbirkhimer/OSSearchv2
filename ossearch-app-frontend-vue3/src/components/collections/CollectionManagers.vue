<template>
  <fieldset :disabled="!isEditing">
    <ImportAddEditCheckTable
        :tableOptions="tableOptions"
        :tableData="userList"
        :selected="users"
        @selected="addCollectionManagers"
        uniqueCheckField="id">
      <template v-slot:roleList="slotProps">
        <div v-for="(role, i) in slotProps.entry" :key="i">
          <span v-if="role === 'ROLE_ADMIN'" class="badge rounded-pill bg-danger">{{ role }}</span>
          <span v-else-if="role === 'ROLE_MANAGER'" class="badge rounded-pill bg-warning">{{ role }}</span>
          <span v-else class="badge rounded-pill bg-primary">{{ role }}</span>
        </div>
      </template>
    </ImportAddEditCheckTable>
  </fieldset>
</template>

<script>
import UserService from "../../services/user.service";
import ImportAddEditCheckTable from "../forms/ImportAddEditCheckTable";

export default {
  name: "CollectionManagers",
  props: ['isEditing', 'collectionId', 'users', 'owner'],
  components: {
    ImportAddEditCheckTable
  },
  data() {
    let columns = [
      {label: 'First Name', name: 'firstName', class: 'text-center'},
      {label: 'Last Name', name: 'lastName', class: 'text-center'},
      {label: 'Username', name: 'username', class: 'text-center'},
      {label: 'Email', name: 'email', class: 'text-center'},
      {label: 'Role', name: 'roleList', class: 'text-center', type: 'slot'},
    ]
    return {
      tableOptions: {
        enableImport: false,
        enableAddRow: false,
        enableActions: false,
        columns: columns
      },
      userList: [],
    }
  },
  methods: {
    async getUsers(url = '/users', params = {projection: 'userIdNameEmailRoles'}) {
      //console.log("[getUsers] url: " + url + ", prams: " + JSON.stringify(params, null, 2))
      await UserService.getUsers(url, params)
          .then(response => {
            let data = response.data;
            if (data.page.totalElements > data.page.size) {
              this.getUsers('/users', {size: data.page.totalElements, projection: 'userIdNameEmailRoles'})
            }
            this.userList = data._embedded.users;
            //console.log("[getUsers] users",JSON.stringify(this.userList, null, 2))
          })
          .catch(errors => {
            console.log(errors);
          });
    },
    addCollectionManagers(users) {
      //console.log("[addCollectionManagers] users",JSON.stringify(users))

      // let newArray = []
      users.forEach(e => {
        // newArray.push({id: e.id, username: e.username})
        delete e._links
      })
      //console.log("[addCollectionManagers] newArray",JSON.stringify(users))
      this.$emit('update:users', users)
    }

  },
  mounted() {
    this.getUsers()
  }
}
</script>

<style scoped>
.text-center td {
  text-align: center;
  vertical-align: middle;
}
</style>