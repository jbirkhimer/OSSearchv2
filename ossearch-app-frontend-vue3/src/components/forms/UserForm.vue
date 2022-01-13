<template>
  <form class="row g-3">
    <template v-if="step === 1">
      <div class="row">
        <div class="col-md-6">
          <label for="validationDefault01" class="form-label">First name</label>
          <input :value="firstName" @input="$emit('update:firstName', $event.target.value)" type="text" class="form-control" id="validationDefault01" required
                 :placeholder="firstName">
        </div>
        <div class="col-md-6">
          <label for="validationDefault02" class="form-label">Last name</label>
          <input :value="lastName" @input="$emit('update:lastName', $event.target.value)" type="text" class="form-control" id="validationDefault02" required
                 :placeholder="lastName">
        </div>
      </div>
      <div class="row">
        <div class="col-md-6">
          <label for="validationDefaultUsername" class="form-label">Username</label>
          <div class="input-group">
            <span class="input-group-text" id="inputGroupPrepend2">@</span>
            <input :value="username" @input="$emit('update:username', $event.target.value)" type="text" class="form-control" id="validationDefaultUsername"
                   aria-describedby="inputGroupPrepend2"
                   required :placeholder="username">
          </div>
        </div>
        <div class="col-md-6">
          <label for="validationDefault03" class="form-label">Email</label>
          <input :value="email" @input="$emit('update:email', $event.target.value)" type="text" class="form-control" id="validationDefault03" required
                 :placeholder="email">
        </div>
      </div>
      <div class="row">
        <div class="col-md-6">
          <label for="validationDefault04" class="form-label">Role</label>
<!--          <select :value="roles" class="form-select" multiple @input="$emit('update:roles', $event.target.value)"
                  aria-label="select multiple roles" id="validationDefault04">
            <option v-for="(role, i) in rolesList" :key="i" :value="role"
                    :selected="roles.some(r => r.name === role.name)">
              {{ role.name }}
            </option>
          </select>-->

          <!-- For Multiselect 2-way binding is not very intuitive this works tho  -->
          <Multiselect
              :modelValue="roles"
              :options="rolesList"
              :multiple="true"
              trackBy="name"
              label="name"
              :searchable="false"
              @update:modelValue="$emit('update:roles', $event)"
          />
        </div>
      </div>
    </template>
    <template v-if="step === 2">
      <ul class="list-group">
        <li v-for="collection in collections" :key="collection.id" class="list-group-item">
          <input :value="collection.name" class="form-check-input me-1" type="checkbox"
                 aria-label="...">
          {{ collection.name }}
        </li>
      </ul>
    </template>
  </form>
</template>

<script>
import RoleService from '../../services/role.service';
import Multiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.css'
// import Multiselect from '@vueform/multiselect'
// import "@vueform/multiselect/themes/default.css";

export default {
  name: "UserForm",
  props: ['step', 'firstName', 'lastName', 'username', 'email', 'roles'],
  components: {Multiselect},
  data() {
    return {
      collections: [
        {
          id: 1,
          name: "Simain"
        },
        {
          id: 2,
          name: "Postal"
        },
        {
          id: 3,
          name: "Airandspace"
        },
        {
          id: 4,
          name: "Nationalzoo"
        },
      ],
      rolesList: [],
      selected: []
    }
  },
  watch: {
    roles() {
      this.selected = this.roles
      console.log(">>>>>>>>>>>selected roles: "+ JSON.stringify(this.selected, null, 2))
    },
  },
  mounted() {
    RoleService.getRoles().then(
        response => {
          // this.content = response.data._embeded;
          // console.log(JSON.stringify(response.data._embedded.roles, null, 2))
          this.rolesList = response.data._embedded.roles
          // this.rolesList = response.data._embedded.roles.map(role => ({label: role.name, value: role}))
        },
        error => {
          this.content =
              (error.response && error.response.data && error.response.data.message) ||
              error.message ||
              error.toString();

          console.log(error)
        }
    )

    // this.selected.roles = JSON.parse(JSON.stringify(this.roles))
    // this.selected = this.roles
    // console.log("selected roles: "+this.selected)
  },
  methods: {
    /*isSelected(role) {
      let isselected = this.roles.some(r => r.name === role.name)
      console.log(role.name + " isselected: " + isselected)
      return isselected
    },*/
    updateRoles(event) {
      console.log(">>>>>>>>>>>>>>> role change >>>>>>>>>"+JSON.stringify(event, null, 2))
      this.$emit('update:roles', event)
    }
  }
}
</script>

<style scoped lang="scss">

</style>