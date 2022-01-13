<template>
  <div class="users-wrapper">
    <div class="user-title">
      <div class="row">

        <div class="col-sm-4">
          <h2><b>Users</b></h2>
        </div>

        <div class="col-sm-8">
          <!-- Button trigger modal -->
          <button type="button" class="btn btn-primary bi-plus-lg" data-bs-toggle="modal" data-bs-target="#addModal">
            Add User
          </button>
        </div>

      </div>
    </div>


    <Datatable :tableOptions="tableOptions"
               @sort="sortBy"
               @rowsPerPage="getSize"
               @search="getProjects"
               @pagination="getProjects($event.url, $event.params)"
    >
<!--      <template v-slot:table-head>

      </template>-->
      <template v-slot:table-body>
        <tr v-for="(item, i) in items" :key="i">
          <td>{{ item.firstName }}</td>
          <td>{{ item.lastName }}</td>
          <td>{{ item.username }}</td>
          <td>{{ item.email }}</td>
          <td>
            <div v-for="(role, i) in item._embedded.roles" :key="i">
              <span v-if="role.name === 'ROLE_ADMIN'" class="badge rounded-pill bg-danger">{{ role.name }}</span>
              <span v-else-if="role.name === 'ROLE_MANAGER'" class="badge rounded-pill bg-warning">{{ role.name }}</span>
              <span v-else class="badge rounded-pill bg-primary">{{ role.name }}</span>
            </div>
          </td>
          <td class="justify-content-evenly text-center">
            <!--          <a href="#" class="view" title="View" data-toggle="tooltip"><i class="bi-eye-fill"></i></a>-->
            <!--          <a href="#" class="edit link-primary" title="Edit" @click="isEditModalVisible === true && edit(item._links.self.href, $event.target)">-->
            <a href="#" class="edit link-primary" title="Edit" data-bs-toggle="modal" data-bs-target="#editModal" @click="selectUser(item, i)">
              <i class="bi-pencil-square text-primary"></i> Edit</a> |
            <a href="#" class="delete link-danger" data-bs-toggle="modal" data-bs-target="#deleteModal" title="Delete"
               @click="selectUser(item, i)">
              <i class="bi-x-square text-danger"></i> Delete</a>
          </td>
        </tr>
      </template>
    </Datatable>

    <Modal id="addModal" @cancel="cancelAddUser">
      <template v-slot:header>
        <h5 class="modal-title text-black">Add User</h5>
        <!--        <ProgressBarSteps :step="step"/>-->
      </template>
      <template v-slot:body>
        <UserForm :step="step"
                  v-model:firstName="userData.user.firstName"
                  v-model:lastName="userData.user.lastName"
                  v-model:username="userData.user.username"
                  v-model:email="userData.user.email"
                  v-model:roles="userData.user._embedded.roles"
        />
      </template>
      <template v-slot:button-action>
        <template id="step-1" v-if="step === 1">
          <button type="button" class="btn btn-secondary" @click.prevent="next()">Next</button>
        </template>
        <template id="step-1" v-if="step === 2">
          <button type="button" class="btn btn-secondary" @click.prevent="prev()">Previous</button>
          <button type="button" class="btn btn-danger" data-bs-dismiss="modal" @click.prevent="addUser()">Confirm
          </button>
        </template>
      </template>
    </Modal>

    <Modal id="editModal" @cancel="step = 1">
      <template v-slot:header>
        <h5 class="modal-title text-black">Edit User</h5>
        <!--        <ProgressBarSteps :step="step"/>-->
      </template>
      <template v-slot:body>
        <UserForm :step="step"
                  v-model:firstName="userData.user.firstName"
                  v-model:lastName="userData.user.lastName"
                  v-model:username="userData.user.username"
                  v-model:email="userData.user.email"
                  v-model:roles="userData.user._embedded.roles"/>
      </template>
      <template v-slot:button-action>
        <template id="step-1" v-if="step === 1">
          <button type="button" class="btn btn-secondary" @click.prevent="next()">Next</button>
        </template>
        <template id="step-1" v-if="step === 2">
          <button type="button" class="btn btn-secondary" @click.prevent="prev()">Previous</button>
          <button type="button" class="btn btn-danger" data-bs-dismiss="modal" @click.prevent="updateUser()">Confirm
          </button>
        </template>
      </template>
    </Modal>

    <Modal id="deleteModal" @cancel="step = 1">
      <template v-slot:header>
        <h5 class="modal-title text-black">Delete User</h5>
      </template>
      <template v-slot:body>
        <p>Are you sure you want to delete user <b>{{ selectedUser.firstName }} {{ selectedUser.lastName }}</b>! This
          can not be undone!</p>
      </template>
      <template v-slot:button-action>
        <template name="button-action">
          <button type="button" class="btn btn-danger" data-bs-dismiss="modal"
                  @click.prevent="deleteUser(userData.user._links.self.href, itemIndex)">Delete
          </button>
        </template>
      </template>
    </Modal>

  </div>
</template>

<script>
// import EventBus from "../common/EventBus";
import UserService from "../services/user.service";
import Datatable from "../components/table/Datatable";
import Modal from "../components/Modal";
import UserForm from "../components/forms/UserForm";
// import ProgressBarSteps from "../components/ProgressBarSteps";
// import axios from "axios";

export default {
  name: "Users",
  components: {
    Datatable,
    Modal,
    UserForm,
    // ProgressBarSteps
  },
  created() {
    this.getProjects();
  },
  data() {
    let sortOrders = {};
    let columns = [
      {label: 'First Name', name: 'firstName'},
      {label: 'Last Name', name: 'lastName'},
      {label: 'Username', name: 'username'},
      {label: 'Email', name: 'email'},
      {label: 'Role', name: 'role'},
      // {label: 'Role Select', name: 'roleSelect'}
    ];
    columns.forEach((column) => {
      sortOrders[column.name] = -1; // sort descending by default
    });
    return {
      tableOptions: {
        columns: columns,
        sortKey: 'firstName',
        sortOrders: sortOrders,
        rowsPerPageSelect: ['5', '10', '20', '30', '40'],
        rowsPerPage: 10,
        search: '',
        sort: '',
        column: '',
        dir: '',
        pagination: {
          _links: '',
          page: ''
        },
      },
      step: 1,
      items: [],
      userData: {
        user: {
          firstName: '',
          lastName: '',
          username:'',
          email:'',
          _embedded: {
            roles: []
          },
          roleList: []
        },
        // roles: [],
        collections: []
      },
      selectedUser: {},
      itemIndex: null
    }
  },
  methods: {
    getProjects(url = '/users', params) {
      console.log("[getProjects] url: " + url + ", prams: " + JSON.stringify(params), null, 2)

      // this.tableData.draw++;
      UserService.getUsers(url, params)
          .then(response => {
            let data = response.data;

            //used during searching to get the last request we sent
            /*if (this.tableData.draw == data.draw) {
              this.items = data._embedded.users;
              this.configPagination(data.data);
            }*/

            this.items = data._embedded.users;
            this.configPagination(data._links, data.page);
          })
          .catch(errors => {
            console.log(errors);
          });
    },
    configPagination(_links, page) {
      this.tableOptions.pagination._links = _links
      this.tableOptions.pagination.page = page

      // console.log('pagination: ' + JSON.stringify(this.tableOptions.pagination, null, 2))
    },
    getSize(rowsPerPage) {
      // alert("this.itemsPerPage: " +this.itemsPerPage +", param: " + itemsPerPage)
      // console.log("getSize value:" + rowsPerPage)
      this.tableOptions.rowsPerPage = Number(rowsPerPage)
      this.getProjects('/users', {size: this.tableOptions.rowsPerPage})
    },
    sortBy(key) {
      this.tableOptions.sortKey = key;
      this.tableOptions.sortOrders[key] = this.sortOrders[key] * -1;
      this.tableOptions.column = this.getIndex(this.columns, 'name', key);
      this.tableOptions.dir = this.sortOrders[key] === 1 ? 'asc' : 'desc';
      this.getProjects();
    },
    getIndex(array, key, value) {
      return array.findIndex(i => i[key] == value)
    },
    getRoles(roles) {
      // console.log(JSON.stringify(roles, null, 2))
      // let test = ""
      return roles.map(function (role) {
        return role.name
      })//.join(", ")
    },
    prev() {
      this.step--;
    },
    next() {
      this.step++;
    },
    async addUser() {
      console.log(JSON.stringify(this.userData, null, 2))
      let userRef = '';

      //add the user
      await UserService.addUser("/users", this.userData.user)
          .then(response => {
            console.log("add user response: " + response);
            userRef = response.data._links.self.href
            console.log("user added (1): " + userRef)
          })
          .catch(errors => {
            console.log("add user error: " + errors);
            alert("error creating user")
          });

      console.log("user added: " + userRef)

      if (userRef) {
        // add the user role relationship
        UserService.addRole(userRef + "/roles", this.userData.roles)
            .then(response => {
              console.log("add role response: " + response);
            })
            .catch(errors => {
              console.log("add role error: " + errors);
            });
      } else {
        alert("error setting user role")
      }
      this.userData.user = {}
      this.userData.collections = []
      this.userData.roles = []
    },
    updateUser(user) {
      console.log("Update user: " + user)
      //do the update request
      this.selectedUser = {}
    },
    async deleteUser(user, itemIndex) {
      // let itemIndex = this.items.indexOf(user)
      console.log("Delete User: " + user + ", item index: " + itemIndex)
      //do the delete request
      await UserService.deleteUser(user)
      // this.items.splice(itemIndex, 1);
      this.getProjects(this.tableOptions.pagination._links.self.href)
      this.userData.user = {}
      this.itemIndex = null
    },
    selectUser(user, itemIndex) {
      console.log(user)
      this.userData.user = JSON.parse(JSON.stringify(user))
      this.selectedUser = JSON.parse(JSON.stringify(user))
      this.itemIndex = itemIndex
    },
    cancelAddUser() {
      this.step = 1
      this.userData = {}
    }
  }
  ,
  // mounted() {
  //   UserService.getUsers().then(
  //       response => {
  //         this.content = response.data;
  //         this.users = this.content._embedded.users
  //         this.$log.debug('getUser response: ', response)
  //       },
  //       error => {
  //         this.content =
  //             (error.response && error.response.data && error.response.data.message) ||
  //             error.message ||
  //             error.toString();
  //
  //         if (error.response && error.response.status === 403) {
  //           EventBus.dispatch("logout");
  //         }
  //       }
  //   )
  // }
}
</script>

<style scoped lang="scss">
.users-wrapper {
  background: #fff;
  //padding: 20px 25px;
  margin: 30px auto;
  border-radius: 3px;
  box-shadow: 0 1px 1px rgba(0, 0, 0, .05);
}

.users-wrapper .btn {
  float: right;
  //color: #333;
  //background-color: #fff;
  border-radius: 3px;
  border: none;
  outline: none !important;
  margin-left: 10px;
}

.users-wrapper .btn:hover {
  color: #333;
  background: #f2f2f2;
}

.users-wrapper .btn.btn-primary {
  color: #fff;
  background: #03A9F4;
}

.users-wrapper .btn.btn-primary:hover {
  background: #03a3e7;
}

.user-title .btn {
  font-size: 13px;
  border: none;
}

.user-title .btn i {
  float: left;
  font-size: 21px;
  margin-right: 5px;
}

.user-title .btn span {
  float: left;
  margin-top: 2px;
}

.user-title {
  color: #fff;
  background: #4b5366;
  padding: 16px 25px;
  margin: -20px -25px 10px;
  border-radius: 3px 3px 0 0;
}

.user-title h2 {
  margin: 5px 0 0;
  font-size: 24px;
}





.pagination {
  float: right;
  margin: 0 0 5px;
}

.pagination li a {
  border: none;
  font-size: 13px;
  min-width: 30px;
  min-height: 30px;
  color: #999;
  margin: 0 2px;
  line-height: 30px;
  border-radius: 2px !important;
  text-align: center;
  padding: 0 6px;
}

.pagination li a:hover {
  color: #666;
}

.pagination li.active a {
  background: #03A9F4;
}

.pagination li.active a:hover {
  background: #0397d6;
}

.pagination li.disabled i {
  color: #ccc;
}

.pagination li i {
  font-size: 16px;
  padding-top: 6px
}

.hint-text {
  float: left;
  margin-top: 10px;
  font-size: 13px;
}
</style>