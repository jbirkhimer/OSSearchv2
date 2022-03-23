<template>

  <div v-if="loading" class="container-fluid px-4 loading">
    Loading...
    <h2>Name: {{ $route.params.name }}</h2>
  </div>

  <div v-if="error" class="container-fluid px-4 error">
    {{ error }}
  </div>

  <div v-if="user" class="container-fluid px-4">
    <h1 class="mt-4">{{ user.username }}</h1>
    <Breadcrumb/>

    <div class="card mb-4">

      <div class="card-header">
        <i class="fas fa-info-circle me-1"></i>
        Basic Information
        <div class="float-end">
          <button class="btn btn-sm btn-primary float-end" type="button" @click="beforeEdit = {...user}; isEditBasicInfo = !isEditBasicInfo" v-if="!isEditBasicInfo">Edit</button>
          <button v-if="isEditBasicInfo" class="btn btn-sm btn-success me-md-2" type="button" @click="saveBasicInfo()">Save</button>
          <button v-if="isEditBasicInfo" class="btn btn-sm btn-danger float-end" type="button" @click="user = beforeEdit; isEditBasicInfo = false">Cancel</button>
        </div>
      </div>

      <div class="card-body">
        <fieldset :disabled="!isEditBasicInfo">
          <div class="row g-3 mb-3">
            <div class="col-md-6">
              <div class="form-floating mb-3">
                <input v-model="user.firstName" type="text" class="form-control" id="validationDefault01" required placeholder="firstName">
                <label for="validationDefault01" class="form-label">First name</label>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating mb-3">
                <input v-model="user.lastName" type="text" class="form-control" id="validationDefault02" required placeholder="lastName">
                <label for="validationDefault02" class="form-label">Last name</label>
              </div>
            </div>
          </div>

          <div class="row g-3 mb-3">
            <div class="col-md-6">
              <div class="form-floating mb-3">
                <input v-model="user.username" type="text" class="form-control" id="validationDefaultUsername"
                       aria-describedby="inputGroupPrepend2"
                       required placeholder="username">
                <label for="validationDefaultUsername" class="form-label">Username</label>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating mb-3">
                <input v-model="user.email" type="text" class="form-control" id="validationDefault03" required
                       placeholder="email">
                <label for="validationDefault03" class="form-label">Email</label>
              </div>
            </div>
          </div>

          <div class="row g-3">
            <div class="col-md-6">
              <div id="accountEnabled" class="form-check form-switch">
                <input class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckCheckedUserEnabled" v-model="user.enabled">
                <label class="form-check-label" for="flexSwitchCheckCheckedUserEnabled">User Account is Enabled</label>
              </div>
            </div>
          </div>
        </fieldset>
      </div>

    </div>

    <div class="card mb-4">

      <div class="card-header">
        <i class="fas fa-info-circle me-1"></i>
        Password Information
        <div class="float-end">
          <button class="btn btn-sm btn-primary float-end" type="button" @click="beforeEdit = user.password; isEditPassword = !isEditPassword" v-if="!isEditPassword">Edit</button>
          <button v-if="isEditPassword" class="btn btn-sm btn-success me-md-2" type="button" @click="savePassword()">Save</button>
          <button v-if="isEditPassword" class="btn btn-sm btn-danger float-end" type="button" @click="user.password = beforeEdit; confirmPassword = ''; isEditPassword = false">Cancel</button>
        </div>
      </div>

      <div class="card-body">
        <fieldset :disabled="!isEditPassword">
          <div class="row g-3">
            <div class="col-md-6">
              <div id="passwordInputForm" class="form-floating">
                <input v-model="user.password" @blur="v$.user.password.$touch()"
                       type="text"
                       class="form-control"
                       :class="(!v$.user.password.$invalid && user.password !== '') ? ' is-valid': user.password === '' ? '' : 'is-invalid'"
                       id="passwordInput" ref="passwordInput"
                       aria-describedby="inputGroupPrepend2"
                       required
                       placeholder="password"
                       autocomplete="off"
                >
                <label for="passwordInput" class="form-label">Password</label>
                <div class="invalid-feedback">
                  Your password must be 8-20 characters long, contain letters, numbers, and special characters, and must not contain spaces or emoji.
                </div>
                <div :class="(v$.user.password.$invalid && user.password !== '') ? 'show' : 'opacity-1'"
                     class="popover fade bs-popover-end"
                     role="tooltip" id="passwordValidationPopover" ref="passwordValidationPopover">
                  <div class="popover-arrow" data-popper-arrow></div>
                  <h3 class="popover-header">Your password must contain:</h3>
                  <div class="popover-body">
                    <div class="form-text">
                      <span v-if="v$.user.password.containsUppercase.$invalid"><i class="fas fa-times text-danger"></i></span>
                      <span v-else><i class="fas fa-check text-success"></i></span>
                      at least <strong>one uppercase letter</strong>
                    </div>
                    <div class="form-text">
                      <span v-if="v$.user.password.containsLowercase.$invalid"><i class="fas fa-times text-danger"></i></span>
                      <span v-else><i class="fas fa-check text-success"></i></span>
                      at least <strong>one lowercase letter</strong>
                    </div>
                    <div class="form-text">
                      <span v-if="v$.user.password.containsNumber.$invalid"><i class="fas fa-times text-danger"></i></span>
                      <span v-else><i class="fas fa-check text-success"></i></span>
                      at least <strong>one number</strong>
                    </div>
                    <div class="form-text">
                      <span v-if="v$.user.password.containsSpecial.$invalid"><i class="fas fa-times text-danger"></i></span>
                      <span v-else><i class="fas fa-check text-success"></i></span>
                      at least <strong>one special character</strong>
                    </div>
                    <div class="form-text">
                      <span v-if="v$.user.password.containsSpace.$invalid"><i class="fas fa-times text-danger"></i></span>
                      <span v-else><i class="fas fa-check text-success"></i></span>
                      not contains spaces
                    </div>
                    <div class="form-text">
                      <span v-if="v$.user.password.minLength.$invalid"><i class="fas fa-times text-danger"></i></span>
                      <span v-else><i class="fas fa-check text-success"></i></span>
                      at least 8 characters
                    </div>
                    <div class="form-text">
                      <span v-if="v$.user.password.maxLength.$invalid"><i class="fas fa-times text-danger"></i></span>
                      <span v-else><i class="fas fa-check text-success"></i></span>
                      no more than 20 characters
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div id="passwordConfirmInput" class="form-floating">
                <input v-model="confirmPassword" type="text" class="form-control" :class="(!v$.confirmPassword.$invalid && confirmPassword !== '') ? ' is-valid': confirmPassword === '' ? '' : 'is-invalid'" id="validationVerifyPassword" ref="validationVerifyPassword"
                       aria-describedby="inputGroupPrepend2"
                       required placeholder="password" autocomplete="off">
                <label for="validationVerifyPassword" class="form-label">Verify Password</label>
                <div class="invalid-feedback">
                  Password does not match!
                </div>
              </div>
            </div>
          </div>
        </fieldset>
      </div>
    </div>

    <div class="card mb-4">

      <div class="card-header">
        <i class="fas fa-info-circle me-1"></i>
        Roles and Permissions
        <div class="float-end">
          <button class="btn btn-sm btn-primary float-end" type="button" @click="beforeEdit = user.roles; isEditRolesPermissions = !isEditRolesPermissions" v-if="!isEditRolesPermissions">Edit</button>
          <button v-if="isEditRolesPermissions" class="btn btn-sm btn-success me-md-2" type="button" @click="saveRolesPermissions()">Save</button>
          <button v-if="isEditRolesPermissions" class="btn btn-sm btn-danger float-end" type="button" @click="user.roles = beforeEdit; isEditRolesPermissions = false">Cancel</button>
        </div>
      </div>

      <div class="card-body">
        <fieldset :disabled="!isEditRolesPermissions">
          <div class="row g-3">
            <div class="col-md-6">
              <div id="rolesSelect" class="form-floating">
                <!--          <select :value="roles" class="form-select" multiple @input="$emit('update:roles', $event.target.value)"
                                  aria-label="select multiple roles" id="validationDefault04">
                            <option v-for="(role, i) in rolesList" :key="i" :value="role"
                                    :selected="roles.some(r => r.name === role.name)">
                              {{ role.name }}
                            </option>
                          </select>-->

                <!-- For Multiselect 2-way binding is not very intuitive this works tho  -->
                <Multiselect id="roleMultiselect"
                             v-model="user.roles"
                             :modelValue="roles"
                             :options="rolesList"
                             :multiple="true"
                             trackBy="name"
                             label="name"
                             :searchable="false"
                             :placeholder="'Select Role(s)'"
                             :allowEmpty="false"
                />
                <!--            <label for="roleMultiselect" class="form-label">Role(s)</label>-->
              </div>
            </div>
          </div>
        </fieldset>
      </div>
    </div>

    <div class="card mb-4">
      <div class="card-header">
        <i class="fas fa-search me-1"></i>
        Collection Access
        <div class="float-end">
          <button class="btn btn-sm btn-primary float-end" type="button" @click="beforeEdit = user.collections; isEditCollections = !isEditCollections" v-if="!isEditCollections">Edit</button>
          <button v-if="isEditCollections" class="btn btn-sm btn-success me-md-2" type="button" @click="saveCollections">Save</button>
          <button v-if="isEditCollections" class="btn btn-sm btn-danger float-end" type="button" @click="user.collections = beforeEdit; isEditCollections = false">Cancel</button>
        </div>
      </div>

      <div class="card-body">
        <fieldset :disabled="!isEditCollections">
          <ul class="list-group">
            <li v-for="collection in collections" :key="collection" class="list-group-item">
              <input v-model="user.collections" :id="collection.name" :value="collection" class="form-check-input me-1" type="checkbox"
                     aria-label="...">
              <label :for="collection.name">{{ collection.name }}</label>
            </li>
          </ul>
        </fieldset>
      </div>
    </div>

    <div class="accordion" id="accordionExample">
      <div class="accordion-item">
        <h2 class="accordion-header" id="headingOne">
          <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">
            User JSON Data
          </button>
        </h2>
        <div id="collapseOne" class="accordion-collapse collapse" aria-labelledby="headingOne" data-bs-parent="#accordionExample">
          <div class="accordion-body">
            <pre>{{ print(user) }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import UserService from "../../services/user.service";
import useVuelidate from '@vuelidate/core'
import {required, minLength, maxLength, sameAs, helpers,} from "@vuelidate/validators";
import { createPopper } from '@popperjs/core';
import Multiselect from "vue-multiselect";
import RoleService from "../../services/role.service";
import CollectionService from "../../services/collection.service";
import Breadcrumb from "../../components/Breadcrumb";

export default {
  name: "UserDetails",
  props: ['id', 'name'],
  setup: () => ({ v$: useVuelidate() }),
  components: {
    Breadcrumb,
    Multiselect,
    // Popper
  },
  data() {
    return {
      loading: false,
      error: null,
      user: null,
      confirmPassword: '',
      rolesList: [],
      collections: [],
      isEditBasicInfo: false,
      isEditPassword: false,
      isEditRolesPermissions: false,
      isEditCollections: false,
      beforeEdit: null
    }
  },
  validations () {
    if (this.user)
    return {
      user: {
        password: {
          required,
          containsUppercase: helpers.regex(/(.*[A-Z].*)/),
          containsLowercase: helpers.regex(/(.*[a-z].*)/),
          containsNumber: helpers.regex(/(.*[0-9].*)/),
          containsSpecial: helpers.regex(/(.*[!"#$%&'()*+,-./:;<=>?@[\\\]^_`{|}~].*)/),
          containsSpace: helpers.regex(/(.*^(?!.*\s).*)/),
          minLength: minLength(8),
          maxLength: maxLength(20),
          $autoDirty: true
        },
      },
      confirmPassword: {
        required,
        sameAsPassword: sameAs(this.user.password),
        $autoDirty: true
      },
      $autoDirty: true
    }
  },
  created () {
    // watch the params of the route to fetch the data again
    this.$watch(
        () => this.$route.params.name,
        async () => {
          await this.fetchData()
          await this.$nextTick(() => {

            const input = this.$refs["passwordInput"]
            const tooltip = this.$refs["passwordValidationPopover"]

            //Using Poperjs
            createPopper(input, tooltip, {
              placement: "right",
              // strategy: "absolute",
              // modifiers: [
              //   // applyStyle: {enabled: false},
              //   // {
              //   //   name: 'offset',
              //   //   options: {
              //   //     offset: [-150, 0],
              //   //   },
              //   // }
              // ]
            });

            // Using Bootstrap Popover
            // new Popover(input, {
            //   placement: "right",
            //   animation: false,
            //   content: tooltip.innerHTML,
            //   html: true
            // })

          })
        },
        // fetch the data when the view is created and the data is
        // already being observed
        { immediate: true }
    )
    // return this.v$.$touch;
  },
  methods: {
    async fetchData() {
      this.error = this.user = null
      this.loading = true
      let promises = []

      let id = await this.getUser()

      promises.push(UserService.getUsers('/users/' + id + '/collections', {projection: 'collectionIdNameInfo'}))
      promises.push(RoleService.getRoles())
      promises.push(CollectionService.getCollections("/collection", {size: 100, projection: 'collectionIdNameInfo'}))

      await Promise.all(promises)
          .then(([getUsersCollectionsResult, getRolesResult, getCollectionsResult]) => {

            // handle user collections
            this.user.collections = getUsersCollectionsResult.data._embedded.collection
            this.user.collections.forEach(collection => {
              delete collection._links.self.templated
              delete collection._links.collection
            })

            // handle roles list
            this.rolesList = getRolesResult.data._embedded.roles

            // handle collections list
            this.collections = getCollectionsResult.data._embedded.collection;
            this.collections.forEach(collection => {
              delete collection._links.collection
            })

          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          })

      this.loading = false
    },
    async getUser() {
      //console.log(">>>>>>>>>>>>getUser()", "username", this.$route.params.name)
      await UserService.getUsers('/users/search/findByUsername', {projection: 'userInfo', username: this.$route.params.name})
          .then(response => {
            let data = response.data;
            this.user = data
            this.user.password = ''
            delete this.user.collections
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });

      //console.log(">>>>>>>>>>>>getUser()", "id", this.user.id)
      return this.user.id
    },
    print(value) {
      return JSON.stringify(value, null, 2)
    },
    async updateUser(url, body) {
      //console.log("userId", JSON.stringify(this.user.id))

      //console.log("[updateUser] url: " + url + ", body: " + JSON.stringify(body), null, 2)

      await UserService.updateUser(url, JSON.stringify(body))
          .then(response => {
            let data = response.data;
            this.user = data;
            this.user.password = ''
            //console.log("data", data)
            this.$store.commit('auth/updateUsername', this.user.username)
          })
          .catch(errors => {
            //console.log(errors);
            this.error = errors
          });

      await this.$router.push({ name: 'userDetails', params: { name: this.user.username, id: this.user.id }})
      this.isEditBasicInfo = false
    },
    async saveBasicInfo() {
      let url = '/users/'+this.user.id
      let body = {
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        username: this.user.username,
        email: this.user.email
      }
      await this.updateUser(url, body)
      this.isEditBasicInfo = false
    },
    async savePassword() {
      let url = '/users/'+this.user.id
      let body = {
        password: this.user.password,
      }
      await this.updateUser(url, body)
      this.isEditPassword = false
    },
    async saveRolesPermissions() {
      let url = '/users/'+this.user.id+'/roles'
      let body = []

      // add the user role relationship
      this.user.roles.forEach(role => {
        body.push(role._links.self.href.replace('{?projection}',''))
      })
      await UserService.updateRoles(url, body.join("\n"))
      this.isEditRolesPermissions = false
    },
    async saveCollections() {
      let url = '/users/'+this.user.id+'/collections'
      let body = []

      // add the user role relationship
      this.user.collections.forEach(collection => {
        body.push(collection._links.self.href.replace('{?projection}',''))
      })
      await UserService.updateCollections(url, body.join("\n"))
      this.isEditCollections = false
    }
  }
}
</script>

<style scoped lang="scss">

.list-group {
  max-height: 300px;
  margin-bottom: 10px;
  overflow-y: scroll;
  -webkit-overflow-scrolling: touch;
}

/* Hide the popper when the reference is hidden */
.popover[data-popper-reference-hidden] {
  visibility: hidden;
  pointer-events: none;
}

.popover {
  // display: none; <---- don't do this
  opacity: 1; // do this

  background-color: white;
  border: 1px solid #c6c6c6;
  box-shadow: 1px 1px 8px 1px rgba(0, 0, 0, 0.2);
  line-height: 1.5;
  padding: 4px 8px;
  border-radius: 4px;
  z-index: -1; // this keeps it from blocking other elements when it's hidden

  &.show {
    visibility: visible;
    pointer-events: auto;
    opacity: 1; // oh yeah
    z-index: 1;
  }
}

</style>

<style lang="scss">
@import '../../../node_modules/bootstrap/scss/functions';
@import '../../../node_modules/bootstrap/scss/variables';
@import '../../../node_modules/bootstrap/scss/mixins/breakpoints';
@import "../../assets/styles/scss/v-multiselect-bs5";
</style>