package edu.si.ossearch.security;


import edu.si.ossearch.security.models.*;
import edu.si.ossearch.security.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound(PrivilegeType.READ_PRIVILEGE.toString());
        final Privilege writePrivilege = createPrivilegeIfNotFound(PrivilegeType.WRITE_PRIVILEGE.toString());
        final Privilege passwordPrivilege = createPrivilegeIfNotFound(PrivilegeType.CHANGE_PASSWORD_PRIVILEGE.toString());

        // == create initial roles
        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role adminRole = createRoleIfNotFound(RoleType.ROLE_ADMIN.toString(), adminPrivileges);
        final Role userRole = createRoleIfNotFound(RoleType.ROLE_USER.toString(), userPrivileges);
        final Role managerRole = createRoleIfNotFound(RoleType.ROLE_MANAGER.toString(), userPrivileges);
        createRoleIfNotFound(RoleType.ROLE_USER.toString(), userPrivileges);

        // == create initial user
        createUserIfNotFound("admin@si.edu", "admin", "admin", "admin", adminPassword, true, new HashSet<>(Arrays.asList(adminRole)));
//        createUserIfNotFound("birkhimerj@si.edu", "Jason", "Birkhimer", "birkhimerj", "admin", true, new HashSet<>(Arrays.asList(adminRole, managerRole, userRole)));
//        createUserIfNotFound("manager@si.edu", "firstName", "lastName", "manager", "manager", true, new HashSet<>(Arrays.asList(managerRole)));
//        createUserIfNotFound("user@si.edu", "firstName", "lastName", "user", "user", true, new HashSet<>(Arrays.asList(userRole)));
//
//        for (int i = 1; i < 20; i++) {
//            Random rd = new Random();
//
//            createUserIfNotFound("user_"+i+"@si.edu", "userFirstName_"+i, "userLastName_"+1, "user_"+i, "user_"+i, rd.nextBoolean(), new HashSet<>(Arrays.asList(userRole)));
//        }


        userRepository.flush();
        roleRepository.flush();
        privilegeRepository.flush();

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name).orElse(new Privilege(name));
        privilege = privilegeRepository.saveAndFlush(privilege);
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(final String name, final List<Privilege> privileges) {
        Role role = roleRepository.findByName(name).orElse(new Role(name));
        role.setPrivileges(privileges);
        role = roleRepository.saveAndFlush(role);
        return role;
    }

    @Transactional
    User createUserIfNotFound(final String email, final String firstName, final String lastName, final String username, final String password, boolean enabled, final Set<Role> roles) {
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User u = new User();
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(password));
//            u.setPassword(password);
            u.setEmail(email);
            u.setEnabled(enabled);
            return u;
        });
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        user = userRepository.saveAndFlush(user);
        return user;
    }

}
